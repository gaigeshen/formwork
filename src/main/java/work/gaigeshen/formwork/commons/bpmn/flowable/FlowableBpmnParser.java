package work.gaigeshen.formwork.commons.bpmn.flowable;

import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.cfg.IdGenerator;
import org.flowable.common.engine.impl.de.odysseus.el.ExpressionFactoryImpl;
import org.flowable.common.engine.impl.de.odysseus.el.util.SimpleContext;
import org.flowable.common.engine.impl.javax.el.ExpressionFactory;
import org.flowable.common.engine.impl.javax.el.ValueExpression;
import org.flowable.common.engine.impl.persistence.StrongUuidGenerator;
import work.gaigeshen.formwork.commons.bpmn.Candidate;
import work.gaigeshen.formwork.commons.bpmn.Condition;
import work.gaigeshen.formwork.commons.bpmn.Conditions;
import work.gaigeshen.formwork.commons.bpmn.ProcessNode;

import java.util.*;

/**
 * 流程模型转换器，此类的方法不会对参数做校验，请确保传入的参数合法性
 *
 * @author gaigeshen
 */
public abstract class FlowableBpmnParser {

    private static final IdGenerator idGenerator = new StrongUuidGenerator();

    private FlowableBpmnParser() { }

    /**
     * 转换流程模型
     *
     * @param processStartNode 流程的开始节点
     * @param processId 流程标识符
     * @param procesName 流程名称
     * @return 转换后的流程模型
     */
    public static BpmnModel parseProcess(ProcessNode processStartNode, String processId, String procesName) {
        Process process = new Process();
        BpmnModel bpmnModel = new BpmnModel();
        bpmnModel.addProcess(process);

        process.setExecutable(true);
        process.setId(processId);
        process.setName(procesName);

        EndEvent endEvent = new EndEvent();
        endEvent.setId("endEvent_" + idGenerator.getNextId());
        process.addFlowElement(endEvent);

        parseProcessNode(processStartNode, process, endEvent, true);

        return bpmnModel;
    }

    /**
     * 转换流程模型
     *
     * @param processNode 流程节点可以是开始节点，也可以是用户节点
     * @param process 传入流程对象用于将转换的流程模型添加进去
     * @param endEvent 流程结束节点
     * @param starter 表示是否是开始节点还是用户节点
     * @return 当前传入的流程节点转换后的流程模型
     */
    public static FlowNode parseProcessNode(ProcessNode processNode, Process process, EndEvent endEvent, boolean starter) {
        // 创建的节点类型有开始节点、用户任务节点、排他网关
        // 只有设置了审批人的情况才会创建用户任务节点
        FlowNode processFlowNode;
        if (starter) {
            processFlowNode = new StartEvent();
            processFlowNode.setId("startEvent_" + idGenerator.getNextId());
        } else {
            if (processNode.hasCandidate()) {
                processFlowNode = new UserTask();
                processFlowNode.setId("userTask_" + idGenerator.getNextId());
                Candidate candidate = processNode.getCandidate();
                Set<String> groups = candidate.getGroups();
                Set<String> users = candidate.getUsers();
                if (!groups.isEmpty()) {
                    ((UserTask) processFlowNode).setCandidateGroups(new ArrayList<>(groups));
                }
                if (!users.isEmpty()) {
                    ((UserTask) processFlowNode).setCandidateUsers(new ArrayList<>(users));
                }
            } else {
                processFlowNode = new ExclusiveGateway();
                processFlowNode.setId("exclusive_" + idGenerator.getNextId());
            }
        }
        process.addFlowElement(processFlowNode);

        // 没有任何分支节点的情况则认为流程结束
        if (!processNode.hasOutgoing()) {
            SequenceFlow endFlow = new SequenceFlow();
            process.addFlowElement(endFlow);
            endFlow.setSourceRef(processFlowNode.getId());
            endFlow.setTargetRef(endEvent.getId());
            return processFlowNode;
        }

        // 添加排他网关连接所有的分支节点
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
        exclusiveGateway.setId("exclusive_" + idGenerator.getNextId());

        SequenceFlow exclusiveFlow = new SequenceFlow();
        exclusiveFlow.setSourceRef(processFlowNode.getId());
        exclusiveFlow.setTargetRef(exclusiveGateway.getId());

        process.addFlowElement(exclusiveGateway);
        process.addFlowElement(exclusiveFlow);

        // 每个排他网关都连接到结束节点用于表示审批拒绝的情况
        SequenceFlow endFlow = new SequenceFlow();
        process.addFlowElement(endFlow);
        endFlow.setSourceRef(exclusiveGateway.getId());
        endFlow.setTargetRef(endEvent.getId());
        endFlow.setConditionExpression(Conditions.createAndToExpression("rejected"));

        // 添加所有的分支节点并连接到排他网关
        // 这些分支节点的执行条件都必须是审批通过的
        for (ProcessNode outgoingNode : processNode.getOutgoing()) {
            FlowNode outgoingFlowNode = parseProcessNode(outgoingNode, process, endEvent, false);

            SequenceFlow outgoingNodeFlow = new SequenceFlow();
            outgoingNodeFlow.setId("outgoing_" + idGenerator.getNextId());

            outgoingNodeFlow.setSourceRef(exclusiveGateway.getId());
            outgoingNodeFlow.setTargetRef(outgoingFlowNode.getId());

            process.addFlowElement(outgoingNodeFlow);

            Conditions conditions = outgoingNode.getConditions();
            Conditions updatedConditions = conditions.appendCondition(Condition.create("!rejected"));
            outgoingNodeFlow.setConditionExpression(updatedConditions.toExpression());
        }

        return processFlowNode;
    }

    /**
     * 调用此方法将会获取指定节点以及后续节点预测的审批人
     *
     * @param flowNode 可以直接传入正在进行的用户任务节点
     * @param candidates 此集合将存储所有预测的审批人
     * @param variables 将会基于此变量来预测审批人
     */
    public static void parsePredictCandidates(FlowNode flowNode, List<Candidate> candidates, Map<String, Object> variables) {
        if (flowNode instanceof UserTask) {
            UserTask userTask = (UserTask) flowNode;
            HashSet<String> groups = new HashSet<>(userTask.getCandidateGroups());
            HashSet<String> users = new HashSet<>(userTask.getCandidateUsers());
            candidates.add(new Candidate(groups, users));
        }
        List<SequenceFlow> outgoingFlows = flowNode.getOutgoingFlows();
        if (outgoingFlows.isEmpty()) {
            return;
        }
        FlowNode nextFlowNode = null;
        if (outgoingFlows.size() > 1) {
            for (SequenceFlow outgoingFlow : outgoingFlows) {
                String expression = outgoingFlow.getConditionExpression();
                ExpressionFactory factory = new ExpressionFactoryImpl();
                SimpleContext context = new SimpleContext();
                variables.forEach((k, v) -> {
                    ValueExpression valueExpression = factory.createValueExpression(v, Object.class);
                    context.setVariable(k, valueExpression);
                });
                Object value = factory.createValueExpression(context, expression, Boolean.class).getValue(context);
                if (Objects.nonNull(value) && ((boolean) value)) {
                    nextFlowNode = (FlowNode) outgoingFlow.getTargetFlowElement();
                    break;
                }
            }
        } else {
            nextFlowNode = (FlowNode) outgoingFlows.get(0).getTargetFlowElement();
        }
        if (Objects.nonNull(nextFlowNode)) {
            parsePredictCandidates(nextFlowNode, candidates, variables);
        }
    }
}

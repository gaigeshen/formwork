package work.gaigeshen.formwork.commons.bpmn.flowable;

import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import work.gaigeshen.formwork.commons.bpmn.Candidate;
import work.gaigeshen.formwork.commons.bpmn.Condition;
import work.gaigeshen.formwork.commons.bpmn.Conditions;
import work.gaigeshen.formwork.commons.bpmn.ProcessNode;
import work.gaigeshen.formwork.commons.identity.IdentityCreator;

import java.util.ArrayList;
import java.util.Set;

import static work.gaigeshen.formwork.commons.bpmn.Conditions.createAndToExpression;

/**
 * 流程模型转换器
 *
 * @author gaigeshen
 */
public abstract class FlowableBpmnParser {

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
        endEvent.setId("endEvent_" + IdentityCreator.createDefault());
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
        // 创建流程节点并添加到流程模型中
        FlowNode processFlowNode;
        if (starter) {
            processFlowNode = new StartEvent();
            processFlowNode.setId("startEvent_" + IdentityCreator.createDefault());
        } else {
            processFlowNode = new UserTask();
            processFlowNode.setId("userTask_" + IdentityCreator.createDefault());
        }
        process.addFlowElement(processFlowNode);

        // 如果是用户任务节点则添加审批人
        Candidate candidate = processNode.getCandidate();
        if (!candidate.isEmpty() && processFlowNode instanceof UserTask) {
            Set<String> groups = candidate.getGroups();
            Set<String> users = candidate.getUsers();
            if (!groups.isEmpty()) {
                ((UserTask) processFlowNode).setCandidateGroups(new ArrayList<>(groups));
            }
            if (!users.isEmpty()) {
                ((UserTask) processFlowNode).setCandidateUsers(new ArrayList<>(users));
            }
        }

        // 没有任何分支节点的情况则认为流程结束
        Set<ProcessNode> outgoing = processNode.getOutgoing();
        if (outgoing.isEmpty()) {
            SequenceFlow endFlow = new SequenceFlow();
            process.addFlowElement(endFlow);
            endFlow.setSourceRef(processFlowNode.getId());
            endFlow.setTargetRef(endEvent.getId());
            return processFlowNode;
        }

        // 添加排他网关连接所有的分支节点
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
        exclusiveGateway.setId("exclusive_" + IdentityCreator.createDefault());

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
        endFlow.setConditionExpression(createAndToExpression("rejceted"));

        // 添加所有的分支节点并连接到排他网关
        // 这些分支节点的执行条件都必须是审批通过的
        for (ProcessNode outgoingNode : outgoing) {
            FlowNode outgoingFlowNode = parseProcessNode(outgoingNode, process, endEvent, false);

            SequenceFlow outgoingNodeFlow = new SequenceFlow();
            outgoingNodeFlow.setId("outgoing_" + IdentityCreator.createDefault());

            outgoingNodeFlow.setSourceRef(exclusiveGateway.getId());
            outgoingNodeFlow.setTargetRef(outgoingFlowNode.getId());

            process.addFlowElement(outgoingNodeFlow);

            Conditions conditions = outgoingNode.getConditions();
            if (!conditions.isEmpty()) {
                Conditions updatedConditions = conditions.appendCondition(Condition.create("!rejected"));
                outgoingNodeFlow.setConditionExpression(updatedConditions.toExpression());
            } else {
                outgoingNodeFlow.setConditionExpression(createAndToExpression("!rejected"));
            }
        }

        return processFlowNode;
    }
}

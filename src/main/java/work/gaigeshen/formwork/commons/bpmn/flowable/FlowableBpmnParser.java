package work.gaigeshen.formwork.commons.bpmn.flowable;

import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.cfg.IdGenerator;
import org.flowable.common.engine.impl.de.odysseus.el.ExpressionFactoryImpl;
import org.flowable.common.engine.impl.de.odysseus.el.util.SimpleContext;
import org.flowable.common.engine.impl.javax.el.ExpressionFactory;
import org.flowable.common.engine.impl.javax.el.ValueExpression;
import org.flowable.common.engine.impl.persistence.StrongUuidGenerator;
import work.gaigeshen.formwork.commons.bpmn.Condition;
import work.gaigeshen.formwork.commons.bpmn.Conditions;
import work.gaigeshen.formwork.commons.bpmn.ProcessNode;
import work.gaigeshen.formwork.commons.bpmn.candidate.*;

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
                applyUserTaskCandidate(processNode.getCandidate(), (UserTask) processFlowNode);
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
     * 获取流程节点模型的审批人（如果该节点是用户任务类型），如果不是用户类型的节点则会返回空对象
     *
     * @param flowNode 流程节点模型
     * @return 流程节点模型的审批人
     */
    public static TypedCandidate getCandidate(FlowNode flowNode) {
        if (flowNode instanceof UserTask) {
            UserTask flowNodeUserTask = (UserTask) flowNode;
            return resolveUserTaskCandidate(flowNodeUserTask);
        }
        return null;
    }

    /**
     * 获取流程节点模型的审批人（如果该节点是用户任务类型），以及后续所有的审批人
     *
     * @param flowNode 流程节点模型
     * @param variables 变量集合
     * @return 后续的所有审批人
     */
    public static List<TypedCandidate> getCandidates(FlowNode flowNode, Map<String, Object> variables) {
        List<UserTask> nextUserTasks = getNextUserTasks(flowNode, variables);
        List<TypedCandidate> candidates = new ArrayList<>();
        if (flowNode instanceof UserTask) {
            UserTask flowNodeUserTask = (UserTask) flowNode;
            candidates.add(resolveUserTaskCandidate(flowNodeUserTask));
        }
        for (UserTask nextUserTask : nextUserTasks) {
            candidates.add(resolveUserTaskCandidate(nextUserTask));
        }
        return candidates;
    }

    /**
     * 获取流程节点模型后续的所有用户任务节点模型
     *
     * @param flowNode 流程节点模型
     * @param variables 变量集合
     * @return 后续的所有用户任务节点模型
     */
    public static List<UserTask> getNextUserTasks(FlowNode flowNode, Map<String, Object> variables) {
        List<UserTask> userTasks = new ArrayList<>();
        UserTask nextUserTask = getNextUserTask(flowNode, variables);
        while (Objects.nonNull(nextUserTask)) {
            userTasks.add(nextUserTask);
            nextUserTask = getNextUserTask(nextUserTask, variables);
        }
        return userTasks;
    }

    /**
     * 获取流程节点模型后续的用户任务节点模型
     *
     * @param flowNode 流程节点模型
     * @param variables 变量集合
     * @return 后续的用户任务节点模型
     */
    public static UserTask getNextUserTask(FlowNode flowNode, Map<String, Object> variables) {
        FlowNode nextFlowNode = getNextFlowNode(flowNode, variables);
        while (!(nextFlowNode instanceof UserTask)) {
            if (Objects.isNull(nextFlowNode)) {
                return null;
            }
            nextFlowNode = getNextFlowNode(nextFlowNode, variables);
        }
        return (UserTask) nextFlowNode;
    }

    /**
     * 获取流程节点模型后续的流程节点模型
     *
     * @param flowNode 流程节点模型
     * @param variables 变量集合
     * @return 后续的流程节点模型
     */
    public static FlowNode getNextFlowNode(FlowNode flowNode, Map<String, Object> variables) {
        List<SequenceFlow> nextFlows = flowNode.getOutgoingFlows();
        if (nextFlows.size() == 1) {
            return (FlowNode) nextFlows.get(0).getTargetFlowElement();
        }
        for (SequenceFlow nextFlow : nextFlows) {
            String expression = nextFlow.getConditionExpression();
            ExpressionFactory factory = new ExpressionFactoryImpl();
            SimpleContext context = new SimpleContext();
            variables.forEach((k, v) -> {
                ValueExpression valueExpression = factory.createValueExpression(v, Object.class);
                context.setVariable(k, valueExpression);
            });
            Object value = factory.createValueExpression(context, expression, Boolean.class).getValue(context);
            if (Objects.nonNull(value) && ((boolean) value)) {
                return (FlowNode) nextFlow.getTargetFlowElement();
            }
        }
        return null;
    }

    /**
     * 将审批人应用到用户任务中，作为用户任务的自定义属性保存
     *
     * @param candidate 审批人
     * @param userTask 用户任务
     */
    public static void applyUserTaskCandidate(TypedCandidate candidate, UserTask userTask) {
        CandidateType candidateType = candidate.getType();

        CustomProperty property = new CustomProperty();
        property.setName("type");
        property.setSimpleValue(candidateType.name());
        userTask.setCustomProperties(Collections.singletonList(property));

        if (candidateType.isApprover() || candidateType.isAutoApprover()) {
            Set<String> groups = candidate.getGroups();
            Set<String> users = candidate.getUsers();
            if (!groups.isEmpty()) {
                userTask.setCandidateGroups(new ArrayList<>(groups));
            }
            if (!users.isEmpty()) {
                userTask.setCandidateUsers(new ArrayList<>(users));
            }
        }
    }

    /**
     * 解析用户任务的审批人
     *
     * @param userTask 用户任务
     * @return 审批人
     */
    public static TypedCandidate resolveUserTaskCandidate(UserTask userTask) {
        List<String> groups = userTask.getCandidateGroups();
        List<String> users = userTask.getCandidateUsers();
        Candidate candidate = DefaultCandidate.create(new HashSet<>(groups), new HashSet<>(users));
        for (Map.Entry<String, List<ExtensionElement>> entry : userTask.getExtensionElements().entrySet()) {
            List<ExtensionElement> elements = entry.getValue();
            if ("type".equals(entry.getKey()) && elements.size() > 0) {
                CandidateType type = CandidateType.valueOf(elements.get(0).getElementText());
                return DefaultTypedCandidate.create(candidate, type);
            }
        }
        throw new IllegalStateException("missing candidate type");
    }

    /**
     * 对流程标识符进行包装，因为某些情况下传入的流程标识符可能不合法（例如数字开头），所以要进行包装
     *
     * @param processId 流程标识符
     * @return 包装后的流程标识符
     */
    public static String wrapProcessId(String processId) {
        return Objects.isNull(processId) ? null : "process_" + processId;
    }

    /**
     * 对包装后的流程标识符解包装
     *
     * @param wrappedProcessId 包装后的流程标识符
     * @return 解包装后的流程标识符
     */
    public static String unWrapProcessId(String wrappedProcessId) {
        if (Objects.isNull(wrappedProcessId)) {
            throw new IllegalArgumentException("wrapped process id cannot be null");
        }
        return wrappedProcessId.replace("process_", "");
    }
}

package work.gaigeshen.formwork.commons.bpmn.flowable;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import work.gaigeshen.formwork.commons.bpmn.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author gaigeshen
 */
public class FlowableBpmnService implements BpmnService {

    private final RepositoryService repositoryService;

    private final HistoryService historyService;

    private final RuntimeService runtimeService;

    private final TaskService taskService;

    public FlowableBpmnService(RepositoryService repositoryService,
                               HistoryService historyService,
                               RuntimeService runtimeService, TaskService taskService) {
        this.repositoryService = repositoryService;
        this.historyService = historyService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }

    @Override
    public Collection<UserTask> queryTasks(UserTaskQueryParameters parameters) {
        if (Objects.isNull(parameters)) {
            throw new IllegalArgumentException("user task query parameters cannot be null");
        }
        TaskQuery taskQuery = taskService.createTaskQuery().processDefinitionKey(parameters.getProcessId())
                .processInstanceBusinessKey(parameters.getBusinessKey());
        Set<String> candidateGroups = parameters.getCandidateGroups();
        if (!candidateGroups.isEmpty()) {
            taskQuery.taskCandidateGroupIn(candidateGroups);
        }
        String candidateUser = parameters.getCandidateUser();
        if (Objects.nonNull(candidateUser)) {
            taskQuery.taskCandidateUser(candidateUser);
        }
        List<Task> queryResult = taskQuery.list();
        if (queryResult.isEmpty()) {
            return Collections.emptyList();
        }
        return queryResult.stream().map(task -> {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();
            if (Objects.isNull(processInstance)) {
                throw new IllegalStateException("process instance not found of task: " + task);
            }
            DefaultUserTask.Builder builder = DefaultUserTask.builder()
                    .id(task.getId()).name(task.getName()).description(task.getDescription())
                    .businessKey(processInstance.getBusinessKey())
                    .assignee(task.getAssignee())
                    .createTime(task.getCreateTime()).dueDate(task.getDueDate()).claimTime(task.getClaimTime());
            return builder.build();
        }).collect(Collectors.toSet());
    }

    @Override
    public List<UserTaskActivity> queryTaskActivities(UserTaskActivityQueryParameters parameters) {
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey(parameters.getProcessId()).processInstanceBusinessKey(parameters.getBusinessKey())
                .singleResult();
        if (Objects.isNull(historicProcessInstance)) {
            return Collections.emptyList();
        }
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(historicProcessInstance.getId())
                .activityType("userTask").orderByHistoricActivityInstanceStartTime()
                .asc().list();
        if (activities.isEmpty()) {
            return Collections.emptyList();
        }
        List<UserTaskActivity> userTaskActivities = new ArrayList<>();
        for (HistoricActivityInstance activity : activities) {
            UserTaskActivity userTaskActivity = DefaultUserTaskActivity.builder()
                    .assignee(activity.getAssignee())
                    .startTime(activity.getStartTime())
                    .endTime(activity.getEndTime())
                    .taskId(activity.getTaskId())
                    .build();
            userTaskActivities.add(userTaskActivity);
        }
        return userTaskActivities;
    }

    @Override
    public void completeTask(UserTaskCompleteParameters parameters) {
        if (Objects.isNull(parameters)) {
            throw new IllegalArgumentException("user task complete parameters cannot be null");
        }
        String taskId = parameters.getUserTask().getId();

        Map<String, Object> variables = new HashMap<>(parameters.getVariables());
        variables.put("rejected", parameters.isRejected());

        try {
            taskService.setAssignee(taskId, parameters.getAssignee());
            taskService.complete(taskId, variables);
        } catch (Exception e) {
            throw new IllegalStateException("could not complete user task: " + parameters, e);
        }
    }

    @Override
    public void startProcess(ProcessStartParameters parameters) {
        if (Objects.isNull(parameters)) {
            throw new IllegalArgumentException("process start parameters cannot be null");
        }
        HashMap<String, Object> variables = new HashMap<>(parameters.getVariables());
        variables.put("rejected", false);
        try {
            runtimeService.startProcessInstanceByKey(parameters.getProcessId(), parameters.getBusinessKey(), variables);
        } catch (Exception e) {
            throw new IllegalStateException("could not start process: " + parameters, e);
        }
    }

    @Override
    public void deployProcess(ProcessDeployParameters parameters) {
        if (Objects.isNull(parameters)) {
            throw new IllegalArgumentException("process deploy parameters cannot be null");
        }
        String processId = parameters.getProcessId();
        String procesName = parameters.getProcesName();
        ProcessNode processNode = parameters.getProcessNode();
        String processResourceName = processId + "(" + procesName + ").bpmn20.xml";
        try {
            BpmnModel bpmnModel = FlowableBpmnParser.parseProcess(processNode, processId, procesName);
            repositoryService.createDeployment().addBpmnModel(processResourceName, bpmnModel).deploy();
        } catch (Exception e) {
            throw new IllegalStateException("could not deploy process: " + parameters, e);
        }
    }
}

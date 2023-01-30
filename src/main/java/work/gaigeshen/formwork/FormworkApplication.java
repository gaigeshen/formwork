package work.gaigeshen.formwork;

import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventDispatcher;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.delegate.event.FlowableProcessStartedEvent;
import org.flowable.engine.delegate.event.impl.FlowableProcessStartedEventImpl;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import work.gaigeshen.formwork.commons.bpmn.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author gaigeshen
 */
@SpringBootApplication
public class FormworkApplication implements CommandLineRunner, ApplicationListener<ContextRefreshedEvent> {

    public static void main(String[] args) {
        SpringApplication.run(FormworkApplication.class, args);
    }

    @Autowired
    private BpmnService bpmnService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private SpringProcessEngineConfiguration engineConfiguration;

    @Override
    public void run(String... args) throws Exception {

        Candidate candidate2 = new Candidate(Collections.singleton("${startUserAppointGroup}"), Collections.singleton("${startUserAppointUser}"));

        Candidate candidate3 = new Candidate(Collections.singleton("${startUserAppointGroup}"), Collections.singleton("${startUserAppointUser}"));

        ProcessNode processNode3 = DefaultProcessNode.create(candidate3, DefaultConditions.createEmpty(1));

        ProcessNode processNode2 = DefaultProcessNode.create(candidate2, DefaultConditions.createEmpty(1), Collections.singleton(processNode3));

        ProcessNode startProcessNode = DefaultProcessNode.create(Candidate.createEmpty(), DefaultConditions.createEmpty(1), Collections.singleton(processNode2));

        bpmnService.deployProcess(ProcessDeployParameters.builder().processId("demo").procesName("demo").processNode(startProcessNode).build());

        UserTaskAutoCompletion userTaskAutoCompletion = bpmnService.startProcess(ProcessStartParameters.builder().processId("demo").businessKey("demo11").userId("ggs").variables(Collections.emptyMap()).build());

        System.out.println(userTaskAutoCompletion);

        Collection<UserTask> userTasks2 = bpmnService.queryTasks(UserTaskQueryParameters.builder().candidateUser("ggs").build());
        System.out.println(userTasks2);
//
//        Collection<UserTask> userTasks3 = bpmnService.queryTasks(UserTaskQueryParameters.builder().candidateUser("u3").build());
//        System.out.println(userTasks3);

//        Set<String> assignees = new HashSet<>();
//        assignees.add("u2");
//        assignees.add("u3");
//        Collection<UserTask> historicTasks = bpmnService.queryHistoricTasks(UserHistoricTaskQueryParameters.builder().assignees(assignees).build());
//
//        System.out.println(historicTasks);

//        UserTaskAutoCompletion autoCompletion = bpmnService.completeTask(UserTaskCompleteParameters.builder().userTask(userTasks3.iterator().next()).assignee("u3").rejected(false).variables(Collections.emptyMap()).build());
//
//        System.out.println(autoCompletion);

    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        FlowableEventDispatcher dispatcher = engineConfiguration.getEventDispatcher();
        dispatcher.addEventListener(new AbstractFlowableEngineEventListener() {
            @Override
            protected void processStarted(FlowableProcessStartedEvent event) {

                FlowableProcessStartedEventImpl eventImpl = (FlowableProcessStartedEventImpl) event;

                String processInstanceId = eventImpl.getProcessInstanceId();

                List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();

                for (Task task : tasks) {
                    taskService.setVariableLocal(task.getId(), "startUserAppointGroup", "g1");
                    taskService.setVariableLocal(task.getId(), "startUserAppointUser", "u1");
                }

            }



            @Override
            protected void taskCreated(FlowableEngineEntityEvent event) {

                String processInstanceId = event.getProcessInstanceId();

                List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();

                for (Task task : tasks) {
                    taskService.setVariableLocal(task.getId(), "startUserAppointGroup", "g2");
                    taskService.setVariableLocal(task.getId(), "startUserAppointUser", "u2");
                }
            }
        });
    }
}

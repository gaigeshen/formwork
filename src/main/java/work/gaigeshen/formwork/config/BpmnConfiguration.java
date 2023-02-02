package work.gaigeshen.formwork.config;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import work.gaigeshen.formwork.commons.bpmn.BpmnService;
import work.gaigeshen.formwork.commons.bpmn.CandidateService;
import work.gaigeshen.formwork.commons.bpmn.VariableService;
import work.gaigeshen.formwork.commons.bpmn.flowable.FlowableBpmnService;
import work.gaigeshen.formwork.commons.bpmn.flowable.FlowableCandidateService;
import work.gaigeshen.formwork.commons.bpmn.flowable.FlowableVariableService;

/**
 *
 * @author gaigeshen
 */
@Configuration
public class BpmnConfiguration {

    private final RepositoryService repositoryService;

    private final HistoryService historyService;

    private final RuntimeService runtimeService;

    private final TaskService taskService;

    public BpmnConfiguration(RepositoryService repositoryService,
                             HistoryService historyService,
                             RuntimeService runtimeService, TaskService taskService) {
        this.repositoryService = repositoryService;
        this.historyService = historyService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }

    @Bean
    public BpmnService bpmnService() {
        return new FlowableBpmnService(repositoryService, historyService, runtimeService, taskService, candidateService());
    }

    @Bean
    public VariableService userTaskService() {
        return new FlowableVariableService(historyService, candidateService);
    }

    @Bean
    public CandidateService candidateService() {
        return new FlowableCandidateService(repositoryService, runtimeService, taskService);
    }
}

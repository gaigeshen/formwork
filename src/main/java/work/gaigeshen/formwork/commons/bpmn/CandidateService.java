package work.gaigeshen.formwork.commons.bpmn;

import work.gaigeshen.formwork.commons.bpmn.candidate.Candidate;
import work.gaigeshen.formwork.commons.bpmn.candidate.CandidateVariables;
import work.gaigeshen.formwork.commons.bpmn.candidate.TypedCandidate;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CandidateService {

    TypedCandidate getTaskCandidate(String taskId);

    List<TypedCandidate> getTaskCandidates(String taskId, Map<String, Object> variables);

    List<TypedCandidate> getProcessCalculateCandidates(String processId, Map<String, Object> variables);

    CandidateVariables getProcessCandidateVariables(String processId, String businessKey);

    CandidateVariables getTaskProcessCandidateVariables(String taskId);

    void updateProcessCandidateVariables(String processId, String businessKey, CandidateVariables variables);

    void updateTaskProcessCandidateVariables(String taskId, CandidateVariables variables);

    void addTaskCandidate(String taskId, Candidate candidate);

    void addTaskCandidates(String taskId, Set<Candidate> candidates);

    void removeTaskCandidate(String taskId, Candidate candidate);

    void removeTaskCandidates(String taskId, Set<Candidate> candidates);

    void updateTaskStarterCandidate(String taskId, CandidateVariables variables);

    void updateTaskStarterAppointeeCandidate(String taskId, CandidateVariables variables);

    void updateTaskCandidateUpdatesCandidate(String taskId, CandidateVariables variables);

    void updateTaskCandidate(String taskId);

}


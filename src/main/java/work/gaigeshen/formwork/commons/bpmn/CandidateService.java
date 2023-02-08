package work.gaigeshen.formwork.commons.bpmn;

import work.gaigeshen.formwork.commons.bpmn.candidate.Candidate;
import work.gaigeshen.formwork.commons.bpmn.candidate.TypedCandidate;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CandidateService {

    TypedCandidate getTaskCandidate(String taskId);

    List<TypedCandidate> getTaskCandidates(String taskId, Map<String, Object> variables);

    List<TypedCandidate> getProcessCalculateCandidates(String processId, Map<String, Object> variables);

    TypedCandidate getTaskAppliedCandidate(String taskId);

    Map<String, TypedCandidate> getProcessTaskAppliedCandidates(String processId, String businessKey);

    Map<String, TypedCandidate> getProcessTaskAssignees(String processId, String businessKey);

    void addTaskCandidate(String taskId, Candidate candidate);

    void removeTaskCandidate(String taskId, Candidate candidate);

    default void addTaskCandidates(String taskId, Set<Candidate> candidates) {
        for (Candidate candidate : candidates) {
            addTaskCandidate(taskId, candidate);
        }
    }

    default void removeTaskCandidates(String taskId, Set<Candidate> candidates) {
        for (Candidate candidate : candidates) {
            removeTaskCandidate(taskId, candidate);
        }
    }
}

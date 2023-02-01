package work.gaigeshen.formwork.commons.bpmn;

import work.gaigeshen.formwork.commons.bpmn.candidate.Candidate;
import work.gaigeshen.formwork.commons.bpmn.candidate.CandidateVariables;
import work.gaigeshen.formwork.commons.bpmn.candidate.TypedCandidate;

import java.util.List;
import java.util.Map;

public interface CandidateService {

    TypedCandidate getTaskCandidate(String taskId);

    List<TypedCandidate> getTaskCandidates(String taskId, Map<String, Object> variables);

    List<TypedCandidate> getProcessCalculateCandidates(String processId, Map<String, Object> variables);

    CandidateVariables getProcessCandidateVariables(String processId, String businessKey);

    void updateProcessCandidateVariables(String processId, String businessKey, CandidateVariables variables);

    void addTaskCandidate(String taskId, Candidate candidate);

    void updateTaskCandidate(String taskId);
}


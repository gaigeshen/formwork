package work.gaigeshen.formwork.commons.bpmn;

import work.gaigeshen.formwork.commons.bpmn.candidate.Candidate;
import work.gaigeshen.formwork.commons.bpmn.candidate.CandidateVariables;
import work.gaigeshen.formwork.commons.bpmn.candidate.DefaultCandidate;
import work.gaigeshen.formwork.commons.bpmn.candidate.TypedCandidate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface CandidateService {

    TypedCandidate getTaskCandidate(String taskId);

    List<TypedCandidate> getTaskCandidates(String taskId, Map<String, Object> variables);

    List<TypedCandidate> getProcessCalculateCandidates(String processId, Map<String, Object> variables);

    CandidateVariables getProcessCandidateVariables(String processId, String businessKey);

    void updateProcessCandidateVariables(String processId, String businessKey, CandidateVariables variables);

    void addTaskCandidate(String taskId, Candidate candidate);

    default void addTaskCandidateGroup(String taskId, String group) {
        addTaskCandidate(taskId, DefaultCandidate.createOnlyGroups(Collections.singleton(group)));
    }

    default void addTaskCandidateUser(String taskId, String user) {
        addTaskCandidate(taskId, DefaultCandidate.createOnlyUsers(Collections.singleton(user)));
    }

    void updateTaskCandidate(String taskId);
}


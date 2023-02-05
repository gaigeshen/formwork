package work.gaigeshen.formwork.commons.bpmn.candidate;

import java.util.HashMap;
import java.util.Map;

/**
 * 审批候选人修改参数，用于动态替换审批候选人
 *
 * @author gaigeshen
 */
public class CandidateUpdates {

    private final Map<String, Candidate> groupUpdates = new HashMap<>();

    private final Map<String, Candidate> userUpdates = new HashMap<>();

    private CandidateUpdates() { }

    public static CandidateUpdates create() {
        return new CandidateUpdates();
    }

    public void addGroupUpdate(String group, Candidate candidate) {
        groupUpdates.put(group, candidate);
    }

    public void addUserUpdate(String user, Candidate candidate) {
        userUpdates.put(user, candidate);
    }

    public Map<String, Candidate> getGroupUpdates() {
        return groupUpdates;
    }

    public Map<String, Candidate> getUserUpdates() {
        return userUpdates;
    }
}

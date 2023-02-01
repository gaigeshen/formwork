package work.gaigeshen.formwork.commons.bpmn.candidate;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class CandidateUpdates {

    private final Map<String, Candidate> groupUpdates = new HashMap<>();

    private final Map<String, Candidate> userUpdates = new HashMap<>();


    public void addGroupUpdate(String group, Candidate candidate) {
        groupUpdates.put(group, candidate);
    }

    public void addUserUpdate(String user, Candidate candidate) {
        userUpdates.put(user, candidate);
    }

    public void handleGroupUpdates(BiConsumer<String, Candidate> handler) {
        groupUpdates.forEach(handler);
    }

    public void handleUserUpdates(BiConsumer<String, Candidate> handler) {
        userUpdates.forEach(handler);
    }
}

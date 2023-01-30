package work.gaigeshen.formwork.commons.bpmn;

import java.util.Collections;
import java.util.Set;

/**
 * 审批人
 *
 * @author gaigeshen
 */
public class Candidate {

    private final Set<String> groups;

    private final Set<String> users;

    private boolean autoApproved = false;

    public Candidate(Set<String> groups, Set<String> users) {
        this.groups = groups;
        this.users = users;
    }

    public static Candidate createEmpty() {
        return new Candidate(Collections.emptySet(), Collections.emptySet());
    }

    public boolean isEmpty() {
        return groups.isEmpty() && users.isEmpty();
    }

    public Set<String> getGroups() {
        return groups;
    }

    public Set<String> getUsers() {
        return users;
    }

    public boolean isAutoApproved() {
        return autoApproved;
    }

    public void setAutoApproved(boolean autoApproved) {
        this.autoApproved = autoApproved;
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "groups=" + groups +
                ", users=" + users +
                ", autoApproved=" + autoApproved +
                '}';
    }
}

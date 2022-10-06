package work.gaigeshen.formwork.commons.bpmn;

import java.util.Collections;
import java.util.Set;

/**
 *
 * @author gaigeshen
 */
public class Candidate {

    private final Set<String> groups;

    private final Set<String> users;

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

    @Override
    public String toString() {
        return "Candidate{groups=" + groups + ", users=" + users + '}';
    }
}

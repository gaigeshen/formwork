package work.gaigeshen.formwork.commons.bpmn.candidate;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author gaigeshen
 */
public class DefaultCandidate implements Candidate {

    private final Set<String> groups;

    private final Set<String> users;

    private DefaultCandidate(Set<String> groups, Set<String> users) {
        if (Objects.isNull(groups) || Objects.isNull(users)) {
            throw new IllegalArgumentException("groups and users cannot be null");
        }
        this.groups = groups;
        this.users = users;
    }

    public static DefaultCandidate create(Set<String> groups, Set<String> users) {
        return new DefaultCandidate(groups, users);
    }

    public static DefaultCandidate createGroups(Set<String> groups) {
        return new DefaultCandidate(groups, Collections.emptySet());
    }

    public static DefaultCandidate createUsers(Set<String> users) {
        return new DefaultCandidate(Collections.emptySet(), users);
    }

    @Override
    public Set<String> getGroups() {
        return groups;
    }

    @Override
    public Set<String> getUsers() {
        return users;
    }

    @Override
    public String toString() {
        return "DefaultCandidate{" +
                "groups=" + groups +
                ", users=" + users +
                '}';
    }
}

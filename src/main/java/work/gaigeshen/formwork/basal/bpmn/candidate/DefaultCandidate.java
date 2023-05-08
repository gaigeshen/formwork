package work.gaigeshen.formwork.basal.bpmn.candidate;

import java.util.Collections;
import java.util.HashSet;
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

    public static DefaultCandidate create() {
        return create(Collections.emptySet(), Collections.emptySet());
    }

    public static DefaultCandidate createGroups(Set<String> groups) {
        return create(groups, Collections.emptySet());
    }

    public static DefaultCandidate createUsers(Set<String> users) {
        return create(Collections.emptySet(), users);
    }

    public static DefaultCandidate createUser(String user) {
        return createUsers(Collections.singleton(user));
    }

    @Override
    public Candidate mergeCandidates(Set<Candidate> candidates) {
        if (Objects.isNull(candidates)) {
            throw new IllegalArgumentException("candidates cannot be null");
        }
        Set<String> mergedGroups = new HashSet<>(groups);
        Set<String> mergedUsers = new HashSet<>(users);
        for (Candidate candidate : candidates) {
            mergedGroups.addAll(candidate.getGroups());
            mergedUsers.addAll(candidate.getUsers());
        }
        return create(mergedGroups, mergedUsers);
    }

    @Override
    public Candidate clearCandidates(Set<Candidate> candidates) {
        if (Objects.isNull(candidates)) {
            throw new IllegalArgumentException("candidates cannot be null");
        }
        Set<String> clearedGroups = new HashSet<>(groups);
        Set<String> clearedUsers = new HashSet<>(users);
        for (Candidate candidate : candidates) {
            clearedGroups.removeAll(candidate.getGroups());
            clearedUsers.removeAll(candidate.getUsers());
        }
        return create(clearedGroups, clearedUsers);
    }

    @Override
    public Set<String> getGroups() {
        return Collections.unmodifiableSet(groups);
    }

    @Override
    public Set<String> getUsers() {
        return Collections.unmodifiableSet(users);
    }

    @Override
    public String toString() {
        return "DefaultCandidate{" +
                "groups=" + groups +
                ", users=" + users +
                '}';
    }
}

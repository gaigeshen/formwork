package work.gaigeshen.formwork.commons.bpmn.candidate;

import java.util.Objects;
import java.util.Set;

/**
 *
 * @author gaigeshen
 */
public class DefaultTypedCandidate implements TypedCandidate {

    private final Candidate candidate;

    private final CandidateType type;

    private DefaultTypedCandidate(Candidate candidate, CandidateType type) {
        if (Objects.isNull(candidate) || Objects.isNull(type)) {
            throw new IllegalArgumentException("candidate and type cannot be null");
        }
        this.candidate = candidate;
        this.type = type;
    }

    public static DefaultTypedCandidate create(Candidate candidate, CandidateType type) {
        return new DefaultTypedCandidate(candidate, type);
    }

    @Override
    public Set<String> getGroups() {
        return candidate.getGroups();
    }

    @Override
    public Set<String> getUsers() {
        return candidate.getUsers();
    }

    @Override
    public CandidateType getType() {
        return type;
    }
}

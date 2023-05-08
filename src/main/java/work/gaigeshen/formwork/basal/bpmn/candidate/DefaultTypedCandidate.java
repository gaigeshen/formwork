package work.gaigeshen.formwork.basal.bpmn.candidate;

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
    public TypedCandidate mergeCandidates(Set<Candidate> candidates) {
        Candidate mergedCandidate = candidate.mergeCandidates(candidates);
        return create(mergedCandidate, type);
    }

    @Override
    public TypedCandidate clearCandidates(Set<Candidate> candidates) {
        Candidate clearedCandidate = candidate.clearCandidates(candidates);
        return create(clearedCandidate, type);
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

    @Override
    public Candidate getCandidate() {
        return candidate;
    }
}

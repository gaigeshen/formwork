package work.gaigeshen.formwork.commons.bpmn.candidate;

import java.util.Collections;
import java.util.Set;

/**
 *
 * @author gaigeshen
 */
public interface TypedCandidate extends Candidate {

    default TypedCandidate replaceCandidate(Candidate candidate) {
        return clearCandidates(Collections.singleton(getCandidate())).mergeCandidate(candidate);
    }

    @Override
    default TypedCandidate mergeCandidate(Candidate candidate) {
        return mergeCandidates(Collections.singleton(candidate));
    }

    @Override
    TypedCandidate mergeCandidates(Set<Candidate> candidates);

    @Override
    TypedCandidate clearCandidates(Set<Candidate> candidates);

    CandidateType getType();

    Candidate getCandidate();
}

package work.gaigeshen.formwork.commons.bpmn.candidate;

import java.util.Collections;
import java.util.Set;

/**
 *
 * @author gaigeshen
 */
public interface Candidate {

    default Candidate mergeCandidate(Candidate candidate) {
        return mergeCandidates(Collections.singleton(candidate));
    }

    Candidate mergeCandidates(Set<Candidate> candidates);

    Candidate clearCandidates(Set<Candidate> candidates);

    Set<String> getGroups();

    Set<String> getUsers();
}

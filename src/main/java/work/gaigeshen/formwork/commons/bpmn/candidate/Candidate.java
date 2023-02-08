package work.gaigeshen.formwork.commons.bpmn.candidate;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

/**
 *
 * @author gaigeshen
 */
public interface Candidate extends Serializable {

    default Candidate mergeCandidate(Candidate candidate) {
        return mergeCandidates(Collections.singleton(candidate));
    }

    Candidate mergeCandidates(Set<Candidate> candidates);

    Candidate clearCandidates(Set<Candidate> candidates);

    Set<String> getGroups();

    Set<String> getUsers();
}

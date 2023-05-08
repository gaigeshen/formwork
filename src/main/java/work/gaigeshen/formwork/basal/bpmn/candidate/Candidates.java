package work.gaigeshen.formwork.basal.bpmn.candidate;

import java.io.Serializable;
import java.util.*;

/**
 * 审批候选人集合
 *
 * @author gaigeshen
 */
public class Candidates implements Iterable<Candidate>, Serializable {

    private final List<Candidate> candidates;

    private Candidates(List<Candidate> candidates) {
        if (Objects.isNull(candidates)) {
            throw new IllegalArgumentException("candidates cannot be null");
        }
        this.candidates = new ArrayList<>(candidates);
    }

    public static Candidates create(List<Candidate> candidates) {
        return new Candidates(candidates);
    }

    public static Candidates create() {
        return new Candidates(Collections.emptyList());
    }

    @Override
    public Iterator<Candidate> iterator() {
        return candidates.iterator();
    }

    public Candidate getCandidate(int index) {
        if (index >= candidates.size()) {
            return null;
        }
        return candidates.get(index);
    }

}

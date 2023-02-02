package work.gaigeshen.formwork.commons.bpmn.candidate;

import java.util.*;

/**
 *
 * @author gaigeshen
 */
public class Candidates implements Iterable<Candidate> {

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

    public Candidate poll() {
        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.remove(0);
    }
}

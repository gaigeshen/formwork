package work.gaigeshen.formwork.commons.bpmn.candidate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author gaigeshen
 */
public class Candidates implements Iterable<Candidate> {

    private final List<Candidate> candidates;

    public Candidates(List<Candidate> candidates) {
        if (Objects.isNull(candidates)) {
            throw new IllegalArgumentException("candidates cannot be null");
        }
        this.candidates = new ArrayList<>(candidates);
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

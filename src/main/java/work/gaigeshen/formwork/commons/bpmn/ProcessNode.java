package work.gaigeshen.formwork.commons.bpmn;

import java.util.Set;

/**
 *
 * @author gaigeshen
 */
public interface ProcessNode extends Comparable<ProcessNode> {

    Candidate getCandidate();

    default boolean hasCandidate() {
        return !getCandidate().isEmpty();
    }

    Conditions getConditions();

    Set<ProcessNode> getOutgoing();

    default boolean hasOutgoing() {
        return !getOutgoing().isEmpty();
    }
}

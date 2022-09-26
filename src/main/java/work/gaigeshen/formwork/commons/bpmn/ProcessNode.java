package work.gaigeshen.formwork.commons.bpmn;

import java.util.Set;

/**
 *
 * @author gaigeshen
 */
public interface ProcessNode extends Comparable<ProcessNode> {

    Candidate getCandidate();

    Conditions getConditions();

    Set<ProcessNode> getOutgoing();
}

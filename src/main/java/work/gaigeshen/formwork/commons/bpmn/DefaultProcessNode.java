package work.gaigeshen.formwork.commons.bpmn;

import work.gaigeshen.formwork.commons.bpmn.candidate.TypedCandidate;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * 默认的业务流程处理节点
 *
 * @author gaigeshen
 */
public class DefaultProcessNode implements ProcessNode {

    private final TypedCandidate candidate;

    private final Conditions conditions;

    private final Set<ProcessNode> outgoing;

    private DefaultProcessNode(TypedCandidate candidate, Conditions conditions, Set<ProcessNode> outgoing) {
        if (Objects.isNull(conditions)) {
            throw new IllegalArgumentException("conditions cannot be null");
        }
        if (Objects.isNull(outgoing)) {
            throw new IllegalArgumentException("outgoing object cannot be null");
        }
        this.candidate = candidate;
        this.conditions = conditions;
        this.outgoing = new TreeSet<>(outgoing);
    }

    public static DefaultProcessNode create(TypedCandidate candidate, Conditions conditions, Set<ProcessNode> outgoing) {
        return new DefaultProcessNode(candidate, conditions, outgoing);
    }

    public static DefaultProcessNode create(TypedCandidate candidate, Conditions conditions) {
        return new DefaultProcessNode(candidate, conditions, Collections.emptySet());
    }

    @Override
    public int compareTo(ProcessNode other) {
        int currentPriority = conditions.getPriority();
        return Integer.compare(currentPriority, other.getConditions().getPriority());
    }

    @Override
    public TypedCandidate getCandidate() {
        return candidate;
    }

    @Override
    public Conditions getConditions() {
        return conditions;
    }

    @Override
    public Set<ProcessNode> getOutgoing() {
        return outgoing;
    }

    @Override
    public String toString() {
        return "DefaultProcessNode{" +
                "candidate=" + candidate +
                ", conditions=" + conditions +
                ", outgoing=" + outgoing +
                '}';
    }
}

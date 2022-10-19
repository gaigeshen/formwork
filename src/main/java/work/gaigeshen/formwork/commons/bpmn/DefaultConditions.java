package work.gaigeshen.formwork.commons.bpmn;

import java.util.*;

/**
 *
 * @author gaigeshen
 */
public class DefaultConditions implements Conditions {

    private final Set<Condition> conditions;

    private final int priority;

    public DefaultConditions(Set<Condition> conditions, int priority) {
        if (Objects.isNull(conditions)) {
            throw new IllegalArgumentException("conditions cannot be null");
        }
        this.conditions = conditions;
        this.priority = priority;
    }

    public static Conditions createEmpty(int priority) {
        return new DefaultConditions(Collections.emptySet(), priority);
    }

    @Override
    public Set<Condition> getConditions() {
        return Collections.unmodifiableSet(conditions);
    }

    @Override
    public Conditions appendCondition(Condition condition) {
        Set<Condition> copiedConditions = new HashSet<>(conditions);
        copiedConditions.add(condition);
        return new DefaultConditions(copiedConditions, priority);
    }

    @Override
    public boolean isEmpty() {
        return conditions.isEmpty();
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public String toExpression() {
        if (isEmpty()) {
            return null;
        }
        StringJoiner joiner = new StringJoiner(" && ", "${", "}");
        for (Condition condition : conditions) {
            joiner.add(condition.toExpression());
        }
        return joiner.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DefaultConditions that = (DefaultConditions) o;
        return Objects.equals(conditions, that.conditions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conditions);
    }

    @Override
    public String toString() {
        return toExpression();
    }
}

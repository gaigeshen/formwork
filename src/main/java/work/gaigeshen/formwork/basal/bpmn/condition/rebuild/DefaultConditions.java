package work.gaigeshen.formwork.basal.bpmn.condition.rebuild;

import java.util.*;

import static java.util.Comparator.comparing;

/**
 * 默认的条件表达式集合
 *
 * @author gaigeshen
 */
public class DefaultConditions implements Conditions {

    private final Set<Condition> conditions = new HashSet<>();

    public void addCondition(Condition condition) {
        if (Objects.isNull(condition)) {
            throw new IllegalArgumentException("condition cannot be null");
        }
        conditions.add(condition);
    }

    @Override
    public String toExpression() {
        StringJoiner joiner = new StringJoiner(" ", "${", "}");
        for (Condition condition : this) {
            String variable = condition.getVariable();
            Object value = condition.getValue();
            Relational relational = condition.getRelational();
            if (joiner.length() > 3) {
                joiner.add(relational.toExpression(variable, value, condition.getLogical()));
            } else {
                joiner.add(relational.toExpression(variable, value));
            }
        }
        return joiner.toString();
    }

    @Override
    public Iterator<Condition> iterator() {
        List<Condition> sortedConditions = new ArrayList<>(conditions);
        sortedConditions.sort(comparing(Condition::getLogical));
        return sortedConditions.iterator();
    }
}

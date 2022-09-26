package work.gaigeshen.formwork.commons.bpmn;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author gaigeshen
 */
public interface Conditions extends Iterable<Condition> {

    static Conditions create(String expression, int priority) {
        if (Objects.isNull(expression)) {
            throw new IllegalArgumentException("expression cannot be null");
        }
        return new DefaultConditions(Collections.singleton(Condition.create(expression)), priority);
    }

    static String createAndToExpression(String expression, int priority) {
        return create(expression, priority).toExpression();
    }

    static String createAndToExpression(String expression) {
        return create(expression, Integer.MAX_VALUE).toExpression();
    }

    @Override
    default Iterator<Condition> iterator() {
        return getConditions().iterator();
    }

    Set<Condition> getConditions();

    Conditions appendCondition(Condition condition);

    boolean isEmpty();

    int getPriority();

    String toExpression();
}

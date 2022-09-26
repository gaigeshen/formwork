package work.gaigeshen.formwork.commons.bpmn;

import java.util.Objects;

/**
 *
 * @author gaigeshen
 */
public interface Condition {

    String toExpression();

    static Condition create(String expression) {
        if (Objects.isNull(expression)) {
            throw new IllegalArgumentException("expression cannot be null");
        }
        return () -> expression;
    }
}

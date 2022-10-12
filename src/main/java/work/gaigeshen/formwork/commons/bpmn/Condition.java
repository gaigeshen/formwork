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
        return new SimpleCondition(expression);
    }

    class SimpleCondition implements Condition {

        private final String expression;

        public SimpleCondition(String expression) {
            this.expression = expression;
        }

        @Override
        public String toExpression() {
            return "(" + expression + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SimpleCondition that = (SimpleCondition) o;
            return Objects.equals(expression, that.expression);
        }

        @Override
        public int hashCode() {
            return Objects.hash(expression);
        }

        @Override
        public String toString() {
            return toExpression();
        }
    }
}

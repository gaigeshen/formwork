package work.gaigeshen.formwork.commons.bpmn.condition;

import java.util.Objects;

/**
 * 条件表达式
 *
 * @author gaigeshen
 */
public interface Condition {

    /**
     * 返回条件表达式字符串
     *
     * @return 条件表达式字符串
     */
    String toExpression();

    /**
     * 直接调用此方法创建条件表达式对象
     *
     * @param expression 条件表达式字符串
     * @return 条件表达式对象
     */
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
            return expression;
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

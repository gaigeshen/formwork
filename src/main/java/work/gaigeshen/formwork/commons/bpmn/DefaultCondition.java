package work.gaigeshen.formwork.commons.bpmn;

import java.util.Objects;

/**
 *
 * @author gaigeshen
 */
public class DefaultCondition implements Condition {

    private final String variable;

    private final Object value;

    private final String operator;

    private DefaultCondition(String variable, Object value, String operator) {
        if (Objects.isNull(variable) || Objects.isNull(operator)) {
            throw new IllegalArgumentException("variable and operator cannot be null");
        }
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("value cannot be null");
        }
        this.variable = variable;
        this.value = value;
        this.operator = operator;
    }

    public static DefaultCondition create(String variable, Object value, String operator) {
        return new DefaultCondition(variable, value, operator);
    }

    public static DefaultCondition equalTo(String variable, Object value) {
        return new DefaultCondition(variable, value, " = ");
    }

    public static DefaultCondition greaterThan(String variable, Object value) {
        return new DefaultCondition(variable, value, " > ");
    }

    public static DefaultCondition greaterThanOrEqualTo(String variable, Object value) {
        return new DefaultCondition(variable, value, " >= ");
    }

    public static DefaultCondition lessThan(String variable, Object value) {
        return new DefaultCondition(variable, value, " < ");
    }

    public static DefaultCondition lessThanOrEqualTo(String variable, Object value) {
        return new DefaultCondition(variable, value, " <= ");
    }

    @Override
    public String toExpression() {
        return "(" + variable + operator + value + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        DefaultCondition that = (DefaultCondition) o;
        return Objects.equals(variable, that.variable)
                && Objects.equals(operator, that.operator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, operator);
    }

    @Override
    public String toString() {
        return toExpression();
    }
}

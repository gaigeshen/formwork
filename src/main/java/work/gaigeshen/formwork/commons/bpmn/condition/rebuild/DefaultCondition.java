package work.gaigeshen.formwork.commons.bpmn.condition.rebuild;

import java.util.Objects;

/**
 * 默认的条件表达式，变量名称是每个条件表达式对象的标识符
 *
 * @author gaigeshen
 */
public class DefaultCondition implements Condition {

    private final Object value;

    private final String variable;

    private final Relational relational;

    private final Logical logical;

    private DefaultCondition(Builder builder) {
        this.value = builder.value;
        this.variable = builder.variable;
        this.relational = builder.relational;
        this.logical = builder.logical;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public String getVariable() {
        return variable;
    }

    @Override
    public Relational getRelational() {
        return relational;
    }

    @Override
    public Logical getLogical() {
        return logical;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultCondition)) {
            return false;
        }
        DefaultCondition that = (DefaultCondition) o;
        return Objects.equals(variable, that.variable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable);
    }

    @Override
    public String toString() {
        return "DefaultCondition{" +
                "value=" + value +
                ", variable='" + variable + '\'' +
                ", relational=" + relational +
                ", logical=" + logical +
                '}';
    }

    public static class Builder {

        private Object value;

        private String variable;

        private Relational relational;

        private Logical logical;

        public Builder value(Object value) {
            this.value = value;
            return this;
        }

        public Builder variable(String variable) {
            this.variable = variable;
            return this;
        }

        public Builder relational(Relational relational) {
            this.relational = relational;
            return this;
        }

        public Builder logical(Logical logical) {
            this.logical = logical;
            return this;
        }

        public DefaultCondition build() {
            if (Objects.isNull(variable)) {
                throw new IllegalArgumentException("variable");
            }
            if (Objects.isNull(relational)) {
                throw new IllegalArgumentException("relational");
            }
            if (Objects.isNull(logical)) {
                throw new IllegalArgumentException("logical");
            }
            return new DefaultCondition(this);
        }
    }
}

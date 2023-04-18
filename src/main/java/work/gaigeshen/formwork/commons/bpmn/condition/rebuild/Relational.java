package work.gaigeshen.formwork.commons.bpmn.condition.rebuild;

import java.util.*;

/**
 * 关系运算符
 *
 * @author gaigeshen
 */
public enum Relational {

    CONTAINS("ct") {
        @Override
        public String toExpression(String variable, Object value) {
            Set<Object> values = new HashSet<>();
            if (value instanceof Object[]) {
                Collections.addAll(values, (Object[]) value);
            }
            else if (value instanceof Iterable) {
                ((Iterable<?>) value).forEach(values::add);
            }
            StringJoiner builder = new StringJoiner(" ", "(", ")");
            for (Object valueItem : values) {
                if (builder.length() > 2) {
                    builder.add(EQUAL_TO.toExpression(variable, valueItem, Logical.OR));
                } else {
                    builder.add(EQUAL_TO.toExpression(variable, valueItem));
                }
            }
            return builder.toString();
        }
    },

    BETWEEN("bt") {
        @Override
        public String toExpression(String variable, Object value) {
            Set<Object> values = new TreeSet<>();
            if (value instanceof Object[]) {
                Collections.addAll(values, (Object[]) value);
            }
            else if (value instanceof Iterable) {
                ((Iterable<?>) value).forEach(values::add);
            }
            Iterator<Object> iterator = values.iterator();
            StringJoiner builder = new StringJoiner(" ", "(", ")");
            if (iterator.hasNext()) {
                builder.add(GREATER_THAN_OR_EQUAL_TO.toExpression(variable, iterator.next()));
            }
            if (iterator.hasNext()) {
                builder.add(LESS_THAN_OR_EQUAL_TO.toExpression(variable, iterator.next(), Logical.AND));
            }
            return builder.toString();
        }
    },

    NOT_EQUAL_TO("ne") {
        @Override
        public String toExpression(String variable, Object value) {
            if (value instanceof CharSequence) {
                return variable + " != '" + value + "'";
            }
            return variable + " != " + value;
        }
    },

    EQUAL_TO("eq") {
        @Override
        public String toExpression(String variable, Object value) {
            if (value instanceof CharSequence) {
                return variable + " == '" + value + "'";
            }
            return variable + " == " + value;
        }
    },

    GREATER_THAN("gt") {
        @Override
        public String toExpression(String variable, Object value) {
            return variable + " > " + value;
        }
    },

    GREATER_THAN_OR_EQUAL_TO("ge") {
        @Override
        public String toExpression(String variable, Object value) {
            return variable + " >= " + value;
        }
    },

    LESS_THAN("lt") {
        @Override
        public String toExpression(String variable, Object value) {
            return variable + " < " + value;
        }
    },

    LESS_THAN_OR_EQUAL_TO("le") {
        @Override
        public String toExpression(String variable, Object value) {
            return variable + " <= " + value;
        }
    };

    private final String code;

    Relational(String code) {
        this.code = code;
    }

    /**
     * 获取标识代号对应的关系运算符枚举
     *
     * @param code 标识代号
     * @return 关系运算符枚举
     */
    public static Optional<Relational> fromCode(String code) {
        for (Relational value : values()) {
            if (value.getCode().equalsIgnoreCase(code)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    /**
     * 传入变量名称和对应的值然后返回计算表达式，该表达式中不含逻辑运算符
     *
     * @param variable 变量名称
     * @param value 变量值
     * @return 返回计算表达式
     */
    public abstract String toExpression(String variable, Object value);

    /**
     * 传入变量名称和对应的值然后返回计算表达式
     *
     * @param variable 变量名称
     * @param value 变量值
     * @param logical 逻辑运算符
     * @return 返回计算表达式
     */
    public String toExpression(String variable, Object value, Logical logical) {
        return logical.getCode() + " " + toExpression(variable, value);
    }

    /**
     * 返回此关系运算符的标识代号
     *
     * @return 此关系运算符的标识代号
     */
    public String getCode() {
        return code;
    }
}

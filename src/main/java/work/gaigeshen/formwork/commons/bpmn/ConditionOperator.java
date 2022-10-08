package work.gaigeshen.formwork.commons.bpmn;

/**
 *
 * @author gaigeshen
 */
public enum ConditionOperator {

    DEFAULT(0) {
        @Override
        public Condition toCondition(String variable, Object value) {
            return DefaultCondition.equalTo(variable, value);
        }
    },

    LESS_THAN(1) {
        @Override
        public Condition toCondition(String variable, Object value) {
            return DefaultCondition.lessThan(variable, value);
        }
    },

    LESS_THAN_OR_EQUAL(2) {
        @Override
        public Condition toCondition(String variable, Object value) {
            return DefaultCondition.lessThanOrEqualTo(variable, value);
        }
    },

    EQUAL(3) {
        @Override
        public Condition toCondition(String variable, Object value) {
            return DefaultCondition.equalTo(variable, value);
        }
    },

    GREATER_THAN_OR_EQUAL(4) {
        @Override
        public Condition toCondition(String variable, Object value) {
            return DefaultCondition.greaterThanOrEqualTo(variable, value);
        }
    },

    GREATER_THAN(5) {
        @Override
        public Condition toCondition(String variable, Object value) {
            return DefaultCondition.greaterThan(variable, value);
        }
    };

    private final int code;

    ConditionOperator(int code) {
        this.code = code;
    }

    public abstract Condition toCondition(String variable, Object value);

    public static ConditionOperator fromCode(int code) {
        for (ConditionOperator parser : values()) {
            if (parser.code == code) {
                return parser;
            }
        }
        return DEFAULT;
    }
}

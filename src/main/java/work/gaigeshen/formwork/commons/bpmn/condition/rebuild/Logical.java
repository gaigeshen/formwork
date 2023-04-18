package work.gaigeshen.formwork.commons.bpmn.condition.rebuild;

import java.util.Optional;

/**
 * 逻辑运算符，用于多个条件表达式之间如何组合
 *
 * @author gaigeshen
 */
public enum Logical {

    AND("and"),

    OR("or");

    private final String code;

    Logical(String code) {
        this.code = code;
    }

    public static Optional<Logical> fromCode(String code) {
        for (Logical value : values()) {
            if (value.getCode().equalsIgnoreCase(code)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    public String getCode() {
        return code;
    }
}

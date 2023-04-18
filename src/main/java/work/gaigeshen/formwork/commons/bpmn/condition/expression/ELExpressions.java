package work.gaigeshen.formwork.commons.bpmn.condition.expression;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.el.ExpressionFactoryImpl;

import javax.el.ExpressionFactory;
import javax.el.StandardELContext;
import javax.el.ValueExpression;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author gaigeshen
 */
public abstract class ELExpressions {

    private ELExpressions() { }

    /**
     * 批量计算条件表达式的结果布尔值
     *
     * @param expressions 所有的条件表达式不能为空对象或者空字符串，此集合对象不能为空对象
     * @param variables 所需的变量集合，表达式所涉及的所有变量都需要传入，不可为空对象
     * @return 结果布尔值
     */
    public static Map<String, Boolean> evaluateBatch(Set<String> expressions, Map<String, Object> variables) {
        if (Objects.isNull(expressions)) {
            throw new IllegalArgumentException("expression cannot be null");
        }
        if (Objects.isNull(variables)) {
            throw new IllegalArgumentException("variables cannot be null");
        }
        Map<String, Boolean> evaluateResult = new HashMap<>();
        if (expressions.isEmpty()) {
            return evaluateResult;
        }
        ExpressionFactory factory = new ExpressionFactoryImpl();
        StandardELContext context = new StandardELContext(factory);
        variables.forEach((k, v) -> {
            ValueExpression valueExpression = factory.createValueExpression(v, Object.class);
            context.getVariableMapper().setVariable(k, valueExpression);
        });
        for (String expression : expressions) {
            Object value = factory.createValueExpression(context, expression, Boolean.class).getValue(context);
            evaluateResult.put(expression, BooleanUtils.toBoolean((Boolean) value));
        }
        return evaluateResult;
    }

    /**
     * 计算条件表达式的结果布尔值
     *
     * @param expression 条件表达式不能为空对象或者空字符串
     * @param variables 所需的变量集合，表达式所涉及的所有变量都需要传入，不可为空对象
     * @return 结果布尔值
     */
    public static boolean evaluate(String expression, Map<String, Object> variables) {
        if (StringUtils.isBlank(expression)) {
            throw new IllegalArgumentException("expression cannot be blank");
        }
        if (Objects.isNull(variables)) {
            throw new IllegalArgumentException("variables cannot be null");
        }
        ExpressionFactory factory = new ExpressionFactoryImpl();
        StandardELContext context = new StandardELContext(factory);
        variables.forEach((k, v) -> {
            ValueExpression valueExpression = factory.createValueExpression(v, Object.class);
            context.getVariableMapper().setVariable(k, valueExpression);
        });
        Object value = factory.createValueExpression(context, expression, Boolean.class).getValue(context);
        return BooleanUtils.toBoolean((Boolean) value);
    }
}

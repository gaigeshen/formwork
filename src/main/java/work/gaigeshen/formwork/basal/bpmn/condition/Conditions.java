package work.gaigeshen.formwork.basal.bpmn.condition;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * 条件表达式集合
 *
 * @author gaigeshen
 */
public interface Conditions extends Iterable<Condition> {

    /**
     * 直接调用此方法创建条件表达式集合，此集合内只包含单个条件表达式
     *
     * @param expression 条件表达式字符串
     * @param priority 创建的条件表达式集合的优先级
     * @return 条件表达式集合
     */
    static Conditions create(String expression, int priority) {
        if (Objects.isNull(expression)) {
            throw new IllegalArgumentException("expression cannot be null");
        }
        return new DefaultConditions(Collections.singleton(Condition.create(expression)), priority);
    }

    /**
     * 直接调用此方法创建条件表达式集合，此集合内只包含单个条件表达式，并返回该集合对应的条件表达式字符串
     *
     * @param expression 条件表达式字符串
     * @param priority 创建的条件表达式集合的优先级
     * @return 条件表达式字符串
     */
    static String createAndToExpression(String expression, int priority) {
        return create(expression, priority).toExpression();
    }

    /**
     * 直接调用此方法创建条件表达式集合，此集合内只包含单个条件表达式，并返回该集合对应的条件表达式字符串
     *
     * @param expression 条件表达式字符串
     * @return 条件表达式字符串
     */
    static String createAndToExpression(String expression) {
        return create(expression, Integer.MAX_VALUE).toExpression();
    }

    @Override
    default Iterator<Condition> iterator() {
        return getConditions().iterator();
    }

    /**
     * 返回此集合内的所有的条件表达式
     *
     * @return 所有的条件表达式
     */
    Set<Condition> getConditions();

    /**
     * 往此集合内添加条件表达式
     *
     * @param condition 需要添加的条件表达式
     * @return 返回此集合
     */
    Conditions appendCondition(Condition condition);

    /**
     * 返回此集合是否不包含任何条件表达式
     *
     * @return 是否不包含任何条件表达式
     */
    boolean isEmpty();

    /**
     * 返回此集合的优先级
     *
     * @return 此集合的优先级
     */
    int getPriority();

    /**
     * 调用此方法将返回此集合对应的条件表达式字符串
     *
     * @return 此集合对应的条件表达式字符串
     */
    String toExpression();
}

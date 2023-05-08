package work.gaigeshen.formwork.basal.bpmn.condition.rebuild;

/**
 * 条件表达式集合
 *
 * @author gaigeshen
 */
public interface Conditions extends Iterable<Condition> {

    /**
     * 调用此方法将得到条件表达式集合对应的字符串
     *
     * @return 条件表达式集合对应的字符串
     */
    String toExpression();

}

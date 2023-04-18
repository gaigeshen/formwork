package work.gaigeshen.formwork.commons.bpmn.condition.rebuild;

/**
 * 条件表达式
 *
 * @author gaigeshen
 */
public interface Condition {

    /**
     * 返回条件值，可能是数组或者集合类型
     *
     * @return 条件值
     */
    Object getValue();

    /**
     * 返回变量名称
     *
     * @return 变量名称
     */
    String getVariable();

    /**
     * 返回关系运算符
     *
     * @return 关系运算符
     */
    Relational getRelational();

    /**
     * 返回逻辑运算符
     *
     * @return 逻辑运算符
     */
    Logical getLogical();
}

package work.gaigeshen.formwork.commons.bpmn;

import work.gaigeshen.formwork.commons.bpmn.candidate.TypedCandidate;

import java.util.Objects;
import java.util.Set;

/**
 * 表示业务流程处理节点
 *
 * @author gaigeshen
 */
public interface ProcessNode extends Comparable<ProcessNode> {

    /**
     * 返回处理审批人，如果为空的情况表示此处理节点无需用户处理
     *
     * @return 处理审批人可能为空
     */
    TypedCandidate getCandidate();

    /**
     * 返回是否包含处理审批人
     *
     * @return 是否包含处理审批人
     */
    default boolean hasCandidate() {
        return Objects.nonNull(getCandidate());
    }

    /**
     * 返回条件表达式集合
     *
     * @return 条件表达式集合不能为空对象
     */
    Conditions getConditions();

    /**
     * 返回此节点的后续分支节点
     *
     * @return 此节点的后续分支节点不能为空对象
     */
    Set<ProcessNode> getOutgoing();

    /**
     * 返回此节点是否存在后续分支节点
     *
     * @return 此节点是否存在后续分支节点
     */
    default boolean hasOutgoing() {
        return !getOutgoing().isEmpty();
    }
}

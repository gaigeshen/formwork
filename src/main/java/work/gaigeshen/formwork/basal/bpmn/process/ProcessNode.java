package work.gaigeshen.formwork.basal.bpmn.process;

import work.gaigeshen.formwork.basal.bpmn.candidate.TypedCandidate;
import work.gaigeshen.formwork.basal.bpmn.condition.Conditions;

import java.util.Objects;
import java.util.Set;

/**
 * 表示流程处理节点
 *
 * @author gaigeshen
 */
public interface ProcessNode extends Comparable<ProcessNode> {

    /**
     * 返回审批候选人，如果为空的情况表示此节点无需用户处理
     *
     * @return 审批候选人可能为空
     */
    TypedCandidate getCandidate();

    /**
     * 返回是否包含审批候选人
     *
     * @return 是否包含审批候选人
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

package work.gaigeshen.formwork.commons.bpmn.candidate;

import java.io.Serializable;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * 审批候选人
 *
 * @author gaigeshen
 */
public interface Candidate extends Serializable {

    /**
     * 合并审批候选人
     *
     * @param candidate 需要合并的审批候选人
     * @return 合并后的审批候选人
     */
    default Candidate mergeCandidate(Candidate candidate) {
        if (Objects.isNull(candidate)) {
            throw new IllegalArgumentException("candidate cannot be null");
        }
        return mergeCandidates(Collections.singleton(candidate));
    }

    /**
     * 批量合并审批候选人
     *
     * @param candidates 需要合并的审批候选人
     * @return 合并后的审批候选人
     */
    Candidate mergeCandidates(Set<Candidate> candidates);

    /**
     * 批量清除审批候选人
     *
     * @param candidates 需要清除的审批候选人
     * @return 清除后的审批候选人
     */
    Candidate clearCandidates(Set<Candidate> candidates);

    /**
     * 返回审批候选人组
     *
     * @return 审批候选人组
     */
    Set<String> getGroups();

    /**
     * 返回审批候选人
     *
     * @return 审批候选人
     */
    Set<String> getUsers();
}

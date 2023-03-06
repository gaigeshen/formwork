package work.gaigeshen.formwork.commons.bpmn.candidate;

import java.util.Collections;
import java.util.Set;

/**
 * 带有类型的审批候选人
 *
 * @author gaigeshen
 */
public interface TypedCandidate extends Candidate {

    /**
     * 直接替换为指定的审批候选人
     *
     * @param candidate 指定的审批候选人
     * @return 替换后的审批候选人
     */
    default TypedCandidate replaceCandidate(Candidate candidate) {
        return clearCandidates(Collections.singleton(getCandidate())).mergeCandidate(candidate);
    }

    @Override
    default TypedCandidate mergeCandidate(Candidate candidate) {
        return mergeCandidates(Collections.singleton(candidate));
    }

    @Override
    TypedCandidate mergeCandidates(Set<Candidate> candidates);

    @Override
    TypedCandidate clearCandidates(Set<Candidate> candidates);

    /**
     * 返回审批候选人类型
     *
     * @return 审批候选人类型
     */
    CandidateType getType();

    /**
     * 返回审批候选人
     *
     * @return 审批候选人
     */
    Candidate getCandidate();
}

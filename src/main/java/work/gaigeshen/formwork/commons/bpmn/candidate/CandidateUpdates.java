package work.gaigeshen.formwork.commons.bpmn.candidate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 审批候选人修改参数，用于动态替换审批候选人
 *
 * @author gaigeshen
 */
public class CandidateUpdates implements Serializable {

    private final Map<String, Candidate> groupUpdates = new HashMap<>();

    private final Map<String, Candidate> userUpdates = new HashMap<>();

    private CandidateUpdates() { }

    /**
     * 创建空的审批候选人修改参数
     *
     * @return 空的审批候选人修改参数
     */
    public static CandidateUpdates create() {
        return new CandidateUpdates();
    }

    /**
     * 添加审批候选人组修改参数
     *
     * @param group 需要修改的批候选人组
     * @param candidate 修改为此审批候选人
     */
    public void addGroupUpdate(String group, Candidate candidate) {
        groupUpdates.put(group, candidate);
    }

    /**
     * 添加审批候选人修改参数
     *
     * @param user 需要修改的批候选人
     * @param candidate 修改为此审批候选人
     */
    public void addUserUpdate(String user, Candidate candidate) {
        userUpdates.put(user, candidate);
    }

    /**
     * 获取审批候选人组修改参数
     *
     * @return 审批候选人组修改参数
     */
    public Map<String, Candidate> getGroupUpdates() {
        return groupUpdates;
    }

    /**
     * 获取审批候选人修改参数
     *
     * @return 审批候选人修改参数
     */
    public Map<String, Candidate> getUserUpdates() {
        return userUpdates;
    }
}

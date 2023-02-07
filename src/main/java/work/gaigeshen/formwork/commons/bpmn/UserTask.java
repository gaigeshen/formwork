package work.gaigeshen.formwork.commons.bpmn;

import work.gaigeshen.formwork.commons.bpmn.candidate.Candidate;

import java.util.Date;

/**
 * 用户任务
 *
 * @author gaigeshen
 */
public interface UserTask {

    String getId();

    String getProcessId();

    String getBusinessKey();

    Candidate getCandidate();

    Date getCreateTime();

    Date getDueDate();

    Date getClaimTime();
}

package work.gaigeshen.formwork.commons.bpmn;

import java.util.Date;

/**
 * 用户任务
 *
 * @author gaigeshen
 */
public interface UserTask {

    String getId();

    String getName();

    String getDescription();

    String getProcessId();

    String getBusinessKey();

    String getAssignee();

    Date getCreateTime();

    Date getDueDate();

    Date getClaimTime();
}

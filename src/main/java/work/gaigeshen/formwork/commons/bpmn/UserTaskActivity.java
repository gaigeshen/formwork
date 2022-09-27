package work.gaigeshen.formwork.commons.bpmn;

import java.util.Date;

/**
 * 用户任务历史
 *
 * @author gaigeshen
 */
public interface UserTaskActivity {

    String getTaskId();

    String getAssignee();

    Date getStartTime();

    Date getEndTime();
}

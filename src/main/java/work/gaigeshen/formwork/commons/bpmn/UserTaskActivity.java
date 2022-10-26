package work.gaigeshen.formwork.commons.bpmn;

import java.util.Date;
import java.util.Set;

/**
 * 用户任务历史
 *
 * @author gaigeshen
 */
public interface UserTaskActivity {

    String getTaskId();

    String getAssignee();

    Set<String> getGroups();

    Set<String> getUsers();

    Date getStartTime();

    Date getEndTime();

    Status getStatus();

    enum Status {

        PROCESSING(1), APPROVED(2), REJECTED(3);

        private final int code;

        Status(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}

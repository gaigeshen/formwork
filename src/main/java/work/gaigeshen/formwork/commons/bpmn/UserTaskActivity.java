package work.gaigeshen.formwork.commons.bpmn;

import java.util.Date;
import java.util.List;

/**
 * 用户任务活动
 *
 * @author gaigeshen
 */
public interface UserTaskActivity {

    /**
     * 返回任务编号
     *
     * @return 任务编号可能为空
     */
    String getTaskId();

    /**
     * 返回任务签收人
     *
     * @return 任务签收人可能为空
     */
    String getAssignee();

    /**
     * 返回任务审批候选组
     *
     * @return 任务审批候选组可能为空
     */
    List<String> getGroups();

    /**
     * 返回任务审批候选人
     *
     * @return 任务审批候选人可能为空
     */
    List<String> getUsers();

    /**
     * 返回任务开始时间
     *
     * @return 任务开始时间可能为空
     */
    Date getStartTime();

    /**
     * 返回任务结束时间
     *
     * @return 任务结束时间可能为空
     */
    Date getEndTime();

    /**
     * 返回任务的状态
     *
     * @return 任务的状态
     */
    Status getStatus();

    /**
     * 任务状态
     *
     * @author gaigeshen
     */
    enum Status {

        /**
         * 任务状态进行中
         */
        PROCESSING(1) {
            @Override
            public boolean isProcessing() {
                return true;
            }
        },
        /**
         * 任务状态已通过
         */
        APPROVED(2) {
            @Override
            public boolean isApproved() {
                return true;
            }
        },
        /**
         * 任务状态已拒绝
         */
        REJECTED(3) {
            @Override
            public boolean isRejected() {
                return true;
            }
        };

        private final int code;

        Status(int code) {
            this.code = code;
        }

        /**
         * 返回任务状态是否进行中
         *
         * @return 任务状态是否进行中
         */
        public boolean isProcessing() {
            return false;
        }

        /**
         * 返回任务状态是否已通过
         *
         * @return 任务状态是否已通过
         */
        public boolean isApproved() {
            return false;
        }

        /**
         * 返回任务状态是否已拒绝
         *
         * @return 任务状态是否已拒绝
         */
        public boolean isRejected() {
            return false;
        }

        /**
         * 返回任务状态的枚举数字代号
         *
         * @return 任务状态的枚举数字代号
         */
        public int getCode() {
            return code;
        }
    }
}

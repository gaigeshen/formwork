package work.gaigeshen.formwork.commons.bpmn;

import work.gaigeshen.formwork.commons.bpmn.candidate.Candidate;

import java.util.Date;

/**
 * 用户任务活动
 *
 * @author gaigeshen
 */
public interface UserTaskActivity {

    String getTaskId();

    Candidate getCandidate();

    Date getStartTime();

    Date getEndTime();

    Status getStatus();

    enum Status {

        PROCESSING(1) {
            @Override
            public boolean isProcessing() {
                return true;
            }
        },
        APPROVED(2) {
            @Override
            public boolean isApproved() {
                return true;
            }
        },
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

        public boolean isProcessing() {
            return false;
        }

        public boolean isApproved() {
            return false;
        }

        public boolean isRejected() {
            return false;
        }

        public int getCode() {
            return code;
        }
    }
}

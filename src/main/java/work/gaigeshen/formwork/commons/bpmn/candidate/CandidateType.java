package work.gaigeshen.formwork.commons.bpmn.candidate;

/**
 * 审批人类型
 *
 * @author gaigeshen
 */
public enum CandidateType {

    APPROVER(1) {
        @Override
        public boolean isApprover() {
            return true;
        }
    },
    AUTO_APPROVER(2) {
        @Override
        public boolean isAutoApprover() {
            return true;
        }
    },
    STARTER(3) {
        @Override
        public boolean isStarter() {
            return true;
        }
    },
    STARTER_APPOINTEE(4) {
        @Override
        public boolean isStarterAppointee() {
            return true;
        }
    };

    private final int typeCode;

    CandidateType(int typeCode) {
        this.typeCode = typeCode;
    }

    public static CandidateType fromTypeCode(int typeCode) {
        for (CandidateType candidateType : values()) {
            if (candidateType.getTypeCode() == typeCode) {
                return candidateType;
            }
        }
        throw new IllegalArgumentException();
    }

    public int getTypeCode() {
        return typeCode;
    }

    public boolean isApprover() {
        return false;
    }

    public boolean isAutoApprover() {
        return false;
    }

    public boolean isStarter() {
        return false;
    }

    public boolean isStarterAppointee() {
        return false;
    }
}

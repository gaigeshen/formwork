package work.gaigeshen.formwork.commons.bpmn.candidate;

/**
 * 审批候选人类型
 *
 * @author gaigeshen
 */
public enum CandidateType {

    /**
     * 普通审批候选人
     */
    APPROVER(1) {
        @Override
        public boolean isApprover() {
            return true;
        }
    },

    /**
     * 抄送人审批候选人
     */
    AUTO_APPROVER(2) {
        @Override
        public boolean isAutoApprover() {
            return true;
        }
    },

    /**
     * 发起人审批候选人
     */
    STARTER(3) {
        @Override
        public boolean isStarter() {
            return true;
        }
    },

    /**
     * 发起人自选审批候选人
     */
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

    /**
     * 将枚举的整数表示转换为枚举对象，如果传入的整数不合法则会抛出异常
     *
     * @param typeCode 枚举的整数表示
     * @return 枚举对象
     */
    public static CandidateType fromTypeCode(int typeCode) {
        for (CandidateType candidateType : values()) {
            if (candidateType.getTypeCode() == typeCode) {
                return candidateType;
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * 返回枚举的整数表示
     *
     * @return 枚举的整数表示
     */
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

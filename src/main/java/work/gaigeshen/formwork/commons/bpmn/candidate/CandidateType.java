package work.gaigeshen.formwork.commons.bpmn.candidate;

import java.util.Objects;

/**
 *
 * @author gaigeshen
 */
public enum CandidateType {

    APPROVER("approver") {
        @Override
        public boolean isApprover() {
            return true;
        }
    },
    AUTO_APPROVER("autoApprover") {
        @Override
        public boolean isAutoApprover() {
            return true;
        }
    },
    STARTER("starter") {
        @Override
        public boolean isStarter() {
            return true;
        }
    },
    STARTER_APPOINT("starterAppoint") {
        @Override
        public boolean isStarterAppoint() {
            return true;
        }
    },
    STARTER_LEADER_INCLUDE("starterLeaderInclude") {
        @Override
        public boolean isStarterLeaderInclude() {
            return true;
        }
    };

    private final String typeCode;

    CandidateType(String typeCode) {
        this.typeCode = typeCode;
    }

    public static CandidateType fromTypeCode(String typeCode) {
        for (CandidateType candidateType : values()) {
            if (Objects.equals(typeCode, candidateType.getTypeCode())) {
                return candidateType;
            }
        }
        throw new IllegalArgumentException();
    }

    public String getTypeCode() {
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

    public boolean isStarterAppoint() {
        return false;
    }

    public boolean isStarterLeaderInclude() {
        return false;
    }
}

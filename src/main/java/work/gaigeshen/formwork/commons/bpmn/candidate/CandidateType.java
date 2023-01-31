package work.gaigeshen.formwork.commons.bpmn.candidate;

public enum CandidateType {
    APPROVER {
        @Override
        public boolean isApprover() {
            return true;
        }
    },
    AUTO_APPROVER {
        @Override
        public boolean isAutoApprover() {
            return true;
        }
    },
    STARTER {
        @Override
        public boolean isStarter() {
            return true;
        }
    },
    STARTER_APPOINT {
        @Override
        public boolean isStarterAppoint() {
            return true;
        }
    };

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
}

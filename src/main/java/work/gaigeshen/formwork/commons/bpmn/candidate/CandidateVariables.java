package work.gaigeshen.formwork.commons.bpmn.candidate;

public class CandidateVariables {

    private final String starter;

    private final Candidates starterAppoint;

    private final Candidate starterLeader;

    private CandidateVariables(Builder builder) {
        this.starter = builder.starter;
        this.starterAppoint = builder.starterAppoint;
        this.starterLeader = builder.starterLeader;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getStarter() {
        return starter;
    }

    public Candidates getStarterAppoint() {
        return starterAppoint;
    }

    public Candidate getStarterLeader() {
        return starterLeader;
    }

    public static class Builder {

        private String starter;

        private Candidates starterAppoint;

        private Candidate starterLeader;

        public Builder starter(String starter) {
            this.starter = starter;
            return this;
        }

        public Builder starterAppoint(Candidates starterAppoint) {
            this.starterAppoint = starterAppoint;
            return this;
        }

        public Builder starterLeader(Candidate starterLeader) {
            this.starterLeader = starterLeader;
            return this;
        }

        public CandidateVariables build() {
            return new CandidateVariables(this);
        }
    }
}

package work.gaigeshen.formwork.commons.bpmn.candidate;

/**
 * 审批候选人变量
 *
 * @author gaigeshen
 */
public class CandidateVariables {

    private final String starter;

    private final Candidates starterAppointee;

    private final CandidateUpdates candidateUpdates;

    private CandidateVariables(Builder builder) {
        this.starter = builder.starter;
        this.starterAppointee = builder.starterAppointee;
        this.candidateUpdates = builder.candidateUpdates;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getStarter() {
        return starter;
    }

    public Candidates getStarterAppointee() {
        return starterAppointee;
    }

    public CandidateUpdates getCandidateUpdates() {
        return candidateUpdates;
    }

    public static class Builder {

        private String starter;

        private Candidates starterAppointee;

        private CandidateUpdates candidateUpdates;

        public Builder starter(String starter) {
            this.starter = starter;
            return this;
        }

        public Builder starterAppointee(Candidates starterAppointee) {
            this.starterAppointee = starterAppointee;
            return this;
        }

        public Builder candidateUpdates(CandidateUpdates candidateUpdates) {
            this.candidateUpdates = candidateUpdates;
            return this;
        }

        public CandidateVariables build() {
            return new CandidateVariables(this);
        }
    }
}

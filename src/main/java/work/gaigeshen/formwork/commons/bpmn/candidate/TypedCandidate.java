package work.gaigeshen.formwork.commons.bpmn.candidate;

/**
 *
 * @author gaigeshen
 */
public interface TypedCandidate extends Candidate {

    CandidateType getType();

    default String getTypeCode() {
        return getType().getTypeCode();
    }

    default boolean isApprover() {
        return getType().isApprover();
    }

    default boolean isAutoApprover() {
        return getType().isAutoApprover();
    }

    default boolean isStarter() {
        return getType().isStarter();
    }

    default boolean isStarterAppoint() {
        return getType().isStarterAppoint();
    }
}

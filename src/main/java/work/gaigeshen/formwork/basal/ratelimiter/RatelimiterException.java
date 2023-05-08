package work.gaigeshen.formwork.basal.ratelimiter;

public class RatelimiterException extends RuntimeException {

    public RatelimiterException(String message) {
        super(message);
    }
}

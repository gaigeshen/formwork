package work.gaigeshen.formwork.commons.ratelimiter;

public class RatelimiterException extends RuntimeException {

    public RatelimiterException(String message) {
        super(message);
    }
}

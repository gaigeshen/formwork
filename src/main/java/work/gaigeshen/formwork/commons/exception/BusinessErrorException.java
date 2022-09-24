package work.gaigeshen.formwork.commons.exception;

/**
 *
 * @author gaigeshen
 */
public class BusinessErrorException extends RuntimeException {

    private final Object data;

    public BusinessErrorException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public BusinessErrorException(String message) {
        this(message, null);
    }

    public Object getData() {
        return data;
    }
}

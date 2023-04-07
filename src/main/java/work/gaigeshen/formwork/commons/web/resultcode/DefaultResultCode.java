package work.gaigeshen.formwork.commons.web.resultcode;

/**
 *
 * @author gaigeshen
 */
public class DefaultResultCode implements ResultCode {

    private final int code;

    private final String message;

    public DefaultResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static DefaultResultCode create(int code, String message) {
        return new DefaultResultCode(code, message);
    }

    public static DefaultResultCode create(ResultCode resultCode, String message) {
        return create(resultCode.getCode(), message);
    }

    public static DefaultResultCode create(ResultCode resultCode) {
        return create(resultCode.getCode(), resultCode.getMessage());
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

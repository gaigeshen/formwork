package work.gaigeshen.formwork.commons.web.resultcode;

/**
 *
 * @author gaigeshen
 */
public enum BusinessErrorResultCode implements ResultCode {

    BUSINESS_ERROR(6000, "Business Error");

    private final int code;

    private final String message;

    BusinessErrorResultCode(int code, String message) {
        this.code = code;
        this.message = message;
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

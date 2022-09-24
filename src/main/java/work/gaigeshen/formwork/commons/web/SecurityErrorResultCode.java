package work.gaigeshen.formwork.commons.web;

/**
 * @author gaigeshen
 */
public enum SecurityErrorResultCode implements ResultCode {

    ACCESS_DENIED(7000, "Access Denied"),

    AUTHENTICATE_FAILED(8000, "Authenticate Failed"),

    AUTHENTICATION_TOKEN_INVALID(8001, "Authentication Token Invalid"),

    ACCOUNT_DISABLED(8002, "Account Disabled"),

    ACCOUNT_LOCKED(8003, "Account Locked"),

    ACCOUNT_EXPIRED(8004, "Account Expired");

    private final int code;

    private final String message;

    SecurityErrorResultCode(int code, String message) {
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

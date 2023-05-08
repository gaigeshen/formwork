package work.gaigeshen.formwork.basal.web.resultcode;

/**
 * @author gaigeshen
 */
public enum HttpStatusErrorResultCode implements ResultCode {

    BAD_REQUEST(9400, "Bad Request"),

    UNAUTHORIZED(9401, "Unauthorized"),

    PAYMENT_REQUIRED(9402, "Payment Required"),

    FORBIDDEN(9403, "Forbidden"),

    NOT_FOUND(9404, "Not Found"),

    METHOD_NOT_ALLOWED(9405, "Method Not Allowed"),

    NOT_ACCEPTABLE(9406, "Not Acceptable"),

    UNSUPPORTED_MEDIA_TYPE(9415, "Unsupported Media Type"),

    TOO_MANY_REQUESTS(9429, "Too Many Requests"),

    INTERNAL_SERVER_ERROR(9500, "Internal Server Error"),

    NOT_IMPLEMENTED(9501, "Not Implemented"),

    BAD_GATEWAY(9502, "Bad Gateway"),

    SERVICE_UNAVAILABLE(9503, "Service Unavailable");

    private final int code;

    private final String message;

    HttpStatusErrorResultCode(int code, String message) {
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

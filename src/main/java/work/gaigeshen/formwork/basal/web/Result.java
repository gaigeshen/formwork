package work.gaigeshen.formwork.basal.web;

/**
 * @author gaigeshen
 */
public class Result<D> {

    public static final int DEFAULT_CODE = 0;

    public static final String DEFAULT_MESSAGE = "ok";

    private int code = DEFAULT_CODE;

    private String message = DEFAULT_MESSAGE;

    private D data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}

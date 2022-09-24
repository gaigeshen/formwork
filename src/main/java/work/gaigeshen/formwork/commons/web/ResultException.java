package work.gaigeshen.formwork.commons.web;

import java.util.Objects;

/**
 * @author gaigeshen
 */
public class ResultException extends RuntimeException {

    private final Result<?> result;

    public ResultException(Result<?> result, String message) {
        super(message);
        this.result = result;
    }

    public Result<?> getResult() {
        return result;
    }

    public Object getResultData() {
        return Objects.isNull(result) ? null : result.getData();
    }
}

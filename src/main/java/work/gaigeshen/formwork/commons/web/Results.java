package work.gaigeshen.formwork.commons.web;

import work.gaigeshen.formwork.commons.web.resultcode.ResultCode;

/**
 * @author gaigeshen
 */
public abstract class Results {

    private Results() { }

    public static <D> Result<D> create(ResultCode resultCode) {
        return create(resultCode, resultCode.getMessage());
    }

    public static <D> Result<D> create(ResultCode resultCode, String message) {
        return create(resultCode, message, null);
    }

    public static <D> Result<D> create(ResultCode resultCode, String message, D data) {
        Result<D> result = new Result<>();
        result.setCode(resultCode.getCode());
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static <D> Result<D> create(D data) {
        Result<D> result = new Result<>();
        result.setData(data);
        return result;
    }

    public static <D> Result<D> create() {
        return new Result<>();
    }

    public static <D> D getData(Result<D> result) {
        return getData(result, new ResultValidator<D>() { }, new ResultExceptionProducer<D>() { });
    }

    public static <D> D getData(Result<D> result,
                                ResultValidator<D> validator) {
        return getData(result, validator, new ResultExceptionProducer<D>() { });
    }

    public static <D> D getData(Result<D> result,
                                ResultExceptionProducer<D> producer) {
        return getData(result, new ResultValidator<D>() { }, producer);
    }

    public static <D> D getData(Result<D> result,
                                ResultValidator<D> validator,
                                ResultExceptionProducer<D> producer) {
        if (!validator.validate(result)) {
            throw producer.produce(result);
        }
        return result.getData();
    }

    /**
     * @author gaigeshen
     */
    public interface ResultValidator<D> {

        default boolean validate(Result<D> result) {
            return Result.DEFAULT_CODE == result.getCode();
        }
    }

    /**
     * @author gaigeshen
     */
    public interface ResultExceptionProducer<D> {

        default ResultException produce(Result<D> result) {
            return new ResultException(result, result.getMessage());
        }
    }
}

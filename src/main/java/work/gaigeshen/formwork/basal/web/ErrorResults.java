package work.gaigeshen.formwork.basal.web;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import work.gaigeshen.formwork.basal.exception.BusinessErrorException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

import static work.gaigeshen.formwork.basal.web.resultcode.HttpStatusErrorResultCode.*;

/**
 * 用于全局异常处理，当发生任何异常的时候决定如何生成响应内容对象
 *
 * @author gaigeshen
 */
public abstract class ErrorResults {

    private ErrorResults() { }

    public static Result<?> createResult(Throwable ex, int httpStatus) {
        if (ex instanceof BusinessErrorException) {
            return BusinessErrorResults.createResult((BusinessErrorException) ex);
        }
        if (ex instanceof MethodArgumentNotValidException) {
            BindingResult bindingResult = ((MethodArgumentNotValidException) ex).getBindingResult();
            ValidationError validationError = bindingResultDetail(bindingResult);
            return Results.create(BAD_REQUEST, validationError.getMessages(), validationError);
        }
        if (ex instanceof BindException) {
            BindingResult bindingResult = ((BindException) ex).getBindingResult();
            ValidationError validationError = bindingResultDetail(bindingResult);
            return Results.create(BAD_REQUEST, validationError.getMessages(), validationError);
        }
        if (ex instanceof ConstraintViolationException) {
            Set<ConstraintViolation<?>> violations = ((ConstraintViolationException) ex).getConstraintViolations();
            ValidationError validationError = constraintViolationsDetail(violations);
            return Results.create(BAD_REQUEST, validationError.getMessages(), validationError);
        }
        switch (httpStatus) {
            case 400: return Results.create(BAD_REQUEST);
            case 401: return Results.create(UNAUTHORIZED);
            case 402: return Results.create(PAYMENT_REQUIRED);
            case 403: return Results.create(FORBIDDEN);
            case 404: return Results.create(NOT_FOUND);
            case 405: return Results.create(METHOD_NOT_ALLOWED);
            case 406: return Results.create(NOT_ACCEPTABLE);
            case 415: return Results.create(UNSUPPORTED_MEDIA_TYPE);
            case 429: return Results.create(TOO_MANY_REQUESTS);
            case 501: return Results.create(NOT_IMPLEMENTED);
            case 502: return Results.create(BAD_GATEWAY);
            case 503: return Results.create(SERVICE_UNAVAILABLE);
        }
        return Results.create(INTERNAL_SERVER_ERROR);
    }

    private static ValidationError bindingResultDetail(BindingResult bindingResult) {
        ValidationError validationError = new ValidationError();
        for (FieldError error : bindingResult.getFieldErrors()) {
            validationError.addViolation(error.getField(), error.getDefaultMessage());
        }
        return validationError;
    }

    private static ValidationError constraintViolationsDetail(Set<ConstraintViolation<?>> violations) {
        ValidationError validationError = new ValidationError();
        for (ConstraintViolation<?> violation : violations) {
            PathImpl propertyPath = (PathImpl) violation.getPropertyPath();
            validationError.addViolation(propertyPath.getLeafNode().getName(), violation.getMessage());
        }
        return validationError;
    }
}

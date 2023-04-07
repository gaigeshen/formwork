package work.gaigeshen.formwork.commons.web;

import work.gaigeshen.formwork.commons.exception.BusinessErrorException;
import work.gaigeshen.formwork.commons.web.resultcode.BusinessErrorResultCode;

/**
 *
 * @author gaigeshen
 */
public abstract class BusinessErrorResults {

    private BusinessErrorResults() { }

    public static Result<?> createResult(BusinessErrorException ex) {
        return Results.create(BusinessErrorResultCode.BUSINESS_ERROR, ex.getMessage(), ex.getData());
    }
}

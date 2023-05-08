package work.gaigeshen.formwork.basal.web;

import work.gaigeshen.formwork.basal.web.resultcode.BusinessErrorResultCode;
import work.gaigeshen.formwork.basal.exception.BusinessErrorException;

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

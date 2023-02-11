package work.gaigeshen.formwork.commons.web;

import work.gaigeshen.formwork.commons.exception.BusinessErrorException;

import static work.gaigeshen.formwork.commons.web.BusinessErrorResultCode.BUSINESS_ERROR;

/**
 *
 * @author gaigeshen
 */
public abstract class BusinessErrorResults {

    private BusinessErrorResults() { }

    public static Result<?> createResult(BusinessErrorException ex) {
        return Results.create(BUSINESS_ERROR, ex.getMessage(), ex.getData());
    }
}

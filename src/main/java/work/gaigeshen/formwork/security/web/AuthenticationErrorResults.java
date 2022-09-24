package work.gaigeshen.formwork.security.web;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import work.gaigeshen.formwork.commons.web.Result;
import work.gaigeshen.formwork.commons.web.Results;
import work.gaigeshen.formwork.commons.web.SecurityErrorResultCode;
import work.gaigeshen.formwork.security.AuthenticationTokenMissingException;
import work.gaigeshen.formwork.security.AuthorizationNotFoundException;

/**
 *
 * @author gaigeshen
 */
public abstract class AuthenticationErrorResults {

    private AuthenticationErrorResults() { }

    public static Result<?> createResult(AuthenticationException ex) {
        if (ex instanceof AuthenticationTokenMissingException) {
            return Results.create(SecurityErrorResultCode.AUTHENTICATION_TOKEN_INVALID);
        }
        if (ex instanceof AuthorizationNotFoundException) {
            return Results.create(SecurityErrorResultCode.AUTHENTICATION_TOKEN_INVALID);
        }
        if (ex instanceof DisabledException) {
            return Results.create(SecurityErrorResultCode.ACCOUNT_DISABLED);
        }
        if (ex instanceof LockedException) {
            return Results.create(SecurityErrorResultCode.ACCOUNT_LOCKED);
        }
        if (ex instanceof AccountExpiredException) {
            return Results.create(SecurityErrorResultCode.ACCOUNT_EXPIRED);
        }
        return Results.create(SecurityErrorResultCode.AUTHENTICATE_FAILED);
    }
}

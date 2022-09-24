package work.gaigeshen.formwork.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

/**
 *
 * @author gaigeshen
 */
public abstract class SecurityContextUtils {

    private SecurityContextUtils() { }

    public static String getAuthorizationUserId() {
        Authorization authorization = getAuthorization();
        if (Objects.isNull(authorization)) {
            return null;
        }
        return authorization.getUserId();
    }

    public static Authorization getAuthorization() {
        Authentication authentication = getAuthentication();
        if (Objects.isNull(authentication)) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Authorization)) {
            return null;
        }
        return (Authorization) principal;
    }

    public static Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AuthenticationToken)) {
            return null;
        }
        else if (!authentication.isAuthenticated()) {
            return null;
        }
        return authentication;
    }
}

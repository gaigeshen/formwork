package work.gaigeshen.formwork.basal.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 *
 * @author gaigeshen
 */
public abstract class SecurityUtils {

    private SecurityUtils() { }

    public static Optional<Long> getUserIdAsNumber() {
        return getUserId().map(Long::parseLong);
    }

    public static Optional<String> getUserId() {
        return getPrincipal().map(Authorization::getUserId);
    }

    public static Optional<Authorization> getPrincipal() {
        return getAuthentication().map(Authentication::getPrincipal).map(a -> (Authorization) a);
    }

    public static Optional<AuthenticationToken> getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AuthenticationToken)) {
            return Optional.empty();
        }
        else if (!authentication.isAuthenticated()) {
            return Optional.empty();
        }
        return Optional.of((AuthenticationToken) authentication);
    }
}

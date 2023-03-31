package work.gaigeshen.formwork.security;

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
        return getPrincipal().map(Authorization::getUserId).map(Long::parseLong);
    }

    public static Optional<String> getUserId() {
        return getPrincipal().map(Authorization::getUserId);
    }

    public static Optional<Authorization> getPrincipal() {
        return getAuthentication().map(Authentication::getPrincipal).map(a -> (Authorization) a);
    }

    public static Optional<Authentication> getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AuthenticationToken)) {
            return Optional.empty();
        }
        else if (!authentication.isAuthenticated()) {
            return Optional.empty();
        }
        return Optional.of(authentication);
    }
}

package work.gaigeshen.formwork.basal.security.userdetails;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

/**
 *
 * @author gaigeshen
 */
public abstract class UserDetailsChecker {

    private UserDetailsChecker() { }

    public static void check(UserDetails userDetails) {
        if (userDetails.isDisabled()) {
            throw new DisabledException("disabled: " + userDetails);
        }
        if (userDetails.isLocked()) {
            throw new LockedException("locked: " + userDetails);
        }
    }
}

package work.gaigeshen.formwork.basal.security;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author gaigeshen
 */
public abstract class AbstractAuthorization implements Authorization {

    private final String userId;

    private final String userName;

    private final Set<String> authorities;

    private String purpose;

    public AbstractAuthorization(String userId, String userName, Set<String> authorities) {
        this.userId = userId;
        this.userName = userName;
        this.authorities = authorities;
    }

    public AbstractAuthorization(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
        this.authorities = Collections.emptySet();
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public Set<String> getAuthorities() {
        return Collections.unmodifiableSet(authorities);
    }

    @Override
    public String getPurpose() {
        if (Objects.isNull(purpose)) {
            return Authorization.super.getPurpose();
        }
        return purpose;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractAuthorization)) {
            return false;
        }
        AbstractAuthorization that = (AbstractAuthorization) o;
        return Objects.equals(userId, that.userId)
                && Objects.equals(purpose, that.purpose);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return userId + "|" + userName;
    }
}

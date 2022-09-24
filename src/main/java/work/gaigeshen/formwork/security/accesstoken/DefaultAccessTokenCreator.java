package work.gaigeshen.formwork.security.accesstoken;

import work.gaigeshen.formwork.security.Authorization;

import java.util.UUID;

/**
 *
 * @author gaigeshen
 */
public class DefaultAccessTokenCreator extends AbstractAccessTokenCreator {

    private DefaultAccessTokenCreator(long expiresSeconds, long maxTokenCount) {
        super(expiresSeconds, maxTokenCount);
    }

    public static DefaultAccessTokenCreator create(long expiresSeconds, long maxTokenCount) {
        return new DefaultAccessTokenCreator(expiresSeconds, maxTokenCount);
    }

    public static DefaultAccessTokenCreator create() {
        return create(1800, 10000);
    }

    @Override
    protected String createTokenInternal(Authorization authorization) {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    protected boolean validateTokenInternal(String token, Authorization authorization) {
        return true;
    }
}

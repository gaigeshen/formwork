package work.gaigeshen.formwork.basal.security.accesstoken;

import work.gaigeshen.formwork.basal.identity.IdentityGenerator;
import work.gaigeshen.formwork.basal.security.Authorization;

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
        return IdentityGenerator.generateDefault();
    }

    @Override
    protected boolean validateTokenInternal(String token, Authorization authorization) {
        return true;
    }
}

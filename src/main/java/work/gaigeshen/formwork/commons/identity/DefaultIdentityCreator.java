package work.gaigeshen.formwork.commons.identity;

import java.util.UUID;

/**
 *
 * @author gaigeshen
 */
public class DefaultIdentityCreator implements IdentityCreator {

    public static DefaultIdentityCreator INSTANCE = new DefaultIdentityCreator();

    @Override
    public String create() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

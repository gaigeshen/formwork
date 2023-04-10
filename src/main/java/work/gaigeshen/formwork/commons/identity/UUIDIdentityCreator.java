package work.gaigeshen.formwork.commons.identity;

import java.util.UUID;

/**
 *
 * @author gaigeshen
 */
public class UUIDIdentityCreator implements IdentityCreator {

    public static final UUIDIdentityCreator INSTANCE = new UUIDIdentityCreator();

    @Override
    public String create() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

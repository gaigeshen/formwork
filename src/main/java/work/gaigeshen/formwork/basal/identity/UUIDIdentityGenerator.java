package work.gaigeshen.formwork.basal.identity;

import java.util.UUID;

/**
 *
 * @author gaigeshen
 */
public class UUIDIdentityGenerator implements IdentityGenerator {

    public static final UUIDIdentityGenerator INSTANCE = new UUIDIdentityGenerator();

    @Override
    public String generate() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

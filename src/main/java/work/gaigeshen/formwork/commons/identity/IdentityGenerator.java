package work.gaigeshen.formwork.commons.identity;

/**
 * 标识符生成器
 *
 * @author gaigeshen
 */
public interface IdentityGenerator {

    static String generateDefault() {
        return UUIDIdentityGenerator.INSTANCE.generate();
    }

    String generate();
}

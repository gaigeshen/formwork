package work.gaigeshen.formwork.commons.identity;

/**
 * 标识符生成器
 *
 * @author gaigeshen
 */
public interface IdentityCreator {

    static String createDefault() {
        return UUIDIdentityCreator.INSTANCE.create();
    }

    String create();
}

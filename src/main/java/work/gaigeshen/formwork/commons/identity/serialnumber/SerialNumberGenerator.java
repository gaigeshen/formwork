package work.gaigeshen.formwork.commons.identity.serialnumber;

/**
 * 序列号生成器
 *
 * @author gaigeshen
 */
public interface SerialNumberGenerator {

    /**
     * 生成序列号
     *
     * @param prefix 序列号前缀应该支持可以为空
     * @return 生成的序列号
     */
    String generate(String prefix);
}

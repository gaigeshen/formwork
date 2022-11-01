package work.gaigeshen.formwork.commons.identity;

/**
 * 序列号生成器
 *
 * @author gaigeshen
 */
public interface SerialNumberCreator {

    /**
     * 直接调用此方法生成默认的序列号
     *
     * @param prefix 序列号对应的前缀
     * @return 生成的序列号
     */
    static String createDefault(String prefix) {
        return DefaultSerialNumberCreator.INSTANCE.create(prefix);
    }

    /**
     * 生成序列号
     *
     * @param prefix 序列号对应的前缀
     * @return 生成的序列号
     */
    String create(String prefix);
}

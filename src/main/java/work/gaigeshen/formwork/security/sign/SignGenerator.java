package work.gaigeshen.formwork.security.sign;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;

/**
 * 签名生成工具
 *
 * @author gaigeshen
 */
public interface SignGenerator {

    /**
     * 创建默认的签名生成工具
     *
     * @param privateKey 签名生成工具需要使用私钥
     * @return 默认的签名生成工具
     */
    static SignGenerator createDefault(PrivateKey privateKey) {
        return new DefaultSignGenerator(privateKey);
    }

    /**
     * 生成签名
     *
     * @param signContent 需要生成签名的内容
     * @return 生成的签名
     * @throws GeneralSecurityException 签名发生任何异常
     */
    String generate(String signContent) throws GeneralSecurityException;
}

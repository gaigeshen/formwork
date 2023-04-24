package work.gaigeshen.formwork.security.sign;

import java.security.GeneralSecurityException;
import java.security.PublicKey;

/**
 * 签名校验工具
 *
 * @author gaigeshen
 */
public interface SignVerifier {

    /**
     * 创建默认的签名校验工具
     *
     * @param publicKey 签名校验工具需要使用公钥
     * @return 默认的签名校验工具
     */
    static SignVerifier createDefault(PublicKey publicKey) {
        return new DefaultSignVerifier(publicKey);
    }

    /**
     * 校验签名是否正确
     *
     * @param signContent 生成签名的内容
     * @param signResult 需要校验的签名
     * @return 校验签名是否正确
     * @throws GeneralSecurityException 校验签名发生任何异常
     */
    boolean verify(String signContent, String signResult) throws GeneralSecurityException;

}

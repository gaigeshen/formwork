package work.gaigeshen.formwork.security.sign;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;

/**
 * 默认的签名生成工具
 *
 * @author gaigeshen
 */
public class DefaultSignGenerator implements SignGenerator {

    private final PrivateKey privateKey;

    public DefaultSignGenerator(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    public String generate(String signContent) throws GeneralSecurityException {

        Signature signature = Signature.getInstance("SHA256withRSA");

        signature.initSign(privateKey);

        signature.update(signContent.getBytes(StandardCharsets.UTF_8));

        byte[] signResultBytes = signature.sign();

        return Base64.getEncoder().encodeToString(signResultBytes);
    }

}

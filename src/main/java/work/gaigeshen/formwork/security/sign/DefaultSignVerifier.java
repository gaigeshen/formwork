package work.gaigeshen.formwork.security.sign;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

/**
 * 默认的签名校验工具
 *
 * @author gaigeshen
 */
public class DefaultSignVerifier implements SignVerifier {

    private final PublicKey publicKey;

    public DefaultSignVerifier(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public boolean verify(String signContent, String signResult) throws GeneralSecurityException {

        byte[] signResultBytes = Base64.getDecoder().decode(signResult);

        Signature signature = Signature.getInstance("SHA256withRSA");

        signature.initVerify(publicKey);

        signature.update(signContent.getBytes(StandardCharsets.UTF_8));

        return signature.verify(signResultBytes);
    }
}

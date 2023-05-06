package work.gaigeshen.formwork.security.web.crypto;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import work.gaigeshen.formwork.util.json.JsonCodec;
import work.gaigeshen.formwork.security.crypto.CryptoProcessor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

/**
 * 此拦截器用于将发送的请求体加密
 *
 * @author gaigeshen
 */
public class CryptoHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private final CryptoProcessor cryptoProcessor;

    public CryptoHttpRequestInterceptor(CryptoProcessor cryptoProcessor) {
        this.cryptoProcessor = cryptoProcessor;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        if (body.length == 0) {
            return execution.execute(request, body);
        }
        String bodyString = new String(body, StandardCharsets.UTF_8);
        String encrypted;
        try {
            encrypted = cryptoProcessor.doEncrypt(bodyString);
        } catch (GeneralSecurityException e) {
            throw new IOException(e.getMessage(), e);
        }
        Map<String, Object> bodyToSend = new HashMap<>();
        bodyToSend.put("data", encrypted);
        String encoded = JsonCodec.instance().encode(bodyToSend);
        return execution.execute(request, encoded.getBytes(StandardCharsets.UTF_8));
    }
}

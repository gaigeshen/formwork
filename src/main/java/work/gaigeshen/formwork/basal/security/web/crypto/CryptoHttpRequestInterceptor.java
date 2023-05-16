package work.gaigeshen.formwork.basal.security.web.crypto;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import work.gaigeshen.formwork.basal.json.JsonCodec;
import work.gaigeshen.formwork.basal.security.crypto.CryptoProcessor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 此拦截器用于将发送的请求体加密
 *
 * @author gaigeshen
 */
public class CryptoHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private final CryptoProcessor cryptoProcessor;

    public CryptoHttpRequestInterceptor(CryptoProcessor cryptoProcessor) {
        if (Objects.isNull(cryptoProcessor)) {
            throw new IllegalArgumentException("cryptoProcessor cannot be null");
        }
        this.cryptoProcessor = cryptoProcessor;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        if (body.length == 0) {
            return execution.execute(request, body);
        }
        HttpHeaders headers = request.getHeaders();
        if (!Objects.equals(headers.getContentType(), MediaType.APPLICATION_JSON)) {
            return execution.execute(request, body);
        }
        if (headers.getOrEmpty("User-Agent").stream().noneMatch(ua -> ua.contains("Encrypted"))) {
            return execution.execute(request, body);
        }
        String bodyString = new String(body, StandardCharsets.UTF_8);
        String encryptedData;
        try {
            encryptedData = cryptoProcessor.doEncrypt(bodyString);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        Map<String, Object> bodyToSend = new HashMap<>();
        bodyToSend.put("data", encryptedData);
        return interceptInternal(request, execution, JsonCodec.instance().encode(bodyToSend));
    }

    /**
     * 发送加密后的数据然后对响应结果进行解密操作，如果响应的内容不存在或者类型不是预期的则不会执行解密操作
     *
     * @param request 原始请求对象
     * @param execution 用于执行请求操作
     * @param encryptedBody 加密后的数据
     * @return 返回原始的响应对象或者执行解密操作后的响应对象
     * @throws IOException 无法发送数据或者从原始响应对象中获取数据失败
     */
    private ClientHttpResponse interceptInternal(HttpRequest request, ClientHttpRequestExecution execution, String encryptedBody) throws IOException {
        ClientHttpResponse httpResponse = execution.execute(request, encryptedBody.getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = httpResponse.getHeaders();
        if (!Objects.equals(headers.getContentType(), MediaType.APPLICATION_JSON)) {
            return httpResponse;
        }
        String bodyString = StreamUtils.copyToString(httpResponse.getBody(), StandardCharsets.UTF_8);
        Map<String, Object> decodedData = JsonCodec.instance().decodeObject(bodyString);
        String encryptedData = (String) decodedData.get("data");
        if (Objects.nonNull(encryptedData)) {
            String decryptedData;
            try {
                decryptedData = cryptoProcessor.doDecrypt(encryptedData);
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            String decryptedBodyString = bodyString.replace(encryptedData, decryptedData);
            ByteArrayInputStream bis = new ByteArrayInputStream(decryptedBodyString.getBytes(StandardCharsets.UTF_8));
            return new DecryptedClientHttpResponse(httpResponse, bis);
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(bodyString.getBytes(StandardCharsets.UTF_8));
        return new DecryptedClientHttpResponse(httpResponse, bis);
    }

    /**
     * 解密后的响应数据
     *
     * @author gaigeshen
     */
    private static class DecryptedClientHttpResponse implements ClientHttpResponse {

        private final ClientHttpResponse oldHttpResponse;

        private final ByteArrayInputStream inputStream;

        private DecryptedClientHttpResponse(ClientHttpResponse oldHttpResponse, ByteArrayInputStream inputStream) {
            this.oldHttpResponse = oldHttpResponse;
            this.inputStream = inputStream;
        }

        @Override
        public HttpStatus getStatusCode() throws IOException {
            return oldHttpResponse.getStatusCode();
        }

        @Override
        public int getRawStatusCode() throws IOException {
            return oldHttpResponse.getRawStatusCode();
        }

        @Override
        public String getStatusText() throws IOException {
            return oldHttpResponse.getStatusText();
        }

        @Override
        public void close() {
            oldHttpResponse.close();
        }

        @Override
        public InputStream getBody() {
            return inputStream;
        }

        @Override
        public HttpHeaders getHeaders() {
            return oldHttpResponse.getHeaders();
        }
    }
}

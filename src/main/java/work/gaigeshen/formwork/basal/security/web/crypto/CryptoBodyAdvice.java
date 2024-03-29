package work.gaigeshen.formwork.basal.security.web.crypto;

import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import work.gaigeshen.formwork.basal.security.crypto.CryptoProcessor;
import work.gaigeshen.formwork.basal.json.JsonCodec;
import work.gaigeshen.formwork.basal.web.Result;
import work.gaigeshen.formwork.basal.web.Results;
import work.gaigeshen.formwork.basal.web.resultcode.DefaultResultCode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.Objects;

/**
 * 用于对指定的响应内容执行加密操作，以及对指定的请求参数执行解密操作
 *
 * @author gaigeshen
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class CryptoBodyAdvice implements RequestBodyAdvice, ResponseBodyAdvice<Result<?>> {

    private final CryptoProcessor cryptoProcessor;

    public CryptoBodyAdvice(CryptoProcessor cryptoProcessor) {
        this.cryptoProcessor = cryptoProcessor;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return methodParameter.hasMethodAnnotation(Encrypted.class);
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.hasMethodAnnotation(Encrypted.class);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        // 读取请求体的内容
        String bodyString = StreamUtils.copyToString(inputMessage.getBody(), StandardCharsets.UTF_8);
        // 请求体内容的格式需要固定
        // 获取加密的数据内容
        Map<String, Object> decodedData = JsonCodec.instance().decodeObject(bodyString);
        String encryptedData = (String) decodedData.get("data");
        if (Objects.isNull(encryptedData)) {
            return inputMessage;
        }
        // 如果存在加密的数据内容则尝试解密
        try {
            String decryptedData = cryptoProcessor.doDecrypt(encryptedData);
            return new InternalHttpInputMessage(inputMessage.getHeaders(), decryptedData);
        }
        catch (GeneralSecurityException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public Result<?> beforeBodyWrite(Result<?> body, MethodParameter returnType, MediaType contentType, Class<? extends HttpMessageConverter<?>> converter, ServerHttpRequest request, ServerHttpResponse response) {
        // 在输出响应之前判断是否有数据需要加密
        if (Objects.isNull(body) || Objects.isNull(body.getData())) {
            return body;
        }
        // 将需要加密的数据对象转换为字符串
        // 然后将字符串执行加密操作
        // 将加密后得到的字符串当作数据内容输出到响应
        String encodedJsonData = JsonCodec.instance().encode(body.getData());
        try {
            String encryptedData = cryptoProcessor.doEncrypt(encodedJsonData);
            return Results.create(DefaultResultCode.create(body.getCode(), body.getMessage()), encryptedData);
        }
        catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    /**
     * @author gaigeshen
     */
    private static class InternalHttpInputMessage implements HttpInputMessage {

        private final HttpHeaders httpHeaders;

        private final String decryptedData;

        private InternalHttpInputMessage(HttpHeaders httpHeaders, String decryptedData) {
            this.httpHeaders = httpHeaders;
            this.decryptedData = decryptedData;
        }

        @Override
        public InputStream getBody() {
            return new ByteArrayInputStream(decryptedData.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public HttpHeaders getHeaders() {
            return httpHeaders;
        }
    }
}

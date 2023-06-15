package work.gaigeshen.formwork.basal.openai;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.net.Proxy;
import java.time.Duration;
import java.util.Objects;

/**
 *
 * @author gaigeshen
 */
public class DefaultOpenAiClientCreator implements OpenAiClientCreator {

    private static final Logger log = LoggerFactory.getLogger(DefaultOpenAiClientCreator.class);

    @Override
    public OpenAiClient create(OpenAiProperties properties, Proxy proxy) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        if (Objects.nonNull(proxy)) {
            httpClientBuilder.proxy(proxy);
        }
        OkHttpClient httpClient = httpClientBuilder
                .connectTimeout(Duration.ofSeconds(5)).readTimeout(Duration.ofSeconds(30))
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .header("Content-Type", "application/json")
                            .header("Authorization", "Bearer " + properties.getApiKey()).build();
                    return chain.proceed(request);
                }).addInterceptor(new HttpLoggingInterceptor(log::info)).build();
        ObjectMapper objectMapper = new ObjectMapper()
                .findAndRegisterModules()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        Retrofit retrofit = new Retrofit.Builder().client(httpClient)
                .baseUrl(properties.getServerHost())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();
        return retrofit.create(OpenAiClient.class);
    }
}

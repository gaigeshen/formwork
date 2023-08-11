package work.gaigeshen.formwork.config;

import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import work.gaigeshen.formwork.basal.retrofit.server.*;
import work.gaigeshen.formwork.basal.retrofit.spring.RetrofitProperties;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author gaigeshen
 */
@EnableConfigurationProperties(RetrofitProperties.class)
@Configuration
public class RetrofitConfiguration {

    private final RetrofitProperties retrofitProperties;

    public RetrofitConfiguration(RetrofitProperties retrofitProperties) {
        this.retrofitProperties = retrofitProperties;
    }

    @Bean
    public Retrofit retrofit(ServerHostSelector serverHostSelector) {
        List<ServerHost> serverHostList = new ArrayList<>();
        for (RetrofitProperties.ServerHost serverHost : retrofitProperties.getServerHosts()) {
            serverHostList.add(ServerHost.create(serverHost.getServerId(), serverHost.getServerHost()));
        }
        ServerHosts serverHosts = DefaultServerHosts.create(serverHostList);
        ServerHost defaultServerHost = serverHosts.getServerHost();
        if (Objects.isNull(defaultServerHost)) {
            throw new IllegalStateException("missing default server host");
        }
        OkHttpClient httpClient = createHttpClient(serverHosts, serverHostSelector);

        Converter.Factory jacksonConverterFactory = JacksonConverterFactory.create(Jackson2ObjectMapperBuilder.json().failOnUnknownProperties(false).build());

        return new Retrofit.Builder()
                .client(httpClient).baseUrl(defaultServerHost.getServerHost())
                .addConverterFactory(jacksonConverterFactory)
                .build();
    }

    private OkHttpClient createHttpClient(ServerHosts serverHosts, ServerHostSelector serverHostSelector) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        if (retrofitProperties.getConnectTimeout() > 0) {
            httpClientBuilder.connectTimeout(Duration.ofMillis(retrofitProperties.getConnectTimeout()));
        }
        if (retrofitProperties.getReadTimeout() > 0) {
            httpClientBuilder.readTimeout(Duration.ofMillis(retrofitProperties.getReadTimeout()));
        }

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(Level.BODY);
        httpClientBuilder.addNetworkInterceptor(loggingInterceptor);

        Interceptor serverHostInterceptor = new ServerHostInterceptor(serverHosts, serverHostSelector);
        httpClientBuilder.addInterceptor(serverHostInterceptor);

        return httpClientBuilder.build();
    }

    @Bean
    public ServerHostSelector serverHostSelector() {
        return new DefaultServerHostSelector();
    }

    /**
     * @author gaigeshen
     */
    private static class ServerHostInterceptor implements Interceptor {

        private final ServerHosts serverHosts;

        private final ServerHostSelector serverHostSelector;

        private ServerHostInterceptor(ServerHosts serverHosts, ServerHostSelector serverHostSelector) {
            this.serverHosts = serverHosts;
            this.serverHostSelector = serverHostSelector;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {

            Request userRequest = chain.request();

            ServerHost serverHostSelected = serverHostSelector.select(serverHosts, userRequest);

            if (Objects.nonNull(serverHostSelected)) {
                HttpUrl serverHostUrl = HttpUrl.get(serverHostSelected.getServerHost());

                HttpUrl newHttpUrl = userRequest.url().newBuilder()
                        .scheme(serverHostUrl.scheme())
                        .host(serverHostUrl.host()).port(serverHostUrl.port())
                        .build();

                Request newRequest = userRequest.newBuilder().url(newHttpUrl).build();

                return chain.proceed(newRequest);
            }
            return chain.proceed(userRequest);
        }
    }
}

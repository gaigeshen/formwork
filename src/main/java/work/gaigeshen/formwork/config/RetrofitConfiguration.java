package work.gaigeshen.formwork.config;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import work.gaigeshen.formwork.basal.retrofit.server.DefaultServerHosts;
import work.gaigeshen.formwork.basal.retrofit.server.ServerHost;
import work.gaigeshen.formwork.basal.retrofit.server.ServerHosts;
import work.gaigeshen.formwork.basal.retrofit.spring.RetrofitProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gaigeshen
 */
@EnableConfigurationProperties(RetrofitProperties.class)
@Configuration
public class RetrofitConfiguration {

    @Bean
    public ServerHosts serverHosts(RetrofitProperties properties) {
        List<ServerHost> serverHostList = new ArrayList<>();
        for (RetrofitProperties.ServerHost serverHost : properties.getServerHosts()) {
            serverHostList.add(ServerHost.create(serverHost.getServerId(), serverHost.getServerHost()));
        }
        return DefaultServerHosts.create(serverHostList);
    }

    @Bean
    public OkHttpClient httpClient(RetrofitProperties properties) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (properties.getConnectTimeout() > 0) {
            builder.connectTimeout(Duration.ofMillis(properties.getConnectTimeout()));
        }
        if (properties.getReadTimeout() > 0) {
            builder.readTimeout(Duration.ofMillis(properties.getReadTimeout()));
        }
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addNetworkInterceptor(loggingInterceptor);
        return builder.build();
    }
}

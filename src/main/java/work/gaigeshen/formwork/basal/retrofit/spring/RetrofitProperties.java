package work.gaigeshen.formwork.basal.retrofit.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gaigeshen
 */
@ConfigurationProperties("spring.retrofit")
public class RetrofitProperties {

    private List<ServerHost> serverHosts = new ArrayList<>();

    private boolean enabled = true;

    private long connectTimeout = 1000;

    private long readTimeout = 3000;

    public List<ServerHost> getServerHosts() {
        return serverHosts;
    }

    public void setServerHosts(List<ServerHost> serverHosts) {
        this.serverHosts = serverHosts;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public long getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }

    /**
     *
     * @author gaigeshen
     */
    public static class ServerHost {

        private String serverId;

        private String serverHost;

        public String getServerId() {
            return serverId;
        }

        public void setServerId(String serverId) {
            this.serverId = serverId;
        }

        public String getServerHost() {
            return serverHost;
        }

        public void setServerHost(String serverHost) {
            this.serverHost = serverHost;
        }
    }
}

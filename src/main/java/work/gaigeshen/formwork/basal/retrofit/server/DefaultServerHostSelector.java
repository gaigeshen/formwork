package work.gaigeshen.formwork.basal.retrofit.server;

import okhttp3.Request;

import java.util.Objects;

public class DefaultServerHostSelector implements ServerHostSelector {

    @Override
    public ServerHost select(ServerHosts serverHosts, Request request) {
        String serverId = request.header("X-Server-ID");
        if (Objects.isNull(serverId)) {
            return serverHosts.getServerHost();
        }
        return serverHosts.getServerHost(serverId);
    }
}

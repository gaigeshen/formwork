package work.gaigeshen.formwork.basal.retrofit.server;

import okhttp3.Request;

import java.util.Objects;

/**
 *
 * @author gaigeshen
 */
public class DefaultServerHostSelector implements ServerHostSelector {

    public static final String SERVER_ID_QUERY_PARAMETER_NAME = "serverId";

    public static final String SERVER_ID_HEADER_NAME = "X-Server-ID";

    @Override
    public ServerHost select(ServerHosts serverHosts, Request request) {
        String serverId = request.url().queryParameter(SERVER_ID_QUERY_PARAMETER_NAME);
        if (Objects.isNull(serverId)) {
            serverId = request.header(SERVER_ID_HEADER_NAME);
            if (Objects.isNull(serverId)) {
                return serverHosts.getServerHost();
            }
        }
        return serverHosts.getServerHost(serverId);
    }
}

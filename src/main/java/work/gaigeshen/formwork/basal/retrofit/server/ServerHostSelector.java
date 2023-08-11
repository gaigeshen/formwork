package work.gaigeshen.formwork.basal.retrofit.server;

import okhttp3.Request;

/**
 *
 * @author gaigeshen
 */
public interface ServerHostSelector {

    default ServerHost select(ServerHosts serverHosts, Request request) {
        return serverHosts.getServerHost();
    }
}

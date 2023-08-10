package work.gaigeshen.formwork.basal.retrofit.server;

import okhttp3.Request;

/**
 *
 * @author gaigeshen
 */
public interface ServerHostSelector {

    ServerHost select(ServerHosts serverHosts, Request request);

}

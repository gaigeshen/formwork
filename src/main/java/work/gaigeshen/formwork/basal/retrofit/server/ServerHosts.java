package work.gaigeshen.formwork.basal.retrofit.server;

import java.util.Collection;
import java.util.Iterator;

/**
 * 维护多个服务器地址
 *
 * @author gaigeshen
 */
public interface ServerHosts extends Iterable<ServerHost> {

    @Override
    default Iterator<ServerHost> iterator() {
        return getServerHosts().iterator();
    }

    ServerHost getServerHost(String serverId);

    Collection<ServerHost> getServerHosts();

    default ServerHost getServerHost() {
        Collection<ServerHost> serverHosts = getServerHosts();
        if (!serverHosts.isEmpty()) {
            return serverHosts.iterator().next();
        }
        return null;
    }
}

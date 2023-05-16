package work.gaigeshen.formwork.basal.security.web.logout;

import work.gaigeshen.formwork.basal.security.accesstoken.AccessTokenCreator;
import work.gaigeshen.formwork.basal.json.JsonCodec;
import work.gaigeshen.formwork.basal.web.Results;
import work.gaigeshen.formwork.basal.security.AuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 默认的访问令牌登出处理器，将会输出默认的响应内容
 *
 * @author gaigeshen
 */
public class DefaultAccessTokenLogoutHandler extends AbstractAccessTokenLogoutHandler {

    public DefaultAccessTokenLogoutHandler(AccessTokenCreator accessTokenCreator) {
        super(accessTokenCreator);
    }

    @Override
    protected void onSuccess(HttpServletRequest request, HttpServletResponse response, AuthenticationToken token) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        response.getWriter().write(JsonCodec.instance().encode(Results.create()));
    }
}

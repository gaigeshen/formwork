package work.gaigeshen.formwork.security.web.authentication;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import work.gaigeshen.formwork.security.AuthenticationToken;
import work.gaigeshen.formwork.security.Authorization;
import work.gaigeshen.formwork.security.accesstoken.AccessTokenCreator;
import work.gaigeshen.formwork.security.web.AuthenticationErrorResults;
import work.gaigeshen.formwork.basal.json.JsonCodec;
import work.gaigeshen.formwork.basal.web.Result;
import work.gaigeshen.formwork.basal.web.Results;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author gaigeshen
 */
public class DefaultAuthenticationFilter extends AbstractAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(DefaultAuthenticationFilter.class);

    public static final String REQUEST_MATCHER_PATTERN = "/login";

    private final AccessTokenCreator accessTokenCreator;

    public DefaultAuthenticationFilter(AuthenticationManager authenticationManager, AccessTokenCreator accessTokenCreator) {
        super(new AntPathRequestMatcher(REQUEST_MATCHER_PATTERN, "POST"), authenticationManager);
        this.accessTokenCreator = accessTokenCreator;
    }

    @Override
    protected AuthenticationToken resolveAuthenticationToken(HttpServletRequest httpRequest) throws AuthenticationException {
        log.debug("resolve authentication token: {}", httpRequest);
        String username = httpRequest.getParameter("username");
        String password = httpRequest.getParameter("password");
        if (StringUtils.isAnyBlank(username, password)) {
            log.warn("could not find username or password parameters");
            return null;
        }
        return AuthenticationToken.unauthenticated(username, password);
    }

    @Override
    protected void onSuccess(HttpServletResponse httpResponse, AuthenticationToken token) throws IOException {
        if (!token.isAuthenticated()) {
            renderResponse(httpResponse, Results.create());
            return;
        }
        Authorization authorization = token.getAuthorization();
        String accessToken = accessTokenCreator.createToken(authorization);
        httpResponse.setHeader(AccessTokenAutoAuthenticationFilter.ACCESS_TOKEN_HEADER, accessToken);
        Map<String, Object> resultData = new LinkedHashMap<>();
        resultData.put("token", accessToken);
        resultData.put("authorization", authorization);
        renderResponse(httpResponse, Results.create(resultData));
    }

    @Override
    protected void onFailure(HttpServletResponse httpResponse, AuthenticationException ex) throws IOException {
        renderResponse(httpResponse, AuthenticationErrorResults.createResult(ex));
    }

    private void renderResponse(HttpServletResponse httpResponse, Result<?> result) throws IOException {
        httpResponse.setCharacterEncoding("utf-8");
        httpResponse.setContentType("application/json");
        httpResponse.getWriter().write(JsonCodec.instance().encode(result));
    }
}

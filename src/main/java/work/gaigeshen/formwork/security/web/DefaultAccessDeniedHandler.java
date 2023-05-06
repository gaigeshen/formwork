package work.gaigeshen.formwork.security.web;

import work.gaigeshen.formwork.commons.json.JsonCodec;
import work.gaigeshen.formwork.commons.web.Result;
import work.gaigeshen.formwork.commons.web.Results;
import work.gaigeshen.formwork.commons.web.resultcode.SecurityErrorResultCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author gaigeshen
 */
public class DefaultAccessDeniedHandler extends AbstractAccessDeniedHandler {

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) throws IOException {
        renderResponse(response, Results.create(SecurityErrorResultCode.ACCESS_DENIED));
    }

    private void renderResponse(HttpServletResponse httpResponse, Result<?> result) throws IOException {
        httpResponse.setCharacterEncoding("utf-8");
        httpResponse.setContentType("application/json");
        httpResponse.getWriter().write(JsonCodec.instance().encode(result));
    }
}

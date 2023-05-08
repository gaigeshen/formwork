package work.gaigeshen.formwork.controller;

import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.handler.DispatcherServletWebRequest;
import work.gaigeshen.formwork.basal.web.ErrorResults;
import work.gaigeshen.formwork.basal.web.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author gaigeshen
 */
@RequestMapping("/error")
@Controller
public class ErrorController extends AbstractErrorController {

    private final ErrorAttributes errorAttributes;

    public ErrorController(ErrorAttributes errorAttributes, List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, errorViewResolvers);
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping
    @ResponseBody
    public Result<?> error(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        Throwable error = errorAttributes.getError(new DispatcherServletWebRequest(request));
        HttpStatus status = getStatus(request);
        return ErrorResults.createResult(error, status.value());
    }
}

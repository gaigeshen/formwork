package work.gaigeshen.formwork.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.gaigeshen.formwork.basal.logging.aop.Logging;
import work.gaigeshen.formwork.basal.web.Result;
import work.gaigeshen.formwork.basal.web.Results;
import work.gaigeshen.formwork.service.TestService;

@RequestMapping("/test")
@RestController
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @Logging(first = true)
    @PostMapping
    public Result<String> test(@RequestBody String content) {
        String[] splited = content.split(",");

        return Results.create(testService.test(splited[0], splited[1]));
    }
}

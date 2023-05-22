package work.gaigeshen.formwork.service;

import org.springframework.stereotype.Service;
import work.gaigeshen.formwork.basal.logging.aop.Logging;

@Service
public class TestServiceImpl implements TestService {



    @Logging
    @Override
    public String test(String param1, String param2) {

        if (true) {
            throw new RuntimeException("xxx");
        }

        return param1 + "_____" + param2;
    }
}

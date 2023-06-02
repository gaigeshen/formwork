package work.gaigeshen.formwork;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author gaigeshen
 */
@SpringBootApplication
public class FormworkApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(FormworkApplication.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.run(args);
    }
}

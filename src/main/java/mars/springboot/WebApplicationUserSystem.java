package mars.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages={"com.datayes.springboot"})
@EnableAutoConfiguration
//@SpringBootApplication
public class WebApplicationUserSystem {

    public static void main(String[] args) throws Exception {
    	SpringApplication.run(WebApplicationUserSystem.class, args);
    }

}

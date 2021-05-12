package bbangduck.bd.bbangduck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import javax.servlet.Filter;

@SpringBootApplication
public class BbangduckApplication {

    public static void main(String[] args) {
        SpringApplication.run(BbangduckApplication.class, args);
    }
}

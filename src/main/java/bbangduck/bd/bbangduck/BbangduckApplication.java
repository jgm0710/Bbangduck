package bbangduck.bd.bbangduck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class BbangduckApplication {

    public static void main(String[] args) {
        SpringApplication.run(BbangduckApplication.class, args);
    }
}

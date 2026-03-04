package pdfservice.docapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class DocApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocApiApplication.class, args);
    }
}

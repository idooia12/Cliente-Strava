package web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication (scanBasePackages = {"web", "proxies"})
public class WebClientApplication {

	 public static void main(String[] args) {
	        SpringApplication.run(WebClientApplication.class, args);
	    }

	    @Bean
	    public RestTemplate restTemplate() {
	        return new RestTemplate();
	    }
}

package com.PCub.APIGateWay;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Mono;

@SpringBootApplication

public class ApiGateWayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGateWayApplication.class, args);

	}
	@Bean
	public GlobalFilter corsDebugFilter() {
		return (exchange, chain) -> {
			exchange.getResponse().beforeCommit(() -> {
				exchange.getResponse().getHeaders().forEach((key, value) -> {
					System.out.println("Header: " + key + " => " + value);
				});
				return Mono.empty();
			});
			return chain.filter(exchange);
		};
	}


}

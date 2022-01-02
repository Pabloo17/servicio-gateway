package com.servicio.gateway.filters;

import java.util.Optional;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class EjemploGlobalFilter implements GlobalFilter, Ordered {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    log.info("ejecutando filtro previo");
    exchange.getRequest().mutate().headers(h -> h.add("token", "123456"));

    // lo que esta antes seria el pre y lo que esta dentro de la lambda es el post
    return chain
        .filter(exchange)
        .then(
            Mono.fromRunnable(
                () -> {
                  log.info("ejecutando filtro post");

                  Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("token"))
                      .ifPresent(
                          valor -> {
                            exchange.getResponse().getHeaders().add("token", valor);
                          });

                  exchange
                      .getResponse()
                      .getCookies()
                      .add("color", ResponseCookie.from("color", "rojo").build());
                  // exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
                }));
  }

  @Override
  public int getOrder() {
    return 1;
  }
}

package ee.rosman.github.test;

import ee.rosman.github.test.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Date;

@Configuration
public class RoutesConfig {

    private UserService userService;

    public RoutesConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    RouterFunction<ServerResponse> routerFunction() {
        return RouterFunctions
                .nest(RequestPredicates.path("/api"), apiRouter());
    }

    private RouterFunction<ServerResponse> apiRouter() {
        return RouterFunctions
                .nest(RequestPredicates.path("/users"), usersRouter())
                .andRoute(RequestPredicates.GET("/ping"), req -> ServerResponse.ok().body(Mono.just(new Date().toString()), String.class));
    }

    private RouterFunction<ServerResponse> usersRouter() {
        return RouterFunctions
                .route(RequestPredicates.GET("/"), userService::handleGetUsers)
                .andRoute(RequestPredicates.GET("/{username}"), userService::handleGetUser);
    }
}

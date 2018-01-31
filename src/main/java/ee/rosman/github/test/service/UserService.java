package ee.rosman.github.test.service;

import ee.rosman.github.test.model.User;
import ee.rosman.github.test.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private UserRepository users;

    public UserService(UserRepository users) {
        this.users = users;
    }

    public Mono<ServerResponse> handleGetUsers(ServerRequest request) {
        return ServerResponse.ok().body(users.findAll(), User.class);
    }

    public Mono<ServerResponse> handleGetUser(ServerRequest request) {
        final String username = request.pathVariable("username");
        return users.findById(username)
                .flatMap(user -> ServerResponse.ok().syncBody(user))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}

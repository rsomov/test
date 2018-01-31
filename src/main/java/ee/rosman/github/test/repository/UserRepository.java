package ee.rosman.github.test.repository;

import ee.rosman.github.test.model.User;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

    @Query("{ roles: { $elemMatch: { $eq: ?0 } } }")
    Flux<User> findByRole(String role);
}

package ee.rosman.github.test.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    @Id
    private String username;
    @JsonIgnore
    private String password;
    private String name;
    private String surname;
    private String email;
    private boolean active;
    private List<String> roles;

    @Override
    public String toString() {
        return "username: " + username + "; e-mail: " + email + "; active: " + active;
    }
}

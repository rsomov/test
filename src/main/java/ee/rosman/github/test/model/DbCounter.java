package ee.rosman.github.test.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@ToString
@Document
@NoArgsConstructor
@AllArgsConstructor
public class DbCounter {

    @Id
    private String id;
    private int next;
}

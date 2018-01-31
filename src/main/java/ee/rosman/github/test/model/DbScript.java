package ee.rosman.github.test.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@ToString
@Document
@NoArgsConstructor
@AllArgsConstructor
public class DbScript {

    @Id
    private String name;
    private String script;
    private String checksum;
    private Date date;
}

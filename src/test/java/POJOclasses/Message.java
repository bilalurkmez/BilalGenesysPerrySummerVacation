package POJOclasses;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {

    User from;
    User to;
    String message;
    String id;
    String time;
}

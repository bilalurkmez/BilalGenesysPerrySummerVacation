package POJOclasses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;


@Data
@JsonIgnoreProperties (ignoreUnknown = true)
public class User {

    public User(){

    }

    public User(String name, String id){
        this.name = name;
        this.id =  id;
    }

    String name;
    String id;


}

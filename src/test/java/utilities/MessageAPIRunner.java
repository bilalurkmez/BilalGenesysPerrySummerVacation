package utilities;

import POJOclasses.Message;
import POJOclasses.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;

import java.io.IOException;
import java.util.List;
import java.util.Random;


public class MessageAPIRunner {


    private static Message message;
    private static Response response;
    private static List<User> userList;
    private static String userBaseURL = "https://perrys-summer-vacation.herokuapp.com/api/users";
    private static String messageBaseURL = "https://perrys-summer-vacation.herokuapp.com/api/messages";
    private static ObjectMapper mapper = new ObjectMapper();
    private static User from;
    private static User to;
    private static Random random = new Random();
    private static List<Message> messages;



    public static Message convertResponseToObject(String jsonBody){

        try {
            message = mapper.readValue(jsonBody, Message.class);
        } catch (IOException e) {
            System.out.println("JSON string couldn't map!");
        }
        return message;
    }

    public static Response messageRunPOST(String messageContent) throws JsonProcessingException {
        userList = UserAPIRunner.userListRunGET(userBaseURL);
        int userFromPos=0;
        int userToPos=0;
        if(userList.size()>=2){
            while (true) {
                userFromPos = random.nextInt(userList.size());
                userToPos = random.nextInt(userList.size());
            if(userFromPos!=userToPos){
                break;
            }
            }
        from = userList.get(userFromPos);
        to = userList.get(userToPos);
        String jsonBody = "{\n" +
                "    \"from\": {\n" +
                "        \"id\": \"fromUserId\"\n" +
                "    },\n" +
                "    \"to\": {\n" +
                "        \"id\": \"toUserId\"\n" +
                "    },\n" +
                "    \"message\": \"message text\"\n" +
                "}";
        jsonBody = jsonBody.replace("fromUserId", from.getId());
        jsonBody = jsonBody.replace("toUserId", to.getId());
        jsonBody = jsonBody.replace("message text", messageContent);
        response = RestAssured.given().contentType(ContentType.JSON).
                    when().body(jsonBody).post(messageBaseURL);
            Assert.assertTrue(response.statusCode()==200);
            return response;
        }else{
            System.out.println("There should be at least two users for messaging!!!");
            return null;
        }
    }

    public static Message runGET(String url, String messageID) throws JsonProcessingException {
        String finalURL = url + "/" + messageID;
        response = RestAssured.get(finalURL);
        Assert.assertTrue(response.statusCode()==200);
        message = mapper.readValue(response.asString(), new TypeReference<Message>(){});
        return message;
    }

    public static List<Message> messageListRunGET(String url, User from, User to) throws JsonProcessingException {
        String finalURL = url + "?from=" + from.getId() + "&to=" + to.getId();
        response = RestAssured.get(finalURL);
        Assert.assertTrue(response.statusCode()==200);
        messages = mapper.readValue(response.asString(), new TypeReference<List<Message>>(){});
        return messages;
    }

    public static void runPUT(String url, Message message, String updatedMessageContent){
        String finalURL = url + "/" + message.getId();
        String currentMessageContent = message.getMessage();
        if(currentMessageContent.equals(updatedMessageContent)){
            System.out.println("Contents of the current and updated messages are the same. No need for update");
            return;
        }else{
            String fromMessageID = message.getFrom().getId();
            String toMessageID = message.getTo().getId();
            String jsonBody = "{\n" +
                    "    \"from\": {\n" +
                    "        \"id\": \"fromUserId\"\n" +
                    "    },\n" +
                    "    \"to\": {\n" +
                    "        \"id\": \"toUserId\"\n" +
                    "    },\n" +
                    "    \"message\": \"message text\"\n" +
                    "}";
            jsonBody = jsonBody.replace("fromUserId", fromMessageID);
            jsonBody = jsonBody.replace("toUserId", toMessageID);
            jsonBody = jsonBody.replace("message text", updatedMessageContent);
            response = RestAssured.given().contentType(ContentType.JSON).
                    when().body(jsonBody).post(finalURL);
            System.out.println(response.statusCode());
            Assert.assertTrue(response.statusCode()==200);
        }
    }

    public static void runDELETE(String url, Message message){
        String finalURL = url + "/" + message.getId();
        response = RestAssured.delete(finalURL);
        Assert.assertTrue(response.statusCode()==204);
    }


}

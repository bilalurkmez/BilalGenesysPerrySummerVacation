package TestClasses;

import POJOclasses.Message;
import POJOclasses.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utilities.MessageAPIRunner;
import utilities.RetryAnalyzer;
import utilities.UserAPIRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class TestsForMessages {


    private static String baseUser = "https://perrys-summer-vacation.herokuapp.com/api/users";
    private static Response response;
    private static String baseURL = "https://perrys-summer-vacation.herokuapp.com/api/messages";
    private static ObjectMapper mapper = new ObjectMapper();
    private static Message message;
    private static User from;
    private static User to;
    private static List<User> userList;
    private static Random random = new Random();

    public static String generateRandomMessageContents(int numberOfWords)
    {
        String[] randomStrings = new String[numberOfWords];
        Random random = new Random();
        for(int i = 0; i < numberOfWords; i++)
        {
            char[] word = new char[random.nextInt(8)+3]; // words of length 3 through 10. (1 and 2 letter words are boring.)
            for(int j = 0; j < word.length; j++)
            {
                word[j] = (char)('a' + random.nextInt(26));
            }
            randomStrings[i] = new String(word);
        }
        String message = "";
        for(int i=0; i<randomStrings.length; i++){
            if(i==0){
                String temp = randomStrings[i].substring(0,1).toUpperCase();
                message += temp;
                message += randomStrings[i].substring(1);
                message += " ";
            } else if(i==randomStrings.length-1){
                message += randomStrings[i];
                message += ".";
            }
            else {
                message += randomStrings[i];
                message += " ";
            }
        }
        return message;
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void test1() throws JsonProcessingException {
        String messageContent = generateRandomMessageContents(4);
        response = MessageAPIRunner.messageRunPOST(messageContent);
        Assert.assertTrue(response!=null);
        message = mapper.readValue(response.asString(), new TypeReference<Message>(){});
        Assert.assertTrue(messageContent.equals(message.getMessage()));
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void test2() throws JsonProcessingException {
        userList = UserAPIRunner.userListRunGET(baseUser);
        int userFromPos = 0;
        int userToPos = 0;
        if (userList.size() >= 2) {
            while (true) {
                userFromPos = random.nextInt(userList.size());
                userToPos = random.nextInt(userList.size());
                if (userFromPos != userToPos) {
                    break;
                }
            }
            from = userList.get(userFromPos);
            to = userList.get(userToPos);
        }else{
            System.out.println("There should be at least two users for messaging!!!");
            Assert.assertTrue(false);
        }
        int numOfMessages = random.nextInt(5);
        String [] generatedMessageContents = new String[numOfMessages];
        for (int i=0; i<numOfMessages; i++){
            String temp = generateRandomMessageContents(4);
            generatedMessageContents[i] = temp;
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
            jsonBody = jsonBody.replace("message text", temp);
            response=RestAssured.given().contentType(ContentType.JSON).
                    when().body(jsonBody).post(baseURL);
            Assert.assertTrue(response.statusCode()==200);
        }
        List<Message>sentMessages = MessageAPIRunner.messageListRunGET(baseURL, from, to);
        System.out.println(sentMessages.size());
        String [] actualMessageContents = new String[sentMessages.size()];
        for(int i=0; i<sentMessages.size(); i++){
            actualMessageContents[i] = sentMessages.get(i).getMessage();
        }
        System.out.println(Arrays.toString(actualMessageContents));
        System.out.println(Arrays.toString(generatedMessageContents));
        Assert.assertTrue(Arrays.equals(generatedMessageContents,actualMessageContents));
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void test3() throws JsonProcessingException {
        boolean check = true;
        do {
            userList = UserAPIRunner.userListRunGET(baseUser);
            int userFromPos = 0;
            int userToPos = 0;
            if (userList.size() >= 2) {
                while (true) {
                    userFromPos = random.nextInt(userList.size());
                    userToPos = random.nextInt(userList.size());
                    if (userFromPos != userToPos) {
                        break;
                    }
                }
                from = userList.get(userFromPos);
                to = userList.get(userToPos);
            } else {
                System.out.println("There should be at least two users for messaging!!!");
                Assert.assertTrue(false);
                break;
            }
            List<Message> sentMessages = MessageAPIRunner.messageListRunGET(baseURL, from, to);
            if(sentMessages.size()>=1){
                check=false;
            }
            int messagePos = random.nextInt(sentMessages.size());
            message = sentMessages.get(messagePos);
        }while(check);
        String messageID = message.getId();
        Message gottenMessage = MessageAPIRunner.runGET(baseURL, messageID);
        Assert.assertTrue(gottenMessage!=null);
        MessageAPIRunner.runDELETE(baseURL, message);
        List<Message> updatedListOfMessagesAfterDeletion = MessageAPIRunner.messageListRunGET(baseURL, from, to);
        boolean messageIDCheckAfterDelete = true;
        for(int i=0; i<updatedListOfMessagesAfterDeletion.size(); i++){
            if(messageID.equals(updatedListOfMessagesAfterDeletion.get(i).getId())){
                messageIDCheckAfterDelete = false;
            }
        }
    Assert.assertTrue(messageIDCheckAfterDelete);
    }

    /*
    The below test is failing because the PUT command was not in the Collection
    for the Messages requests. I tried with POST based on my own logic,
    but it did not work.
     */
    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void test4() throws JsonProcessingException {
        String messageContent = generateRandomMessageContents(4);
        response = MessageAPIRunner.messageRunPOST(messageContent);
        message = mapper.readValue(response.asString(), new TypeReference<Message>(){});
        String updatedMessageContent = generateRandomMessageContents(4);
        MessageAPIRunner.runPUT(baseURL, message, updatedMessageContent);
        Message updatedMessage = MessageAPIRunner.runGET(baseURL, message.getId());
        Assert.assertTrue(updatedMessageContent.equals(updatedMessage.getMessage()));
    }
}

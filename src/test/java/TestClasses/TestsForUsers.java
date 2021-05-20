package TestClasses;

import POJOclasses.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utilities.RetryAnalyzer;
import utilities.UserAPIRunner;

import java.util.List;
import java.util.Random;


public class TestsForUsers {

    private static String baseURL = "https://perrys-summer-vacation.herokuapp.com/api/users";
    private static Response response;
    private static User user;
    private static List<User> userList;
    private static Faker faker = new Faker();
    private static Random random = new Random();
    private static ObjectMapper mapper = new ObjectMapper();

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void test1() throws JsonProcessingException {
        userList = UserAPIRunner.userListRunGET(baseURL);
        String userJson = UserAPIRunner.userJsonBodyGenerator();
        user = UserAPIRunner.convertResponseToObject(userJson);
        response = UserAPIRunner.userRunPOST(baseURL, user);
        List<User>updatedUserList = UserAPIRunner.userListRunGET(baseURL);
        Assert.assertTrue(updatedUserList.size()==userList.size()+1);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void test2() throws JsonProcessingException {
        userList = UserAPIRunner.userListRunGET(baseURL);
        String updatedName = faker.name().fullName();
        if(userList.size()>=0) {
            int userPosition = random.nextInt(userList.size());
            user = userList.get(userPosition);
            String updatedID = UserAPIRunner.userIDModifier(userList.get(userPosition));
            User updatedUser = new User(updatedName, updatedID);
            UserAPIRunner.runPUT(baseURL, user, updatedUser);
            userList = UserAPIRunner.userListRunGET(baseURL);
            user = userList.get(userPosition);
            Assert.assertTrue(user.getName().equals(updatedName));
            Assert.assertTrue(user.getId().equals(updatedID));
        }
        else{
            System.out.println("There is no user");
        }
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void test3() throws JsonProcessingException {
        String userJson = UserAPIRunner.userJsonBodyGenerator();
        user = UserAPIRunner.convertResponseToObject(userJson);
        response = UserAPIRunner.userRunPOST(baseURL, user);
        user = mapper.readValue(response.asString(), new TypeReference<User>(){});
        User userCheck = UserAPIRunner.runGET(baseURL, user.getId());
        Assert.assertTrue(userCheck.equals(user));
    }

    /*
    The below test is failing because the system is not stable for the DELETE
    command. The system is too slow for deleting. When I attempted on both
    Postman and my automation code, the deletion process took place but was too slow.
    Also, the status code was given as 503.
     */
    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void test4() throws JsonProcessingException {
        userList = UserAPIRunner.userListRunGET(baseURL);
        if(userList.size()>=0) {
            int userPosition = random.nextInt(userList.size());
            System.out.println(userPosition);
            if(userList.size()==1){
                user = userList.get(userPosition);
                UserAPIRunner.runDELETE(baseURL, user.getId());
                userList = UserAPIRunner.userListRunGET(baseURL);
                Assert.assertTrue(userList.size()==0);
            }
            else{
                user = userList.get(userPosition-1);
                User nextUser = userList.get(userPosition);
                UserAPIRunner.runDELETE(baseURL, user.getId());
                userList = UserAPIRunner.userListRunGET(baseURL);
                Assert.assertTrue(nextUser.equals(userList.get(userPosition-1)));
            }
        }
        else{
            System.out.println("There is no user");
        }
    }



}

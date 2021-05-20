package utilities;


import POJOclasses.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;

import java.io.IOException;
import java.util.List;

public class UserAPIRunner {

    private static Faker faker = new Faker();
    private static User user;
    private static Response response;
    private static ObjectMapper mapper = new ObjectMapper();

    public static String userIDModifier(User user){
        int length = 12;
        boolean useLetters = true;
        boolean useNumbers = true;
        String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
        String modifiedString = generatedString.toLowerCase();
        String modifiedUserID = user.getId().substring(0,24) + modifiedString;
        return modifiedUserID;
    }

    public static String userJsonBodyGenerator(){
        String fullName = faker.name().fullName();
        String userJsonBody = "{\"name\": \"" + fullName + "\"}";
        return userJsonBody;
    }

    public static User convertResponseToObject(String jsonBody){

        try {
            user = mapper.readValue(jsonBody, User.class);
        } catch (IOException e) {
            System.out.println("JSON string couldn't map!");
        }
        return user;
    }

    public static Response userRunPOST(String url, User user) {
        response = RestAssured.given().contentType(ContentType.JSON).
                when().body(user).post(url);
        Assert.assertTrue(response.statusCode()==200);
        return response;
    }

    public static List<User> userListRunGET(String url) throws JsonProcessingException {
        response = RestAssured.get(url);
        Assert.assertTrue(response.statusCode()==200);
        List<User> users = mapper.readValue(response.asString(), new TypeReference<List<User>>(){});
        return users;
    }

    public static User runGET(String url, String userID) throws JsonProcessingException {
        String finalURL = url + "/" + userID;
        response = RestAssured.get(finalURL);
        Assert.assertTrue(response.statusCode()==200);
        user = mapper.readValue(response.asString(), new TypeReference<User>(){});
        return user;
    }

    public static void runPUT(String url, User user, User updatedUser){
        String finalURL = url + "/" + user.getId();
        response = RestAssured.given().contentType(ContentType.JSON).
                body(updatedUser).when().put(finalURL);
        Assert.assertTrue(response.statusCode()==200);
    }

    public static void runDELETE(String url, String userID){
        String finalURL = url + "/" + userID;
        response = RestAssured.delete(finalURL);
        Assert.assertTrue(response.statusCode()==200);
    }

}

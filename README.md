# BilalGenesysPerrySummerVacation

# How to run the test script:
- I prepared a Java-TestNG framework.
- I separated the runnable test cases in two separate classes.
-   a-) TestsForUsers
-   b-) TestsForMessages
- It will be enough if you run those two classes to execute my test scripts.

# What I tested:
- I tried to test all the given API calls which are in the provided Postman Collection.
- For testing and automation, I moved through my own approach. I will explain during the interview.

# Issues/Bugs:
- The system is not stable for the DELETE
  API calls. The system is too slow for deleting. When I attempted via both
  Postman and my automation code, the deletion process took place but was too slow and late.
  Also, the status code was given as 503.
- The PUT command was not in the provided Collection of API calls
  in the Messages section. I wrote automation code for POST based on my own logic,
  but it did not work. I will explain my logic during the interview.

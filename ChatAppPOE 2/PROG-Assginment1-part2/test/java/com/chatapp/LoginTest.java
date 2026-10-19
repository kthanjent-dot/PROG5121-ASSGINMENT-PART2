package com.chatapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;<dependency>
    <groupId>org.junit.jupiter</groupId    testImplementation 'org.junit.jupiter:junit-jupiter:5.11.4'>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.4</version>
    <scope>test</scope>
</dependency>


/** Unit tests using the exact test data from the assessment brief. */
public class LoginTest {

    private static final String GOOD_PASSWORD = "Ch&&sec@ke99!";
    private static final String GOOD_CELL = "+27838968976";

    // ---------- assertEquals: registerUser() messages ----------

    @Test
    void usernameCorrectlyFormattedMessage() {
        Login login = new Login();
        String result = login.registerUser("Kyle", "Smith", "kyl_1", GOOD_PASSWORD, GOOD_CELL);
        assertTrue(result.contains("Username successfully captured."));
    }

    @Test
    void usernameIncorrectlyFormattedMessage() {
        Login login = new Login();
        String result = login.registerUser("Kyle", "Smith", "kyle!!!!!!", GOOD_PASSWORD, GOOD_CELL);
        assertTrue(result.contains("Username is not correctly formatted; please ensure that your "
                + "username contains an underscore and is no more than five characters in length."));
    }

    @Test
    void passwordMeetsComplexityMessage() {
        Login login = new Login();
        String result = login.registerUser("Kyle", "Smith", "kyl_1", GOOD_PASSWORD, GOOD_CELL);
        assertTrue(result.contains("Password successfully captured."));
    }

    @Test
    void passwordFailsComplexityMessage() {
        Login login = new Login();
        String result = login.registerUser("Kyle", "Smith", "kyl_1", "password", GOOD_CELL);
        assertTrue(result.contains("Password is not correctly formatted; please ensure that the "
                + "password contains at least eight characters, a capital letter, a number, and a "
                + "special character."));
    }

    @Test
    void cellNumberCorrectMessage() {
        Login login = new Login();
        String result = login.registerUser("Kyle", "Smith", "kyl_1", GOOD_PASSWORD, GOOD_CELL);
        assertTrue(result.contains("Cell phone number successfully captured."));
    }

    @Test
    void cellNumberIncorrectMessage() {
        Login login = new Login();
        String result = login.registerUser("Kyle", "Smith", "kyl_1", GOOD_PASSWORD, "08966553");
        assertTrue(result.contains("Cell phone number is incorrectly formatted or does not contain "
                + "an international code; please correct the number and try again."));
    }

    // ---------- assertTrue / assertFalse: boolean methods ----------

    @Test
    void usernameCorrectlyFormatted() {
        assertTrue(new Login().checkUserName("kyl_1"));
    }

    @Test
    void usernameIncorrectlyFormatted() {
        assertFalse(new Login().checkUserName("kyle!!!!!!"));
    }

    @Test
    void passwordMeetsComplexity() {
        assertTrue(new Login().checkPasswordComplexity(GOOD_PASSWORD));
    }

    @Test
    void passwordDoesNotMeetComplexity() {
        assertFalse(new Login().checkPasswordComplexity("password"));
    }

    @Test
    void cellNumberCorrectlyFormatted() {
        assertTrue(new Login().checkCellPhoneNumber(GOOD_CELL));
    }

    @Test
    void cellNumberIncorrectlyFormatted() {
        assertFalse(new Login().checkCellPhoneNumber("08966553"));
    }

    // ---------- Login ----------

    @Test
    void loginSuccessful() {
        Login login = new Login();
        login.registerUser("Kyle", "Smith", "kyl_1", GOOD_PASSWORD, GOOD_CELL);
        assertTrue(login.loginUser("kyl_1", GOOD_PASSWORD));
    }

    @Test
    void loginFailed() {
        Login login = new Login();
        login.registerUser("Kyle", "Smith", "kyl_1", GOOD_PASSWORD, GOOD_CELL);
        assertFalse(login.loginUser("kyl_1", "wrongPassword"));
    }

    @Test
    void loginStatusSuccessMessage() {
        Login login = new Login();
        login.registerUser("Kyle", "Smith", "kyl_1", GOOD_PASSWORD, GOOD_CELL);
        boolean ok = login.loginUser("kyl_1", GOOD_PASSWORD);
        assertEquals("Welcome Kyle, Smith, it is great to see you again.", login.returnLoginStatus(ok));
    }

    @Test
    void loginStatusFailureMessage() {
        Login login = new Login();
        login.registerUser("Kyle", "Smith", "kyl_1", GOOD_PASSWORD, GOOD_CELL);
        boolean ok = login.loginUser("kyl_1", "wrongPassword");
        assertEquals("Username or password incorrect, please try again.", login.returnLoginStatus(ok));
    }
}

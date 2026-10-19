package com.chatapp;

import java.util.regex.Pattern;

/**
 * Handles user registration and login for the chat application.
 * PROG5121 POE - Part 1: Registration and Login feature.
 */
public class Login {

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String cellphoneNumber;
    private boolean registered = false;

    // At least 8 chars, 1 capital letter, 1 digit, 1 special character.
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$");

    // South African international code (+27) followed by 1-10 digits.
    private static final Pattern CELLPHONE_PATTERN = Pattern.compile("^\\+27\\d{1,10}$");

    /** Username must contain an underscore and be no more than 5 characters. */
    public boolean checkUserName(String username) {
        return username != null && username.contains("_") && username.length() <= 5;
    }

    /** Password: 8+ characters, a capital letter, a number and a special character. */
    public boolean checkPasswordComplexity(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    /** Cell number: international code +27 followed by no more than ten digits. */
    public boolean checkCellPhoneNumber(String cellphoneNumber) {
        return cellphoneNumber != null && CELLPHONE_PATTERN.matcher(cellphoneNumber).matches();
    }

    /**
     * Validates all details and returns a message for each check. The user is
     * only stored if the username, password and cell number are all valid.
     */
    public String registerUser(String firstName, String lastName, String username,
                               String password, String cellphoneNumber) {

        boolean usernameOk = checkUserName(username);
        boolean passwordOk = checkPasswordComplexity(password);
        boolean cellOk = checkCellPhoneNumber(cellphoneNumber);

        StringBuilder result = new StringBuilder();

        result.append(usernameOk
                ? "Username successfully captured."
                : "Username is not correctly formatted; please ensure that your username "
                  + "contains an underscore and is no more than five characters in length.");
        result.append("\n");

        result.append(passwordOk
                ? "Password successfully captured."
                : "Password is not correctly formatted; please ensure that the password "
                  + "contains at least eight characters, a capital letter, a number, and a "
                  + "special character.");
        result.append("\n");

        result.append(cellOk
                ? "Cell phone number successfully captured."
                : "Cell phone number is incorrectly formatted or does not contain an "
                  + "international code; please correct the number and try again.");

        if (usernameOk && passwordOk && cellOk) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.username = username;
            this.password = password;
            this.cellphoneNumber = cellphoneNumber;
            this.registered = true;
            result.append("\nUser registered successfully.");
        }

        return result.toString();
    }

    /** True only if the details match those stored at registration. */
    public boolean loginUser(String username, String password) {
        return registered
                && this.username.equals(username)
                && this.password.equals(password);
    }

    /** Message for a successful or failed login. */
    public String returnLoginStatus(boolean loginSuccessful) {
        if (loginSuccessful) {
            return "Welcome " + firstName + ", " + lastName + ", it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }

    public boolean isRegistered() { return registered; }
}

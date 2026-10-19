package com.chatapp;

import java.util.Scanner;

/**
 * Console entry point: registration, login, then the QuickChat menu.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Login login = new Login();

        registerUser(login);

        // Users can only send messages if they log in successfully.
        if (!loginUser(login)) {
            System.out.println("Too many failed login attempts. Goodbye.");
            scanner.close();
            return;
        }

        runQuickChat();
        scanner.close();
    }

    // ------------------------------------------------------------------
    // Part 1: registration and login
    // ------------------------------------------------------------------

    private static void registerUser(Login login) {
        System.out.println("=== USER REGISTRATION ===");

        while (!login.isRegistered()) {
            System.out.print("Enter first name: ");
            String firstName = scanner.nextLine();

            System.out.print("Enter last name: ");
            String lastName = scanner.nextLine();

            System.out.print("Enter username (contains '_' and max 5 characters): ");
            String username = scanner.nextLine();

            System.out.print("Enter password (8+ chars, capital, number, special character): ");
            String password = scanner.nextLine();

            System.out.print("Enter cell number (e.g. +27838968976): ");
            String cell = scanner.nextLine();

            System.out.println();
            System.out.println(login.registerUser(firstName, lastName, username, password, cell));
            System.out.println();
        }
    }

    private static boolean loginUser(Login login) {
        System.out.println("=== LOGIN ===");

        for (int attempt = 1; attempt <= 3; attempt++) {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            boolean success = login.loginUser(username, password);
            System.out.println();
            System.out.println(login.returnLoginStatus(success));
            System.out.println();

            if (success) {
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------------------------
    // Part 2: QuickChat
    // ------------------------------------------------------------------

    private static void runQuickChat() {
        System.out.println("Welcome to QuickChat.");

        int maxMessages = readPositiveInt("How many messages do you wish to enter? ");
        boolean allMessagesEntered = false;
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1:
                    if (allMessagesEntered) {
                        System.out.println("You have already entered all " + maxMessages + " messages.");
                    } else {
                        sendMessages(maxMessages);
                        allMessagesEntered = true;
                    }
                    break;
                case 2:
                    System.out.println("Coming Soon.");
                    break;
                case 3:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1, 2 or 3.");
            }
        }

        System.out.println();
        System.out.println("Total number of messages sent: " + Message.returnTotalMessages());
        System.out.println("Goodbye.");
    }

    /** Captures exactly maxMessages messages, one at a time. */
    private static void sendMessages(int maxMessages) {
        for (int i = 1; i <= maxMessages; i++) {
            System.out.println();
            System.out.println("--- Message " + i + " of " + maxMessages + " ---");

            String recipient = readRecipient();
            String text = readMessageText();

            Message message = new Message(recipient, text);
            System.out.println(message.getMessageIDMessage());

            int option = readSendOption();
            System.out.println(message.sentMessage(option));

            if (option == 1) {
                System.out.println();
                System.out.println(message.getDetails());
            } else if (option == 2) {
                waitForZero();
                System.out.println("Message deleted.");
            }
        }

        // Total is displayed once all the messages have been entered.
        System.out.println();
        System.out.println("Total number of messages sent: " + Message.returnTotalMessages());
    }

    // ------------------------------------------------------------------
    // Input helpers
    // ------------------------------------------------------------------

    private static String readRecipient() {
        while (true) {
            System.out.print("Enter recipient cell number (e.g. +27718693002): ");
            String cell = scanner.nextLine().trim();
            System.out.println(Message.checkRecipientCell(cell));
            if (Message.isValidRecipient(cell)) {
                return cell;
            }
        }
    }

    private static String readMessageText() {
        while (true) {
            System.out.print("Enter your message (max 250 characters): ");
            String text = scanner.nextLine();
            if (text.trim().isEmpty()) {
                System.out.println("The message cannot be empty.");
                continue;
            }
            System.out.println(Message.checkMessageLength(text));
            if (Message.isMessageLengthValid(text)) {
                return text;
            }
        }
    }

    private static int readSendOption() {
        while (true) {
            System.out.println("What would you like to do with this message?");
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            int option = readInt("Choose an option: ");
            if (option >= 1 && option <= 3) {
                return option;
            }
            System.out.println("Invalid option. Please choose 1, 2 or 3.");
        }
    }

    private static void waitForZero() {
        String input;
        do {
            System.out.print("> ");
            input = scanner.nextLine().trim();
        } while (!input.equals("0"));
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private static int readPositiveInt(String prompt) {
        while (true) {
            int value = readInt(prompt);
            if (value > 0) {
                return value;
            }
            System.out.println("Please enter a number greater than 0.");
        }
    }
}

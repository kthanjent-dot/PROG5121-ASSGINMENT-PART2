package com.chatapp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a single QuickChat message and keeps track of the messages
 * sent / stored while the program is running.
 *
 * PROG5121 POE - Part 2: Sending Messages
 */
public class Message {

    public static final int MAX_MESSAGE_LENGTH = 250;

    // Shared state for the whole program run.
    private static final List<Message> sentMessages = new ArrayList<>();
    private static final List<Message> storedMessages = new ArrayList<>();
    private static int totalMessagesSent = 0;
    private static int messageCounter = 0;
    private static String storageFilePath = "storedMessages.json";

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String messageText;
    private final String messageHash;

    /** Creates a new message with an auto-generated ID and message number. */
    public Message(String recipient, String messageText) {
        this(generateMessageID(), messageCounter++, recipient, messageText);
    }

    /** Creates a message with a known ID and number (used for testing). */
    public Message(String messageID, int messageNumber, String recipient, String messageText) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = createMessageHash();
    }

    // ------------------------------------------------------------------
    // Required methods
    // ------------------------------------------------------------------

    /** The message ID must be no more than ten characters long. */
    public boolean checkMessageID() {
        return messageID != null && !messageID.isEmpty() && messageID.length() <= 10;
    }

    /** Checks this message's recipient number and returns the result message. */
    public String checkRecipientCell() {
        return checkRecipientCell(recipient);
    }

    /**
     * Checks a recipient cell number (international code, max ten digits after
     * the code). Reuses the cell number rule from Part 1.
     */
    public static String checkRecipientCell(String cellNumber) {
        if (isValidRecipient(cellNumber)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.";
    }

    /**
     * Creates the message hash: first two digits of the ID, a colon, the
     * message number, a colon, then the first and last words, all in capitals.
     * Example: 00:0:HITONIGHT
     */
    public String createMessageHash() {
        String idPrefix = messageID.length() >= 2 ? messageID.substring(0, 2) : messageID;
        String[] words = messageText.trim().split("\\s+");
        String firstWord = cleanWord(words[0]);
        String lastWord = cleanWord(words[words.length - 1]);
        return (idPrefix + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
    }

    /**
     * Applies the user's choice for this message:
     * 1 = send, 2 = disregard, 3 = store to send later.
     */
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                sentMessages.add(this);
                totalMessagesSent++;
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                if (storeMessage()) {
                    return "Message successfully stored.";
                }
                return "Message could not be stored.";
            default:
                return "Invalid option. Please choose 1, 2 or 3.";
        }
    }

    /** Returns the details of every message sent while the program is running. */
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent yet.";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sentMessages.size(); i++) {
            if (i > 0) {
                sb.append("\n\n");
            }
            sb.append(sentMessages.get(i).getDetails());
        }
        return sb.toString();
    }

    /** Returns the total number of messages sent. */
    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    /**
     * Stores this message in a JSON file so it can be sent later. The file
     * holds a JSON array of every message stored during this run.
     * Returns true if the file was written successfully.
     */
    public boolean storeMessage() {
        storedMessages.add(this);

        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < storedMessages.size(); i++) {
            Message m = storedMessages.get(i);
            json.append("  {\n")
                .append("    \"messageID\": \"").append(escapeJson(m.messageID)).append("\",\n")
                .append("    \"messageHash\": \"").append(escapeJson(m.messageHash)).append("\",\n")
                .append("    \"recipient\": \"").append(escapeJson(m.recipient)).append("\",\n")
                .append("    \"message\": \"").append(escapeJson(m.messageText)).append("\"\n")
                .append("  }").append(i < storedMessages.size() - 1 ? "," : "").append("\n");
        }
        json.append("]\n");

        try {
            Files.write(Paths.get(storageFilePath), json.toString().getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            System.err.println("Could not write to JSON file: " + e.getMessage());
            return false;
        }
    }

    // ------------------------------------------------------------------
    // Validation helpers
    // ------------------------------------------------------------------

    public static boolean isValidRecipient(String cellNumber) {
        return new Login().checkCellPhoneNumber(cellNumber);
    }

    public static boolean isMessageLengthValid(String text) {
        return text != null && text.length() <= MAX_MESSAGE_LENGTH;
    }

    /** Returns the success or failure message for the 250 character rule. */
    public static String checkMessageLength(String text) {
        if (isMessageLengthValid(text)) {
            return "Message ready to send.";
        }
        int excess = text.length() - MAX_MESSAGE_LENGTH;
        return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
    }

    // ------------------------------------------------------------------
    // Display / accessors
    // ------------------------------------------------------------------

    /** Full details in the required order: ID, hash, recipient, message. */
    public String getDetails() {
        return "Message ID: " + messageID + "\n"
                + "Message Hash: " + messageHash + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + messageText;
    }

    public String getMessageIDMessage() {
        return "Message ID generated: " + messageID;
    }

    public String getMessageID() { return messageID; }
    public int getMessageNumber() { return messageNumber; }
    public String getRecipient() { return recipient; }
    public String getMessageText() { return messageText; }
    public String getMessageHash() { return messageHash; }

    public static int getStoredMessageCount() { return storedMessages.size(); }

    /** Points the JSON storage at a different file (used by the tests). */
    public static void setStorageFile(String path) { storageFilePath = path; }

    /** Clears all shared state (used by the tests). */
    public static void clearAll() {
        sentMessages.clear();
        storedMessages.clear();
        totalMessagesSent = 0;
        messageCounter = 0;
    }

    // ------------------------------------------------------------------
    // Private helpers
    // ------------------------------------------------------------------

    /** Random ten-digit ID (leading zeros allowed, e.g. 0012345678). */
    private static String generateMessageID() {
        return String.format("%010d", ThreadLocalRandom.current().nextLong(10_000_000_000L));
    }

    /** Removes punctuation from the start and end of a word. */
    private static String cleanWord(String word) {
        return word.replaceAll("^[^A-Za-z0-9]+|[^A-Za-z0-9]+$", "");
    }

    private static String escapeJson(String value) {
        StringBuilder sb = new StringBuilder();
        for (char c : value.toCharArray()) {
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }
}

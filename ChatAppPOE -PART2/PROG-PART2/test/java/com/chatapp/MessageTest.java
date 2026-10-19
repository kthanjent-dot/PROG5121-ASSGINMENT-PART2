package com.chatapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/** Unit tests for the Message class using the test data from the brief. */
public class MessageTest {

    @TempDir
    static Path tempDir;

    // Test Case 1
    private static final String RECIPIENT_1 = "+27718693002";
    private static final String TEXT_1 = "Hi Mike, can you join us for dinner tonight?";
    // Test Case 2
    private static final String RECIPIENT_2 = "08575975889";
    private static final String TEXT_2 = "Hi Keegan, did you receive the payment?";

    @BeforeEach
    void setUp() {
        Message.clearAll();
        Message.setStorageFile(tempDir.resolve("storedMessages.json").toString());
    }

    // ---------- Message length (assertEquals) ----------

    @Test
    void messageLengthSuccess() {
        assertEquals("Message ready to send.", Message.checkMessageLength(TEXT_1));
    }

    @Test
    void messageLengthExactlyAtLimitSucceeds() {
        assertEquals("Message ready to send.", Message.checkMessageLength("a".repeat(250)));
    }

    @Test
    void messageLengthFailure() {
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                Message.checkMessageLength("a".repeat(260)));
    }

    // ---------- Recipient number (assertEquals) ----------

    @Test
    void recipientCorrectlyFormatted() {
        assertEquals("Cell phone number successfully captured.",
                Message.checkRecipientCell(RECIPIENT_1));
    }

    @Test
    void recipientIncorrectlyFormatted() {
        assertEquals("Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.",
                Message.checkRecipientCell(RECIPIENT_2));
    }

    // ---------- Message hash ----------

    @Test
    void messageHashTestCase1() {
        Message m = new Message("0012345678", 0, RECIPIENT_1, TEXT_1);
        assertEquals("00:0:HITONIGHT", m.createMessageHash());
        assertEquals("00:0:HITONIGHT", m.getMessageHash());
    }

    @Test
    void messageHashesInALoop() {
        String[] ids = {"0012345678", "0098765432", "1234567890"};
        int[] numbers = {0, 1, 2};
        String[] texts = {TEXT_1, TEXT_2, "Thanks for the update."};
        String[] expected = {"00:0:HITONIGHT", "00:1:HIPAYMENT", "12:2:THANKSUPDATE"};

        for (int i = 0; i < ids.length; i++) {
            Message m = new Message(ids[i], numbers[i], RECIPIENT_1, texts[i]);
            assertEquals(expected[i], m.createMessageHash(), "Hash mismatch for message " + i);
        }
    }

    // ---------- Message ID ----------

    @Test
    void messageIDIsCreated() {
        Message m = new Message(RECIPIENT_1, TEXT_1);
        assertEquals("Message ID generated: " + m.getMessageID(), m.getMessageIDMessage());
        assertEquals(10, m.getMessageID().length());
        assertTrue(m.getMessageID().matches("\\d{10}"));
        assertTrue(m.checkMessageID());
    }

    @Test
    void messageIDLongerThanTenCharactersFails() {
        Message m = new Message("12345678901", 0, RECIPIENT_1, TEXT_1);
        assertFalse(m.checkMessageID());
    }

    // ---------- SentMessage options ----------

    @Test
    void userSelectedSendMessage() {
        Message m = new Message("0012345678", 0, RECIPIENT_1, TEXT_1);
        assertEquals("Message successfully sent.", m.sentMessage(1));
        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    void userSelectedDisregardMessage() {
        Message m = new Message("0098765432", 1, RECIPIENT_2, TEXT_2);
        assertEquals("Press 0 to delete the message.", m.sentMessage(2));
        assertEquals(0, Message.returnTotalMessages());
    }

    @Test
    void userSelectedStoreMessage() throws IOException {
        Message m = new Message("0012345678", 0, RECIPIENT_1, TEXT_1);
        assertEquals("Message successfully stored.", m.sentMessage(3));
        assertEquals(1, Message.getStoredMessageCount());

        String json = Files.readString(tempDir.resolve("storedMessages.json"));
        assertTrue(json.contains("\"messageID\": \"0012345678\""));
        assertTrue(json.contains("\"messageHash\": \"00:0:HITONIGHT\""));
        assertTrue(json.contains("\"recipient\": \"+27718693002\""));
        assertTrue(json.contains("\"message\": \"" + TEXT_1 + "\""));
    }

    // ---------- Totals and printing ----------

    @Test
    void totalMessagesSentWithTestData() {
        new Message("0012345678", 0, RECIPIENT_1, TEXT_1).sentMessage(1);   // Send
        new Message("0098765432", 1, RECIPIENT_2, TEXT_2).sentMessage(2);   // Discard
        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    void printMessagesShowsDetailsInRequiredOrder() {
        new Message("0012345678", 0, RECIPIENT_1, TEXT_1).sentMessage(1);
        String expected = "Message ID: 0012345678\n"
                + "Message Hash: 00:0:HITONIGHT\n"
                + "Recipient: +27718693002\n"
                + "Message: " + TEXT_1;
        assertEquals(expected, Message.printMessages());
    }

    @Test
    void printMessagesWhenNothingSent() {
        assertEquals("No messages have been sent yet.", Message.printMessages());
    }
}

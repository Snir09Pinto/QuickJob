package com.example.project.objects;

import com.example.project.managers.ConstantsManager;

import java.util.HashMap;
import java.util.Map;

/**
 * The message object class.
 */
public class Message
{
    /**
     * The sender of the message id (user).
     */
    private String senderId;
    /**
     * The receiver of the message id (user).
     */
    private String receiverId;
    /**
     * The message text.
     */
    private String message;
    /**
     * The message date and time of sending - the amount of milliseconds since approximately year 1970 - long number.
     */
    private long milliseconds;
    /**
     * A variable holding whether the message was read by the other user.
     */
    private boolean read;



    /**
     * Sets the member variables to the given parameters.
     * @param senderId - The sender of the message id (user).
     * @param receiverId - The receiver of the message id (user).
     * @param message - The message text.
     * @param milliseconds - The message date and time of sending.
     * @param read - A variable holding whether the message was read by the other user.
     */
    public Message(String senderId, String receiverId, String message, long milliseconds, boolean read)
    {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.milliseconds = milliseconds;
        this.read = read;
    }

    /**
     * An Empty constructor for firebase.
     */
    public Message()
    {

    }


    /**
     * @return a hashmap containing all message's fields - ordered by: (key, value).
     */
    public Map<String, Object> getMessageInfo()
    {
        Map<String, Object> messageInfo = new HashMap<>();
        messageInfo.put(ConstantsManager.MESSAGE_SENDER_ID_FIELD, senderId);
        messageInfo.put(ConstantsManager.MESSAGE_RECEIVER_ID_FIELD, receiverId);
        messageInfo.put(ConstantsManager.MESSAGE_MESSAGE_FIELD, message);
        messageInfo.put(ConstantsManager.MESSAGE_MILLISECONDS_FIELD, milliseconds);
        messageInfo.put(ConstantsManager.MESSAGE_READ_FIELD, read);

        return messageInfo;
    }


    /**
     * Sets member variable "senderId" to given senderId (parameter).
     * @param senderId
     */
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    /**
     * Sets member variable "receiverId" to given receiverId (parameter).
     * @param receiverId
     */
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    /**
     * Sets member variable "message" to given message (parameter).
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Sets member variable "milliseconds" to given milliseconds (parameter).
     * @param milliseconds
     */
    public void setMilliseconds(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    /**
     * Sets member variable "read" to given read (parameter).
     * @param read
     */
    public void setRead(boolean read) {
        this.read = read;
    }

    /**
     *
     * @return the sender id.
     */
    public String getSenderId() {
        return senderId;
    }

    /**
     *
     * @return the receiver id.
     */
    public String getReceiverId() {
        return receiverId;
    }

    /**
     *
     * @return the message.
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @return the milliseconds.
     */
    public long getMilliseconds() {
        return milliseconds;
    }

    /**
     *
     * @return the read.
     */
    public boolean isRead()
    {
        return read;
    }
}

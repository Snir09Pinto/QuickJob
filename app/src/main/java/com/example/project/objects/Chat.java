package com.example.project.objects;

/**
 * The chat object class.
 */
public class Chat
{
    /**
     * The other user of the chat (not current user).
     */
    private User user;
    /**
     * The unread messages of current chat (amount).
     */
    private long unreadMessages;
    /**
     * The last message of current chat (object).
     */
    private Message lastMessage;


    /**
     * Sets the member variables to the given parameters.
     * @param user - other user of the chat.
     * @param unreadMessages - amount of unread messages.
     * @param lastMessage - last message sent in chat.
     */
    public Chat(User user, int unreadMessages, Message lastMessage)
    {
        this.user = user;
        this.unreadMessages = unreadMessages;
        this.lastMessage = lastMessage;
    }


    /**
     * Sets member variable "user" to given user (parameter).
     * @param user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Sets member variable "unreadMessages" to given unreadMessages (parameter).
     * @param unreadMessages
     */
    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    /**
     * Sets member variable "lastMessage" to given lastMessage (parameter).
     * @param lastMessage
     */
    public void setLastMessage(Message lastMessage)
    {
        this.lastMessage = lastMessage;
    }

    /**
     *
     * @return chat's lastMessage.
     */
    public Message getLastMessage() {
        return lastMessage;
    }

    /**
     *
     * @return The other user of the chat (not current user).
     */
    public User getUser() {
        return user;
    }

    /**
     *
     * @return Amount of unread messages in current chat.
     */
    public long getUnreadMessages() {
        return unreadMessages;
    }


}

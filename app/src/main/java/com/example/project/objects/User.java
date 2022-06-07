package com.example.project.objects;

import com.example.project.managers.ConstantsManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The user object class.
 */
public class User implements Serializable
{
    /**
     * The user's name.
     */
    private String username;
    /**
     * The user's password.
     */
    private String password;
    /**
     * The user's email.
     */
    private String email;
    /**
     * The user's phone.
     */
    private String phone;
    /**
     * The user's id.
     */
    private String userId;
    /**
     * The user's image.
     */
    private String userImage;
    /**
     * The user's description.
     */
    private String description;


    /**
     * An Empty constructor for firebase.
     */
    public User()
    {

    }

    /**
     * The Constructor sets the member fields to the given user's fields.
     * @param another - user object.
     */
    public User(User another)
    {
        this.userId = another.getUserId();
        this.username = another.getUsername();
        this.password = another.getPassword();
        this.email = another.getEmail();
        this.phone = another.getPhone();
        this.userImage = another.getUserImage();
        this.description = another.getDescription();
    }

    /**
     * Sets the member variables to the given parameters.
     * @param username
     * @param password
     * @param email
     * @param phone
     * @param userId
     * @param userImage
     * @param description
     */
    public User(String username, String password, String email, String phone, String userId, String userImage, String description)
    {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.userId = userId;
        this.userImage = userImage;
        this.description = description;
    }

    /**
     * @return a hashmap containing all users's fields - ordered by: (key, value).
     */
    public Map<String, Object> getUserInfo()
    {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put(ConstantsManager.USER_USER_ID_FIELD, this.userId);
        userInfo.put(ConstantsManager.USER_USERNAME_FIELD, this.username);
        userInfo.put(ConstantsManager.USER_PASSWORD_FIELD, this.password);
        userInfo.put(ConstantsManager.USER_EMAIL_FIELD, this.email);
        userInfo.put(ConstantsManager.USER_PHONE_FIELD, this.phone);
        userInfo.put(ConstantsManager.USER_USER_IMAGE_FIELD, this.userImage);
        userInfo.put(ConstantsManager.USER_DESCRIPTION_FIELD, this.description);

        return userInfo;
    }


    /**
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets member variable "description" to given description (parameter).
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets member variable "userId" to given userId (parameter).
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }
    /**
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }
    /**
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }
    /**
     *
     * @return phone
     */
    public String getPhone() {
        return phone;
    }
    /**
     *
     * @return userImage
     */
    public String getUserImage() {
        return userImage;
    }

    /**
     * Sets member variable "username" to given username (parameter).
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets member variable "password" to given password (parameter).
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets member variable "email" to given email (parameter).
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets member variable "phone" to given phone (parameter).
     * @param phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Sets member variable "userImage" to given userImage (parameter).
     * @param userImage
     */
    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }


}

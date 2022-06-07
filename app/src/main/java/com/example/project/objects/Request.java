package com.example.project.objects;

import android.content.Context;
import android.content.Intent;

import com.example.project.managers.ConstantsManager;
import com.example.project.managers.DateAndTimeManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The abstract Request object class.
 */
public abstract class Request implements Serializable
{
    /**
     * Request title.
     */
    protected String title;
    /**
     * User of request id.
     */
    protected String userId;
    /**
     * The subcategory which the request belongs to.
     */
    protected String subcategoryId;
    /**
     * The category which the request belongs to.
     */
    protected String categoryId;
    /**
     * The request's description.
     */
    protected String description;
    /**
     * The request's deadline.
     */
    protected long milliseconds;
    /**
     * The request's id.
     */
    protected String requestId;
    /**
     * The request's image.
     */
    protected String requestImage;
    /**
     * The request's state.
     */
    protected int state;
    /**
     * The request's type - online / offline.
     */
    protected int type;



    /**
     * An Empty constructor for firebase.
     */
    public Request()
    {

    }

    /**
     * Sets the member variables to the given parameters.
     * @param title
     * @param userId
     * @param subcategoryId
     * @param categoryId
     * @param description
     * @param milliseconds
     * @param requestId
     * @param requestImage
     * @param state
     */
    public Request(String title, String userId, String subcategoryId, String categoryId, String description, long milliseconds, String requestId, String requestImage, int state)
    {
        this.title = title;
        this.userId = userId;
        this.subcategoryId = subcategoryId;
        this.categoryId = categoryId;
        this.description = description;
        this.milliseconds = milliseconds;
        this.requestId = requestId;
        this.requestImage = requestImage;
        this.state = state;
        this.type = getType();
    }

    /**
     * @return a hashmap containing all request's fields - ordered by: (key, value).
     */
    public Map<String, Object> getRequestInfo()
    {
        Map<String, Object> requestInfo = new HashMap<>();
        requestInfo.put(ConstantsManager.REQUEST_TITLE_FIELD, title);
        requestInfo.put(ConstantsManager.REQUEST_USER_ID_FIELD, userId);
        requestInfo.put(ConstantsManager.REQUEST_SUBCATEGORY_ID_FIELD, subcategoryId);
        requestInfo.put(ConstantsManager.REQUEST_CATEGORY_ID_FIELD, categoryId);
        requestInfo.put(ConstantsManager.REQUEST_DESCRIPTION_FIELD, description);
        requestInfo.put(ConstantsManager.REQUEST_REQUEST_ID_FIELD, requestId);
        requestInfo.put(ConstantsManager.REQUEST_REQUEST_IMAGE_FIELD, requestImage);
        requestInfo.put(ConstantsManager.REQUEST_STATE_FIELD, state);
        requestInfo.put(ConstantsManager.REQUEST_MILLISECONDS_FIELD, milliseconds);

        return requestInfo;
    }
    /**
     * @return the state described by string - "PENDING", "PROCESSING", "DELIVERED".
     */
    public String getStateString()
    {
        return ConstantsManager.tabOptions[state + 1];
    }
    /**
     *
     * @return the state described by color - orange, black, green.
     */
    public int getStateColorInt()
    {
        return ConstantsManager.statesColors1[state + 1];
    }

    /**
     * @return the correct xml color by the request type described by a string.
     */
    public abstract String getColor();

    /**
     * @return the correct type of request described by an int.
     */
    public abstract int getType();

    /**
     * @param context - the context of the activity who called the function.
     * @return an intent of the request view (offline or online) in terms of the user view.
     */
    public abstract Intent createUserViewActivity(Context context);

    /**
     * @param context - the context of the activity who called the function.
     * @return an intent of the request view (offline or online) in terms of other users' view.
     */
    public abstract Intent createOthersViewActivity(Context context);

    /**
     * @return the type of request described in string - "Offline" / "Online".
     */
    public abstract String getTypeString();

    @Override
    public String toString()
    {
        return "Request: " + title + "\n" + "Deadline: " + DateAndTimeManager.getDateAndTimeString(milliseconds);

    }

    /**
     * @return requestImage
     */
    public String getRequestImage()
    {
        return requestImage;
    }

    /**
     * Sets member variable "requestImage" to given requestImage (parameter).
     * @param requestImage
     */
    public void setRequestImage(String requestImage)
    {
        this.requestImage = requestImage;
    }

    /**
     * @return milliseconds
     */
    public long getMilliseconds()
    {
        return milliseconds;
    }

    /**
     * Sets member variable "milliseconds" to given milliseconds (parameter).
     * @param milliseconds
     */
    public void setMilliseconds(long milliseconds)
    {
        this.milliseconds = milliseconds;
    }
    /**
     * Sets member variable "state" to given state (parameter).
     * @param state
     */
    public void setState(int state) {
        this.state = state;
    }
    /**
     * Sets member variable "type" to given type (parameter).
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }
    /**
     * @return state
     */
    public int getState() {
        return state;
    }
    /**
     * @return title
     */
    public String getTitle()
    {
        return title;
    }
    /**
     * @return userId
     */
    public String getUserId()
    {
        return userId;
    }
    /**
     * @return getSubcategoryId
     */
    public String getSubcategoryId()
    {
        return subcategoryId;
    }
    /**
     * @return categoryId
     */
    public String getCategoryId()
    {
        return categoryId;
    }
    /**
     * @return description
     */
    public String getDescription()
    {
        return description;
    }
    /**
     * @return requestId
     */
    public String getRequestId()
    {
        return requestId;
    }
    /**
     * Sets member variable "title" to given title (parameter).
     * @param title
     */
    public void setTitle(String title)
    {
        this.title = title;
    }
    /**
     * Sets member variable "userId" to given userId (parameter).
     * @param userId
     */
    public void setUserId(String userId)
    {
        this.userId = userId;
    }
    /**
     * Sets member variable "subcategoryId" to given subcategoryId (parameter).
     * @param subcategoryId
     */
    public void setSubcategoryId(String subcategoryId)
    {
        this.subcategoryId = subcategoryId;
    }
    /**
     * Sets member variable "categoryId" to given categoryId (parameter).
     * @param categoryId
     */
    public void setCategoryId(String categoryId)
    {
        this.categoryId = categoryId;
    }
    /**
     * Sets member variable "description" to given description (parameter).
     * @param description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Sets member variable "requestId" to given requestId (parameter).
     * @param requestId
     */
    public void setRequestId(String requestId)
    {
        this.requestId = requestId;
    }
}

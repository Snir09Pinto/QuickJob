package com.example.project.objects;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.project.activities.offerAndRequestViews.requestViews.OnlineRequestOthersViewActivity;
import com.example.project.activities.offerAndRequestViews.requestViews.OnlineRequestUserViewActivity;
import com.example.project.managers.ConstantsManager;

import java.util.Map;

/**
 * The online request class.
 */
public class OnlineRequest extends Request
{

    /**
     * A link attached to the online request.
     */
    private String link;

    /**
     * An empty constructor for firebase.
     */
    public OnlineRequest()
    {
        super();
    }

    /**
     * Sets the member variables to the given parameters - Calls super() - parent function.
     * @param title
     * @param userId
     * @param subcategoryId
     * @param categoryId
     * @param description
     * @param milliSeconds
     * @param requestId
     * @param requestImage
     * @param state
     * @param link
     */
    public OnlineRequest(String title, String userId, String subcategoryId, String categoryId, String description, long milliSeconds, String requestId, String requestImage, int state, String link) {
        super(title, userId, subcategoryId, categoryId, description, milliSeconds, requestId, requestImage, state);
        this.link = link;
    }

    /**
     * Overrides the parent getRequestInfo, adds the link field to the hashmap (including type of request).
     * @return the new hashmap.
     */
    @Override
    public Map<String, Object> getRequestInfo()
    {
        Map<String, Object> onlineRequestInfo =  super.getRequestInfo();
        onlineRequestInfo.put(ConstantsManager.REQUEST_TYPE_FIELD, getType());
        onlineRequestInfo.put(ConstantsManager.REQUEST_LINK_FIELD, link);
        return onlineRequestInfo;
    }


    /**
     * @return the online request xml color (string).
     */
    @Override
    public String getColor() {
        return ConstantsManager.ONLINE_REQUEST_COLOR;
    }

    /**
     * @return the online request type (int).
     */
    @Override
    public int getType() {
        return ConstantsManager.ONLINE_REQUEST_TYPE;
    }

    /**
     * @return the online request type (string).
     */
    @Override
    public String getTypeString()
    {
        return ConstantsManager.ONLINE_REQUEST_TYPE_STRING;
    }

    /**
     *
     * @param context - the context of the activity who called the function.
     * @return an intent contains the destination of OnlineRequestUserViewActivity.
     */
    public Intent createUserViewActivity(Context context)
    {
        Activity activity = new OnlineRequestUserViewActivity();
        Intent intent = new Intent(context, activity.getClass());
        intent.putExtra(ConstantsManager.REQUEST, this);
        return intent;
    }

    /**
     *
     * @param context - the context of the activity who called the function.
     * @return an intent contains the destination of OnlineRequestOthersViewActivity.
     */
    public Intent createOthersViewActivity(Context context)
    {
        Activity activity = new OnlineRequestOthersViewActivity();
        Intent intent = new Intent(context, activity.getClass());
        intent.putExtra(ConstantsManager.REQUEST, this);
        return intent;
    }

    /**
     * Sets member variable "link" to given link (parameter).
     * @param link
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     *
     * @return link.
     */
    public String getLink() {
        return link;
    }
}

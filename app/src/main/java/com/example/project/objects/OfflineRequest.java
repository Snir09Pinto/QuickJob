package com.example.project.objects;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.project.activities.offerAndRequestViews.requestViews.OfflineRequestOthersViewActivity;
import com.example.project.activities.offerAndRequestViews.requestViews.OfflineRequestUserViewActivity;
import com.example.project.managers.ConstantsManager;
import com.google.android.gms.maps.model.LatLng;

import java.util.Map;

/**
 * The offline request class.
 */
public class OfflineRequest extends Request
{
    /**
     * Two variables for the location of the offline request - longitude, latitude (GPS).
     */
    private double longitude, latitude;


    /**
     * An Empty constructor for firebase.
     */
    public OfflineRequest(){
        super();
    }

    /**
     * Sets the member variables to the given parameters.
     * Convert LatLng object to specific longitude and latitude (sets the members).
     * @param title
     * @param userId
     * @param subcategoryId
     * @param categoryId
     * @param description
     * @param milliSeconds
     * @param requestId
     * @param requestImage
     * @param state
     * @param latLng
     */
    public OfflineRequest(String title, String userId, String subcategoryId, String categoryId, String description, long milliSeconds, String requestId, String requestImage, int state, LatLng latLng) {
        super(title, userId, subcategoryId, categoryId, description, milliSeconds, requestId, requestImage, state);
        this.longitude = latLng.longitude;
        this.latitude = latLng.latitude;
    }

    /**
     * Overrides the parent getRequestInfo, adds the longitude and latitude fields to the hashmap (including type of request).
     * @return the new hashmap.
     */
    @Override
    public Map<String, Object> getRequestInfo()
    {
        Map<String, Object> offlineRequestInfo =  super.getRequestInfo();
        offlineRequestInfo.put(ConstantsManager.OFFLINE_REQUEST_LONGITUDE, longitude);
        offlineRequestInfo.put(ConstantsManager.OFFLINE_REQUEST_LATITUDE, latitude);
        offlineRequestInfo.put(ConstantsManager.REQUEST_TYPE_FIELD, getType());
        return offlineRequestInfo;
    }

    /**
     *
     * @return longitude
     */
    public double getLongitude()
    {
        return longitude;
    }
    /**
     *
     * @return latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets member variable "longitude" to given longitude (parameter).
     * @param longitude
     */
    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    /**
     * Sets member variable "latitude" to given latitude (parameter).
     * @param latitude
     */
    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }
    /**
     * @return the offline request xml color (string).
     */
    @Override
    public String getColor() {
        return ConstantsManager.OFFLINE_REQUEST_COLOR;
    }

    /**
     * @return the offline request type (int).
     */
    @Override
    public int getType() {
        return ConstantsManager.OFFLINE_REQUEST_TYPE;
    }

    /**
     * @return the online request type (string).
     */
    @Override
    public String getTypeString()
    {
        return ConstantsManager.OFFLINE_REQUEST_TYPE_STRING;
    }

    /**
     *
     * @param context - the context of the activity who called the function.
     * @return an intent contains the destination of OfflineRequestUserViewActivity.
     */
    public Intent createUserViewActivity(Context context)
    {
        Activity activity = new OfflineRequestUserViewActivity();
        Intent intent = new Intent(context, activity.getClass());
        intent.putExtra(ConstantsManager.REQUEST, this);
        return intent;
    }

    /**
     *
     * @param context - the context of the activity who called the function.
     * @return an intent contains the destination of OfflineRequestOthersViewActivity.
     */
    public Intent createOthersViewActivity(Context context)
    {
        Activity activity = new OfflineRequestOthersViewActivity();
        Intent intent = new Intent(context, activity.getClass());
        intent.putExtra(ConstantsManager.REQUEST, this);
        return intent;
    }

}

package com.example.project.objects;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.project.activities.offerAndRequestViews.offerViews.UserOfRequestViewActivity;
import com.example.project.activities.offerAndRequestViews.offerViews.UserOfOfferViewActivity;
import com.example.project.managers.ConstantsManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The offer object class.
 */
public class Offer implements Serializable
{
    /**
     * The user of offer id.
     */
    private String userId;
    /**
     * The request id which the offer belongs to.
     */
    private String requestId;
    /**
     * The offer id.
     */
    private String offerId;
    /**
     * The offer price.
     */
    private float price;
    /**
     * The offer description.
     */
    private String description;
    /**
     * The offer date and time (of creation).
     */
    private long milliseconds;
    /**
     * The offer state (pending, processing and delivered).
     */
    private int state;
    /**
     * The user of request and offer inputs (cancel, clear, mark as done).
     */
    private int userOfOfferInput, userOfRequestInput;

    /**
     * Sets the member variables to the given parameters.
     * @param userId
     * @param requestId
     * @param offerId
     * @param price
     * @param description
     * @param milliseconds
     * @param state
     * @param userOfOfferInput
     * @param userOfRequestInput
     */
    public Offer(String userId, String requestId, String offerId, float price, String description, long milliseconds, int state, int userOfOfferInput, int userOfRequestInput) {
        this.userId = userId;
        this.requestId = requestId;
        this.offerId = offerId;
        this.price = price;
        this.description = description;
        this.milliseconds = milliseconds;
        this.state = state;
        this.userOfOfferInput = userOfOfferInput;
        this.userOfRequestInput = userOfRequestInput;
    }

    /**
     * An Empty constructor for firebase.
     */
    public Offer()
    {

    }

    /**
     * @return a hashmap containing all offer's fields - ordered by: (key, value).
     */
    public Map<String, Object> getOfferInfo()
    {
        Map<String, Object> offerInfo = new HashMap<>();
        offerInfo.put(ConstantsManager.OFFER_USER_ID_FIELD, userId);
        offerInfo.put(ConstantsManager.OFFER_OFFER_ID_FIELD, offerId);
        offerInfo.put(ConstantsManager.OFFER_PRICE_FIELD, price);
        offerInfo.put(ConstantsManager.OFFER_DESCRIPTION_FIELD, description);
        offerInfo.put(ConstantsManager.OFFER_MILLISECONDS_FIELD, milliseconds);
        offerInfo.put(ConstantsManager.OFFER_STATE_FIELD, state);
        offerInfo.put(ConstantsManager.OFFER_REQUEST_ID_FIELD, requestId);
        offerInfo.put(ConstantsManager.OFFER_USER_OF_REQUEST_INPUT_FIELD, userOfRequestInput);
        offerInfo.put(ConstantsManager.OFFER_USER_OF_OFFER_INPUT_FIELD, userOfOfferInput);

        return offerInfo;
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
     *
     * @return the user of offer state described by color - dark green, black, bright green.
     */
    public int getUserOfOfferStateColor()
    {
        return ConstantsManager.statesColors2[userOfOfferInput + 1];
    }
    /**
     *
     * @return the user of request state described by color - dark green, black, bright green.
     */
    public int getUserOfRequestStateColor()
    {
        return ConstantsManager.statesColors2[userOfRequestInput + 1];
    }

    /**
     *
     * @param context - context of relevant activity.
     * @return an intent containing offer object and userOfRequestViewActivity.
     */
    public Intent createUserOfRequestViewIntent(Context context)
    {
        Activity activity = new UserOfRequestViewActivity();
        Intent intent = new Intent(context, activity.getClass());
        intent.putExtra(ConstantsManager.OFFER, this);
        return intent;
    }

    /**
     * @param context - context of relevant activity.
     * @return an intent containing offer object and UserOfOfferViewActivity.
     */
    public Intent createUserOfOfferViewIntent(Context context)
    {
        Activity activity = new UserOfOfferViewActivity();
        Intent intent = new Intent(context, activity.getClass());
        intent.putExtra(ConstantsManager.OFFER, this);
        return intent;
    }

    /**
     * Sets member variable "userId" to given userId (parameter).
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Sets member variable "requestId" to given userId (parameter).
     * @param requestId
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Sets member variable "offerId" to given userId (parameter).
     * @param offerId
     */
    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    /**
     * Sets member variable "price" to given price (parameter).
     * @param price
     */
    public void setPrice(float price) {
        this.price = price;
    }
    /**
     * Sets member variable "description" to given description (parameter).
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * Sets member variable "milliseconds" to given milliseconds (parameter).
     * @param milliseconds
     */
    public void setMilliseconds(long milliseconds) {
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
     * Sets member variable "userOfOfferInput" to given userOfOfferInput (parameter).
     * @param userOfOfferInput
     */
    public void setUserOfOfferInput(int userOfOfferInput) {
        this.userOfOfferInput = userOfOfferInput;
    }
    /**
     * Sets member variable "userOfRequestInput" to given userOfRequestInput (parameter).
     * @param userOfRequestInput
     */
    public void setUserOfRequestInput(int userOfRequestInput) {
        this.userOfRequestInput = userOfRequestInput;
    }


    /**
     *
     * @return userId
     */
    public String getUserId() {
        return userId;
    }
    /**
     *
     * @return requestId
     */
    public String getRequestId() {
        return requestId;
    }
    /**
     *
     * @return offerId
     */
    public String getOfferId() {
        return offerId;
    }
    /**
     *
     * @return price
     */
    public float getPrice() {
        return price;
    }
    /**
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }
    /**
     *
     * @return milliseconds
     */
    public long getMilliseconds() {
        return milliseconds;
    }
    /**
     *
     * @return state
     */
    public int getState() {
        return state;
    }
    /**
     *
     * @return userOfOfferInput
     */
    public int getUserOfOfferInput() {
        return userOfOfferInput;
    }
    /**
     *
     * @return userOfRequestInput
     */
    public int getUserOfRequestInput() {
        return userOfRequestInput;
    }

}

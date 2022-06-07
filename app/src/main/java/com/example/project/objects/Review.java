package com.example.project.objects;

import com.example.project.managers.ConstantsManager;

import java.util.HashMap;
import java.util.Map;

/**
 * The review object class.
 */
public class Review
{
    /**
     * The review id.
     */
    private String reviewId;
    /**
     * The review description.
     */
    private String description;
    /**
     * The review rating.
     */
    private float rating;
    /**
     * The review date and time (publication date).
     */
    private long milliseconds;
    /**
     * The offer id which "attached" to current review.
     */
    private String offerId;



    /**
     * An Empty constructor for firebase.
     */
    public Review(){

    }

    /**
     * Sets the member variables to the given parameters.
     * @param reviewId
     * @param description
     * @param rating
     * @param milliseconds
     * @param offerId
     */
    public Review(String reviewId, String description, float rating, long milliseconds, String offerId)
    {
        this.reviewId = reviewId;
        this.description = description;
        this.rating = rating;
        this.milliseconds = milliseconds;
        this.offerId = offerId;
    }

    /**
     * @return a hashmap containing all reviews's fields - ordered by: (key, value).
     */
    public Map<String, Object> getReviewInfo()
    {
        Map<String, Object> reviewInfo = new HashMap<>();
        reviewInfo.put(ConstantsManager.REVIEW_OFFER_ID_FIELD, offerId);
        reviewInfo.put(ConstantsManager.REVIEW_REVIEW_ID_FIELD, reviewId);
        reviewInfo.put(ConstantsManager.REVIEW_DESCRIPTION_FIELD, description);
        reviewInfo.put(ConstantsManager.REVIEW_RATING_FIELD, rating);
        reviewInfo.put(ConstantsManager.REVIEW_MILLISECONDS_FIELD, milliseconds);

        return reviewInfo;
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
     *
     * @return milliseconds
     */
    public long getMilliseconds() {
        return milliseconds;
    }
    /**
     * Sets member variable "reviewId" to given reviewId (parameter).
     * @param reviewId
     */
    public void setReviewId(String reviewId)
    {
        this.reviewId = reviewId;
    }

    /**
     * Sets member variable "description" to given description (parameter).
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * Sets member variable "rating" to given rating (parameter).
     * @param rating
     */
    public void setRating(float rating) {
        this.rating = rating;
    }

    /**
     *
     * @return reviewId
     */
    public String getReviewId() {
        return reviewId;
    }
    /**
     * Sets member variable "offerId" to given offerId (parameter).
     * @param offerId
     */
    public void setOfferId(String offerId)
    {
        this.offerId = offerId;
    }

    /**
     *
     * @return offerId
     */
    public String getOfferId()
    {
        return offerId;
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
     * @return rating
     */
    public float getRating() {
        return rating;
    }
}

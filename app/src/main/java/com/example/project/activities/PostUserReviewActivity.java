package com.example.project.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project.R;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.FirebaseManager;
import com.example.project.objects.Offer;
import com.example.project.objects.Review;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

public class PostUserReviewActivity extends AppCompatActivity implements View.OnClickListener
{
    /**
     * The review description edit text.
     */
    private EditText reviewDescriptionET;
    /**
     * The cancel button - cancel posting the review.
     */
    private Button cancelBtn;
    /**
     * The submit button - submitting the review.
     */
    private Button submitBtn;
    /**
     * The offer which belongs to the review.
     */
    private Offer offer;
    /**
     * The rating bar.
     */
    private RatingBar ratingBar;

    /**
     * Calls init().
     * Initializes the screen view.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_user_review_activity_layout);
        init();
    }

    /**
     * Initializes all the widgets + onClick for buttons.
     */
    private void init()
    {
        reviewDescriptionET = findViewById(R.id.request_description_edit_text);
        cancelBtn = findViewById(R.id.pural_cancel_button);
        submitBtn = findViewById(R.id.pural_submit_button);
        ratingBar = findViewById(R.id.pural_rating_bar);
        this.offer = (Offer) getIntent().getSerializableExtra(ConstantsManager.OFFER);

        cancelBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    /**
     * Handles cancel button + submit button.
     * Cancel button - exit the activity.
     * submit button - calls fieldsAreValid() and uploadReviewAndUpdateUI().
     * @param view
     */
    @Override
    public void onClick(View view)
    {
        if(view.getId() == cancelBtn.getId())
        {
            setResult(RESULT_CANCELED);
            finish();
        }

        else if(view.getId() == submitBtn.getId())
        {
            if(fieldsAreValid())
            {
                uploadReviewAndUpdateUI();
            }
        }
    }

    /**
     *
     * @return Whether the review's fields are valid.
     */
    private boolean fieldsAreValid()
    {
        if(reviewDescriptionET.getText().toString().isEmpty())
        {
            reviewDescriptionET.setError("Invalid description");
            return false;
        }
        return true;
    }


    /**
     * Creates a new review.
     * Calls uploadReview(review).
     */
    private void uploadReviewAndUpdateUI()
    {
        Review review = new Review("", reviewDescriptionET.getText().toString(), ratingBar.getRating(), System.currentTimeMillis(), offer.getOfferId());
        attemptToUploadReview(review);
    }


    /**
     * Takes the review object and tries to upload it to firebase database.
     */
    private void attemptToUploadReview(Review review)
    {
        FirebaseManager.getDBReference().collection(ConstantsManager.REVIEWS_COLLECTION).add(review.getReviewInfo())
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>()
                {
                    @Override
                    public void onSuccess(DocumentReference documentReference)
                    {
                        review.setReviewId(documentReference.getId());
                        documentReference.set(review.getReviewInfo());
                        setResult(RESULT_OK);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}

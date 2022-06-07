package com.example.project.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.adapters.ReviewRecyclerViewAdapter;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.FirebaseManager;
import com.example.project.managers.UserManager;
import com.example.project.objects.Offer;
import com.example.project.objects.Review;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * The class where the user sees all of his reviews.
 */
public class UserReviewsActivity extends AppCompatActivity
{
    /**
     * The reviews recycler view adapter.
     */
    private ReviewRecyclerViewAdapter reviewRecyclerViewAdapter;
    /**
     * The recycler view (widget).
     */
    private RecyclerView reviewsRV;
    /**
     * The reviews list.
     */
    private ArrayList<Review> reviews;
    /**
     * An interface for attaching and detaching the listener.
     */
    private ListenerRegistration registration;


    /**
     * Initializes the view and calls init().
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_reviews_activity_layout);
        init();
    }


    /**
     * Initializes all widgets and variables of this class.
     */
    private void init()
    {
        reviews = new ArrayList<>();
        reviewsRV = findViewById(R.id.ural_reviews_recycler_view);
        reviewRecyclerViewAdapter = new ReviewRecyclerViewAdapter(UserReviewsActivity.this, reviews);
        reviewsRV.setAdapter(reviewRecyclerViewAdapter);
    }

    /**
     * Attaches a listener to all reviews in app.
     */
    private void attachListener()
    {
        if(registration == null)
        {
            registration = FirebaseManager.getDBReference().collection(ConstantsManager.REVIEWS_COLLECTION)
                    .addSnapshotListener(reviewsEventListener);
        }
    }

    /**
     * Detaches the listener.
     */
    private void detachListener()
    {
        if(registration != null){
            registration.remove();
            registration = null;
        }
    }

    /**
     * Calls detachListener().
     * Calls onPause() parent function.
     */
    @Override
    public void onPause()
    {
        detachListener();
        super.onPause();
    }


    /**
     * Calls attachListener().
     * Calls onResume() parent function.
     */
    @Override
    public void onResume()
    {
        attachListener();
        super.onResume();
    }

    /**
     * Calls attachListener().
     * Calls onDestroy() parent function.
     */
    @Override
    public void onDestroy()
    {
        detachListener();
        super.onDestroy();
    }

    /**
     * An event listener to when a change in the reviews collection is made - calls updateList().
     */
    private final EventListener<QuerySnapshot> reviewsEventListener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
            if (error != null) return;

            if (value != null)
            {
                updateList();
            }
        }
    };

    /**
     * Goes through all user's offers and for each one checks if a review exists.
     * calls addReviewByOfferId(...) with offer id in case such review exists.
     */
        private void updateList()
        {
            reviews.clear();
            FirebaseManager.getDBReference().collection(ConstantsManager.OFFERS_COLLECTION)
                    .whereEqualTo(ConstantsManager.OFFER_USER_ID_FIELD, UserManager.getCurrentUser().getUserId())
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots == null) return;
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            Offer offer = documentSnapshot.toObject(Offer.class);
                            addReviewByOfferId(offer.getOfferId());
                        }
                    }
                }
            });
        }

    /**
     * Gets an offer id and searches in the reviews collection for such a review that is attached to that offer.
     * If exists then it adds the review to the reviews list.
     * @param offerId
     */
    private void addReviewByOfferId(String offerId)
        {
            FirebaseManager.getDBReference().collection(ConstantsManager.REVIEWS_COLLECTION)
                    .whereEqualTo(ConstantsManager.REVIEW_OFFER_ID_FIELD, offerId)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                {
                    if(queryDocumentSnapshots == null) return;
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            Review review = documentSnapshot.toObject(Review.class);
                            reviews.add(review);
                        }
                    }
                    reviewRecyclerViewAdapter.notifyDataSetChanged();
                }
            });
        }

}
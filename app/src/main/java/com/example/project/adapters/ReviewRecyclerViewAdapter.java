package com.example.project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.DateAndTimeManager;
import com.example.project.managers.FirebaseManager;
import com.example.project.managers.ImageManager;
import com.example.project.objects.Offer;
import com.example.project.objects.Review;
import com.example.project.objects.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * An adapter for handling the reviews list.
 */
public class ReviewRecyclerViewAdapter extends RecyclerView.Adapter
{
    /**
     * The relevant context.
     */
    private Context context;
    /**
     * The reviews list.
     */
    private ArrayList<Review> list;


    /**
     *
     * @param context - relevant context.
     * @param list - the reviews list (array list object).
     * Initializes context and list.
     */
    public ReviewRecyclerViewAdapter(Context context, ArrayList<Review> list)
    {
        this.context = context;
        this.list = list;
    }

    /**
     * @param parent - the view containing all child views.
     * @param viewType - the type of the view.
     * @return the correct view (view of review).
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item_cardview_layout, parent, false);
        if(view != null)
        {
            return new ReviewRecyclerViewAdapter.ReviewViewHolder(view);
        }
        return null;
    }

    /**
     * Calls bind() function for the current item's view.
     * @param holder - the view.
     * @param position - current position of item in list.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        ((ReviewRecyclerViewAdapter.ReviewViewHolder) holder).bind(position);
    }

    /**
     * @return size of reviews list.
     */
    @Override
    public int getItemCount()
    {
        return list.size();
    }


    /**
     * A class for a review view.
     */
    private class ReviewViewHolder extends RecyclerView.ViewHolder
    {
        /**
         * The user who wrote the review (author).
         */
        private ImageView authorIV;
        /**
         * The author's name.
         */
        private TextView authorUsernameTV;
        /**
         * The review description.
         */
        private TextView reviewDescriptionTV;
        /**
         * The review rating.
         */
        private TextView reviewRatingTV;
        /**
         * The review date and time.
         */
        private TextView reviewDateAndTimeTV;

        /**
         * Initializes the current item view widgets.
         * @param itemView - the view.
         */
        public ReviewViewHolder(@NonNull View itemView)
        {
            super(itemView);
            authorIV = itemView.findViewById(R.id.ricl_author_image_image_view);
            authorUsernameTV = itemView.findViewById(R.id.ricl_author_user_name_text_view);

            reviewDescriptionTV = itemView.findViewById(R.id.ricl_review_description_text_view);
            reviewRatingTV = itemView.findViewById(R.id.ricl_review_rating_text_view);
            reviewDateAndTimeTV = itemView.findViewById(R.id.ricl_review_date_and_time_text_view);
        }

        /**
         * @param position - current position of review in list.
         * Sets the information of the review (sets the view).
         * Calls setAuthorDetails(...)
         */
        public void bind(int position)
        {
            Review review = list.get(position);
            setAuthorDetails(review.getOfferId());
            reviewDescriptionTV.setText(review.getDescription());
            reviewRatingTV.setText("" + review.getRating());
            reviewDateAndTimeTV.setText(DateAndTimeManager.getDateAndTimeString(review.getMilliseconds()));
        }

        /**
         *
         * @param offerId
         * Sets the correct offer.
         * Calls getRequest(...) with that offer.
         */
        private void setAuthorDetails(String offerId)
        {
            FirebaseManager.getDBReference().collection(ConstantsManager.OFFERS_COLLECTION)
                    .whereEqualTo(ConstantsManager.OFFER_OFFER_ID_FIELD, offerId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot != null) {
                            Offer offer = documentSnapshot.toObject(Offer.class);
                            getRequest(offer.getRequestId());
                        }
                    }
                }
            });
        }

        /**
         *
         * @param requestId
         * Calls getUser(...) with the correct request from database.
         */
        private void getRequest(String requestId)
        {
            FirebaseManager.getDBReference().collection(ConstantsManager.REQUESTS_COLLECTION)
                    .whereEqualTo(ConstantsManager.REQUEST_REQUEST_ID_FIELD, requestId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot != null) {
                            getUser(documentSnapshot.getString(ConstantsManager.REQUEST_USER_ID_FIELD));
                        }
                    }
                }
            });
        }

        /**
         * Searches for the correct author in database.
         * Updates his details and widgets.
         * @param userId
         */
        private void getUser(String userId)
        {
            FirebaseManager.getDBReference().collection(ConstantsManager.USERS_COLLECTION).whereEqualTo(ConstantsManager.USER_USER_ID_FIELD, userId)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot != null) {
                            User user = documentSnapshot.toObject(User.class);
                            ImageManager.setImageViewString(context, user.getUserImage(), authorIV, R.drawable.profile_ic);
                            authorUsernameTV.setText(user.getUsername());
                        }
                    }
                }
            });
        }
    }
}

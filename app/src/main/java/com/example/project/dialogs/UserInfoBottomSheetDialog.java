package com.example.project.dialogs;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.activities.ChatActivity;
import com.example.project.adapters.ReviewRecyclerViewAdapter;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.FirebaseManager;
import com.example.project.managers.ImageManager;
import com.example.project.objects.Offer;
import com.example.project.objects.Review;
import com.example.project.objects.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A bottom sheet dialog (pops from bottom of screen) of user information.
 */
public class UserInfoBottomSheetDialog extends BottomSheetDialog implements View.OnClickListener
{
    /**
     * Relevant context.
     */
    private Context context;
    /**
     * The contact button - moves to a specific chat screen.
     */
    private Button contactBtn;
    /**
     * The user's reviews list.
     */
    private RecyclerView reviewsRV;
    /**
     * The user's reviews list adapter.
     */
    private ReviewRecyclerViewAdapter reviewRecyclerViewAdapter;
    /**
     * The user's name.
     */
    private TextView usernameBSDTV;
    /**
     * The user's rating.
     */
    private TextView userRatingBSDTV;
    /**
     * The user's description.
     */
    private TextView userDescriptionBSDTV;
    /**
     * The user's image.
     */
    private ImageView userIVBSDTV;
    /**
     * The user's object.
     */
    private User user;
    /**
     * The user's email.
     */
    private TextView emailTV;
    /**
     * The user's phone.
     */
    private TextView phoneTV;
    /**
     * The user's reviews actual list (array list).
     */
    private ArrayList<Review> reviews;

    /**
     * Initializes the context, current user and calls super(context) with the correct context (parent function).
     * @param context - relevant context.
     * @param user - the given user.
     */
    public UserInfoBottomSheetDialog(@NonNull Context context, User user)
    {
        super(context);
        this.context = context;
        this.user = user;
    }

    /**
     * Sets the dialog view and shows the dialog on the screen.
     * Calls initDialogWidgets().
     */
    public void showUserInfoBottomSheetDialog()
    {
        this.setContentView(R.layout.user_info_bottom_sheet_layout);
        this.setCancelable(true);
        initDialogWidgets();
        this.show();
    }

    /**
     * Initializing the widgets and variables of the dialog.
     * Calls updateList() and setUserOffers().
     */
    private void initDialogWidgets()
    {
        if(user != null)
        {
            reviews = new ArrayList<>();
            contactBtn = this.findViewById(R.id.uibsl_contact_button);
            reviewsRV = this.findViewById(R.id.uibsl_reviews_recycler_view);
            contactBtn.setOnClickListener(this);
            usernameBSDTV = this.findViewById(R.id.uibsl_user_name_text_view);
            userRatingBSDTV = this.findViewById(R.id.uibsl_user_rating_text_view);
            userIVBSDTV = this.findViewById(R.id.uibsl_user_image_image_view);
            userDescriptionBSDTV = this.findViewById(R.id.uibsl_user_description_text_view);
            phoneTV = this.findViewById(R.id.uibsl_user_phone_text_view);
            emailTV = this.findViewById(R.id.uibsl_user_email_text_view);

            reviewRecyclerViewAdapter = new ReviewRecyclerViewAdapter(getContext(), reviews);
            reviewsRV.setAdapter(reviewRecyclerViewAdapter);


            usernameBSDTV.setText(user.getUsername());
            ImageManager.setImageViewString(context, user.getUserImage(), userIVBSDTV, R.drawable.profile_ic);

            userDescriptionBSDTV.setText(user.getDescription());
            emailTV.setText(user.getEmail());
            phoneTV.setText(user.getPhone());

            setUserOffers();
            updateList();
        }
    }

    /**
     * Sets an array list of offers id to a new array list containing all delivered offers id related to such user
     * Calls setUserRating(...) with offers id list.
     */
    private void setUserOffers()
    {
        ArrayList<String> offersId = new ArrayList<>();
        FirebaseManager.getDBReference().collection(ConstantsManager.OFFERS_COLLECTION)
                .whereEqualTo(ConstantsManager.OFFER_USER_ID_FIELD, user.getUserId())
                .whereEqualTo(ConstantsManager.OFFER_STATE_FIELD, ConstantsManager.statesCodes[2])
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                    {
                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            if(documentSnapshot != null)
                            {
                                offersId.add(documentSnapshot.getString(ConstantsManager.OFFER_OFFER_ID_FIELD));
                            }
                        }
                        setUserRating(offersId);
                    }
                });
    }

    /**
     *
     * @param offersId - the list containing all delivered offers id related to such user.
     * The functions searches for each review if it belongs to one of the offers and sums up the rating.
     * Finally it does an average and by that calculating the user's rating.
     */
    private void setUserRating(ArrayList<String> offersId)
    {
        FirebaseManager.getDBReference().collection(ConstantsManager.REVIEWS_COLLECTION)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                /**
                 * Amount of reviews.
                 */
                int count = 0;
                /**
                 * Rating sum.
                 */
                float sum = 0;
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    Review review = documentSnapshot.toObject(Review.class);
                    if(existsInOffersIdList(review.getOfferId(), offersId))
                    {
                        count++;
                        sum += review.getRating();
                    }
                }
                if(count != 0)
                {
                    userRatingBSDTV.setText("" + String.format("%.2f", (sum / count)));
                }
                else{
                    userRatingBSDTV.setText("No rating");
                }
            }
        });
    }


    /**
     * @param offerId - specific offer id
     * @param offersId - the offers id list.
     * @return if offer id exists in the offers id list.
     */
    private boolean existsInOffersIdList(String offerId, ArrayList<String> offersId)
    {
        for(String currOfferId : offersId)
        {
            if(currOfferId.equals(offerId)) return true;
        }
        return false;
    }

    /**
     * Sets the contact button onClick listener.
     * @param view
     */
    @Override
    public void onClick(View view)
    {
       if(view.getId() == contactBtn.getId())
        {
            if(user != null)
            {
                /**
                 * Launches Chat activity with specific user.
                 */
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra(ConstantsManager.USER, user);
                context.startActivity(intent);
            }
        }
    }

    /**
     * Searches for all delivered offers related to such user and for each one calls addReviewByOfferId(...) with the specific offer.
     */
    private void updateList()
    {
        FirebaseManager.getDBReference().collection(ConstantsManager.OFFERS_COLLECTION)
                .whereEqualTo(ConstantsManager.OFFER_USER_ID_FIELD, user.getUserId())
                .whereEqualTo(ConstantsManager.OFFER_STATE_FIELD, ConstantsManager.statesCodes[2])
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    if(documentSnapshot != null)
                    {
                        Offer offer = documentSnapshot.toObject(Offer.class);
                        addReviewByOfferId(offer.getOfferId());
                    }
                }
            }
        });

    }

    /**
     * The function adds the review related to such offer id (if it exists obviously) - also updates the list.
     * @param offerId - the current offer's id
     */
    private void addReviewByOfferId(String offerId)
    {
        FirebaseManager.getDBReference().collection(ConstantsManager.REVIEWS_COLLECTION)
                .whereEqualTo(ConstantsManager.REVIEW_OFFER_ID_FIELD, offerId)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    if(documentSnapshot != null)
                    {
                        Review review = documentSnapshot.toObject(Review.class);
                        reviews.add(review);
                    }
                }
                reviewRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }





}

package com.example.project.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.project.activities.EditUserProfileActivity;
import com.example.project.activities.LoginActivity;
import com.example.project.activities.UserOffersActivity;
import com.example.project.activities.UserRequestsActivity;
import com.example.project.activities.UserReviewsActivity;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.FirebaseManager;
import com.example.project.managers.ImageManager;
import com.example.project.managers.UserManager;
import com.example.project.R;
import com.example.project.objects.Review;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A class for the user view (profile) - all user's details.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener
{
    /**
     * User's name
     */
    private TextView usernameTV;
    /**
     * User's phone
     */
    private TextView phoneTV;
    /**
     * User's email
     */
    private TextView emailTV;
    /**
     * User's completed requests (amount).
     */
    private TextView completedRequestsTV;
    /**
     * User's rating
     */
    private TextView ratingTV;
    /**
     * User's image
     */
    private ImageView userIV;

    /**
     * The view of the screen (xml).
     */
    private View view;
    /**
     * The user's requests button
     */
    private Button myRequestsBtn;
    /**
     * The user's offers button
     */
    private Button myOffersBtn;
    /**
     * The user's reviews button
     */
    private Button myReviewsBtn;
    /**
     * The edit profile button
     */
    private Button editProfileBtn;
    /**
     * The logout button
     */
    private Button logoutBtn;


    /**
     * Two interfaces that responsible for attaching and detaching the listeners.
     */
    private ListenerRegistration registration1, registration2;

    /**
     * Initializes the view and calls init().
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.profile_fragment_layout, container, false);
        init();
        return view;
    }

    /**
     * Calls initButtonsListeners().
     */
    public void init()
    {
        initButtonsListeners();
    }

    /**
     * Initializes the buttons and their onClick method.
     */
    private void initButtonsListeners()
    {
        myRequestsBtn = view.findViewById(R.id.pfl_user_requests_button);
        myOffersBtn = view.findViewById(R.id.pfl_user_offers_button);
        myReviewsBtn = view.findViewById(R.id.pfl_user_reviews_button);
        editProfileBtn = view.findViewById(R.id.pfl_user_edit_profile_button);
        logoutBtn = view.findViewById(R.id.pfl_logout_button);

        myRequestsBtn.setOnClickListener(this);
        myOffersBtn.setOnClickListener(this);
        myReviewsBtn.setOnClickListener(this);
        editProfileBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);

    }

    /**
     * Initializes the user's details and calls setUserWidgets().
     */
    private void initUserDetails()
    {

        usernameTV = view.findViewById(R.id.pfl_user_name_text_view);
        userIV = view.findViewById(R.id.pfl_user_image_image_view);
        phoneTV = view.findViewById(R.id.user_phone_text_view);
        emailTV = view.findViewById(R.id.user_email_text_view);
        completedRequestsTV = view.findViewById(R.id.pfl_user_completed_requests_text_view);
        ratingTV = view.findViewById(R.id.pfl_user_rating_text_view);


        setUserWidgets();
    }

    /**
     * Sets the user's widgets with his details.
     * Calls setUserRatingAndCompletedRequests().
     */
    private void setUserWidgets()
    {
        ImageManager.setImageViewString(getContext(), UserManager.getCurrentUser().getUserImage(), userIV, R.drawable.profile_ic);
        usernameTV.setText(UserManager.getCurrentUser().getUsername());
        phoneTV.setText(UserManager.getCurrentUser().getPhone());
        emailTV.setText(UserManager.getCurrentUser().getEmail());
        setUserRatingAndCompletedRequests();
    }

    /**
     * Searches in database for all delivered offers of current user and calculates the amount of completed requests.
     * Calls setUserRating(...) with the offersId list that contain all offers id.
     */
    private void setUserRatingAndCompletedRequests()
    {
        ArrayList<String> offersId = new ArrayList<>();
        FirebaseManager.getDBReference().collection(ConstantsManager.OFFERS_COLLECTION)
                .whereEqualTo(ConstantsManager.OFFER_USER_ID_FIELD, UserManager.getCurrentUser().getUserId())
                .whereEqualTo(ConstantsManager.OFFER_STATE_FIELD, ConstantsManager.statesCodes[2])
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                    {
                        if(queryDocumentSnapshots == null) return;
                        long count = 0;
                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            if(documentSnapshot != null && documentSnapshot.exists())
                            {
                                count++;
                                offersId.add(documentSnapshot.getString(ConstantsManager.OFFER_OFFER_ID_FIELD));
                            }
                        }
                        completedRequestsTV.setText(count + "");
                        setUserRating(offersId);
                    }
                });
    }

    /**
     * Takes offersId list and calculates the rating of the user with summing all the reviews ratings and dividing by the amount of those reviews.
     * @param offersId
     */
    private void setUserRating(ArrayList<String> offersId)
    {
        FirebaseManager.getDBReference().collection(ConstantsManager.REVIEWS_COLLECTION)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                if(queryDocumentSnapshots == null) return;
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
                    ratingTV.setText("" + String.format("%.2f", (sum / count)));
                }
                else{
                    ratingTV.setText("No rating");
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
     * Launches the login activity.
     */
    private void updateUI()
    {
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }


    /**
     * Sets the onClick methods of the buttons.
     * myRequestsBtn - goes to UserRequestsActivity.
     * myOffersBtn - goes to UserOffersActivity.
     * myReviewsBtn - goes to UserReviewsActivity.
     * editProfileBtn - goes to UserReviewsActivity
     * logoutBtn - goes to loginActivity - calls updateUI() and UserManager.logout().
     * @param view - the clicked view.
     */
    @Override
    public void onClick(View view)
    {
        if(view.getId() == myRequestsBtn.getId())
        {
            startActivity(new Intent(getContext(), UserRequestsActivity.class));
        }
        else if(view.getId() == myOffersBtn.getId())
        {
            startActivity(new Intent(getContext(), UserOffersActivity.class));
        }
        else if(view.getId() == myReviewsBtn.getId())
        {
            startActivity(new Intent(getContext(), UserReviewsActivity.class));
        }
        else if(view.getId() == editProfileBtn.getId())
        {
            startActivity(new Intent(getContext(), EditUserProfileActivity.class));
        }
        else if(view.getId() == logoutBtn.getId())
        {
            UserManager.logout();
            updateUI();
        }
    }

    /**
     * Attaches the listeners -
     */
    private void attachListeners()
    {
        if(registration1 == null)
        {
            registration1 = UserManager.getDocumentReference().addSnapshotListener(userInfoListener);
        }
        if(registration2 == null)
        {
            registration2 = FirebaseManager.getDBReference().collection(ConstantsManager.OFFERS_COLLECTION)
                    .whereEqualTo(ConstantsManager.OFFER_USER_ID_FIELD, UserManager.getCurrentUser().getUserId())
                    .whereEqualTo(ConstantsManager.OFFER_STATE_FIELD, ConstantsManager.statesCodes[2]).addSnapshotListener(offersListener);
        }
    }

    /**
     * Detaches the listeners.
     */
    private void detachListeners()
    {
        if(registration1 != null)
        {
            registration1.remove();
            registration1 = null;
        }
        if(registration2 != null)
        {
            registration2.remove();
            registration2 = null;
        }
    }

    /**
     * Calls detachListeners() and parent method - onPause().
     */
    @Override
    public void onPause()
    {
        detachListeners();
        super.onPause();
    }

    /**
     * Calls attachListeners() and parent method - onResume().
     */
    @Override
    public void onResume()
    {
        attachListeners();
        super.onResume();
    }


    /**
     * Calls detachListeners() and parent method - onDestroy().
     */
    @Override
    public void onDestroy()
    {
        detachListeners();
        super.onDestroy();
    }

    /**
     * Responsible for updating the user's details when changes occur in his document
     * Calls initUserDetails().
     */
    private final EventListener<DocumentSnapshot> userInfoListener = new EventListener<DocumentSnapshot>()
    {
        @Override
        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error)
        {
            if(error != null) return;

            if(value != null)
            {
                initUserDetails();
            }
        }
    };

    /**
     * Responsible for updating the user's details when changes occur in his offers.
     * Calls initUserDetails().
     */
    private final EventListener<QuerySnapshot> offersListener = new EventListener<QuerySnapshot>()
    {
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error)
        {
            if(error != null) return;

            if(value != null)
            {
                initUserDetails();
            }
        }
    };
}
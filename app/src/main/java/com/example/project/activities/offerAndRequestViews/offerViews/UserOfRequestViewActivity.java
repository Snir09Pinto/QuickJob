package com.example.project.activities.offerAndRequestViews.offerViews;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.project.R;
import com.example.project.activities.PostUserReviewActivity;
import com.example.project.dialogs.UserInfoBottomSheetDialog;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.DialogsManager;
import com.example.project.managers.FirebaseManager;
import com.example.project.managers.ImageManager;
import com.example.project.objects.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * UserOfRequest offer's view activity
 */
public class UserOfRequestViewActivity extends ParentOfferViewActivity implements View.OnClickListener
{
    /**
     * Variable that contain the answer to whether the offer was reviewed or not.
     */
    private boolean isReviewExist;

    /**
     * A layout reference to check onClick Events.
     */
    private ConstraintLayout userInfoView;

    /**
     * A dialog showing the user of offer details.
     */
    private UserInfoBottomSheetDialog userInfoBottomSheetDialog;

    /**
     * The user name text view.
     */
    private TextView usernameTV;

    /**
     * The user name image view.
     */
    private ImageView userIV;

    /**
     * Updates the user of request state to 0 (Mark as still processing).
     */
    @Override
    protected void updateUserInputWhenClearBtnIsPressed()
    {
        offerDR.update(ConstantsManager.OFFER_USER_OF_REQUEST_INPUT_FIELD, ConstantsManager.statesCodes[1]);
    }

    /**
     * Updates the user of request state to 1 (Mark as done).
     */
    @Override
    protected void updateUserInputWhenMarkAsDoneBtnIsPressed()
    {
        offerDR.update(ConstantsManager.OFFER_USER_OF_REQUEST_INPUT_FIELD, ConstantsManager.statesCodes[2]);
    }

    /**
     * Updates the user of request state to -1 (Mark as canceled).
     */
    @Override
    protected void updateUserInputWhenCancelBtnIsPressed()
    {
        offerDR.update(ConstantsManager.OFFER_USER_OF_REQUEST_INPUT_FIELD, ConstantsManager.statesCodes[0]);
    }


    /**
     *
     * @return User of request perspective.
     */
    @Override
    protected View getView()
    {
        return getLayoutInflater().inflate(R.layout.offer_user_of_request_view_activity_layout, null);
    }


    /**
     *
     * Initializes the class variables.
     * Calls initUserInfoBottomSheet()
     */
    @Override
    protected void prepare()
    {
        isReviewExist = false;
        userInfoView = view.findViewById(R.id.uorval_user_of_offer_info_constraint_layout);
        userInfoView.setOnClickListener(this);
        userIV = view.findViewById(R.id.uorval_user_of_offer_image_view);
        usernameTV = view.findViewById(R.id.uorval_user_of_offer_name_text_view);
        initUserInfoBottomSheet();
    }


    /**
     * Initializes the user information (xml widgets) and the userInfoBottomSheetDialog Object.
     */
    private void initUserInfoBottomSheet()
    {
        FirebaseManager.getDBReference().collection(ConstantsManager.USERS_COLLECTION)
                .whereEqualTo(ConstantsManager.USER_USER_ID_FIELD, offer.getUserId())
                .limit(1)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                if(queryDocumentSnapshots == null || queryDocumentSnapshots.isEmpty()) return;

                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    User user = documentSnapshot.toObject(User.class);
                    ImageManager.setImageViewString(UserOfRequestViewActivity.this, user.getUserImage(), userIV, R.drawable.profile_ic);
                    usernameTV.setText(user.getUsername());
                    userInfoBottomSheetDialog = new UserInfoBottomSheetDialog(UserOfRequestViewActivity.this, user);
                    return;
                }
            }
        });
    }


    /**
     * Update multi button color by states.
     * light green - offer isn't accepted yet. dark gray - offer is in process. light green or dark gray - offer is done.
     */
    @Override
    protected void updateMultiButtonColorByStates()
    {
        if(offer.getState() == ConstantsManager.statesCodes[0] && currentRequest.getState() != ConstantsManager.statesCodes[0])
        {
            multiBtn.setBackgroundTintList(getResources().getColorStateList(R.color.dark_gray));
        }
        else if(offer.getState() == ConstantsManager.statesCodes[0])
        {
            multiBtn.setBackgroundTintList(getResources().getColorStateList(R.color.light_green));
        }
        else if(offer.getState() == ConstantsManager.statesCodes[1])
        {
            multiBtn.setBackgroundTintList(getResources().getColorStateList(R.color.dark_gray));
        }
        else if(offer.getState() == ConstantsManager.statesCodes[2])
        {
            checkIfReviewExists();
        }
    }


    /**
     * Checks if there's a review attached to the current offer in database.
     * Updates the UI and isReviewExist accordingly.
     */
    private void checkIfReviewExists()
    {
        FirebaseManager.getDBReference().collection(ConstantsManager.REVIEWS_COLLECTION).whereEqualTo(ConstantsManager.REVIEW_OFFER_ID_FIELD, offer.getOfferId())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                if(queryDocumentSnapshots != null && queryDocumentSnapshots.isEmpty())
                {
                    multiBtn.setText("Post a review");
                    multiBtn.setBackgroundTintList(getResources().getColorStateList(R.color.light_green));
                    isReviewExist = false;
                }
                else
                {
                    multiBtn.setText("Post a review");
                    multiBtn.setBackgroundTintList(getResources().getColorStateList(R.color.dark_gray));
                    isReviewExist = true;
                }
            }
        });
    }


    /**
     * Checks if the view that was clicked is multiBtn, or userInfoView.
     * Updates UI or pops a dialog if that's the case.
     * @param view
     */
    @Override
    public void onClick(View view)
    {
        if(view.getId() == multiBtn.getId())
        {
            if(currentRequest != null && currentRequest.getState() == ConstantsManager.statesCodes[0])
            {
                showAcceptOfferAlertDialog();
            }
            else if(offer != null && currentRequest != null && offer.getState() == ConstantsManager.statesCodes[2] && currentRequest.getState() == ConstantsManager.statesCodes[2] && !isReviewExist)
            {
                /**
                 * Launches a new activity that the user can post a review on the other user (PostUserReviewActivity).
                 */
                Intent intent = new Intent(UserOfRequestViewActivity.this, PostUserReviewActivity.class);
                intent.putExtra(ConstantsManager.OFFER, this.offer);
                startActivityForResult(intent, ConstantsManager.POST_A_REVIEW_REQUEST_CODE);
            }
        }
        else if(view.getId() == userInfoView.getId())
        {
            userInfoBottomSheetDialog.showUserInfoBottomSheetDialog();
        }
    }


    /**
     * Pops a dialog to ask the user if he wants to accept the offer.
     * Updates offer and request states accordingly.
     */
    private void showAcceptOfferAlertDialog()
    {
        DialogInterface.OnClickListener positiveDialogInterface = new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                if(offerDR != null && requestDR != null  && currentRequest != null && currentRequest.getState() == ConstantsManager.statesCodes[0])
                {
                    requestDR.update(ConstantsManager.REQUEST_STATE_FIELD, ConstantsManager.statesCodes[1]);
                    offerDR.update(ConstantsManager.OFFER_STATE_FIELD, ConstantsManager.statesCodes[1]);
                }
                dialog.cancel();
            }
        };

        DialogInterface.OnClickListener negativeDialogInterface = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        };

        DialogsManager.showNegativeOrPositiveAlertDialog(UserOfRequestViewActivity.this,
                "Accept Offer", "Are you sure you want to accept this offer?",
                positiveDialogInterface, negativeDialogInterface);
    }


    /**
     *
     * @return the suitable user input.
     */
    @Override
    protected int getUserInput()
    {
        return offer.getUserOfRequestInput();
    }


    @Override
    protected void showRequestWasDeliveredDialog()
    {

    }


    @Override
    protected void showOfferDeletedDialog()
    {
            DialogInterface.OnClickListener
                    dialogInterface = new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    dialog.cancel();
                    finish();
                }
            };
            DialogsManager.showNeutralAlertDialog(UserOfRequestViewActivity.this,
                    "Offer deleted", "The other user just deleted their offer", dialogInterface);
    }

    @Override
    protected void showRequestDeletedDialog()
    {
        finish();
    }

    /**
     *
     * @param requestCode - checks if the code is equal to ConstantsManager.POST_A_REVIEW_REQUEST_CODE (activity for posting a review)
     * @param resultCode - checks if the user managed to return back successfully.
     * @param data - returns an intent contains information and possible to contain other information decided by the programmer.
     * Updates UI and isReviewExist accordingly.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ConstantsManager.POST_A_REVIEW_REQUEST_CODE)
        {
            if(resultCode == UserOfRequestViewActivity.RESULT_OK && multiBtn != null)
            {
                multiBtn.setBackgroundTintList(getResources().getColorStateList(R.color.dark_gray));
                isReviewExist = true;
            }
        }
    }
}


package com.example.project.activities.offerAndRequestViews.offerViews;

import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;

import com.example.project.R;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.DialogsManager;
import com.example.project.objects.OfflineRequest;
import com.example.project.objects.OnlineRequest;
import com.example.project.objects.Request;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * UserOfOffer offer's view activity
 */
public class UserOfOfferViewActivity extends ParentOfferViewActivity implements View.OnClickListener
{

    /**
     * A button for viewing the request details.
     */
    private Button viewRequestBtn;


    /**
     *
     * @return User of offer view.
     */
    @Override
    protected View getView()
    {
        return getLayoutInflater().inflate(R.layout.offer_user_of_offer_view_activity_layout, null);
    }


    /**
     * Sets the viewRequestBtn reference and listener.
     */
    @Override
    protected void prepare()
    {
        viewRequestBtn = view.findViewById(R.id.uooval_view_request_button);
        viewRequestBtn.setOnClickListener(this);
    }


    /**
     * Update multi button color by states.
     * dark green - offer isn't accepted yet. dark gray - offer is in process or done.
     */
    @Override
    protected void updateMultiButtonColorByStates()
    {
        if(offer.getState() == ConstantsManager.statesCodes[0])
        {
            multiBtn.setBackgroundTintList(getResources().getColorStateList(R.color.dark_green));
        }
        else if(offer.getState() == ConstantsManager.statesCodes[1])
        {
            multiBtn.setBackgroundTintList(getResources().getColorStateList(R.color.dark_gray));
        }
        else if(offer.getState() == ConstantsManager.statesCodes[2])
        {
            multiBtn.setBackgroundTintList(getResources().getColorStateList(R.color.dark_gray));
        }
    }


    /**
     * Updates the user of offer state to 0 (Mark as still processing).
     */
    @Override
    protected void updateUserInputWhenClearBtnIsPressed()
    {
        offerDR.update(ConstantsManager.OFFER_USER_OF_OFFER_INPUT_FIELD, ConstantsManager.statesCodes[1]);
    }

    /**
     * Updates the user of offer state to 1 (Mark as done).
     */
    @Override
    protected void updateUserInputWhenMarkAsDoneBtnIsPressed()
    {
        offerDR.update(ConstantsManager.OFFER_USER_OF_OFFER_INPUT_FIELD, ConstantsManager.statesCodes[2]);
    }

    /**
     * Updates the user of offer state to -1 (Mark as canceled).
     */
    @Override
    protected void updateUserInputWhenCancelBtnIsPressed()
    {
        offerDR.update(ConstantsManager.OFFER_USER_OF_OFFER_INPUT_FIELD, ConstantsManager.statesCodes[0]);
    }

    @Override
    protected void showRequestDeletedDialog()
    {
        DialogInterface.OnClickListener dialogInterface = new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
                finish();
            }
        };
        DialogsManager.showNeutralAlertDialog(UserOfOfferViewActivity.this,
                "Request deleted", "The other user just deleted their request", dialogInterface);
    }

    /**
     * Checks if the view that was clicked is multiBtn, or viewRequestBtn.
     * Updates UI or pops a dialog if that's the case, viewRequestBtn - opens new activity for watching the request details.
     * @param view
     */
    @Override
    public void onClick(View view)
    {
        if(view.getId() == multiBtn.getId())
        {
            if(offer != null && offer.getState() == ConstantsManager.statesCodes[0])
            {
                showDeleteOfferAlertDialog();
            }
        }
        if(view.getId() == viewRequestBtn.getId())
        {
            if(requestDR == null) return;

                requestDR.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if(documentSnapshot == null || !documentSnapshot.exists()) return;

                            Request request = null;
                            if((long) documentSnapshot.get(ConstantsManager.REQUEST_TYPE_FIELD) == (ConstantsManager.OFFLINE_REQUEST_TYPE))
                            {
                                request = (Request) documentSnapshot.toObject(OfflineRequest.class);
                            }
                            else if((long) documentSnapshot.get(ConstantsManager.REQUEST_TYPE_FIELD) == (ConstantsManager.ONLINE_REQUEST_TYPE))
                            {
                                request = (Request)  documentSnapshot.toObject(OnlineRequest.class);
                            }
                            /**
                             * Starting a new activity that shows the request details.
                             */
                            startActivity(request.createOthersViewActivity(UserOfOfferViewActivity.this));
                    }
                });
        }
    }


    @Override
    protected void showRequestWasDeliveredDialog()
    {
        //means that current user's offer wasn't accepted and the request is delivered
        if(offer != null && offer.getState() != ConstantsManager.statesCodes[2])
        {
            DialogInterface.OnClickListener dialogInterface = new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    dialog.cancel();
                    finish();
                }
            };

            DialogsManager.showNeutralAlertDialog(UserOfOfferViewActivity.this,
                    "Request was delivered successfully",
                    "The request was just delivered so your offer is getting deleted",
                    dialogInterface);
        }
    }


    /**
     * Pops a dialog that asks if the user wants to delete his offer.
     */
    private void showDeleteOfferAlertDialog()
    {
        DialogInterface.OnClickListener positiveDialogInterface = new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                if(offer != null && offerDR != null && offer.getState() == ConstantsManager.statesCodes[0])
                {
                    showOfferDeletedDialog();
                    deleteOffer();
                    dialog.cancel();
                    finish();
                }
                else
                {
                    dialog.cancel();
                }
            }
        };

        DialogInterface.OnClickListener negativeDialogInterface = new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        };

        DialogsManager.showNegativeOrPositiveAlertDialog(UserOfOfferViewActivity.this,
                "Delete Offer", "Are you sure you want to delete this offer?",
                positiveDialogInterface, negativeDialogInterface);
    }


    /**
     *
     * @return user of offer input.
     */
    @Override
    protected int getUserInput()
    {
        return offer.getUserOfOfferInput();
    }

    @Override
    protected void showOfferDeletedDialog()
    {
        finish();
    }


    /**
     * Deletes current offer from database and close the screen eventually (the listener is responsible for closing the screen).
     */
    private void deleteOffer()
    {
        offerDR.delete();
    }

}

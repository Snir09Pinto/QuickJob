package com.example.project.activities.offerAndRequestViews.offerViews;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.project.R;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.DateAndTimeManager;
import com.example.project.managers.DialogsManager;
import com.example.project.managers.FirebaseManager;
import com.example.project.managers.UserManager;
import com.example.project.objects.Offer;
import com.example.project.objects.OfflineRequest;
import com.example.project.objects.OnlineRequest;
import com.example.project.objects.Request;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Parent offer view activity (abstract class).
 */
public abstract class ParentOfferViewActivity extends Activity implements View.OnClickListener
{
    /**
     * Current offer.
     */
    protected Offer offer;

    /**
     * The xml view reference
     */
    protected View view;

    /**
     * The offer's description text view.
     */
    protected TextView offerDescriptionTV;

    /**
     * The offer's price text view.
     */
    protected TextView offerPriceTV;

    /**
     * The button for updating current user input as canceled.
     */
    protected Button cancelOfferBtn;

    /**
     * The button for updating current user input as done/finished.
     */
    protected Button markAsDoneBtn;

    /**
     * The button represents the user of offer input
     */
    protected RadioButton userOfOfferBtn;

    /**
     * The button represents the user of request input
     */
    protected RadioButton userOfRequestBtn;

    /**
     * The button for updating current user input to "still processing / doing the service".
     */
    protected Button clearBtn;

    /**
     * The button has multi functionalities - deleting offer, accepting offer or posting a review after offer is finished
     */
    protected Button multiBtn;

    /**
     * The Count down timer text view.
     */
    protected TextView countDownTimerTV;

    /**
     * The Count down timer image view.
     */
    protected ImageView countDownTimerIV;

    /**
     * The Count down timer.
     */
    protected CountDownTimer countDownTimer;

    /**
     * The offer reference in database.
     */
    protected DocumentReference offerDR;

    /**
     * The request reference in database.
     */
    protected DocumentReference requestDR;

    /**
     * A variable for whether the timer is running or not.
     */
    protected boolean timerIsRunning;

    /**
     * responsible to attach and detach the database listeners - offerDR and requestDR (interface)
     */
    protected ListenerRegistration registration1, registration2;


    /**
     * The current request which the offer belongs to
     */
    protected Request currentRequest;

    /**
     *
     * Initialize the activity view ,offer's value, timeIsRunning variable.
     * Calls setOfferReferenceAndObject().
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.offer = (Offer) getIntent().getSerializableExtra(ConstantsManager.OFFER);

        timerIsRunning = false;

        view = getView();

        setContentView(view);

        setOfferReference();
    }


    /**
     *
     * An abstract function.
     * @return The according view - user of request perspective / user of offer perspective.
     */
    protected abstract View getView();


    /**
     *
     * Initialize the uncommon widgets and variables that exist in UserOfOfferViewActivity and UserOfRequestViewActivity.
     */
    protected abstract void prepare();


    /**
     * Initialize the common widgets and variables that are both in UserOfOfferViewActivity and UserOfRequestViewActivity.
     * Calls setButtonsListeners().
     */
    private void initBaseWidgets()
    {
        offerDescriptionTV = view.findViewById(R.id.pova_offer_description_text_view);
        offerPriceTV = view.findViewById(R.id.pova_offer_price_text_view);
        cancelOfferBtn = view.findViewById(R.id.pova_cancel_offer_button);
        markAsDoneBtn = view.findViewById(R.id.pova_mark_offer_as_done_button);
        userOfOfferBtn = view.findViewById(R.id.pova_user_of_offer_state_radio_button);
        userOfRequestBtn = view.findViewById(R.id.pova_user_of_request_state_radio_button);
        clearBtn = view.findViewById(R.id.pova_clear_button);
        countDownTimerIV = view.findViewById(R.id.pova_countdown_timer_image_view);
        countDownTimerTV = view.findViewById(R.id.pova_countdown_timer_text_view);
        multiBtn = view.findViewById(R.id.pova_multi_button);

        multiBtn.setOnClickListener(this);

        offerDescriptionTV.setText(offer.getDescription());
        offerPriceTV.setText(offer.getPrice() + "");

        offerDescriptionTV.setMovementMethod(new ScrollingMovementMethod());

        setButtonsListeners();
    }

    /**
     * Searches in database and initializes the offerDR.
     * Calls initRequestReference().
     */
    private void setOfferReference()
    {
        FirebaseManager.getDBReference().collection(ConstantsManager.OFFERS_COLLECTION).whereEqualTo(ConstantsManager.OFFER_OFFER_ID_FIELD, offer.getOfferId())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    if(documentSnapshot != null && documentSnapshot.exists())
                    {
                        offerDR = documentSnapshot.getReference();
                        initRequestReferenceAndObject();
                        return;
                    }
                }
            }
        });
    }


    /**
     *  Searches in database and initializes the requestDR (the request that is related to current offer).
     *  Calls initBaseWidgets(), prepare() and attachListener() - to avoid edge case that the listener isn't attached.
     */
    private void initRequestReferenceAndObject()
    {
        FirebaseManager.getDBReference().collection(ConstantsManager.REQUESTS_COLLECTION).whereEqualTo(ConstantsManager.REQUEST_REQUEST_ID_FIELD, offer.getRequestId())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    if(queryDocumentSnapshots == null) return;

                    if(documentSnapshot != null && documentSnapshot.exists())
                    {
                        requestDR = documentSnapshot.getReference();
                        if((long) documentSnapshot.get(ConstantsManager.REQUEST_TYPE_FIELD) == (ConstantsManager.OFFLINE_REQUEST_TYPE))
                        {
                            currentRequest = documentSnapshot.toObject(OfflineRequest.class);
                        }
                        else if((long) documentSnapshot.get(ConstantsManager.REQUEST_TYPE_FIELD) == (ConstantsManager.ONLINE_REQUEST_TYPE))
                        {
                            currentRequest = documentSnapshot.toObject(OnlineRequest.class);
                        }
                        initBaseWidgets();
                        prepare();
                        attachListeners();
                        break;
                    }
                }
            }
        });
    }



    /**
     * Sets the button listeners (cancel, clear, markAsDone) - have same functionality unlike multi button.
     */
    private void setButtonsListeners()
    {
        cancelOfferBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(offer == null || currentRequest == null) return;

                if(offer.getState() == ConstantsManager.statesCodes[1] && getUserInput() != ConstantsManager.statesCodes[0])
                {
                    showCancelOfferAlertDialog();
                    return;
                }
            }
        });

        markAsDoneBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(offer == null || currentRequest == null) return;

                if(offer.getState() == ConstantsManager.statesCodes[1]  && getUserInput() != ConstantsManager.statesCodes[2])
                {
                    showMarkAsDoneOfferAlertDialog();
                    return;
                }
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(offer == null || currentRequest == null) return;

                if(offer.getState() == ConstantsManager.statesCodes[1] && getUserInput() != ConstantsManager.statesCodes[1])
                {
                    updateUserInputWhenClearBtnIsPressed();
                    return;
                }
            }
        });

    }


    /**
     * Update multi button color by states.
     */
    protected abstract void updateMultiButtonColorByStates();


    /**
     * Update user state after "mark as done" button is pressed.
     */
    protected abstract void updateUserInputWhenMarkAsDoneBtnIsPressed();


    /**
     * Update user state after "cancel" button is pressed.
     */
    protected abstract void updateUserInputWhenCancelBtnIsPressed();


    /**
     * Update user state after "clear" button is pressed.
     */
    protected abstract void updateUserInputWhenClearBtnIsPressed();


    /**
     * user input (represented by an int [-1,0,1]).
     *
     * @return the user input
     */
    protected abstract int getUserInput();

    /**
     * Pops a dialog notifying the user that the other user has deleted the current offer.
     */
    protected abstract void showOfferDeletedDialog();


    /**
     * Pops a dialog notifying the user that the other user has deleted the current request.
     */
    protected abstract void showRequestDeletedDialog();


    /**
     * Shows all other users that made an offer that the request was delivered successfully.
     */
    protected abstract void showRequestWasDeliveredDialog();

    /**
     * Pops a dialog asking the user if he wants to mark the offer as done.
     */
    private void showMarkAsDoneOfferAlertDialog()
    {
        DialogInterface.OnClickListener positiveDialogInterface = new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                if(offer == null || currentRequest == null) return;
                if(offer.getState() == ConstantsManager.statesCodes[1]  && getUserInput() != ConstantsManager.statesCodes[2] && currentRequest.getState() == ConstantsManager.statesCodes[1])
                {
                    updateUserInputWhenMarkAsDoneBtnIsPressed();
                }
                dialog.cancel();
            }
        };

        DialogInterface.OnClickListener negativeDialogInterface = new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        };

        DialogsManager.showNegativeOrPositiveAlertDialog(ParentOfferViewActivity.this,
                "Offer Finished", "Are you sure you want to mark this offer as done?",
                positiveDialogInterface, negativeDialogInterface);
    }


    /**
     * Pops a dialog asking the user if he wants to mark the offer as canceled.
     */
    private void showCancelOfferAlertDialog()
    {
        DialogInterface.OnClickListener positiveDialogInterface = new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                if(offer == null || currentRequest == null) return;

                if(offer.getState() == ConstantsManager.statesCodes[1] && getUserInput() != ConstantsManager.statesCodes[0] && currentRequest.getState() == ConstantsManager.statesCodes[1])
                {
                    updateUserInputWhenCancelBtnIsPressed();
                }
                dialog.dismiss();
            }
        };

        DialogInterface.OnClickListener negativeDialogInterface = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        };

        DialogsManager.showNegativeOrPositiveAlertDialog(ParentOfferViewActivity.this,
                "Cancel Offer", "Are you sure you want to cancel this offer?",
                positiveDialogInterface, negativeDialogInterface);
    }



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Try to start the countdown timer.
     */
    protected void tryToStartCountdownTimer()
    {
        if(timerIsRunning || requestDR == null) return;

            requestDR.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
            {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot)
                {
                    if(documentSnapshot != null && documentSnapshot.exists())
                    {
                        checkValidity(documentSnapshot);
                    }
                }
            });
    }

    /**
     * Try to stop the countdown timer.
     */
    protected void tryToStopCountdownTimer()
    {
        if(timerIsRunning && countDownTimerTV != null)
        {
            timerIsRunning = false;
            if(countDownTimer != null)
            {
                countDownTimer.cancel();
            }
            countDownTimerTV.setText(null);
        }
    }


    /**
     * Gets the Document snapshot (an object reference that is in the database - request).
     * Checks if it's valid to continue the timer (if deadline hasn't passed yet). if it does - continue, otherwise sets the timer 0d, 0h, 0m, 0s (highlighted in red).
     * @param documentSnapshot
     */
    private void checkValidity(DocumentSnapshot documentSnapshot)
    {
        long requestMillis = documentSnapshot.getLong(ConstantsManager.REQUEST_MILLISECONDS_FIELD);
        long millisUntilFinished = requestMillis - System.currentTimeMillis();
        //still didn't pass deadline
        if(millisUntilFinished > 0)
        {
            startCountdownTimer(millisUntilFinished);
        }
        else
        {
            timerIsRunning = true;
            highlightCountdownTimerTV(R.color.red);
            countDownTimerTV.setText(DateAndTimeManager.getDetailedTimeString(0));
        }
    }


    /**
     * Starts the countdown timer with given milliseconds (remaining time till deadline)
     * @param millisUntilFinished
     */
    private void startCountdownTimer(long millisUntilFinished)
    {
        if(countDownTimerTV == null) return;

        timerIsRunning = true;
        countDownTimer = new CountDownTimer(millisUntilFinished, 1000)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                highlightCountdownTimerTV(R.color.dark_gray);
                countDownTimerTV.setText(DateAndTimeManager.getDetailedTimeString(millisUntilFinished));
            }

            @Override
            public void onFinish()
            {
                highlightCountdownTimerTV(R.color.red);
                countDownTimerTV.setText(DateAndTimeManager.getDetailedTimeString(0));
            }
        }.start();

    }



    /**
     * Highlights the countdown timer text view with a given resource id.
     * @param resourceId
     */
    private void highlightCountdownTimerTV(int resourceId)
    {
        countDownTimerTV.setTextColor(getResources().getColorStateList(resourceId));
    }


    protected void deleteAllOtherOffers()
    {
        if(currentRequest == null || offerDR == null) return;

        FirebaseManager.getDBReference().collection(ConstantsManager.OFFERS_COLLECTION).whereEqualTo(ConstantsManager.OFFER_REQUEST_ID_FIELD, currentRequest.getRequestId())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                    {
                        if(queryDocumentSnapshots == null) return;
                        /**
                         * delete all other offers if none was found as accepted
                         */
                        showRequestWasDeliveredDialog();
                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                        {
                            if(documentSnapshot != null && documentSnapshot.exists() && (long) documentSnapshot.get(ConstantsManager.OFFER_STATE_FIELD) != ConstantsManager.statesCodes[2])
                            {
                                DocumentReference documentReference = documentSnapshot.getReference();
                                documentReference.delete();
                            }
                        }
                    }
                });
    }


    /**
     * Checks for all possible changes and updates UI and variables accordingly.
     */
    private void checkForChangesAndUpdate()
    {
        if(offerDR == null || requestDR == null || offer == null || currentRequest == null) return;

        updateMultiButtonColorByStates();
        //PENDING STATE
        if(offer.getState() == ConstantsManager.statesCodes[0])
        {
            tryToStopCountdownTimer();
            cancelOfferBtn.setBackgroundTintList(getResources().getColorStateList(R.color.dark_gray));
            markAsDoneBtn.setBackgroundTintList(getResources().getColorStateList(R.color.dark_gray));
            clearBtn.setBackgroundTintList(getResources().getColorStateList(R.color.dark_gray));
            userOfRequestBtn.setButtonTintList(getResources().getColorStateList(R.color.dark_gray));
            userOfOfferBtn.setButtonTintList(getResources().getColorStateList(R.color.dark_gray));
        }
        else if(offer.getState() == ConstantsManager.statesCodes[1])
        {
            if(offer.getUserOfOfferInput() == offer.getUserOfRequestInput() && getUserInput() != ConstantsManager.statesCodes[1])
            {
                //OFFER IS CANCELED OR FINISHED

                //CANCELED
                if(getUserInput() == ConstantsManager.statesCodes[0])
                {
                    offerDR.update(ConstantsManager.OFFER_USER_OF_REQUEST_INPUT_FIELD, ConstantsManager.statesCodes[1]);
                    offerDR.update(ConstantsManager.OFFER_USER_OF_OFFER_INPUT_FIELD, ConstantsManager.statesCodes[1]);
                    requestDR.update(ConstantsManager.REQUEST_STATE_FIELD, ConstantsManager.statesCodes[0]);
                    offerDR.update(ConstantsManager.OFFER_STATE_FIELD, ConstantsManager.statesCodes[0]);
                }
                //FINISHED
                else
                {
                    offerDR.update(ConstantsManager.OFFER_USER_OF_REQUEST_INPUT_FIELD, ConstantsManager.statesCodes[2]);
                    offerDR.update(ConstantsManager.OFFER_USER_OF_OFFER_INPUT_FIELD, ConstantsManager.statesCodes[2]);
                    requestDR.update(ConstantsManager.REQUEST_STATE_FIELD, ConstantsManager.statesCodes[2]);
                    offerDR.update(ConstantsManager.OFFER_STATE_FIELD, ConstantsManager.statesCodes[2]);
                }
            }
            else
            {
                tryToStartCountdownTimer();
                cancelOfferBtn.setBackgroundTintList(getResources().getColorStateList(R.color.dark_green));
                markAsDoneBtn.setBackgroundTintList(getResources().getColorStateList(R.color.light_green));
                clearBtn.setBackgroundTintList(getResources().getColorStateList(R.color.black));
                userOfRequestBtn.setButtonTintList(getResources().getColorStateList(offer.getUserOfRequestStateColor()));
                userOfOfferBtn.setButtonTintList(getResources().getColorStateList(offer.getUserOfOfferStateColor()));
            }
        }
        else if(offer.getState() == ConstantsManager.statesCodes[2])
        {
            tryToStopCountdownTimer();
            cancelOfferBtn.setBackgroundTintList(getResources().getColorStateList(R.color.dark_gray));
            markAsDoneBtn.setBackgroundTintList(getResources().getColorStateList(R.color.dark_gray));
            clearBtn.setBackgroundTintList(getResources().getColorStateList(R.color.dark_gray));
            userOfRequestBtn.setButtonTintList(getResources().getColorStateList(offer.getUserOfRequestStateColor()));
            userOfOfferBtn.setButtonTintList(getResources().getColorStateList(offer.getUserOfOfferStateColor()));
            deleteAllOtherOffers();
        }
    }

    /**
     * Firebase listener that is responsible for updating UI and current request's value when changes occur.
     */
    private final EventListener<DocumentSnapshot> requestEventListener = new EventListener<DocumentSnapshot>()
    {
        @Override
        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error)
        {
            if(error != null) return;


            if(value != null)
            {
                if(!value.exists())
                {
                    showRequestDeletedDialog();
                    return;
                }

                if((long) value.get(ConstantsManager.REQUEST_TYPE_FIELD) == (ConstantsManager.OFFLINE_REQUEST_TYPE))
                {
                    currentRequest = value.toObject(OfflineRequest.class);
                }
                else if((long) value.get(ConstantsManager.REQUEST_TYPE_FIELD) == (ConstantsManager.ONLINE_REQUEST_TYPE))
                {
                    currentRequest = value.toObject(OnlineRequest.class);
                }
            }
        }
    };


    /**
     * Firebase listener that is responsible for updating UI and current offer's value when changes occur.
     */
    private final EventListener<DocumentSnapshot> offerEventListener = new EventListener<DocumentSnapshot>()
    {
        @Override
        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error)
        {
            if(error != null) return;

            if(value != null)
            {
                if(!value.exists())
                {
                    if(offer != null && currentRequest != null && offer.getState() == ConstantsManager.statesCodes[0] && currentRequest.getState() == ConstantsManager.statesCodes[2])
                    {
                        showRequestWasDeliveredDialog();
                    }
                    else
                    {
                        showOfferDeletedDialog();
                    }
                    return;
                }
                offer = value.toObject(Offer.class);
                checkForChangesAndUpdate();
            }
        }
    };

    /**
     * Attaches the offer listener (checks for changes in offer document that is in database).
     */
    private void attachListeners()
    {
        if(registration1 == null && offerDR != null)
        {
            registration1 = offerDR.addSnapshotListener(offerEventListener);
        }
        if(registration2 == null && requestDR != null)
        {
            registration2 = requestDR.addSnapshotListener(requestEventListener);
        }
    }

    /**
     * Detaches the listener (maintain efficiency).
     */
    private void detachListeners()
    {
        if(registration1 != null && offerDR != null)
        {
            registration1.remove();
            registration1 = null;
        }
        if(registration2 != null && requestDR != null)
        {
            registration2.remove();
            registration2 = null;
        }
    }


    /**
     * Calls onResume() - parent function.
     * Calls attachListener().
     */
    @Override
    protected void onResume()
    {
        attachListeners();
        super.onResume();
    }

    /**
     * Calls onPause() - parent function.
     * Calls detachListener().
     */
    @Override
    protected void onPause()
    {
        detachListeners();
        super.onPause();
    }

    /**
     * Calls onDestroy() - parent function.
     * Calls detachListener().
     */
    @Override
    protected void onDestroy()
    {
        detachListeners();
        super.onDestroy();
    }

}

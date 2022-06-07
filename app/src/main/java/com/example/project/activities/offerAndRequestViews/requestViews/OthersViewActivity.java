package com.example.project.activities.offerAndRequestViews.requestViews;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.project.R;
import com.example.project.activities.CreateOfferActivity;
import com.example.project.activities.offerAndRequestViews.offerViews.ParentOfferViewActivity;
import com.example.project.dialogs.UserInfoBottomSheetDialog;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.DateAndTimeManager;
import com.example.project.managers.DialogsManager;
import com.example.project.managers.FirebaseManager;
import com.example.project.managers.ImageManager;
import com.example.project.managers.UserManager;
import com.example.project.objects.Offer;
import com.example.project.objects.OfflineRequest;
import com.example.project.objects.OnlineRequest;
import com.example.project.objects.Request;
import com.example.project.objects.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Other users perspective of the request activity
 *
 * @param <RequestType> the type parameter - type of request (Offline or Online)
 */
public abstract class OthersViewActivity<RequestType extends Request> extends Activity implements View.OnClickListener
{


    /**
     * The Request - Offline request / Online request.
     */
    protected RequestType request;

    /**
     * The xml view reference.
     */
    protected View view;

    /**
     * The request image view.
     */
    private ImageView requestIV;

    /**
     * The user image view.
     */
    private ImageView userIV;

    /**
     * The request title text view.
     */
    private TextView requestTitleTV;
    /**
     * The user name text view.
     */
    private TextView usernameTV;
    /**
     * The request description text view.
     */
    private TextView requestDescriptionTV;
    /**
     * The request date and time text view (milliseconds).
     */
    private TextView requestDateAndTimeTV;
    /**
     * The text view of the amount of offer that the request already has.
     */
    private TextView offersQuantityTV;
    /**
     * A button for creating or canceling an offer (realtime).
     */
    private Button createOrCancelOfferBtn;
    /**
     * A layout for opening the UserInfoBottomSheetDialog when it's clicked.
     */
    private ConstraintLayout userInfoView;
    /**
     * A dialog that shows the user of request information.
     */
    private UserInfoBottomSheetDialog userInfoBottomSheetDialog;
    /**
     * An object of the current user of request.
     */
    private User userOfRequest;
    /**
     * An object of the current offer.
     */
    private Offer currentOffer;
    /**
     * A reference for the offer in the database (for listening to realtime changes).
     */
    private DocumentReference offerDR;
    /**
     * Two interfaces responsible of attaching and detaching the offer and request listeners.
     */
    private ListenerRegistration registration1, registration2, registration3;

    /**
     * The request reference in database (for listening to realtime changes).
     */
    protected DocumentReference requestDR;


    /**
     * Initialize the current request, sets the screen view.
     * Calls initBaseWidgets().
     * @param savedInstanceState
     */
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.request = (RequestType) getIntent().getSerializableExtra(ConstantsManager.REQUEST);

        view = getView();

        setContentView(view);

        setRequestReference();

    }

    /**
     *
     * @return the suitable view - offline request type of view / online request type of view.
     */
    protected abstract View getView();

    /**
     *
     * Initialize the uncommon widgets and variables that exist in OfflineRequestOthersViewActivity and OnlineRequestOthersViewActivity.
     */
    protected abstract void prepare();

    /**
     * Initialize the common widgets and variables of both OfflineRequestOthersViewActivity and OnlineRequestOthersViewActivity.
     * Calls setAmountOfOffers(), applyUserDetails(request.getUserId()), setRequestReference(), updateOfferCreated().
     */
    private void initBaseWidgets()
    {
        requestIV = view.findViewById(R.id.ova_request_image_image_view);
        userIV = view.findViewById(R.id.ova_user_of_request_image_image_view);
        requestTitleTV = view.findViewById(R.id.ova_request_title_text_view);
        usernameTV = view.findViewById(R.id.ova_user_of_request_name_text_view);
        requestDescriptionTV = view.findViewById(R.id.ova_request_description_text_view);
        requestDateAndTimeTV = view.findViewById(R.id.ova_request_date_and_time_text_view);
        createOrCancelOfferBtn = view.findViewById(R.id.ova_create_or_cancel_offer_button);
        userInfoView = view.findViewById(R.id.ova_user_of_request_info_constraint_layout);
        offersQuantityTV = view.findViewById(R.id.ova_offers_quantity_text_view);
        createOrCancelOfferBtn.setOnClickListener(this);
        userInfoView.setOnClickListener(this);

        ImageManager.setImageViewString(OthersViewActivity.this, request.getRequestImage(), requestIV, R.drawable.gallery_icon);
        requestTitleTV.setText(request.getTitle());
        requestDescriptionTV.setText(request.getDescription());
        requestDateAndTimeTV.setText(DateAndTimeManager.getDateAndTimeString(request.getMilliseconds()));

        applyUserDetails(request.getUserId());
        prepare();

        attachListeners();
    }


    /**
     * Detaches the firebase listeners.
     */
    private void detachListeners()
    {
        if(registration1 != null && requestDR != null)
        {
            registration1.remove();
            registration1 = null;
        }
        if(registration2 != null)
        {
            registration2.remove();
            registration2 = null;
        }
        if(registration3 != null && offerDR != null)
        {
            registration3.remove();
            registration3 = null;
        }
    }

    /**
     * Calls onDestroy - parent function.
     * Calls detachListeners() - to detach the listeners (efficiency).
     */
    @Override
    protected void onDestroy()
    {
        detachListeners();
        super.onDestroy();
    }

    /**
     * Attaches the firebase listeners.
     */
    private void attachListeners()
    {
        if (registration1 == null && requestDR != null)
        {
            registration1 = requestDR.addSnapshotListener(requestEventListener);
        }
        if(registration2 == null && request != null && offersQuantityTV != null)
        {
            registration2 = FirebaseManager.getDBReference().collection(ConstantsManager.OFFERS_COLLECTION)
                    .whereEqualTo(ConstantsManager.OFFER_REQUEST_ID_FIELD, request.getRequestId()).addSnapshotListener(newOffersEventListener);
        }
        if(registration3 == null && offerDR != null)
        {
            registration3 = offerDR.addSnapshotListener(currentOfferEventListener);
        }
    }

    /**
     * Calls onPause - parent function.
     * Calls detachListeners() - to detach the listeners (efficiency).
     */
    @Override
    protected void onPause()
    {
        detachListeners();
        super.onPause();
    }

    /**
     * Calls onResume - parent function.
     * Calls attachListeners() - to attach the listeners (efficiency).
     */
    @Override
    protected void onResume()
    {
        attachListeners();
        super.onResume();
    }

    /**
     * Sets the requestDR (request reference) by searching in the database.
     * Calls prepare(), attachListeners().
     */
    private void setRequestReference()
    {
        FirebaseManager.getDBReference().collection(ConstantsManager.REQUESTS_COLLECTION).whereEqualTo(ConstantsManager.REQUEST_REQUEST_ID_FIELD, request.getRequestId())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                if(queryDocumentSnapshots == null || queryDocumentSnapshots.isEmpty()) return;

                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    if (documentSnapshot != null && documentSnapshot.exists())
                    {
                        requestDR = documentSnapshot.getReference();
                        if((long) documentSnapshot.get(ConstantsManager.REQUEST_TYPE_FIELD) == (ConstantsManager.OFFLINE_REQUEST_TYPE))
                        {
                            request = (RequestType) documentSnapshot.toObject(OfflineRequest.class);
                        }
                        else if((long) documentSnapshot.get(ConstantsManager.REQUEST_TYPE_FIELD) == (ConstantsManager.ONLINE_REQUEST_TYPE))
                        {
                            request = (RequestType) documentSnapshot.toObject(OnlineRequest.class);
                        }
                        checkIfOfferExists();
                        initBaseWidgets();
                        return;
                    }
                }

            }
        });
    }


    /**
     * Apply user details by the user details (widgets and variables) that are in the database.
     *
     * @param userId the user id string - found in database.
     */
    public void applyUserDetails(String userId)
    {
        FirebaseManager.getDBReference().collection(ConstantsManager.USERS_COLLECTION).whereEqualTo(ConstantsManager.USER_USER_ID_FIELD, userId)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                if(queryDocumentSnapshots == null || queryDocumentSnapshots.isEmpty()) return;

                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    if(documentSnapshot != null && documentSnapshot.exists())
                    {
                        userOfRequest = documentSnapshot.toObject(User.class);
                        ImageManager.setImageViewString(OthersViewActivity.this, userOfRequest.getUserImage(), userIV, R.drawable.profile_ic);
                        usernameTV.setText(userOfRequest.getUsername());
                        userInfoBottomSheetDialog = new UserInfoBottomSheetDialog(OthersViewActivity.this, userOfRequest);
                    }
                }
            }
        });
    }

    /**
     * updates the UI and variables (especially the OfferDR and currentOffer) if the current user has an offer attached to the current request.
     */
    private void checkIfOfferExists()
    {
        if(request == null) return;

        FirebaseManager.getDBReference().collection(ConstantsManager.OFFERS_COLLECTION)
                .whereEqualTo(ConstantsManager.OFFER_REQUEST_ID_FIELD, request.getRequestId())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                if(queryDocumentSnapshots == null || queryDocumentSnapshots.isEmpty()) return;

                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    if(documentSnapshot != null && documentSnapshot.exists())
                    {
                        Offer offer = documentSnapshot.toObject(Offer.class);
                        if(offer.getUserId().equals(UserManager.getCurrentUser().getUserId()))
                        {
                            currentOffer = offer;
                            offerDR = documentSnapshot.getReference();
                            attachListeners();
                        }

                    }
                }
            }
        });
    }


    /**
     * Checks if the view which was clicked is createOrCancelOfferBtn or userInfoView.
     * createOrCancelOfferBtn - launches new activity that user can create an offer.
     * userInfoView - details about the user of request.
     * @param view
     */
        @Override
        public void onClick(View view)
        {
        if(view.getId() == createOrCancelOfferBtn.getId())
        {
            if(request == null || request.getState() == ConstantsManager.statesCodes[2]) return;

            if(currentOffer == null)
            {
                /**
                 * Launches new activity that the user can create a new offer for current request.
                 */
                Intent intent = new Intent(OthersViewActivity.this, CreateOfferActivity.class);
                intent.putExtra(ConstantsManager.REQUEST, request);
                startActivityForResult(intent, ConstantsManager.CREATE_OFFER_REQUEST_CODE);
            }
            else
            {
                if(currentOffer != null && currentOffer.getState() == ConstantsManager.statesCodes[0])
                {
                    showCancelOfferDialog();
                }
            }
        }
        else if(request != null && view.getId() == userInfoView.getId())
        {
            showUserInfoBottomSheetDialog();
        }

    }

    /**
     * Pops a dialog that asks the user if he wants to cancel the offer (in case he has one attached to current request).
     * deletes the offer and update UI in case he cancels the offer.
     */
    private void showCancelOfferDialog()
    {
        DialogInterface.OnClickListener positiveDialogInterface = new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                if(currentOffer != null && currentOffer.getState() == ConstantsManager.statesCodes[0] && request.getState() != ConstantsManager.statesCodes[2])
                {
                    deleteOffer();
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

        DialogsManager.showNegativeOrPositiveAlertDialog(OthersViewActivity.this,
                "Cancel Offer", "Are you sure you want to cancel this offer?",
                positiveDialogInterface, negativeDialogInterface);
    }

    /**
     * Deletes the offer from database.
     */
    private void deleteOffer()
    {
        if(offerDR != null)
        {
            offerDR.delete();
            currentOffer = null;
            offerDR = null;
            registration3 = null;
        }
    }

    /**
     * Shows a dialog with the user of request info.
     */
    private void showUserInfoBottomSheetDialog()
    {
        userInfoBottomSheetDialog.showUserInfoBottomSheetDialog();
    }

    /**
     * Handles the creation of a new offer.
     * Also handles the case of the user not creating an offer.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ConstantsManager.CREATE_OFFER_REQUEST_CODE)
        {
            if (resultCode == OthersViewActivity.RESULT_OK)
            {
                currentOffer = (Offer) data.getSerializableExtra(ConstantsManager.OFFER);
                if(currentOffer != null)
                {
                    setOfferReferenceById(currentOffer.getOfferId());
                }
            }
        }
    }

    /**
     * Sets the offerDR (reference) with a given offer id (from database).
     * @param offerId
     */
    private void setOfferReferenceById(String offerId)
    {
        FirebaseManager.getDBReference().collection(ConstantsManager.OFFERS_COLLECTION)
                .whereEqualTo(ConstantsManager.OFFER_OFFER_ID_FIELD, offerId)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                if(queryDocumentSnapshots == null || queryDocumentSnapshots.isEmpty()) return;

                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    if(documentSnapshot != null && documentSnapshot.exists())
                    {
                        offerDR = documentSnapshot.getReference();
                        attachListeners();
                        return;
                    }
                }
            }
        });
    }


    /**
     * An event listener (realtime changes) for the current request changes.
     * Calls checkForUpdates() - in case of realtime changes.
     */
    private final EventListener<DocumentSnapshot> requestEventListener = new EventListener<DocumentSnapshot>()
    {
        @Override
        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error)
        {
            if (error != null) return;

            if (value != null)
            {
                if(!value.exists())
                {
                    showRequestDeletedDialog();
                    return;
                }

                if((long) value.get(ConstantsManager.REQUEST_TYPE_FIELD) == (ConstantsManager.OFFLINE_REQUEST_TYPE))
                {
                    request = (RequestType) value.toObject(OfflineRequest.class);
                }
                else if((long) value.get(ConstantsManager.REQUEST_TYPE_FIELD) == (ConstantsManager.ONLINE_REQUEST_TYPE))
                {
                    request = (RequestType) value.toObject(OnlineRequest.class);
                }

                if(request.getState() == ConstantsManager.statesCodes[2])
                {
                    showAllOthersOffersDeletedDialog();
                    return;
                }
                checkForUpdates();
            }
        }
    };

    /**
     * Pops a dialog notifying that current request has been delivered and all other offers are irrelevant.
     */
    private void showAllOthersOffersDeletedDialog()
    {
        DialogInterface.OnClickListener dialogInterface = new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
                finish();
            }
        };
        DialogsManager.showNeutralAlertDialog(OthersViewActivity.this,
                "All Other offers are deleted", "The request was just delivered successfully",
                dialogInterface);
    }


    /**
     * An event listener (realtime changes) for new offers attached to current request.
     * Calls checkForUpdates() and setAmountOfOffers().
     */
    private final EventListener<QuerySnapshot> newOffersEventListener = new EventListener<QuerySnapshot>()
    {
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error)
        {
            if (error != null) return;


            if(value != null)
            {
                offersQuantityTV.setText(value.size() + "");
            }
        }
    };


    /**
     * An event listener (realtime changes) for the current offer changes.
     * Calls checkForUpdates() - in case of realtime changes.
     */
    private final EventListener<DocumentSnapshot> currentOfferEventListener = new EventListener<DocumentSnapshot>()
    {
        @Override
        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error)
        {
            if (error != null) return;

            if (value != null)
            {
                currentOffer = value.toObject(Offer.class);
                checkForUpdates();
            }
        }
    };

    /**
     * Updates UI in case of offer / request states' are changing.
     */
    private void checkForUpdates()
    {
        if(request.getState() == ConstantsManager.statesCodes[0])
        {
            if(offerDR == null || currentOffer == null)
            {
                createOrCancelOfferBtn.setBackgroundTintList(getResources().getColorStateList(R.color.light_green));
                createOrCancelOfferBtn.setText("Make an offer");
            }
            else{
                createOrCancelOfferBtn.setBackgroundTintList(getResources().getColorStateList(R.color.dark_green));
                createOrCancelOfferBtn.setText("Cancel your offer");
            }
        }
        else if(request.getState() == ConstantsManager.statesCodes[1])
        {
            if(offerDR == null || currentOffer == null)
            {
                createOrCancelOfferBtn.setBackgroundTintList(getResources().getColorStateList(R.color.light_green));
                createOrCancelOfferBtn.setText("Make an offer");
            }
            else
            {
                if(currentOffer.getState() == ConstantsManager.statesCodes[1])
                {
                    createOrCancelOfferBtn.setBackgroundTintList(getResources().getColorStateList(R.color.dark_gray));
                }
                else{
                    createOrCancelOfferBtn.setBackgroundTintList(getResources().getColorStateList(R.color.dark_green));
                }
                createOrCancelOfferBtn.setText("Cancel your offer");
            }
        }
        else if(request.getState() == ConstantsManager.statesCodes[2])
        {
            createOrCancelOfferBtn.setBackgroundTintList(getResources().getColorStateList(R.color.dark_gray));
            createOrCancelOfferBtn.setText("Request was delivered");
        }
    }


    /**
     * Pops a dialog notifying that current request has been deleted by the other user.
     */
    private void showRequestDeletedDialog()
    {
            DialogInterface.OnClickListener dialogInterface = new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    dialog.cancel();
                    finish();
                }
            };
            DialogsManager.showNeutralAlertDialog(OthersViewActivity.this,
                    "Request deleted", "The other user just deleted their request",
                    dialogInterface);
    }
    }


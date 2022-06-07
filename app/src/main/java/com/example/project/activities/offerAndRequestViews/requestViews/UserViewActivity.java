package com.example.project.activities.offerAndRequestViews.requestViews;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.project.R;
import com.example.project.activities.ViewRequestOffersActivity;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.DateAndTimeManager;
import com.example.project.managers.DialogsManager;
import com.example.project.managers.FirebaseManager;
import com.example.project.managers.ImageManager;
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
import com.google.firebase.storage.StorageReference;

/**
 * User perspective of the request activity
 *
 * @param <RequestType> the type parameter - type of request (Offline or Online)
 */
public abstract class UserViewActivity<RequestType extends Request> extends Activity implements View.OnClickListener
{
    /**
     * The current request object.
     */
    protected RequestType request;

    /**
     * The xml view reference.
     */
    protected View view;

    /**
     * The request image view
     */
    private ImageView requestIV;
    /**
     * The request title text view
     */
    private TextView requestTitleTV;
    /**
     * The request description text view
     */
    private TextView requestDescriptionTV;
    /**
     * The request deadline text view
     */
    private TextView requestDeadlineTV;
    /**
     * The amount of offers "attached" to current request text view
     */
    private TextView offersQuantityTV;
    /**
     * The button that gets the user to see all the offers to the current request.
     */
    private Button viewOffersBtn;
    /**
     * A button to delete the request.
     */
    private Button deleteRequestBtn;

    /**
     * The firebase reference to the current request.
     */
    protected DocumentReference requestDR;

    /**
     * Two interfaces responsible of attaching and detaching the offer and request listeners.
     */
    private ListenerRegistration registration1, registration2;


    /**
     * Initialize the request object and sets the view of the screen.
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
     *
     * @return the view of the user perspective.
     */
    protected abstract View getView();

    /**
     *
     * Initialize the uncommon widgets and variables that exist in OfflineRequestUserViewActivity and OnlineRequestUserViewActivity.
     */
    protected abstract void prepare();


    /**
     * Initialize the common widgets and variables of both OfflineRequestOthersViewActivity and OnlineRequestOthersViewActivity.
     * Calls setAmountOfOffers(), setRequestReference().
     */
    private void initBaseWidgets()
    {
        requestIV = view.findViewById(R.id.ova_request_image_image_view);
        requestTitleTV = view.findViewById(R.id.ova_request_title_text_view);
        requestDescriptionTV = view.findViewById(R.id.ova_request_description_text_view);
        requestDeadlineTV = view.findViewById(R.id.ova_request_date_and_time_text_view);
        viewOffersBtn = view.findViewById(R.id.uva_view_offers_button);
        deleteRequestBtn = view.findViewById(R.id.uva_delete_request_button);
        offersQuantityTV = view.findViewById(R.id.ova_offers_quantity_text_view);

        viewOffersBtn.setOnClickListener(this);
        deleteRequestBtn.setOnClickListener(this);

        ImageManager.setImageViewString(UserViewActivity.this, request.getRequestImage(), requestIV, R.drawable.gallery_icon);
        requestTitleTV.setText(request.getTitle());
        requestDescriptionTV.setText(request.getDescription());
        requestDeadlineTV.setText(DateAndTimeManager.getDateAndTimeString(request.getMilliseconds()));

        attachListeners();
    }



    /**
     * Sets the request reference (database reference).
     * Calls prepare(), attachListener() - edge case that the listener isn't attached.
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

                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    if(documentSnapshot != null && documentSnapshot.exists())
                    {
                        requestDR = documentSnapshot.getReference();
                        initBaseWidgets();
                        prepare();
                        break;
                    }
                }
            }
        });
    }

    /**
     * Detaches the listeners
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
    }

    /**
     * Calls onResume - parent function.
     * Calls attachListener().
     */
    @Override
    protected void onResume()
    {
        attachListeners();
        super.onResume();
    }

    /**
     * Calls onPause - parent function.
     * Calls detachListener().
     */
    @Override
    protected void onPause()
    {
        detachListeners();
        super.onPause();
    }


    /**
     * Calls onDestroy - parent function.
     * Calls detachListener().
     */
    @Override
    protected void onDestroy()
    {
        detachListeners();
        super.onDestroy();
    }

    /**
     * Attaches the listeners
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
    }

    /**
     * An event listener for changes in current request.
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
                    finish();
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

                checkForUpdates();
            }
        }
    };

    private void checkForUpdates()
    {
        if(request == null || requestDR == null) return;

        if(request.getState() == ConstantsManager.statesCodes[0])
        {
            deleteRequestBtn.setBackgroundTintList(getResources().getColorStateList(R.color.dark_green));
            deleteRequestBtn.setText("Delete");
        }
        else if(request.getState() == ConstantsManager.statesCodes[1])
        {
            deleteRequestBtn.setBackgroundTintList(getResources().getColorStateList(R.color.dark_gray));
            deleteRequestBtn.setText("Delete");
        }
        else if(request.getState() == ConstantsManager.statesCodes[2])
        {
            deleteRequestBtn.setBackgroundTintList(getResources().getColorStateList(R.color.dark_gray));
            deleteRequestBtn.setText("Request was delivered");
        }
    }

    /**
     * An event listener for changes in new offers that are "attached" to current request.
     */
    private final EventListener<QuerySnapshot> newOffersEventListener = new EventListener<QuerySnapshot>()
    {
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error)
        {
            if(error != null) return;

            if(value != null)
            {
                offersQuantityTV.setText(value.size() + "");
            }
        }
    };


    /**
     * Checks if the view which was clicked is viewOffersBtn or deleteRequestBtn.
     * viewOffersBtn - launches new activity with all offers related to current request.
     * deleteRequestBtn - showDeleteRequestAlertDialog() - pops a dialog.
     * @param view
     */
    @Override
    public void onClick(View view)
    {
        if(view.getId() == viewOffersBtn.getId())
        {
            /**
             * launches new activity with all offers related to current request.
             */
            Intent intent = new Intent(UserViewActivity.this, ViewRequestOffersActivity.class);
            intent.putExtra(ConstantsManager.REQUEST, request);
            startActivity(intent);
        }
        else if(view.getId() == deleteRequestBtn.getId())
        {
            if(request != null && request.getState() == ConstantsManager.statesCodes[0])
            {
                showDeleteRequestAlertDialog();
                return;
            }
        }
    }


    /**
     * Deletes the request and all of its offers.
     */
    private void deleteRequestAndItsOffers()
    {
        if(requestDR == null) return;

        FirebaseManager.getDBReference().collection(ConstantsManager.OFFERS_COLLECTION).whereEqualTo(ConstantsManager.OFFER_REQUEST_ID_FIELD, request.getRequestId())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                if(queryDocumentSnapshots == null) return;
                /**
                 * delete all offers if none was found as accepted
                 */
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    if(documentSnapshot != null && documentSnapshot.exists())
                    {
                        DocumentReference documentReference = documentSnapshot.getReference();
                        documentReference.delete();
                    }
                }
                /**
                 * deletes the request
                 */
                requestDR.delete();
                deleteRequestImage();
            }
        });

    }

    /**
     * The function deletes the request's image from the FireStorage and exits the activity.
     */
    private void deleteRequestImage()
    {
        StorageReference storageReference = FirebaseManager.FSReference.child(ConstantsManager.REQUESTS_IMAGES_PREFIX).child(request.getRequestId());
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void unused)
            {
                finish();
            }
        });
    }

    /**
     * Pops a dialog asking the user if he wants to delete the current request.
     */
    private void showDeleteRequestAlertDialog()
    {
        DialogInterface.OnClickListener positiveDialogInterface = new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                if(request != null && request.getState() == ConstantsManager.statesCodes[0])
                {
                    deleteRequestAndItsOffers();
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
        DialogsManager.showNegativeOrPositiveAlertDialog(UserViewActivity.this,
                "Delete Request and its offers",
                "Are you sure you want to delete this request and all of its offers?",
                positiveDialogInterface, negativeDialogInterface);
    }
}
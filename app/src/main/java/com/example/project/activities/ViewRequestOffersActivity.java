package com.example.project.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.adapters.OfferRecyclerViewAdapter;
import com.example.project.interfaces.RecyclerItemClickListener;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.FirebaseManager;
import com.example.project.objects.Offer;
import com.example.project.objects.Request;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A class that shows all offers that belong to specific request
 */
public class ViewRequestOffersActivity extends AppCompatActivity
{
    /**
     * The offers recycler view (list) - widget.
     */
    private RecyclerView offersRV;
    /**
     * The offers recycler view adapter.
     */
    private OfferRecyclerViewAdapter offerRecyclerViewAdapter;
    /**
     * The array list containing all offers objects.
     */
    private ArrayList<Offer> offers;
    /**
     * The specific request which all offers belong to.
     */
    private Request request;
    /**
     * An interface to attach and detach the firebase listener.
     */
    private ListenerRegistration registration = null;

    /**
     * Initializes the view.
     * @param savedInstanceState
     * Calls init().
     */
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_request_offers_layout);
        init();
    }

    /**
     * Initializes the widgets, variables and sets offer recycler view onClick listener and adapter.
     */
    private void init()
    {
        request = (Request) getIntent().getSerializableExtra(ConstantsManager.REQUEST);
        offers = new ArrayList<>();
        offersRV = findViewById(R.id.vrol_request_offers_recycler_view);
        offerRecyclerViewAdapter = new OfferRecyclerViewAdapter(ViewRequestOffersActivity.this, offers, new RecyclerItemClickListener()
        {
            @Override
            public void onItemClick(Object object)
            {
                /**
                 * If an offer is clicked then move to the offer view activity.
                 */
                Offer offer = (Offer) object;
                startActivity(offer.createUserOfRequestViewIntent(ViewRequestOffersActivity.this));
            }
        });
        offersRV.setAdapter(offerRecyclerViewAdapter);
        attachListener();
    }

    /**
     * Attaches the firebase listener - for any new or removed offers events (related to current request).
     */
    private void attachListener()
    {
        if(registration == null && offers != null)
        {
            registration = FirebaseManager.getDBReference().collection(ConstantsManager.OFFERS_COLLECTION)
                    .whereEqualTo(ConstantsManager.OFFER_REQUEST_ID_FIELD, request.getRequestId())
                    .addSnapshotListener(offersEventListener);
        }
    }

    /**
     * Detaches the firebase listener.
     */
    private void detachListener()
    {
        if(registration != null)
        {
            registration.remove();
            registration = null;
        }
    }

    /**
     * Calls detachListener().
     * Calls Parent function - onPause().
     */
    @Override
    public void onPause()
    {
        detachListener();
        super.onPause();
    }

    /**
     * Calls attachListener().
     * Calls Parent function - onResume().
     */
    @Override
    public void onResume()
    {
        attachListener();
        super.onResume();
    }

    /**
     * Calls detachListener().
     * Calls Parent function - onDestroy().
     */
    @Override
    public void onDestroy()
    {
        detachListener();
        super.onDestroy();
    }

    /**
     * Updates the offers list when a change is made or it's needed to be initialized.
     */
    private void updateOffersList(QuerySnapshot value)
    {
        if(value == null) return;
        offers.clear();
        for(DocumentSnapshot documentSnapshot : value)
        {
            if(documentSnapshot != null && documentSnapshot.exists() && documentSnapshot.get(ConstantsManager.OFFER_REQUEST_ID_FIELD).equals(request.getRequestId()))
            {
                Offer offer = documentSnapshot.toObject(Offer.class);
                offers.add(offer);
            }
        }
        offerRecyclerViewAdapter.notifyDataSetChanged();
    }


    /**
     * A variable for triggering the onEvent when a change is made at the relevant offers (related to current request).
     * Calls updateOffersList().
     */
    private final EventListener<QuerySnapshot> offersEventListener = new EventListener<QuerySnapshot>()
    {
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error)
        {
            if(error != null) return;

            if(value != null)
            {
                updateOffersList(value);
            }
        }
    };


}

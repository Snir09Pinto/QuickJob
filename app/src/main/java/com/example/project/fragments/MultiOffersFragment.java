package com.example.project.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.adapters.OfferRecyclerViewAdapter;
import com.example.project.interfaces.RecyclerItemClickListener;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.FirebaseManager;
import com.example.project.managers.UserManager;
import com.example.project.objects.Offer;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * The class is responsible for the multi offers "pages" in the viewPager - where the user sees all of his offers at all STATES!
 */
public class MultiOffersFragment extends Fragment
{
    /**
     * The view of the screen (xml).
     */
    private View view;
    /**
     * The offers recycler view.
     */
    private RecyclerView offersRV;
    /**
     * The offers list.
     */
    private ArrayList<Offer> list;
    /**
     * The offers list adapter.
     */
    private OfferRecyclerViewAdapter offerRecyclerViewAdapter;
    /**
     * The interface responsible for attaching and detaching the listener.
     */
    private ListenerRegistration registration;
    /**
     * The current offers' state.
     */
    private int state;

    /**
     *
     * @param state - current offers' state.
     * @return the correct fragment with an Argument of the offer state field.
     */
    public static MultiOffersFragment newInstance(int state)
    {
        MultiOffersFragment multiOffersFragment = new MultiOffersFragment();
        Bundle args = new Bundle();

        args.putInt(ConstantsManager.OFFER_STATE_FIELD, state);
        multiOffersFragment.setArguments(args);
        return multiOffersFragment;
    }

    /**
     *
     * @param inflater - inflater object.
     * @param container - the view container.
     * @param savedInstanceState - the bundle.
     * @return the correct view.
     * Calls init().
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.multi_offers_fragment_layout, container, false);
        init();
        return view;
    }

    /**
     * Initializes all variables (state and more) and all widgets.
     * Also sets the list onClick listener.
     * Calls attachListeners().
     */
    private void init()
    {
        this.state = getArguments().getInt(ConstantsManager.OFFER_STATE_FIELD);
        list = new ArrayList<>();
        offerRecyclerViewAdapter = new OfferRecyclerViewAdapter(getContext(), list, new RecyclerItemClickListener()
        {
            @Override
            public void onItemClick(Object object)
            {
                /**
                 * If a offer is clicked - launch offer info window.
                 */
                Offer offer = (Offer) object;
                startActivity(offer.createUserOfOfferViewIntent(getContext()));
            }
        });
        offersRV = view.findViewById(R.id.mofl_multi_offers_recycler_view);
        offersRV.setAdapter(offerRecyclerViewAdapter);

        attachListener();
    }

    /**
     * Updates the current requests list and adapter + list.
     */
    private void updateList(QuerySnapshot value)
    {
        if(value == null) return;
        list.clear();
        for(DocumentSnapshot documentSnapshot : value)
        {
            if(documentSnapshot != null && documentSnapshot.exists()
                    && (long) documentSnapshot.get(ConstantsManager.OFFER_STATE_FIELD) == state
                    && documentSnapshot.get(ConstantsManager.OFFER_USER_ID_FIELD).equals(UserManager.getCurrentUser().getUserId()))
            {
                Offer offer = documentSnapshot.toObject(Offer.class);
                list.add(offer);
            }
        }
        offerRecyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * Attaches the firebase listener - listening to all offers related to current user.
     */
    private void attachListener()
    {
        if(registration == null && list != null)
        {
            registration = FirebaseManager.getDBReference().collection(ConstantsManager.OFFERS_COLLECTION)
                    .whereEqualTo(ConstantsManager.OFFER_USER_ID_FIELD, UserManager.getCurrentUser().getUserId())
                    .addSnapshotListener(offersListener);
        }
    }

    /**
     * Detaches the listener.
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
     * A variable that triggers the updateList() function each time a change occurs in the offers related to current user.
     */
    private final EventListener<QuerySnapshot> offersListener = new EventListener<QuerySnapshot>()
    {
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error)
        {
            if(error != null) return;

            if(value != null)
            {
                updateList(value);
            }
        }
    };
}

package com.example.project.fragments.multiRequests;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.adapters.RequestsMultiViewTypeAdapter;
import com.example.project.interfaces.RecyclerItemClickListener;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.FirebaseManager;
import com.example.project.managers.UserManager;
import com.example.project.objects.OfflineRequest;
import com.example.project.objects.OnlineRequest;
import com.example.project.objects.Request;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A class for handling the fragments of the user requests (pending, processing and delivered).
 */
public abstract class MultiRequestsFragment extends Fragment
{
    /**
     * The appropriate view (with or without an "add request" button).
     */
    protected View view;
    /**
     * The request recycler view.
     */
    private RecyclerView requestsRV;
    /**
     * The requests list.
     */
    private ArrayList<Request> list;
    /**
     * The requests' list adapter.
     */
    private RequestsMultiViewTypeAdapter requestsMultiViewTypeAdapter;
    /**
     * An interface for attaching and detaching the firebase listener.
     */
    private ListenerRegistration registration = null;
    /**
     * The current state of all requests that are in the list.
     */
    private int state;

    /**
     * @return the appropriate fragment view.
     */
    protected abstract View getFragmentView();


    /**
     * Initializes the uncommon parts between the fragments' views (the button).
     */
    protected abstract void prepare();


    /**
     *
     * @param state - current requests' state.
     * @return the correct fragment with an Argument of the request state field.
     */
    public static MultiRequestsFragment newInstance(int state)
    {
        MultiRequestsFragment multiRequestsFragment = null;
        if(state == ConstantsManager.statesCodes[0])
        {
            multiRequestsFragment = new PendingRequestsFragment();
        }
        else{
            multiRequestsFragment = new OtherTypeOfRequestsFragment();
        }

        Bundle args = new Bundle();

        args.putInt(ConstantsManager.REQUEST_STATE_FIELD, state);
        multiRequestsFragment.setArguments(args);
        return multiRequestsFragment;
    }


    /**
     *
     * @param inflater - inflater object.
     * @param container - the view container.
     * @param savedInstanceState - the bundle.
     * @return the correct view.
     * Calls init() and prepare().
     */
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = getFragmentView();
        init();
        prepare();
        return view;
    }

    /**
     * Initializes all variables (state and more) and all widgets.
     * Also sets the list onClick listener.
     * Calls attachListeners().
     */
    private void init()
    {
        this.state = getArguments().getInt(ConstantsManager.REQUEST_STATE_FIELD);
        list = new ArrayList<>();
        requestsMultiViewTypeAdapter = new RequestsMultiViewTypeAdapter(getContext(), list, ConstantsManager.HORIZONTAL_VIEW, new RecyclerItemClickListener() {
            @Override
            public void onItemClick(Object object)
            {
                /**
                 * If a request is clicked - launch request info window.
                 */
                Request request = (Request) object;
                startActivity(request.createUserViewActivity(getContext()));
            }
        });
        requestsRV = view.findViewById(R.id.uprfl_multi_requests_recycler_view);
        requestsRV.setAdapter(requestsMultiViewTypeAdapter);

        attachListener();
    }

    /**
     * Attaches the firebase listener - listening to all requests related to current user.
     */
    private void attachListener()
    {
        if(registration == null && list != null)
        {
            registration = FirebaseManager.getDBReference().collection(ConstantsManager.REQUESTS_COLLECTION)
                    .whereEqualTo(ConstantsManager.REQUEST_USER_ID_FIELD, UserManager.getCurrentUser().getUserId()).addSnapshotListener(requestsListener);
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
     * Updates the current requests list and adapter + list.
     */
    private void updateList(QuerySnapshot value)
    {
        if(value == null) return;
        list.clear();
        for (DocumentSnapshot documentSnapshot : value)
        {
            if(documentSnapshot != null && documentSnapshot.exists()
                    && (long) documentSnapshot.get(ConstantsManager.REQUEST_STATE_FIELD) == state
                    && documentSnapshot.get(ConstantsManager.REQUEST_USER_ID_FIELD).equals(UserManager.getCurrentUser().getUserId()))
            {
                if ((long) documentSnapshot.get(ConstantsManager.REQUEST_TYPE_FIELD) == (ConstantsManager.OFFLINE_REQUEST_TYPE))
                {
                    OfflineRequest offlineRequest = documentSnapshot.toObject(OfflineRequest.class);
                    list.add(offlineRequest);
                }

                else if ((long) documentSnapshot.get(ConstantsManager.REQUEST_TYPE_FIELD) == (ConstantsManager.ONLINE_REQUEST_TYPE))
                {
                    OnlineRequest onlineRequest = documentSnapshot.toObject(OnlineRequest.class);
                    list.add(onlineRequest);
                }
            }
        }
        requestsMultiViewTypeAdapter.notifyDataSetChanged();
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
     * A variable that triggers the updateList() function each time a change occurs in the requests related to current user.
     */
    private final EventListener<QuerySnapshot> requestsListener = new EventListener<QuerySnapshot>()
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

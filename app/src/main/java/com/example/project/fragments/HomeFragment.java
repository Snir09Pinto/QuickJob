package com.example.project.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.activities.SearchRequestsActivity;
import com.example.project.adapters.RequestsMultiViewTypeAdapter;
import com.example.project.adapters.SubcategoryRecyclerViewAdapter;
import com.example.project.interfaces.RecyclerItemClickListener;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.FirebaseManager;
import com.example.project.managers.UserManager;
import com.example.project.objects.OfflineRequest;
import com.example.project.objects.OnlineRequest;
import com.example.project.objects.Request;
import com.example.project.objects.Subcategory;
import com.example.project.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * The user home screen class.
 */
public class HomeFragment extends Fragment {
    /**
     * The view of the screen.
     */
    private View view;
    /**
     * The random requests list.
     */
    private ArrayList<Request> requestsList;
    /**
     * The random subcategories list.
     */
    private ArrayList<Subcategory> subcategoriesList;
    /**
     * requests list adapter.
     */
    private RequestsMultiViewTypeAdapter requestsMultiViewTypeAdapter;
    /**
     * subcategories list adapter.
     */
    private SubcategoryRecyclerViewAdapter subcategoryRecyclerViewAdapter;
    /**
     * both recyclerViews - requests and subcategories lists.
     */
    private RecyclerView subcategoriesRV, requestRV;
    /**
     * An interface for attaching and detaching the listener.
     */
    private ListenerRegistration registration;


    /**
     * Initializes the view and calls init().
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_fragment_layout, container, false);
        init();
        return view;
    }


    /**
     * Initializes all lists including onClick methods, adapters and more.
     */
    public void init()
    {
        subcategoriesList = new ArrayList<>();
        subcategoryRecyclerViewAdapter = new SubcategoryRecyclerViewAdapter(getContext(), subcategoriesList, new RecyclerItemClickListener() {
            @Override
            public void onItemClick(Object object) {
                /**
                 * Goes to search requests activity with the chosen subcategory.
                 */
                Intent intent = new Intent(getContext(), SearchRequestsActivity.class);
                intent.putExtra(ConstantsManager.SUBCATEGORY, (Subcategory) object); //so no need to handle adapter view indexes boundaries
                startActivity(intent);
            }
        });

        subcategoriesRV = view.findViewById(R.id.hfl_subcategories_recycler_view);
        subcategoriesRV.setAdapter(subcategoryRecyclerViewAdapter);

        requestsList = new ArrayList<>();
        requestsMultiViewTypeAdapter = new RequestsMultiViewTypeAdapter(getContext(), requestsList, ConstantsManager.VERTICAL_VIEW, new RecyclerItemClickListener() {
            @Override
            public void onItemClick(Object object) {
                Request request = (Request) object;

                /**
                 * Launches the correct view of request's info window.
                 */
                if (request.getUserId().equals(UserManager.getCurrentUser().getUserId())) {
                    startActivity(request.createUserViewActivity(getContext()));
                } else {
                    startActivity(request.createOthersViewActivity(getContext()));
                }
            }
        });

        requestRV = view.findViewById(R.id.requests_recycler_view);
        requestRV.setAdapter(requestsMultiViewTypeAdapter);

        updateSubcategoriesList();
        attachListener();
    }


    /**
     * Updates / Initializes the subcategories list from database.
     */
    private void updateSubcategoriesList()
    {
        if (subcategoriesList == null) return;

        subcategoriesList.clear();
        FirebaseManager.getDBReference().collection(ConstantsManager.SUBCATEGORIES_COLLECTION).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                int count = 0;
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    if (documentSnapshot != null && documentSnapshot.exists())
                    {
                        Subcategory subcategory = documentSnapshot.toObject(Subcategory.class);
                        subcategoriesList.add(subcategory);
                        count++;
                    }
                    if(count >= FirebaseManager.MAX_DOCUMENTS) break;
                }
                subcategoryRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Initializes the requests list with the new / updated requests from database.
     * @param value - Request documents.
     */
    private void updateRequestsList(QuerySnapshot value)
    {
        if(value == null) return;
        requestsList.clear();
        int count = 0;
        for(DocumentSnapshot documentSnapshot : value)
        {
            if(documentSnapshot != null && documentSnapshot.exists())
            {
                if((long) documentSnapshot.get(ConstantsManager.REQUEST_TYPE_FIELD) == (ConstantsManager.OFFLINE_REQUEST_TYPE))
                {
                    OfflineRequest offlineRequest = documentSnapshot.toObject(OfflineRequest.class);
                    if(!offlineRequest.getUserId().equals(UserManager.getCurrentUser().getUserId()) && offlineRequest.getState() != ConstantsManager.statesCodes[2])
                    {
                        requestsList.add(offlineRequest);
                        count++;
                    }
                }
                else if((long) documentSnapshot.get(ConstantsManager.REQUEST_TYPE_FIELD) == (ConstantsManager.ONLINE_REQUEST_TYPE))
                {
                    OnlineRequest onlineRequest = documentSnapshot.toObject(OnlineRequest.class);
                    if(!onlineRequest.getUserId().equals(UserManager.getCurrentUser().getUserId()) && onlineRequest.getState() != ConstantsManager.statesCodes[2])
                    {
                        requestsList.add(onlineRequest);
                        count++;
                    }
                }
            }
            if(count >= FirebaseManager.MAX_DOCUMENTS) break;
        }
        requestsMultiViewTypeAdapter.notifyDataSetChanged();
    }
    /**
     * Attaches the requests listener - all requests in the app.
     */
    private void attachListener()
    {
        if(registration == null && requestsList != null)
        {
            registration = FirebaseManager.getDBReference().collection(ConstantsManager.REQUESTS_COLLECTION)
                    .addSnapshotListener(requestsListener);
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
     * Triggers the onEvent method calling updateSubcategoriesList() and updateRequestsList() when change occurs in requests list.
     */
    private final EventListener<QuerySnapshot> requestsListener = new EventListener<QuerySnapshot>()
    {
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error)
        {
            if(error != null) return;

            if(value != null)
            {
                updateRequestsList(value);
            }
        }
    };
}

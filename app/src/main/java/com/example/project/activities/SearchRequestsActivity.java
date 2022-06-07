package com.example.project.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.adapters.RequestsMultiViewTypeAdapter;
import com.example.project.interfaces.RecyclerItemClickListener;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.FirebaseManager;
import com.example.project.managers.UserManager;
import com.example.project.objects.Offer;
import com.example.project.objects.OfflineRequest;
import com.example.project.objects.OnlineRequest;
import com.example.project.objects.Request;
import com.example.project.objects.Subcategory;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchRequestsActivity extends AppCompatActivity implements View.OnClickListener
{
    /**
     * All requests filter button.
     */
    private RadioButton allFilterBtn;
    /**
     * Offline requests only filter button.
     */
    private RadioButton offlineFilterBtn;
    /**
     * Online requests only filter button.
     */
    private RadioButton onlineFilterBtn;
    /**
     * Sorts the requests by deadline (from closets to farthest).
     */
    private RadioButton deadlineFilterBtn;
    /**
     * Sorts the requests by their amount of offers (from smallest to biggest).
     */
    private RadioButton offersQuantityFilterBtn;
    /**
     * The request recycler view.
     */
    private RecyclerView requestsRV;
    /**
     * Toolbar.
     */
    private Toolbar toolbar;
    /**
     * The request multi view type adapter (multi types of view).
     */
    private RequestsMultiViewTypeAdapter requestsMultiViewTypeAdapter;
    /**
     * The current list of requests (with the filters).
     */
    private ArrayList<Request> requestsList;
    /**
     * The current request list that has all requests (without filters).
     */
    private ArrayList<Request> requestsListAll;
    /**
     * Chosen subcategory (by the user).
     */
    private Subcategory subcategory;
    /**
     * An interface for attaching and detaching the listener.
     */
    private ListenerRegistration registration = null;

    /**
     * Current search bar text - string
     */
    private String currentSearchString;

    /**
     * Current filter (represented by an int).
     * Sets to all requests filter (initially).
     */

    private int filter = ConstantsManager.filters[0];

    /**
     *
     * @param savedInstanceState
     * Calls init().
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.requests_search_layout);
        init();
    }

    /**
     * Initializes all lists, subcategory, widgets.
     * Calls setAdapter(), initButtons(), initToolBar() and setAllRequestsFilterBtnChecked().
     */
    private void init()
    {
        subcategory = (Subcategory) getIntent().getSerializableExtra(ConstantsManager.SUBCATEGORY);
        requestsRV = findViewById(R.id.requests_recycler_view);
        requestsList = new ArrayList<>();
        requestsListAll = new ArrayList<>();
        currentSearchString = "";
        setAdapter();
        initButtons();
        initToolBar();
        attachListener();
    }



    /**
     * Updates the requests list and also filters it in case it was already filtered and just a new request was added by another user.
     * Calls requestsMultiViewTypeAdapter.updateObjectsAll(), filterByCurrentFilter().
     */
    private void updateList(QuerySnapshot value)
    {
        if(value == null) return;
        requestsList.clear();
        for(DocumentSnapshot documentSnapshot : value)
        {
            if(documentSnapshot != null && documentSnapshot.exists())
            {
                if((long) documentSnapshot.get(ConstantsManager.REQUEST_TYPE_FIELD) == (ConstantsManager.OFFLINE_REQUEST_TYPE))
                {
                    OfflineRequest offlineRequest = documentSnapshot.toObject(OfflineRequest.class);
                    if(offlineRequest.getState() != ConstantsManager.statesCodes[2])
                    requestsList.add(offlineRequest);
                }
                else if((long) documentSnapshot.get(ConstantsManager.REQUEST_TYPE_FIELD) == (ConstantsManager.ONLINE_REQUEST_TYPE))
                {
                    OnlineRequest onlineRequest = documentSnapshot.toObject(OnlineRequest.class);
                    if(onlineRequest.getState() != ConstantsManager.statesCodes[2])
                    requestsList.add(onlineRequest);
                }
            }
        }
        requestsListAll = new ArrayList<>(requestsList);
        /**
         * Updates the requestAll list (because only requestList will change if I wouldn't do so).
         */
        requestsMultiViewTypeAdapter.updateObjectsAll();
        filterByCurrentFilter();
    }

    /**
     * Filters the requests by the current filter variable.
     */
    private void filterByCurrentFilter()
    {
        if(filter == ConstantsManager.filters[0])
        {
            showAllRequests();
        }
        else if(filter == ConstantsManager.filters[1])
        {
            filterByOnlineRequests();
        }
        else if(filter == ConstantsManager.filters[2])
        {
            filterByOfflineRequests();
        }
        else if(filter == ConstantsManager.filters[3])
        {
            filterByDeadline();
        }
        else if(filter == ConstantsManager.filters[4])
        {
            filterByOffersQuantity();
        }
    }

    /**
     * Sets the multi view adapter - on clicks brings the user to another activity of the requests' details.
     */
    private void setAdapter()
    {
        requestsMultiViewTypeAdapter = new RequestsMultiViewTypeAdapter(SearchRequestsActivity.this, requestsList, ConstantsManager.VERTICAL_VIEW, new RecyclerItemClickListener()
        {
            @Override
            public void onItemClick(Object object)
            {
                Request request = (Request) object;

                if(request.getUserId().equals(UserManager.getCurrentUser().getUserId()))
                {
                    startActivity(request.createUserViewActivity(SearchRequestsActivity.this));
                }
                else {
                    startActivity(request.createOthersViewActivity(SearchRequestsActivity.this));
                }
            }
        });
        requestsRV.setAdapter(requestsMultiViewTypeAdapter);
    }


    /**
     * Initializes the toolbar
     */
    private void initToolBar()
    {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(subcategory.getTitle());
    }

    /**
     * Initializes the buttons and their onClick method.
     */
    private void initButtons()
    {
        offlineFilterBtn = findViewById(R.id.rsl_offline_requests_filter_button);
        onlineFilterBtn = findViewById(R.id.rsl_online_requests_filter_button);
        deadlineFilterBtn = findViewById(R.id.rsl_date_and_time_requests_filter_button);
        offersQuantityFilterBtn = findViewById(R.id.rsl_offers_quantity_filter_button);
        allFilterBtn = findViewById(R.id.rsl_all_requests_filter_button);

        offlineFilterBtn.setOnClickListener(this);
        onlineFilterBtn.setOnClickListener(this);
        deadlineFilterBtn.setOnClickListener(this);
        offersQuantityFilterBtn.setOnClickListener(this);
        allFilterBtn.setOnClickListener(this);
    }

    /**
     * Handles when the user clicks on the arrow back icon - finishes the activity.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Creates an options menu - search is enabled.
     * Filter the requests by title if user's searching something with a specific word or character.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_bar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);

        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) MenuItemCompat.getActionView(menuItem);

        searchView.setQueryHint("Search...");
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String s)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s)
            {
                currentSearchString = s;
                requestsMultiViewTypeAdapter.getFilter().filter(currentSearchString);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Handles all buttons - when clicking on some button it launches the filter function accordingly.
     * @param view
     */
    @Override
    public void onClick(View view)
    {
        if(view.getId() == offlineFilterBtn.getId())
        {
            filter = ConstantsManager.filters[2];
        }
        else if(view.getId() == onlineFilterBtn.getId())
        {
            filter = ConstantsManager.filters[1];
        }
        else if(view.getId() == deadlineFilterBtn.getId())
        {
            filter = ConstantsManager.filters[3];
        }
        else if(view.getId() == offersQuantityFilterBtn.getId())
        {
            filter = ConstantsManager.filters[4];
        }
        else if(view.getId() == allFilterBtn.getId())
        {
            filter = ConstantsManager.filters[0];
        }
        filterByCurrentFilter();

    }

    /**
     * All filter button function
     */
    private void showAllRequests()
    {
        requestsList.clear();
        requestsList.addAll(requestsListAll);
        requestsMultiViewTypeAdapter.updateObjectsAll();
        requestsMultiViewTypeAdapter.getFilter().filter(currentSearchString);
    }


    /**
     * Sort by offer amount button function
     */
    private void filterByOffersQuantity()
    {
        ArrayList<Integer> offersAmountList = new ArrayList<>();
        initOffersQuantityList(offersAmountList);
    }

    /**
     * Counts all offers attached to each of the requests in the list.
     */
    private void initOffersQuantityList(ArrayList<Integer> list)
    {
        FirebaseManager.getDBReference().collection(ConstantsManager.OFFERS_COLLECTION).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                if(queryDocumentSnapshots == null) return;

                for(Request request : requestsListAll)
                {
                    int count = 0;
                    for(DocumentSnapshot ds : queryDocumentSnapshots)
                    {
                        if(ds != null)
                        {
                            Offer offer = ds.toObject(Offer.class);
                            if(offer.getRequestId().equals(request.getRequestId()))
                            {
                                count++;
                            }
                        }
                    }
                    list.add(count);
                }
                requestsList.clear();
                sortAndUpdateRequestsListAndAdapter(list);
            }
        });
    }

    /**
     * updates request list and its adapter.
     * @param list
     */
    private void sortAndUpdateRequestsListAndAdapter(ArrayList<Integer> list)
    {
        ArrayList<Request> syncWithRemovedList = new ArrayList<>(requestsListAll);
        for(int i = 0; i < requestsListAll.size(); i++)
        {
            requestsList.add(findMinAndRemove(list, syncWithRemovedList));
        }
        requestsMultiViewTypeAdapter.updateObjectsAll();
        requestsMultiViewTypeAdapter.getFilter().filter(currentSearchString);
    }

    public Request findMinAndRemove(ArrayList<Integer> list, ArrayList<Request> syncWithRemovedList)
    {
        int index = 0;
        int min = list.get(index);
        Request request = null;
        for(int i = 0; i < list.size(); i++)
        {
            if(min > list.get(i))
            {
                index = i;
                min = list.get(i);
            }
        }
        request = syncWithRemovedList.get(index);
        list.remove(index);
        syncWithRemovedList.remove(index);
        return request;
    }


    /**
     * Sorts the requests list by deadline.
     */
    private void filterByDeadline()
    {
        requestsList.clear();
        requestsList.addAll(requestsListAll);
        for(int i = 0; i < requestsList.size() - 1; i++)
        {
            int minIndex = i;
            for(int j = i + 1; j < requestsList.size(); j++)
            {
                if(requestsList.get(minIndex).getMilliseconds() > requestsList.get(j).getMilliseconds())
                {
                    minIndex = j;
                }
            }
            swap(minIndex, i);
        }
        requestsMultiViewTypeAdapter.updateObjectsAll();
        requestsMultiViewTypeAdapter.getFilter().filter(currentSearchString);
    }

    private void swap(int minIndex, int i)
    {
        Request temp = requestsList.get(i);
        requestsList.set(i, requestsList.get(minIndex));
        requestsList.set(minIndex, temp);
    }

    /**
     * Filters the list by online requests only.
     */
    private void filterByOnlineRequests()
    {
        requestsList.clear();
        for(Request request : requestsListAll)
        {
            if(request.getType() == ConstantsManager.ONLINE_REQUEST_TYPE)
            {
                requestsList.add(request);
            }
        }
        requestsMultiViewTypeAdapter.updateObjectsAll();
        requestsMultiViewTypeAdapter.getFilter().filter(currentSearchString);
    }

    /**
     * Filters the list by offline requests only.
     */
    private void filterByOfflineRequests()
    {
        requestsList.clear();
        for(Request request : requestsListAll)
        {
            if(request.getType() == ConstantsManager.OFFLINE_REQUEST_TYPE)
            {
                requestsList.add(request);
            }
        }
        requestsMultiViewTypeAdapter.updateObjectsAll();
        requestsMultiViewTypeAdapter.getFilter().filter(currentSearchString);
    }


    /**
     * Attaches a listener for all requests "under" current subcategory.
     */
    private void attachListener()
    {
        if(registration == null && requestsList != null)
        {
            registration = FirebaseManager.getDBReference().collection(ConstantsManager.REQUESTS_COLLECTION)
                    .whereEqualTo(ConstantsManager.REQUEST_SUBCATEGORY_ID_FIELD, subcategory.getSubcategoryId())
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
     * An event listener that calls updateList(...) when a change is made at the requests which are "under" current subcategory.
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

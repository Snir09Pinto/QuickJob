package com.example.project.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.interfaces.RecyclerItemClickListener;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.DateAndTimeManager;
import com.example.project.managers.FirebaseManager;
import com.example.project.managers.ImageManager;
import com.example.project.objects.Chat;
import com.example.project.objects.Request;
import com.example.project.objects.User;
import com.example.project.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * An adapter for handling the requests list.
 */
public class RequestsMultiViewTypeAdapter extends RecyclerView.Adapter implements Filterable
{
    /**
     * The context of the relevant activity.
     */
    private Context context;
    /**
     * The current list of requests
     */
    private ArrayList<Request> list;
    /**
     * A copy of the full list of requests.
     */
    private ArrayList<Request> listAll;
    /**
     * An object responsible for filtering the list.
     */
    private FilterObject filterObject;

    /**
     * The type of view.
     */
    private int type;
    /**
     * The interface for listening and acting accordingly if user clicks on item in the list.
     */
    private RecyclerItemClickListener recyclerItemClickListener;


    /**
     *
     * @param context - relevant context.
     * @param list - the requests list (array list object).
     * Initializes context and list.
     * Sets the recycler view item onClick interface and the filter object.
     */
    public RequestsMultiViewTypeAdapter(Context context, ArrayList<Request> list, int type, RecyclerItemClickListener recyclerItemClickListener)
    {
        this.context = context;
        this.type = type;
        this.list = list;
        this.recyclerItemClickListener = recyclerItemClickListener;
        this.listAll = new ArrayList<>(list);

        getFilter();
    }

    /**
     *
     * @param parent - the view containing all child views.
     * @param viewType - the type of the view.
     * @return the correct view with inflating it - vertical or horizontal.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;
        switch (viewType){
            case ConstantsManager.VERTICAL_VIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item_cardview_vertical_layout, parent, false);
                return new VerticalRequestsViewHolder(view);
            case ConstantsManager.HORIZONTAL_VIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item_cardview_horizontal_layout, parent, false);
                return new HorizontalRequestsViewHolder(view);
        }

        return null;
    }

    /**
     * Updates the list containing all requests to a new list which is the current list but that it's just got updated.
     */
    public void updateObjectsAll()
    {
        this.listAll = new ArrayList<>(list);
    }

    /**
     * @return the view type (view variable).
     */
    @Override
    public int getItemViewType(int position)
    {
        return type;
    }

    /**
     * Calls bind() function for the current item's view.
     * @param holder - the view.
     * @param position - current position of item in list.
     * Attaches the onClick listener.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        if(getItemViewType(position) == ConstantsManager.VERTICAL_VIEW)
        {
            ((VerticalRequestsViewHolder) holder).bind(position);
        }
        else if(getItemViewType(position) == ConstantsManager.HORIZONTAL_VIEW)
        {
            ((HorizontalRequestsViewHolder) holder).bind(position);
        }

        holder.itemView.setOnClickListener(view ->
        {
            recyclerItemClickListener.onItemClick(list.get(position));
        });
    }

    /**
     * @return the size of the requests list.
     */
    @Override
    public int getItemCount()
    {
        return list.size();
    }

    /**
     * A class for the vertical view of a request.
     */
    private class VerticalRequestsViewHolder extends RecyclerView.ViewHolder
    {
        /**
         * Request image.
         */
        private ImageView requestImage;
        /**
         * Request type.
         */
        private TextView type;
        /**
         * Request title.
         */
        private TextView title;
        /**
         * Request date and time.
         */
        private TextView dateAndTime;

        /**
         * User of request image.
         */
        private ImageView userImage;
        /**
         * User of request name.
         */
        private TextView userName;

        /**
         * Initializes the current item view widgets.
         * @param itemView - the view.
         */
        public VerticalRequestsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            requestImage = itemView.findViewById(R.id.ricvl_request_image_image_view);
            type = itemView.findViewById(R.id.ricvl_request_type_text_view);
            title = itemView.findViewById(R.id.ricvl_request_title_text_view);
            dateAndTime = itemView.findViewById(R.id.ricvl_request_date_and_time_text_view);
            userImage = itemView.findViewById(R.id.ricvl_user_image_image_view);
            userName = itemView.findViewById(R.id.ricvl_user_name_text_view);
        }

        /**
         * @param position - current position of request in list.
         * Sets the information of the request (sets the view).
         * Calls setUserDetails().
         */
        public void bind(int position)
        {
            Request request = (Request) list.get(position);
            setUserDetails(userName, userImage, request);

            ImageManager.setImageViewString(context, request.getRequestImage(), requestImage, R.drawable.gallery_icon);
            type.setTextColor(Color.parseColor(request.getColor()));
            type.setText(request.getTypeString());
            title.setText(String.valueOf(request.getTitle()));
            String dateAndTimeString = DateAndTimeManager.getDateString(request.getMilliseconds()) + " " + DateAndTimeManager.getTimeString(request.getMilliseconds());
            dateAndTime.setText(dateAndTimeString);
        }

        /**
         * Sets the user information (widgets) by getting the correct user from database.
         * @param userNameTV - user name text view widget.
         * @param userIV - user name image view widget.
         * @param request - current request.
         */
        private void setUserDetails(TextView userNameTV, ImageView userIV, Request request)
        {
            FirebaseManager.getDBReference().collection(ConstantsManager.USERS_COLLECTION).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                    {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                        {
                            for(DocumentSnapshot ds : queryDocumentSnapshots)
                            {
                                if(ds != null)
                                {
                                    User user = ds.toObject(User.class);
                                    if(user.getUserId().equals(request.getUserId()))
                                    {
                                        userNameTV.setText(user.getUsername());
                                        ImageManager.setImageViewString(context, user.getUserImage(), userIV, R.drawable.profile_ic);
                                        return;
                                    }
                                }
                            }
                        }
                    });
        }

    }

    /**
     * A class for the vertical view of a request.
     */
    private class HorizontalRequestsViewHolder extends RecyclerView.ViewHolder
    {
        /**
         * Request image.
         */
        private ImageView requestImage;
        /**
         * Request type.
         */
        private TextView type;
        /**
         * Request title.
         */
        private TextView title;
        /**
         * Request date and time.
         */
        private TextView dateAndTime;
        /**
         * Request state.
         */
        private TextView requestState;

        /**
         * Initializes the current item view widgets.
         * @param itemView - the view.
         */
        public HorizontalRequestsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            requestImage = itemView.findViewById(R.id.richl_request_image_image_view);
            type = itemView.findViewById(R.id.richl_request_type_text_view);
            title = itemView.findViewById(R.id.richl_request_title_text_view);
            dateAndTime = itemView.findViewById(R.id.richl_request_date_text_view);
            requestState = itemView.findViewById(R.id.richl_request_state_text_view);
        }

        /**
         * @param position - current position of request in list.
         * Sets the information of the request (sets the view).
         * Calls setUserDetails().
         */
        public void bind(int position)
        {
            Request request = (Request) list.get(position);

            ImageManager.setImageViewString(context, request.getRequestImage(), requestImage, R.drawable.gallery_icon);

            type.setTextColor(Color.parseColor(request.getColor()));
            type.setText(request.getTypeString());
            title.setText(String.valueOf(request.getTitle()));
            String dateAndTimeString = DateAndTimeManager.getDateString(request.getMilliseconds()) + " " + DateAndTimeManager.getTimeString(request.getMilliseconds());
            dateAndTime.setText(dateAndTimeString);
            if(request.getStateColorInt() != -1 && !request.getStateString().isEmpty())
            {
                requestState.setText(request.getStateString());
                requestState.setBackgroundColor(context.getColor(request.getStateColorInt()));
                requestState.setBackgroundTintList(context.getResources().getColorStateList(request.getStateColorInt()));
            }

        }
    }

    /**
     *
     * @return the filter object.
     */
    @NonNull
    @Override
    public Filter getFilter()
    {
        if(filterObject == null)
        {
            filterObject = new FilterObject();
        }
        return filterObject;
    }

    /**
     * A class responsible for filtering a categories list.
     */
    private class FilterObject extends Filter
    {
        /**
         * Filters the list by the categories' titles.
         * @param charSequence - the user input text.
         * @return FilterResults object containing all relevant information - list and size.
         */
        @Override
        protected FilterResults performFiltering(CharSequence charSequence)
        {
            List<Request> filteredList = new ArrayList<>();
            FilterResults filterResults = new FilterResults();

            if(charSequence.toString().isEmpty())
            {
                filteredList.addAll(listAll);
            }
            else
            {
                for(Request request : listAll)
                {
                    if(request.getTitle().toLowerCase().contains(charSequence.toString().toLowerCase()))
                    {
                        filteredList.add(request);
                    }
                }
            }

            filterResults.values = filteredList;
            filterResults.count = filteredList.size();
            return filterResults;
        }

        /**
         * Makes the changes and updates the adapter and the actual list.
         * @param charSequence - the user input text.
         * @param filterResults - the returned filterResults object.
         */
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults)
        {
            list.clear();
            list.addAll((ArrayList<Request>) filterResults.values);
            notifyDataSetChanged();
        }
    }


}


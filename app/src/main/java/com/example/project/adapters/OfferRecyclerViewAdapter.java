package com.example.project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.interfaces.RecyclerItemClickListener;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.DateAndTimeManager;
import com.example.project.objects.Offer;

import java.util.ArrayList;

public class OfferRecyclerViewAdapter extends RecyclerView.Adapter
{
    /**
     * The context of the relevant activity.
     */
    private Context context;
    /**
     * The list of offers.
     */
    private ArrayList<Offer> list;
    /**
     * The interface for listening and acting accordingly if user clicks on item in the list.
     */
    private RecyclerItemClickListener recyclerItemClickListener;


    /**
     *
     * @param context - relevant context.
     * @param list - the offers list (array list object).
     * Initializes context and list.
     * Sets the recycler view item onClick interface.
     */
    public OfferRecyclerViewAdapter(Context context, ArrayList<Offer> list, RecyclerItemClickListener recyclerItemClickListener)
    {
        this.context = context;
        this.list = list;
        this.recyclerItemClickListener = recyclerItemClickListener;
    }

    /**
     *
     * @param parent - the view containing all child views.
     * @param viewType - the type of the view.
     * @return the correct view with inflating it.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_item_card_view_horizontal_layout, parent, false);
        if(view != null)
        {
            return new OfferRecyclerViewAdapter.OfferViewHolder(view);
        }
        return null;
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
        if(position >= getItemCount()) return;

        ((OfferRecyclerViewAdapter.OfferViewHolder) holder).bind(position);
        holder.itemView.setOnClickListener(view ->
        {
            recyclerItemClickListener.onItemClick(list.get(position));
        });
    }

    /**
     * @return the size of the offers list.
     */
    @Override
    public int getItemCount()
    {
        return list.size();
    }


    /**
     * A class for an offer view.
     */
    private class OfferViewHolder extends RecyclerView.ViewHolder
    {
        /**
         * Offer date and time.
         */
        private TextView dateAndTimeTV;
        /**
         * Offer current state.
         */
        private TextView stateTV;
        /**
         * Offer price.
         */
        private TextView priceTV;
        /**
         * Offer description.
         */
        private TextView descriptionTV;


        /**
         * Initializes the current item view widgets.
         * @param itemView - the view.
         */
        public OfferViewHolder(@NonNull View itemView)
        {
            super(itemView);
            dateAndTimeTV = itemView.findViewById(R.id.oicvhl_offer_date_and_time_text_view);
            stateTV = itemView.findViewById(R.id.oicvhl_offer_state_text_view);
            priceTV = itemView.findViewById(R.id.oicvhl_offer_price_text_view);
            descriptionTV = itemView.findViewById(R.id.offer_description_text_view);
        }

        /**
         * @param position - current position of request in list.
         * Sets the information of the offer (sets the view).
         */
        public void bind(int position)
        {
            Offer offer = list.get(position);
            dateAndTimeTV.setText(DateAndTimeManager.getDateAndTimeString(offer.getMilliseconds()));
            if(offer.getStateColorInt() != -1 && !offer.getStateString().isEmpty())
            {
                stateTV.setText(offer.getStateString());
                stateTV.setBackgroundColor(context.getColor(offer.getStateColorInt()));
                stateTV.setBackgroundTintList(context.getResources().getColorStateList(offer.getStateColorInt()));
            }
            priceTV.setText(""+ offer.getPrice());
            descriptionTV.setText(offer.getDescription());
        }
    }
}

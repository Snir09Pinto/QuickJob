package com.example.project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.interfaces.RecyclerItemClickListener;
import com.example.project.managers.ImageManager;
import com.example.project.objects.Subcategory;

import java.util.ArrayList;

public class SubcategoryRecyclerViewAdapter extends RecyclerView.Adapter
{
    /**
     * The context from relevant activity
     */
    private Context context;
    /**
     * The current list of subcategories
     */
    private ArrayList<Subcategory> list;
    /**
     * The interface for listening and acting accordingly if user clicks on item in the list.
     */
    private RecyclerItemClickListener recyclerItemClickListener;


    /**
     * Constructor - initializes the context, two lists and filter object.
     * @param context - the relevant context.
     * @param list - the subcategories list.
     * @param recyclerItemClickListener - the onClick interface.
     */
    public SubcategoryRecyclerViewAdapter(Context context, ArrayList<Subcategory> list, RecyclerItemClickListener recyclerItemClickListener)
    {
        this.context = context;
        this.list = list;
        this.recyclerItemClickListener = recyclerItemClickListener;
    }



    /**
     * @param parent - the view containing all child views.
     * @param viewType - the type of the view.
     * @return the correct view (view of review).
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subcategory_item_cardview_layout, parent, false);
        if(view != null)
        {
            return new SubcategoryViewHolder(view);
        }
        return null;
    }

    /**
     * Calls bind() function for the current item's view.
     * @param holder - the view.
     * @param position - current position of item in list.
     * Sets an onClick interface to each item (subcategory).
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        ((SubcategoryRecyclerViewAdapter.SubcategoryViewHolder) holder).bind(position);
        holder.itemView.setOnClickListener(view ->
        {
            recyclerItemClickListener.onItemClick(list.get(position));
        });
    }

    /**
     *
     * @return size of subcategories list.
     */
    @Override
    public int getItemCount()
    {
        return list.size();
    }


    /**
     * A class the for a subcategory item view.
     */
    private class SubcategoryViewHolder extends RecyclerView.ViewHolder
    {
        /**
         * Subcategory image view.
         */
        private ImageView subcategoryImage;
        /**
         * Subcategory title (text view).
         */
        private TextView title;

        /**
         * Initializes the current item view widgets.
         * @param itemView - the view.
         */
        public SubcategoryViewHolder(@NonNull View itemView)
        {
            super(itemView);
            subcategoryImage = itemView.findViewById(R.id.sicl_subcategory_image_image_view);
            title = itemView.findViewById(R.id.sicl_subcategory_title_text_view);
        }

        /**
         * @param position - current position of subcategory in list.
         * Sets the information of the subcategory (sets the view).
         */
        public void bind(int position)
        {
            Subcategory subcategory = list.get(position);
            title.setText(subcategory.getTitle());
            ImageManager.setImageViewString(context, subcategory.getSubcategoryImage(), subcategoryImage, R.drawable.gallery_icon);
        }
    }
}

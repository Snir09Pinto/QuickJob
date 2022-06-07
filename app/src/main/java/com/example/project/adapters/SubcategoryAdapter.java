package com.example.project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.project.managers.ImageManager;
import com.example.project.objects.Category;
import com.example.project.objects.Subcategory;
import com.example.project.R;

import java.util.ArrayList;
import java.util.List;

public class SubcategoryAdapter extends ArrayAdapter<Subcategory> implements Filterable
{
    /**
     * The context from relevant activity
     */
    private Context context;
    /**
     * The current list of subcategories
     */
    private List<Subcategory> objects;
    /**
     * A copy of the full list of subcategories.
     */
    private List<Subcategory> objectsAll;
    /**
     * An object responsible for filtering the subcategories list.
     */
    private FilterObject filterObject;

    /**
     * Constructor - initializes the context, two lists and filter object.
     * Calls parent function - super(context, resource, textViewResourceId, objects).
     * @param context
     * @param resource
     * @param textViewResourceId
     * @param objects
     */
    public SubcategoryAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<Subcategory> objects)
    {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.objects = objects;
        this.objectsAll = new ArrayList<>(objects);

        getFilter();
    }

    /**
     * inflates and returns the view of current subcategory item in the list.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.category_and_subcategory_item_layout, parent, false);


        /**
         * Subcategory title widget.
         */
        TextView title = (TextView)view.findViewById(R.id.casil_item_title_text_view);

        /**
         * Subcategory image widget.
         */
        ImageView image = (ImageView)view.findViewById(R.id.casil_item_image_image_view);

        /**
         * Current Subcategory object.
         */
        Subcategory temp = objects.get(position);

        if(temp != null)
        {
            title.setText(temp.getTitle());
            ImageManager.setImageViewString(context, temp.getSubcategoryImage(), image, R.drawable.gallery_icon);
        }

        return view;
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
     * A class responsible for filtering a subcategories list.
     */
    private class FilterObject extends Filter
    {
        /**
         * Filters the list by the subcategories' titles.
         * @param charSequence - the user input text.
         * @return FilterResults object containing all relevant information - list and size.
         */
        @Override
        protected FilterResults performFiltering(CharSequence charSequence)
        {
            List<Subcategory> filteredList = new ArrayList<>();
            FilterResults filterResults = new FilterResults();

            if(charSequence.toString().isEmpty())
            {
                filteredList.addAll(objectsAll);
            }
            else
            {
                for(Subcategory subcategory : objectsAll)
                {
                    if(subcategory.getTitle().toLowerCase().contains(charSequence.toString().toLowerCase()))
                    {
                        filteredList.add(subcategory);
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
            objects.clear();
            objects.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }

    }
}
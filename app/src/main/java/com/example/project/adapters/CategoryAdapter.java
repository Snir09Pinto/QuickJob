package com.example.project.adapters;

import android.app.Activity;
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
import com.example.project.R;

import java.util.ArrayList;
import java.util.List;

/**
 * An adapter for handling the categories list.
 */
public class CategoryAdapter extends ArrayAdapter<Category> implements Filterable
{
    /**
     * The context from relevant activity
     */
    private Context context;
    /**
     * The current list of categories
     */
    private List<Category> objects;
    /**
     * A copy of the full list of categories.
     */
    private List<Category> objectsAll;
    /**
     * An object responsible for filtering the list.
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
    public CategoryAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<Category> objects)
    {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.objects = objects;
        this.objectsAll = new ArrayList<>(objects);

        getFilter();
    }

    /**
     * inflates and returns the view of current category item in the list.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.category_and_subcategory_item_layout, parent, false);


        /**
         * Category title widget.
         */
        TextView title = (TextView)view.findViewById(R.id.casil_item_title_text_view);
        /**
         * Category image widget.
         */
        ImageView image = (ImageView)view.findViewById(R.id.casil_item_image_image_view);

        /**
         * Current Category object.
         */
        Category temp = objects.get(position);

        if(temp != null)
        {
            title.setText(temp.getTitle());
            ImageManager.setImageViewString(context, temp.getCategoryImage(), image, R.drawable.gallery_icon);
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
            List<Category> filteredList = new ArrayList<>();
            FilterResults filterResults = new FilterResults();

            if(charSequence.toString().isEmpty())
            {
                filteredList.addAll(objectsAll);
            }
            else
                {
                for(Category category : objectsAll)
                {
                    if(category.getTitle().toLowerCase().contains(charSequence.toString().toLowerCase()))
                    {
                        filteredList.add(category);
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


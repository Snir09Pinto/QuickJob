package com.example.project.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import com.example.project.activities.SearchSubcategoriesActivity;
import com.example.project.adapters.CategoryAdapter;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.FirebaseManager;
import com.example.project.R;

/**
 * A class for showing the user a list of all categories in the app
 */
public class SearchCategoriesFragment extends Fragment
{
    /**
     * The view of the screen (xml).
     */
    private View view;
    /**
     * The categories list view.
     */
    private ListView categoriesLV;
    /**
     * The categories list adapter.
     */
    private CategoryAdapter categoryAdapter;
    /**
     * The toolbar - search.
     */
    private Toolbar toolbar;

    /**
     * The function initializes the view and calls init().
     * Also sets an optionsMenu in the fragment.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.search_fragment_layout, container, false);
        setHasOptionsMenu(true);
        init();
        return view;
    }

    /**
     * The function initializes all variables and widgets of the class.
     * Calls initToolBar() and listViewListener().
     */
    public void init()
    {
        categoriesLV = view.findViewById(R.id.sfl_categories_list_view);
        categoryAdapter = new CategoryAdapter(getContext(), 0, 0, FirebaseManager.categories);
        categoriesLV.setAdapter(categoryAdapter);

        initToolBar();


        listViewListener();
    }

    /**
     * Initializes the toolbar.
     */
    private void initToolBar()
    {
        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Explore");
    }

    /**
     * Sets the categories list view onClick method.
     */
    private void listViewListener()
    {
        categoriesLV.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                /**
                 * Move to the subcategories search activity.
                 */
                Intent intent = new Intent(getContext(), SearchSubcategoriesActivity.class);
                intent.putExtra(ConstantsManager.CATEGORY, categoryAdapter.getItem(i)); //so no need to handle adapter view indexes boundaries
                startActivity(intent);
            }
        });
    }


    /**
     * Creates an options menu - search is enabled.
     * Filters the categories by title if user's searching something with a specific word or character.
     * @param menu - the menu (search button).
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        MenuInflater menuInflater = getActivity().getMenuInflater();
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
                categoryAdapter.getFilter().filter(s);
                return false;
            }
        });


        super.onCreateOptionsMenu(menu, inflater);
    }

}


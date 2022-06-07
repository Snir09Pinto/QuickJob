package com.example.project.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import com.example.project.R;
import com.example.project.adapters.SubcategoryAdapter;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.FirebaseManager;
import com.example.project.objects.Category;

/**
 * A class for choosing a subcategory from the wide variety of subcategories.
 */
public class SearchSubcategoriesActivity extends AppCompatActivity
{

    /**
     * The list view of the subcategories
     */
    private ListView subcategoriesLV;
    /**
     * The adapter of the subcategories list view
     */
    private SubcategoryAdapter subcategoryAdapter;
    /**
     * The toolbar - search subcategory
     */
    private Toolbar toolbar;
    /**
     * The previous chosen category - which all subcategories belong to
     */
    private Category category;
    /**
     * The previous chosen category index
     */
    private int categoryIndex;

    /**
     * Initializes the view.
     * Calls init().
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_fragment_layout);
        init();
    }

    /**
     * Initializes the widgets and variables
     * Calls initToolBar(), listViewListener().
     */
    public void init()
    {
        category = (Category) getIntent().getSerializableExtra(ConstantsManager.CATEGORY);
        categoryIndex = getCategoryIndex(category);
        subcategoriesLV = findViewById(R.id.sfl_categories_list_view);
        subcategoryAdapter = new SubcategoryAdapter(SearchSubcategoriesActivity.this, 0, 0, FirebaseManager.subcategories.get(categoryIndex));
        subcategoriesLV.setAdapter(subcategoryAdapter);


        initToolBar();

        listViewListener();
    }

    /**
     *
     * @param category - the chosen category object.
     * @return the index of the category in the CONSTANT categories list.
     */
    private int getCategoryIndex(Category category)
    {
        int index = 0;
        for(int i = 0; i < FirebaseManager.categories.size(); i++)
        {
            if(category.getCategoryId().equals(FirebaseManager.categories.get(i).getCategoryId()))
            {
                index = i;
                break;
            }
        }

        return index;
    }

    /**
     * Initializes the toolbar.
     */
    private void initToolBar()
    {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(category.getTitle());
    }

    /**
     * Sets the list view listener.
     */
    private void listViewListener()
    {
        subcategoriesLV.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                /**
                 * Goes to search requests activity in case a subcategory was clicked.
                 */
                Intent intent = new Intent(SearchSubcategoriesActivity.this, SearchRequestsActivity.class);
                intent.putExtra(ConstantsManager.SUBCATEGORY, subcategoryAdapter.getItem(i)); //so no need to handle adapter view indexes boundaries
                startActivity(intent);
            }
        });
    }

    /**
     *
     * @param item - the clicked item.
     * @return true if the clicked item was the "home" item.
     * Calls finish() - closes the activity, in case "home" was pressed.
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
     *
     * @param menu - the search menu.
     * @return whether a text input was committed.
     * updates the subcategories list with text filtering.
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
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s)
            {
                subcategoryAdapter.getFilter().filter(s);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

}

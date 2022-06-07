package com.example.project.managers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.project.activities.LoginActivity;
import com.example.project.objects.Category;
import com.example.project.objects.Subcategory;
import com.example.project.objects.User;
import com.example.project.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A class responsible for launching the app with correct screen.
 */
public class LaunchManager extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener
{
    /**
     * The navigation bar (fragments navigation).
     */
    private BottomNavigationView bottomNavigationView;
    /**
     * The progress dialog (loading home screen).
     */
    private ProgressDialog progressDialog;

    /**
     * Initializes the view and calls prepare().
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_manager_layout);
        prepare();
    }


    /**
     * Initializes all constants, categories, subcategories, Managers, shared preferences and more.
     * Main Calls: ConstantsManager.initConstArrays(), setCategoriesListAndSubcategoriesLists(), navigationViewSetListener(), UserManager.setSharedPreferences(getApplicationContext()).
     *
     */
    private void prepare()
    {
        ConstantsManager.initConstArrays();
        setCategoriesListAndSubcategoriesLists();

        progressDialog = new ProgressDialog(LaunchManager.this);
        progressDialog.show();
        bottomNavigationView = findViewById(R.id.lml_bottom_navigation_view);
        navigationViewSetListener();
        UserManager.setSharedPreferences(getApplicationContext());
        NavigationFragmentManager.setFragmentManager(getSupportFragmentManager());
        NavigationFragmentManager.getFragmentManager().addOnBackStackChangedListener(this);
    }


    /**
     * Sets categories list and subcategories lists.
     */
    public void setCategoriesListAndSubcategoriesLists()
    {
        setCategoriesList();
    }

    /**
     * Sets the categories constant list from database.
     * Calls setSubcategoriesLists().
     */
    private void setCategoriesList()
    {
        FirebaseManager.categories = new ArrayList<>();
        FirebaseManager.categoriesStrings = new ArrayList<>();
        FirebaseManager.getDBReference().collection(ConstantsManager.CATEGORIES_COLLECTION).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot ds : queryDocumentSnapshots)
                {
                    if(ds != null && ds.exists())
                    {
                        Category category = ds.toObject(Category.class);
                        FirebaseManager.categories.add(category);
                        FirebaseManager.categoriesStrings.add(category.getTitle());
                    }
                }
                setSubcategoriesLists();
            }
        });
    }

    /**
     * Sets the subcategories constant list from database.
     * Calls handleLaunch().
     */
    private void setSubcategoriesLists()
    {
        FirebaseManager.subcategories = new ArrayList<ArrayList<Subcategory>>();
        FirebaseManager.subcategoriesStrings = new ArrayList<ArrayList<String>>();
        FirebaseManager.getDBReference().collection(ConstantsManager.SUBCATEGORIES_COLLECTION).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                for(Category category : FirebaseManager.categories)
                {
                    ArrayList<Subcategory> currentList = new ArrayList<>();
                    ArrayList<String> currentListStrings = new ArrayList<>();
                    for(DocumentSnapshot ds : queryDocumentSnapshots)
                    {
                        if(ds != null && ds.exists())
                        {
                            Subcategory subcategory = ds.toObject(Subcategory.class);
                            if(subcategory.getCategoryId().equals(category.getCategoryId()))
                            {
                                currentList.add(subcategory);
                                currentListStrings.add(subcategory.getTitle());
                            }
                        }
                    }
                    FirebaseManager.subcategories.add(currentList);
                    FirebaseManager.subcategoriesStrings.add(currentListStrings);
                }
                handleLaunch();
            }
        });
    }


    /**
     * Sets the navigation view items listener.
     */
    private void navigationViewSetListener()
    {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.search_item:
                        NavigationFragmentManager.launchFragment(ConstantsManager.menuFragments[1]);
                        break;
                    case R.id.chat_item:
                        NavigationFragmentManager.launchFragment(ConstantsManager.menuFragments[2]);
                        break;
                    case R.id.map_item:
                        NavigationFragmentManager.launchFragment(ConstantsManager.menuFragments[3]);
                        break;
                    case R.id.profile_item:
                        NavigationFragmentManager.launchFragment(ConstantsManager.menuFragments[4]);
                        break;
                    default:
                        NavigationFragmentManager.launchFragment(ConstantsManager.menuFragments[0]);
                }
                return true;
            }
        });
    }


    /**
     * Checks if user was logged before and acts accordingly.
     * Calls setWasLoggedUserToCurrentUserAndUpdateUI() or launchLoginActivity().
     */
    private void handleLaunch()
    {
        if(UserManager.wasUserLogged())
        {
            setWasLoggedUserToCurrentUserAndUpdateUI();
        }
        else
        {
            launchLoginActivity();
            progressDialog.dismiss();
        }
    }

    /**
     * Sets the logged user to current user and calls updateUI().
     */
    private void setWasLoggedUserToCurrentUserAndUpdateUI()
    {
        String wasLoggedUserId = UserManager.getSharedPreferences().getString(ConstantsManager.USER_USER_ID_FIELD, "");
        FirebaseManager.getDBReference().collection(ConstantsManager.USERS_COLLECTION).
                whereEqualTo(ConstantsManager.USER_USER_ID_FIELD, wasLoggedUserId)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                if(queryDocumentSnapshots.isEmpty())
                {
                    launchLoginActivity();
                    progressDialog.dismiss();
                    return;
                }
                for(DocumentSnapshot ds : queryDocumentSnapshots)
                {
                    if(ds != null && ds.exists())
                    {
                        User user = ds.toObject(User.class);
                        DocumentReference documentReference = ds.getReference();
                        UserManager.setCurrentUser(user);
                        UserManager.setDocumentReference(documentReference);
                        UserManager.setCurrentUserId(user.getUserId());
                        documentReference.set(user.getUserInfo());
                        updateUI();
                        progressDialog.dismiss();
                    }
                }
            }
        });


    }

    /**
     * Launches home fragment.
     */
    private void updateUI()
    {

        NavigationFragmentManager.launchFragment(ConstantsManager.menuFragments[0]);
    }

    /**
     * Launches login activity.
     */
    private void launchLoginActivity()
    {
        startActivity(new Intent(this, LoginActivity.class));
    }


    /**
     * Updates the current item color on navigation bar if he goes back.
     * Calls updateCurrentFragmentItemColor().
     */
    @Override
    public void onBackStackChanged()
    {
        if(NavigationFragmentManager.getFragmentManager().getBackStackEntryCount() > 0)
        {
            updateCurrentFragmentItemColor();
        }
    }

    /**
     * Exits the app and moves it to background.
     */
    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

    /**
     * Updating the correct item (setting it to "checked").
     */
    private void updateCurrentFragmentItemColor()
    {
        for(int i = 0; i < ConstantsManager.menuFragments.length; i++)
        {
            if(ConstantsManager.menuFragments[i].getClass().toString().equals(NavigationFragmentManager.getFragmentManager().findFragmentById(R.id.lml_fragment_container).getClass().toString()))
            {
                bottomNavigationView.getMenu().getItem(i).setChecked(true);
                break;
            }
        }
    }




}

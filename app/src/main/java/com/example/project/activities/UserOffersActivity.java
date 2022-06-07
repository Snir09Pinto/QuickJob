package com.example.project.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.project.R;
import com.example.project.adapters.ViewPagerAdapter;
import com.example.project.managers.ConstantsManager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * The class shows the user its offers.
 */
public class UserOffersActivity extends FragmentActivity
{
    /**
     * A tab layout to represent the titles and states of each offers list.
     */
    private TabLayout tabLayout;
    /**
     * A view pager to contain the offers list view.
     */
    private ViewPager2 viewPager;
    /**
     * A view pager adapter, to show the correct offers list view.
     */
    private ViewPagerAdapter adapter;


    /**
     * Initializes the view.
     * @param savedInstanceState
     * Calls init().
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_offers_activity_layout);
        init();
    }


    /**
     * Initializes the tab layout and view pager.
     * Calls setUpViewPager().
     */
    private void init()
    {
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager2) findViewById(R.id.view_pager);

        setUpViewPager();
    }

    /**
     * Sets the view pager adapter - offer's type.
     * Attaches the tab layout to the view pager.
     */
    private void setUpViewPager()
    {
        adapter = new ViewPagerAdapter(this, ConstantsManager.OFFERS_ADAPTER_CODE);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy()
        {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position)
            {
                tab.setText(ConstantsManager.tabOptions[position]);
            }
        }).attach();

        viewPager.setCurrentItem(0);
    }

}

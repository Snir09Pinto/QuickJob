package com.example.project.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.project.managers.ConstantsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * An adapter for handling the offers and requests lists belongs to the user.
 */
public class ViewPagerAdapter extends FragmentStateAdapter {

    /**
     * Type of adapter - requests or offers.
     */
    private int type;

    /**
     * Constructor.
     * @param fragmentActivity - the host activity.
     * @param type - the type of view - requests or offers.
     */
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, int type)
    {
        super(fragmentActivity);
        this.type = type;
    }


    /**
     *
     * @param position - the current page position (a whole screen view).
     * @return the correct view - offers list or requests list.
     */
    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
        if(type == ConstantsManager.OFFERS_ADAPTER_CODE)
        {
            return ConstantsManager.offerTabFragments[position];
        }
        else if(type == ConstantsManager.REQUESTS_ADAPTER_CODE)
        {
            return ConstantsManager.requestTabFragments[position];
        }
        return null;
    }


    /**
     * @return the amount of "pages".
     */
    @Override
    public int getItemCount()
    {
        return ConstantsManager.tabOptions.length;
    }
}

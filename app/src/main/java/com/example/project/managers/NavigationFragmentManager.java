package com.example.project.managers;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.project.R;

/**
 * A class responsible for moving through the fragments in navigation bar.
 */
public class NavigationFragmentManager
{
    /**
     * The fragment manager.
     */
    private static FragmentManager fragmentManager;

    /**
     *
     * @return fragmentManager
     */
    public static FragmentManager getFragmentManager()
    {
        return fragmentManager;
    }

    /**
     * Sets the fragment manager to given parameter.
     * @param fragmentManager
     */
    public static void setFragmentManager(FragmentManager fragmentManager)
    {
        NavigationFragmentManager.fragmentManager = fragmentManager;
    }

    /**
     * "Launches" the new fragment (parameter).
     * @param fragment
     */
    public static void launchFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.lml_fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
    }

}


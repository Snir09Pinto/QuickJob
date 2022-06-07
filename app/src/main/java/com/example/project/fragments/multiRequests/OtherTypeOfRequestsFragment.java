package com.example.project.fragments.multiRequests;

import android.view.View;

import com.example.project.R;

public class OtherTypeOfRequestsFragment extends MultiRequestsFragment
{

    /**
     * @return fragment with only list of requests.
     */
    @Override
    protected View getFragmentView()
    {
        return getLayoutInflater().inflate(R.layout.multi_requests_fragment_layout, null);
    }

    @Override
    protected void prepare()
    {

    }



}
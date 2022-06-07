package com.example.project.fragments.multiRequests;

import android.content.Intent;
import android.view.View;

import com.example.project.R;
import com.example.project.activities.CreateRequestActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PendingRequestsFragment extends MultiRequestsFragment implements View.OnClickListener
{
    /**
     * Add request button.
     */
    private FloatingActionButton fab;

    /**
     * @return fragment with list of requests and add button.
     */
    @Override
    protected View getFragmentView()
    {
        return getLayoutInflater().inflate(R.layout.user_pending_requests_fragment_layout, null);
    }

    /**
     * Calls initButtonListener().
     */
    @Override
    protected void prepare()
    {
        initButtonListener();
    }


    /**
     * Initializes the button and its listener.
     */
    private void initButtonListener()
    {
        fab = view.findViewById(R.id.uprfl_add_request_fab);
        fab.setOnClickListener(this);
    }

    /**
     * Sets the button onClick.
     * @param view - the clicked view.
     */
    @Override
    public void onClick(View view)
    {
        if(view.getId() == fab.getId())
        {
            /**
             * Launches the "create request activity".
             */
            Intent intent = new Intent(getContext(), CreateRequestActivity.class);
            startActivity(intent);
        }
    }

}

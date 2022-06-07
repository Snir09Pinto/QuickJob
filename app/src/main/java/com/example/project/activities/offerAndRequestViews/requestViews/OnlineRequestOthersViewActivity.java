package com.example.project.activities.offerAndRequestViews.requestViews;

import android.view.View;
import android.widget.TextView;

import com.example.project.R;
import com.example.project.objects.OnlineRequest;

public class OnlineRequestOthersViewActivity extends OthersViewActivity<OnlineRequest>
{

    /**
     * The request link text view.
     */
    private TextView linkTV;


    /**
     *
     * @return view - online request others view.
     */
    @Override
    protected View getView()
    {
        return getLayoutInflater().inflate(R.layout.online_request_others_view_activity_layout, null);
    }

    /**
     * Sets the link widget.
     */
    @Override
    protected void prepare()
    {
        linkTV = view.findViewById(R.id.uva_request_link_text_view);
        if(request.getLink().isEmpty())
        {
            linkTV.setText("No Link Attached");
        }
        else{
            linkTV.setText(request.getLink());
        }
    }
}

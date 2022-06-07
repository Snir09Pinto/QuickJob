package com.example.project.activities.offerAndRequestViews.requestViews;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.example.project.R;
import com.example.project.activities.MapActivity;
import com.example.project.managers.ConstantsManager;
import com.example.project.objects.OfflineRequest;
import com.google.android.gms.maps.model.LatLng;

public class OfflineRequestOthersViewActivity extends OthersViewActivity<OfflineRequest>
{
    /**
     * A button to view the request location and more details about the location - duration, distance and highlighted path.
     */
    private Button viewLocationBtn;


    /**
     *
     * @return view - offline request user view.
     */
    @Override
    protected View getView()
    {
        return getLayoutInflater().inflate(R.layout.offline_request_others_view_activity_layout, null);
    }

    /**
     * Sets the view location button.
     * onClick - moves the user to new activity to see the details about the location of the request.
     */
    @Override
    protected void prepare()
    {
        viewLocationBtn = view.findViewById(R.id.view_request_location_button);
        viewLocationBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /**
                 * Launches MapActivity
                 */
                Intent intent = new Intent(OfflineRequestOthersViewActivity.this, MapActivity.class);
                intent.putExtra(ConstantsManager.LAT_LNG, new LatLng(request.getLatitude(), request.getLongitude()));
                startActivity(intent);
            }
        });
    }


}

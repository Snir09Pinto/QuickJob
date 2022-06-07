package com.example.project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.project.objects.OfflineRequest;
import com.example.project.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * A class for showing and handling the marker info window details.
 */
public class CustomMarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
{
    /**
     * The view of the marker window.
     */
    private final View window;
    /**
     * The context of the relevant activity.
     */
    private Context context;

    /**
     * Constructor
     * @param context - The context of the relevant activity.
     * Inflates and initializes the view and the context.
     */
    public CustomMarkerInfoWindowAdapter(Context context)
    {
        this.context = context;
        this.window = LayoutInflater.from(context).inflate(R.layout.custom_marker_info_window_layout, null);

    }

    /**
     * Updates and renders the text of the window wht the request's toString() method.
     * @param marker - current marker.
     * @param view - marker's view.
     */
    private void renderWindowText(Marker marker, View view)
    {
        OfflineRequest offlineRequest = (OfflineRequest) marker.getTag();
        TextView markerTV = (TextView) view.findViewById(R.id.cmiwl_marker_text_view);
        if(offlineRequest != null)
        {
            markerTV.setText(offlineRequest.toString());
        }
    }


    /**
     *
     * @param marker - current marker.
     * @return getting the marker's view for editing only.
     */
    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker)
    {
        renderWindowText(marker, window);
        return window;
    }

    /**
     *
     * @param marker - current marker.
     * @return the marker's view.
     */
    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker)
    {
        renderWindowText(marker, window);
        return window;
    }
}

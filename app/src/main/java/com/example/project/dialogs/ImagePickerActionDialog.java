package com.example.project.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.project.R;
import com.example.project.managers.ConstantsManager;

/**
 * A dialog class for handling the "image picking" process.
 */
public class ImagePickerActionDialog extends Dialog implements View.OnClickListener
{
    /**
     * The relevant context.
     */
    private Context context;
    /**
     * The camera image button.
     */
    private ImageButton cameraOptionBtn;
    /**
     * The gallery image button.
     */
    private ImageButton galleryOptionBtn;
    /**
     * The cancel text view.
     */
    private TextView cancelTV;

    /**
     * Constructor initializing the context.
     * @param context - relevant context.
     * Calls parent function - super(context).
     */
    public ImagePickerActionDialog(@NonNull Context context)
    {
        super(context);
        this.context = context;
    }

    /**
     * Shows the dialog on the screen.
     * Calls initDialogButtons().
     */
    public void showDialog()
    {
        this.setContentView(R.layout.camera_action_dialog_layout);
        this.setCancelable(false);
        initDialogButtons();
        this.show();
    }

    /**
     * Initializes the dialog buttons.
     */
    private void initDialogButtons()
    {
        cancelTV = this.findViewById(R.id.cadl_cancel_text_view);
        cameraOptionBtn = this.findViewById(R.id.cadl_camera_option_image_button);
        galleryOptionBtn = this.findViewById(R.id.cadl_gallery_option_image_button);
        cancelTV.setOnClickListener(this);
        cameraOptionBtn.setOnClickListener(this);
        galleryOptionBtn.setOnClickListener(this);
    }

    /**
     * Launches the gallery activity (on the phone) with an intent.
     */
    private void getGalleryImage()
    {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Activity activity = (Activity) context;
        activity.startActivityForResult(galleryIntent, ConstantsManager.GALLERY_REQUEST_CODE);
    }

    /**
     * Launches the camera activity (on the phone) with an intent.
     */
    private void getCameraImage()
    {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Activity activity = (Activity) context;
        activity.startActivityForResult(cameraIntent, ConstantsManager.CAMERA_REQUEST_CODE);
    }


    /**
     * Sets the cancel, camera and gallery image buttons listeners.
     * @param view
     * cancel - dismiss() - exits the dialog.
     * camera - getCameraImage().
     * gallery - getGalleryImage().
     */
    @Override
    public void onClick(View view)
    {
        if(view.getId() == cancelTV.getId())
        {
            this.dismiss();
        }

        else if(view.getId() == cameraOptionBtn.getId())
        {
            getCameraImage();
        }
        else if(view.getId() == galleryOptionBtn.getId())
        {
            getGalleryImage();
        }
    }



}

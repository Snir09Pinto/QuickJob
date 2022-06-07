package com.example.project.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * A class for handling images.
 * All functions use Glide which is a library responsible loading images with different formats - much convenient
 */
public class ImageManager
{
    /**
     * Sets an image view with a string describing an image.
     * @param context
     * @param image
     * @param imageView
     * @param resourceId
     */
    public static void setImageViewString(Context context, String image, ImageView imageView, int resourceId)
    {
            Uri uri;
            if(image == null || image.isEmpty())
            {
                uri = null;
            }
            else
            {
                uri = Uri.parse(image);
            }
            setImageViewURI(context, uri, imageView, resourceId);
    }

    /**
     * Sets an image view with a Uri object.
     * @param context
     * @param uri
     * @param imageView
     * @param resourceId
     */
    public static void setImageViewURI(Context context, Uri uri, ImageView imageView, int resourceId)
    {
        Glide.with(context).load(uri).placeholder(resourceId).error(resourceId).into(imageView);
    }

    /**
     * Sets an image view with a Bitmap object.
     * @param context
     * @param bitmap
     * @param imageView
     * @param resourceId
     */
    public static void setImageViewBitmap(Context context, Bitmap bitmap, ImageView imageView, int resourceId)
    {
        Glide.with(context).load(bitmap).placeholder(resourceId).error(resourceId).into(imageView);
    }
}

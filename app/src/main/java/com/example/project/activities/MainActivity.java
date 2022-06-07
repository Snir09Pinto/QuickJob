package com.example.project.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project.R;
import com.example.project.managers.LaunchManager;

public class MainActivity extends AppCompatActivity
{
    /**
     * The app logo image view.
     */
    private ImageView appLogoIV;
    /**
     * The app title text view.
     */
    private TextView appTitleTV;

    /**
     * The animation.
     */
    private Animation animation;

    /**
     * Calls init() and startAnim().
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        init();
        startAnim();
    }

    /**
     * Initialize logo and title of the app.
     */
    private void init()
    {
        appLogoIV = findViewById(R.id.mal_app_logo_image_view);
        appTitleTV = findViewById(R.id.mal_app_title_text_view);
    }

    /**
     * Starts the fade in animation on the logo and title (duration of 5 seconds - found in the xml).
     */
    private void startAnim()
    {
        animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in);
        animation.reset();
        appTitleTV.clearAnimation();
        appLogoIV.clearAnimation();
        appTitleTV.startAnimation(animation);
        appLogoIV.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                /**
                 * Launches the launchManagerActivity.
                 */
                startActivity(new Intent(MainActivity.this, LaunchManager.class));
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });
    }

}

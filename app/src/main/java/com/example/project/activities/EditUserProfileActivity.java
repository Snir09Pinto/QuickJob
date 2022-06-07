package com.example.project.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.project.R;
import com.example.project.dialogs.ImagePickerActionDialog;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.FirebaseManager;
import com.example.project.managers.ImageManager;
import com.example.project.managers.UserManager;
import com.example.project.objects.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class EditUserProfileActivity extends AppCompatActivity implements View.OnClickListener
{
    /**
     * User image view.
     */
    private ImageView userIV;
    /**
     * User name edit text.
     */
    private EditText usernameET;
    /**
     * User email edit text.
     */
    private EditText emailET;
    /**
     * User phone edit text.
     */
    private EditText phoneET;
    /**
     * User description edit text.
     */
    private EditText descriptionET;
    /**
     * User password edit text.
     */
    private EditText passwordET;
    /**
     * Submit button - to update changes.
     */
    private Button submitBtn;
    /**
     * Cancel button - to undo / ignore changes.
     */
    private Button cancelBtn;
    /**
     * User image card view (layout)
     */
    private CardView userImageCV;
    /**
     * User image uri (gallery)
     */
    private Uri imageUri;
    /**
     * User image bitmap (camera)
     */
    private Bitmap imageBitmap;
    /**
     * Camera action dialog - choose camera or gallery.
     */
    private ImagePickerActionDialog imagePickerActionDialog;
    /**
     * Progress dialog.
     */
    private ProgressDialog progressDialog;

    /**
     * Calls init().
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_profile_activity_layout);
        init();
    }

    /**
     * Initializes all widgets and variables + onClick listeners.
     */
    private void init()
    {
        imagePickerActionDialog = new ImagePickerActionDialog(EditUserProfileActivity.this);
        progressDialog = new ProgressDialog(EditUserProfileActivity.this);
        userIV = findViewById(R.id.user_image_image_view);
        usernameET = findViewById(R.id.eupal_user_name_edit_text);
        emailET = findViewById(R.id.eupal_user_email_edit_text);
        phoneET = findViewById(R.id.eupal_user_phone_edit_text);
        descriptionET = findViewById(R.id.coal_offer_description_edit_text);
        passwordET = findViewById(R.id.eupal_user_password_edit_text);


        ImageManager.setImageViewString(EditUserProfileActivity.this, UserManager.getCurrentUser().getUserImage(), userIV, R.drawable.profile_ic);
        usernameET.setText(UserManager.getCurrentUser().getUsername());
        emailET.setText(UserManager.getCurrentUser().getEmail());
        phoneET.setText(UserManager.getCurrentUser().getPhone());
        descriptionET.setText(UserManager.getCurrentUser().getDescription());
        descriptionET.setMovementMethod(new ScrollingMovementMethod());
        passwordET.setText(UserManager.getCurrentUser().getPassword());

        submitBtn = findViewById(R.id.submit_button);
        cancelBtn = findViewById(R.id.cancel_button);
        userImageCV = findViewById(R.id.eupal_user_image_card_view);

        submitBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        userImageCV.setOnClickListener(this);
    }

    /**
     *
     * @param view
     * Handles submit button - update the user's details.
     * Also cancel button - returns to prev activity (finish).
     * And last - opens the camera action dialog when userImageCV is pressed.
     */
    @Override
    public void onClick(View view)
    {
        if(view.getId() == submitBtn.getId())
        {
            if(fieldsAreValid())
            {
                updateDetails();
                finish();
            }
        }

        else if(view.getId() == cancelBtn.getId())
        {
            finish();
        }
        else if(view.getId() == userImageCV.getId())
        {
            imagePickerActionDialog.showDialog();
        }
    }


    /**
     *
     * @return whether the user's fields are valid or not.
     */
    private boolean fieldsAreValid()
    {
        if(emailET.getText().toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailET.getText().toString()).matches())
        {
            emailET.setError("Invalid Email");
            return false;
        }

        if(phoneET.getText().toString().isEmpty() || !Patterns.PHONE.matcher(phoneET.getText().toString()).matches() || phoneET.getText().length() < 6 || phoneET.getText().length() > 15)
        {
            phoneET.setError("Invalid Phone Number");
            return false;
        }
        if(passwordET.getText().toString().isEmpty()){
            passwordET.setError("Invalid Password");
            return false;
        }
        if(passwordET.getText().toString().length() < 6)
        {
            passwordET.setError("Password is too short, must contain at least 6 characters");
            return false;
        }
        return true;
    }


    /**
     * Handles camera and gallery Activity results.
     * @param requestCode
     * @param resultCode
     * @param data - bitmap or uri.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ConstantsManager.CAMERA_REQUEST_CODE)
        {
            if(resultCode == AppCompatActivity.RESULT_OK)
            {
                imageBitmap = (Bitmap) data.getExtras().get("data");
                ImageManager.setImageViewBitmap(EditUserProfileActivity.this, imageBitmap, userIV, R.drawable.gallery_icon);
                imageUri = null;
            }
        }
        else if(requestCode == ConstantsManager.GALLERY_REQUEST_CODE)
        {
            if(resultCode == AppCompatActivity.RESULT_OK)
            {
                imageUri = data.getData();
                ImageManager.setImageViewURI(EditUserProfileActivity.this, imageUri, userIV, R.drawable.profile_ic);
                imageBitmap = null;
            }
        }
        imagePickerActionDialog.dismiss();
    }

    /**
     * Updates the current user's details (UserManager user object).
     * Uploads an updated image if there was one (to fire storage).
     */
    public void updateDetails()
    {
        UserManager.getCurrentUser().setEmail(emailET.getText().toString());
        UserManager.getCurrentUser().setPassword(passwordET.getText().toString());
        UserManager.getCurrentUser().setDescription(descriptionET.getText().toString());
        UserManager.getCurrentUser().setPhone(phoneET.getText().toString());

        DocumentReference documentReference = UserManager.getDocumentReference();
        documentReference.update(UserManager.getCurrentUser().getUserInfo());

        if(imageUri != null)
        {
            uploadUrlImage(imageUri, ConstantsManager.USERS_IMAGES_PREFIX, documentReference, UserManager.getCurrentUser());
        }
        else if(imageBitmap != null)
        {
            uploadBitmapImage(imageBitmap, ConstantsManager.USERS_IMAGES_PREFIX, documentReference, UserManager.getCurrentUser());
        }
    }

    /**
     *
     * @param bitmap
     * @param prefix
     * @param documentReference
     * @param user
     * uploads the user image (in case of bitmap)
     */
    public void uploadBitmapImage(Bitmap bitmap, String prefix, DocumentReference documentReference, User user)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference imageRef = FirebaseManager.getFSReference().child(prefix).child(user.getUserId());

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                getDownloadUrl(imageRef, user, documentReference);
            }
        });
    }

    /**
     *
     * @param imageUri
     * @param prefix
     * @param documentReference
     * @param user
     * uploads the user image (in case of uri)
     */
    public void uploadUrlImage(Uri imageUri, String prefix, DocumentReference documentReference, User user)
    {
        StorageReference imageRef = FirebaseManager.getFSReference().child(prefix).child(user.getUserId());
        imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                getDownloadUrl(imageRef, user, documentReference);
            }
        });
    }

    /**
     * Gets the url download
     * @param storageReference
     * @param user
     * @param documentReference
     */
    private void getDownloadUrl(StorageReference storageReference, User user, DocumentReference documentReference)
    {
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri)
            {
                setUserImageUrl(uri, user, documentReference);
            }
        });
    }

    /**
     * Sets the user image in both object and database to the downloaded uri (string).
     * @param uri
     * @param user
     * @param documentReference
     */
    private void setUserImageUrl(Uri uri, User user, DocumentReference documentReference)
    {
        user.setUserImage(uri.toString());
        documentReference.update(user.getUserInfo());
        progressDialog.dismiss();
        finish();
    }

}

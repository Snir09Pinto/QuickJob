package com.example.project.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.project.dialogs.ImagePickerActionDialog;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.FirebaseManager;
import com.example.project.managers.ImageManager;
import com.example.project.managers.LaunchManager;
import com.example.project.managers.UserManager;
import com.example.project.objects.User;
import com.example.project.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener
{
    /**
     * The username field widget.
     */
    private TextInputEditText usernameTIET;
    /**
     * The password field widget.
     */
    private TextInputEditText passwordTIET;
    /**
     * The email field widget.
     */
    private TextInputEditText emailTIET;
    /**
     * The phone field widget.
     */
    private TextInputEditText phoneTIET;
    /**
     * The confirm password field widget.
     */
    private TextInputEditText confirmPasswordTIET;
    /**
     * A card view (layout) for the user image (circle shape).
     */
    private CardView userImageCV;
    /**
     * The user image view.
     */
    private ImageView userIV;
    /**
     * A button for registering after filling all details.
     */
    private Button registerBtn;
    /**
     * A text view to move to login activity.
     */
    private TextView loginTV;
    /**
     * A uri for the user image (gallery).
     */
    private Uri imageUri;
    /**
     * A bitmap for the user image (camera).
     */
    private Bitmap imageBitmap;
    /**
     * Progress dialog.
     */
    private ProgressDialog progressDialog;
    /**
     * action dialog - choose between camera and gallery.
     */
    private ImagePickerActionDialog imagePickerActionDialog;


    /**
     * Calls init().
     * Initializes the screen view.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity_layout);
        init();
    }

    /**
     * Initializes the widgets, variables and buttons click listeners as well.
     */
    private void init()
    {
        imagePickerActionDialog = new ImagePickerActionDialog(RegisterActivity.this);
        usernameTIET = findViewById(R.id.ral_user_name_text_input_edit_text);
        passwordTIET = findViewById(R.id.ral_user_password_text_input_edit_text);
        emailTIET = findViewById(R.id.ral_user_email_text_input_edit_text);
        phoneTIET = findViewById(R.id.ral_user_phone_text_input_edit_text);
        registerBtn = findViewById(R.id.ral_register_button);
        loginTV = findViewById(R.id.ral_login_text_view);
        userImageCV = findViewById(R.id.ral_user_image_card_view);
        userIV = findViewById(R.id.ral_user_image_image_view);
        confirmPasswordTIET = findViewById(R.id.ral_confirm_user_password_text_input_edit_text);
        registerBtn.setOnClickListener(this);
        loginTV.setOnClickListener(this);
        userImageCV.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
    }


    /**
     *
     * @return whether the user's fields are valid or not.
     */
    private boolean fieldsAreValid()
    {
        if(usernameTIET.getText().toString().isEmpty())
        {
            usernameTIET.setError("Invalid User Name");
            return false;
        }
        if(emailTIET.getText().toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailTIET.getText().toString()).matches())
        {
            emailTIET.setError("Invalid Email");
            return false;
        }
        if(phoneTIET.getText().toString().isEmpty() || !Patterns.PHONE.matcher(phoneTIET.getText().toString()).matches() || phoneTIET.getText().length() < 6 || phoneTIET.getText().length() > 15)
        {
            phoneTIET.setError("Invalid Phone Number");
            return false;
        }
        if(passwordTIET.getText().toString().isEmpty()){
            passwordTIET.setError("Invalid Password");
            return false;
        }
        if(passwordTIET.getText().toString().length() < 6)
        {
            passwordTIET.setError("Password is too short, must contain at least 6 characters");
            return false;
        }
        if(!confirmPasswordTIET.getText().toString().equals(passwordTIET.getText().toString()))
        {
            confirmPasswordTIET.setError("Passwords don't match");
            return false;
        }
        return true;
    }


    /**
     * Handles the register button, login text view, and user image card view.
     * register - calls fieldsAreValid(), creates a user object and attemptRegistration(user).
     * login - moves to LoginActivity.
     * userImageCV - opens the image picker action dialog.
     * @param view
     */
    @Override
    public void onClick(View view)
    {
        if(view.getId() == registerBtn.getId())
        {
            if(fieldsAreValid())
            {
                User user = getUser();
                attemptRegistration(user);
            }
        }
        else if(view.getId() == loginTV.getId())
        {
            startActivity(new Intent(this, LoginActivity.class));
        }
        else if(view.getId() == userImageCV.getId())
        {
            imagePickerActionDialog.showDialog();
        }

    }

    /**
     *
     * @param user - current user
     * edge cases - user's name is already taken.
     * the function checks this edge case and calls registerUser(user) if everything's fine.
     */
    public void attemptRegistration(User user)
    {
        FirebaseManager.getDBReference().collection(ConstantsManager.USERS_COLLECTION)
                .whereEqualTo(ConstantsManager.USER_USERNAME_FIELD, user.getUsername())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                if(queryDocumentSnapshots.isEmpty())
                {
                    registerUser(user);
                }
                else{
                    usernameTIET.setError("Username is already taken");
                    return;
                }
            }
        });
    }

    /**
     *
     * @param user - current user.
     * The functions tries to register the user and basically add him to firebase database.
     * Calls the uploadUrlImage(...) or uploadBitmapImage(...) in case he chose a image, otherwise calls updateUI().
     */
    private void registerUser(User user)
    {
        progressDialog.show();
        FirebaseManager.getDBReference().collection(ConstantsManager.USERS_COLLECTION).add(user.getUserInfo())
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference)
                    {
                        user.setUserId(documentReference.getId());
                        if(imageUri == null && imageBitmap == null)
                        {
                            UserManager.setCurrentUser(user);
                            UserManager.setDocumentReference(documentReference);
                            UserManager.setCurrentUserId(user.getUserId());
                            documentReference.set(user.getUserInfo());
                            progressDialog.dismiss();
                            updateUI();
                        }
                        else
                        {
                            if(imageUri != null)
                            {
                                uploadUrlImage(imageUri, ConstantsManager.USERS_IMAGES_PREFIX, documentReference, user);
                            }
                            else if(imageBitmap != null)
                            {
                                uploadBitmapImage(imageBitmap, ConstantsManager.USERS_IMAGES_PREFIX, documentReference, user);
                            }
                        }
                    }
                });
    }

    /**
     * Uploads the bitmap to fire storage.
     * @param bitmap - the image of the user.
     * @param prefix - of the file path
     * @param documentReference - of the user in database.
     * @param user - user's object
     * Calls getDownloadUrl(...)
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
     * @param imageUri - the image of the user.
     * @param prefix - of the file path
     * @param documentReference - of the user in database.
     * @param user - user's object
     * Uploads the uri to fire storage.
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
     * Gets the download url from fire storage, and calls  setUserImageUrl(uri, user, documentReference).
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
     * Sets the user image, details and updates firebase data to current data.
     * @param uri
     * @param user
     * @param documentReference
     */
    private void setUserImageUrl(Uri uri, User user, DocumentReference documentReference)
    {
        user.setUserImage(uri.toString());
        UserManager.setCurrentUser(user);
        UserManager.setDocumentReference(documentReference);
        UserManager.setCurrentUserId(user.getUserId());
        documentReference.set(user.getUserInfo());
        progressDialog.dismiss();
        updateUI();
    }


    /**
     * Launches LaunchManagerActivity.
     */
    private void updateUI()
    {
        startActivity(new Intent(RegisterActivity.this, LaunchManager.class));
    }

    /**
     *
     * @return new user with the specified fields.
     */
    private User getUser()
    {
        User user = new User(usernameTIET.getText().toString(), passwordTIET.getText().toString(),
                emailTIET.getText().toString(), phoneTIET.getText().toString(),"", "", "");

        return user;
    }

    /**
     * Handles the imagePickerActionDialog actions: camera or gallery.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ConstantsManager.CAMERA_REQUEST_CODE)
        {
            if(resultCode == RegisterActivity.RESULT_OK)
            {
                imageBitmap = (Bitmap) data.getExtras().get("data");
                ImageManager.setImageViewBitmap(RegisterActivity.this, imageBitmap, userIV, R.drawable.gallery_icon);
                imageUri = null;
            }
        }
        else if(requestCode == ConstantsManager.GALLERY_REQUEST_CODE)
        {
            if(resultCode == RegisterActivity.RESULT_OK)
            {
                imageUri = data.getData();
                ImageManager.setImageViewURI(RegisterActivity.this, imageUri, userIV, R.drawable.profile_ic);
                imageBitmap = null;
            }
        }
        imagePickerActionDialog.dismiss();
    }

    }


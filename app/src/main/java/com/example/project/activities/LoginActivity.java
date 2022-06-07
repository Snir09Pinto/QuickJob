package com.example.project.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project.managers.ConstantsManager;
import com.example.project.managers.DialogsManager;
import com.example.project.managers.FirebaseManager;
import com.example.project.managers.LaunchManager;
import com.example.project.managers.UserManager;
import com.example.project.R;
import com.example.project.objects.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    /**
     * User name TextInputEditText.
     */
    private TextInputEditText userNameTIET;
    /**
     * User password TextInputEditText.
     */
    private TextInputEditText passwordTIET;
    /**
     * Button for login in the app.
     */
    private Button loginBtn;
    /**
     * Text view for moving to register activity (registration).
     */
    private TextView registerTV;
    /**
     * Progress dialog.
     */
    private ProgressDialog progressDialog;

    /**
     * Calls init().
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_layout);
        init();
    }

    /**
     * Initializes all variables and widgets.
     */
    private void init()
    {
        userNameTIET = findViewById(R.id.lal_user_name_text_input_edit_text);
        passwordTIET = findViewById(R.id.lal_user_password_text_input_edit_text);
        loginBtn = findViewById(R.id.lal_login_button);
        registerTV = findViewById(R.id.lal_register_text_view);
        loginBtn.setOnClickListener(this);
        registerTV.setOnClickListener(this);
        UserManager.setSharedPreferences(LoginActivity.this);
        setDialogs();
    }


    /**
     * Sets and initializes the progress dialog
     */
    private void setDialogs()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

    }

    /**
     * "Closing" the app if back button is pressed (moving to background).
     */
    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

    /**
     * Handles login button - tries to login
     * Handles register button - launches register activity.
     * @param view
     *
     * Calls attemptLogin(...), fieldsAreValid() and launchRegisterActivity().
     */
    @Override
    public void onClick(View view)
    {
        if(view.getId() == loginBtn.getId())
        {
            if(areFieldsValid())
            {
                progressDialog.show();
                attemptLogin(userNameTIET.getText().toString(), passwordTIET.getText().toString());
            }
        }
        else if(view.getId() == registerTV.getId())
        {
            launchRegisterActivity();
        }
    }

    /**
     * Tries to login (checking if such user really exists).
     * @param username - name of the user.
     * @param password - password of the user.
     */
    private void attemptLogin(String username, String password)
    {
        FirebaseManager.getDBReference().collection(ConstantsManager.USERS_COLLECTION).whereEqualTo(ConstantsManager.USER_USERNAME_FIELD, username)
                .whereEqualTo(ConstantsManager.USER_PASSWORD_FIELD, password).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                if(queryDocumentSnapshots.isEmpty())
                {
                    progressDialog.dismiss();
                    showWrongUserDetailsDialog();
                }
                for(DocumentSnapshot ds : queryDocumentSnapshots)
                {
                    if(ds!= null && ds.exists())
                    {
                        User user = ds.toObject(User.class);
                        DocumentReference documentReference = ds.getReference();
                        UserManager.setCurrentUser(user);
                        UserManager.setDocumentReference(documentReference);
                        UserManager.setCurrentUserId(user.getUserId());
                        documentReference.set(user.getUserInfo());
                        progressDialog.dismiss();
                        updateUI();
                        return;
                    }
                }
            }
        });
    }

    /**
     * In case the details are wrong, pops an alert dialog, notifying the user something about the user's details is wrong.
     */
    private void showWrongUserDetailsDialog()
    {
        DialogInterface.OnClickListener dialogInterface = new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        };
        DialogsManager.showNeutralAlertDialog(LoginActivity.this,
                "Error occurred while trying to login", "Wrong username or password",
                dialogInterface);
    }


    /**
     * Update UI launches the LaunchManager activity.
     */
    private void updateUI()
    {
        startActivity(new Intent(LoginActivity.this, LaunchManager.class));
    }

    /**
     * Launches the register activity.
     */
    private void launchRegisterActivity()
    {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    /**
     *
     * @return whether the user's fields are valid.
     */
    private boolean areFieldsValid()
    {
        if(userNameTIET == null || passwordTIET == null) return false;
        if(userNameTIET.getText().toString().isEmpty())
        {
            userNameTIET.setError("Invalid username");
            return false;
        }
        if(passwordTIET.getText().toString().isEmpty())
        {
            passwordTIET.setError("Invalid password");
            return false;
        }

        return true;
    }
}

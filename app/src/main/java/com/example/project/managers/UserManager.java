package com.example.project.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.project.objects.User;
import com.google.firebase.firestore.DocumentReference;

/**
 * A class for handling the current user.
 */
public class UserManager
{
    /**
     * Current logged user
     */
    private static User currentUser;
    /**
     * SharedPreference in order to Know if user was already connected before on this device
     */
    private static SharedPreferences sharedPreferences;
    /**
     * Name of the shared preferences file
     */
    private static final String spFilePath = ConstantsManager.spFilePath;
    /**
     * Reference to firebase database document
     */
    private static DocumentReference documentReference;


    /**
     * Sets the current user id in the shared preference to the given parameter.
     * @param userId
     */
    public static void setCurrentUserId(String userId)
    {
        SharedPreferences.Editor editor = UserManager.sharedPreferences.edit();
        editor.putString(ConstantsManager.USER_USER_ID_FIELD, userId);
        editor.commit();
    }


    /**
     * Sets the current user object to given parameter.
     * @param user
     */
    public static void setCurrentUser(User user)
    {
        UserManager.currentUser = user;
    }

    /**
     *
     * @return if user was logged before on current device.
     */
    public static boolean wasUserLogged()
    {
        return !UserManager.sharedPreferences.getString(ConstantsManager.USER_USER_ID_FIELD, "").isEmpty();
    }

    /**
     *
     * @return documentReference
     */
    public static DocumentReference getDocumentReference()
    {
        return documentReference;
    }

    /**
     * Sets the documentReference to the given parameter.
     * @param documentReference
     */
    public static void setDocumentReference(DocumentReference documentReference)
    {
        UserManager.documentReference = documentReference;
    }

    /**
     *
     * @return current user.
     */
    public static User getCurrentUser()
    {
        return UserManager.currentUser;
    }

    /**
     *
     * @return Shared preferences.
     */
    public static SharedPreferences getSharedPreferences()
    {
        return UserManager.sharedPreferences;
    }

    /**
     * Initializes the shared preferences with context.
     * @param context
     */
    public static void setSharedPreferences(Context context)
    {
        UserManager.sharedPreferences = context.getSharedPreferences(spFilePath, Context.MODE_PRIVATE);
    }

    /**
     * Logout the user and resets his details.
     */
    public static void logout()
    {
        documentReference.set(UserManager.currentUser.getUserInfo());
        UserManager.currentUser = null;
        setCurrentUserId("");
    }

}
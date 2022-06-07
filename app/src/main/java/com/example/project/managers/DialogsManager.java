package com.example.project.managers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * A class for handling Dialogs
 */
public class DialogsManager
{
    /**
     * Shows a dialog with only one option - "OK"
     * takes all needed parameters in order to build such dialog and a dialogInterface that programmer can choose what to do.
     * @param context
     * @param title
     * @param message
     * @param dialogInterface
     */
    public static void showNeutralAlertDialog(Context context, String title, String message, DialogInterface.OnClickListener dialogInterface)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setNeutralButton(
                "OK",
                dialogInterface);

        AlertDialog alert = builder.create();
        alert.show();
    }


    /**
     * Shows a dialog with two options - "YES / NO"
     * takes all needed parameters in order to build such dialog and two dialogInterfaces that programmer can choose what to do whether it's a positive or a negative decision by the user.
     * @param context
     * @param title
     * @param message
     * @param positiveDialogInterface
     * @param negativeDialogInterface
     */
    public static void showNegativeOrPositiveAlertDialog(Context context, String title, String message, DialogInterface.OnClickListener positiveDialogInterface, DialogInterface.OnClickListener negativeDialogInterface)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton(
                "Yes",
                positiveDialogInterface);

        builder.setNegativeButton(
                "No",
                negativeDialogInterface);

        AlertDialog alert = builder.create();
        alert.show();
    }
}

package com.example.project.broadcasts;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.project.R;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.FirebaseManager;
import com.example.project.objects.OfflineRequest;
import com.example.project.objects.OnlineRequest;
import com.example.project.objects.Request;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A broadcast receiver for notification.
 */
public class SendNotificationReceiver extends BroadcastReceiver
{
    /**
     * The function builds a notification and notifies in case the current request hasn't been completed yet and the deadline has passed.
     * @param context - relevant context.
     * @param intent - an intent containing the relevant information about the request and other details.
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String appName = context.getString(R.string.app_name);

        /**
         * The notification channel
         */
        NotificationChannel channel = new NotificationChannel(ConstantsManager.NOTIFICATION_CHANNEL, appName, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(appName);

        /**
         * The manager of the notifications - building the channel and posting the notifications.
         */
        NotificationManager manager = (NotificationManager) context.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        /**
         * The id of the current request.
         */
        String requestId = intent.getStringExtra(ConstantsManager.REQUEST_REQUEST_ID_FIELD);

        /**
         * The builder of the notification.
         */
        Notification.Builder builder = new Notification.Builder(context, ConstantsManager.NOTIFICATION_CHANNEL);


        /**
         * Searches for the current request in database.
         * Handles the case of the current request not being completed yet and the deadline passing away.
         */
        FirebaseManager.getDBReference().collection(ConstantsManager.REQUESTS_COLLECTION).whereEqualTo(ConstantsManager.REQUEST_REQUEST_ID_FIELD, requestId)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    if(documentSnapshot != null && documentSnapshot.exists())
                    {
                        Request request = null;
                        if((long) documentSnapshot.get(ConstantsManager.REQUEST_TYPE_FIELD) == (ConstantsManager.OFFLINE_REQUEST_TYPE))
                        {
                            request = documentSnapshot.toObject(OfflineRequest.class);
                        }
                        else if((long) documentSnapshot.get(ConstantsManager.REQUEST_TYPE_FIELD) == (ConstantsManager.ONLINE_REQUEST_TYPE))
                        {
                            request = documentSnapshot.toObject(OnlineRequest.class);
                        }
                        if(request.getState() != ConstantsManager.statesCodes[2])
                        {
                            /**
                             * An intent of request information activity.
                             */
                            Intent requestWindow = request.createUserViewActivity(context);
                            /**
                             * Sets the conditions while launching the activity (if the app is closed or not).
                             */
                            requestWindow.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                            /**
                             * Creates a pending intent of the request content (intent of correct activity and more).
                             */
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, ConstantsManager.PENDING_INTENT_REQUEST_CODE, requestWindow, PendingIntent.FLAG_CANCEL_CURRENT);

                            /**
                             * Builds the notification. Sets its title, content, pending intent and more.
                             */
                            builder
                                    .setContentTitle("Watch request")
                                    .setContentText("One of your requests hasn't been delivered on the planned time")
                                    .setSmallIcon(R.drawable.notification_ic)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true);

                            /**
                             * "Posts" the notification.
                             */
                            manager.notify(0, builder.build());
                        }

                    }
                }
            }
        });

    }
    
}

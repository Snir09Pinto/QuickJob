package com.example.project.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.adapters.MessageAdapter;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.FirebaseManager;
import com.example.project.managers.ImageManager;
import com.example.project.managers.UserManager;
import com.example.project.objects.Message;
import com.example.project.objects.User;
import com.example.project.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener
{

    /**
     * User image image view.
     */
    private ImageView userIV;
    /**
     * Send message layout (button).
     */
    private CardView sendCV;
    /**
     * Message text input.
     */
    private EditText textInput;
    /**
     * Messages recycler view.
     */
    private RecyclerView messagesRecyclerView;
    /**
     * User name text view.
     */
    private TextView usernameTV;
    /**
     * The receiver user object (not current user).
     */
    private User receiverUser;
    /**
     * Message adapter (responsible for message item design).
     */
    private MessageAdapter messageAdapter;
    /**
     * The list of messages of current chat.
     */
    private List<Message> messagesList;
    /**
     * Two interfaces responsible for attaching and detaching the listeners.
     */
    private ListenerRegistration registration1 = null, registration2 = null;
    /**
     * Toolbar contains the other user details.
     */
    private Toolbar toolbar;

    /**
     * Sets activity view.
     * Calls init().
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity_layout);
        init();
    }

    /**
     * Initializes all widgets and variables.
     * Calls initUserDetails() and initToolBar().
     */
    private void init()
    {
        receiverUser = (User) getIntent().getSerializableExtra(ConstantsManager.USER);
        userIV = findViewById(R.id.cal_user_image_image_view);
        usernameTV = findViewById(R.id.user_name_text_view);
        initUserDetails();


        messagesList = new ArrayList<>();
        messageAdapter = new MessageAdapter(ChatActivity.this, messagesList);


        sendCV = findViewById(R.id.cal_send_button);
        textInput = findViewById(R.id.cal_text_input);

        messagesRecyclerView = findViewById(R.id.cal_messages_recycler_view );
        messagesRecyclerView.setAdapter(messageAdapter);

        initToolBar();

        sendCV.setOnClickListener(this);
    }


    /**
     * A listener that listen for changes in received and sent messages.
     */
    private final EventListener<QuerySnapshot> messagesEventListener = new EventListener<QuerySnapshot>()
    {
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error)
        {
            if(error != null) return;

            if(value != null)
            {
                if(value.isEmpty()) return;

                /**
                 * Checks on the new documents in the messages collection related to this specific chat.
                 * Adds the message in case it was added previously also updates it to "read" if the receiver is current user, and finally updates the messagesList.
                 */
                for(DocumentChange documentChange : value.getDocumentChanges())
                {
                    if(documentChange.getType() == DocumentChange.Type.ADDED)
                    {
                        Message message = documentChange.getDocument().toObject(Message.class);
                        if(message.getReceiverId().equals(UserManager.getCurrentUser().getUserId()))
                        {
                            message.setRead(true);
                            documentChange.getDocument().getReference().update(message.getMessageInfo());
                        }
                        messagesList.add(message);
                    }
                }
                sortMessagesList();
                messageAdapter.notifyDataSetChanged();
                if(messagesList.size() != 0)
                    messagesRecyclerView.smoothScrollToPosition(messagesList.size() - 1);
            }
        }
    };

    /**
     * Sorts the messagesList by their publication date.
     */
    private void sortMessagesList()
    {
        for(int i = 0; i < messagesList.size() - 1; i++)
        {
            int minIndex = i;
            for(int j = i + 1; j < messagesList.size(); j++)
            {
                if(messagesList.get(j).getMilliseconds() < messagesList.get(minIndex).getMilliseconds())
                {
                    minIndex = j;
                }
            }
            swap(i, minIndex);
        }
    }

    /**
     *
     * @param i - first position
     * @param minIndex - second position
     * The function switches the objects that are in those two indexes (i, minIndex) - in the messagesList.
     */
    private void swap(int i, int minIndex)
    {
        Message temp = messagesList.get(i);
        messagesList.set(i, messagesList.get(minIndex));
        messagesList.set(minIndex, temp);
    }

    /**
     *
     * @param senderId - sender of the message
     * @param receiverId - the one who receives the message
     * @param messageStr - the text of the message
     * The function adds a new document to firebase database (to Messages Collection) containing the message fields mentioned above.
     */
    private void sendMessage(String senderId, String receiverId, String messageStr)
    {
        Message message = new Message(senderId, receiverId, messageStr, System.currentTimeMillis(), false);

        FirebaseManager.getDBReference().collection(ConstantsManager.MESSAGES_COLLECTION).add(message.getMessageInfo()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference)
            {

            }
        });
    }

    /**
     * The function initialize the user's widgets by it's image and username.
     */
    private void initUserDetails()
    {
        ImageManager.setImageViewString(ChatActivity.this, receiverUser.getUserImage(), userIV, R.drawable.profile_ic);
        usernameTV.setText(receiverUser.getUsername());
    }


    /**
     *
     * @param view
     * The function triggers sendMessage(...) function if the user presses on the sendVC (send layout).
     * The function also triggers lowerKeyboard().
     */
    @Override
    public void onClick(View view)
    {
        if(view.getId() == sendCV.getId() && !textInput.getText().toString().isEmpty())
        {
            sendMessage(UserManager.getCurrentUser().getUserId(), receiverUser.getUserId(), textInput.getText().toString());
            textInput.setText(null);
            lowerKeyboard();
        }
    }


    /**
     * The function lowers the keyboard on the user's screen.
     */
    private void lowerKeyboard()
    {
        View view = this.getCurrentFocus();
        if(view != null)
        {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    /**
     * The function initializes the toolbar.
     */
    private void initToolBar()
    {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * The function calls onPause() - parent function.
     * The function calls detachListeners() function.
     */
    @Override
    protected void onPause()
    {
        detachListeners();
        super.onPause();
    }

    /**
     * The function attaches the two listeners.
     */
    private void attachListeners()
    {
        if(registration1 == null)
        {
            /**
             * Listens to where sender = currentUser and receiver = receiverUser.
             */
            registration1 = FirebaseManager.getDBReference().collection(ConstantsManager.MESSAGES_COLLECTION)
                    .whereEqualTo(ConstantsManager.MESSAGE_SENDER_ID_FIELD, UserManager.getCurrentUser().getUserId())
                    .whereEqualTo(ConstantsManager.MESSAGE_RECEIVER_ID_FIELD, receiverUser.getUserId())
                    .addSnapshotListener(messagesEventListener);
        }

        if(registration2 == null)
        {
            /**
             * Listens to where sender = receiverUser and receiver = currentUser.
             */
            registration2 = FirebaseManager.getDBReference().collection(ConstantsManager.MESSAGES_COLLECTION)
                    .whereEqualTo(ConstantsManager.MESSAGE_SENDER_ID_FIELD, receiverUser.getUserId())
                    .whereEqualTo(ConstantsManager.MESSAGE_RECEIVER_ID_FIELD, UserManager.getCurrentUser().getUserId())
                    .addSnapshotListener(messagesEventListener);
        }

    }

    /**
     * The function detaches the two listeners.
     */
    private void detachListeners()
    {
        if(registration1 != null)
        {
            registration1.remove();
            registration1 = null;
        }
        if(registration2 != null)
        {
            registration2.remove();
            registration2 = null;
        }
    }

    /**
     * Calls onResume - parent function.
     * Calls attachListeners().
     */
    @Override
    public void onResume()
    {
        attachListeners();
        super.onResume();
    }


    /**
     * The function calls onDestroy() - parent function.
     * The function calls detachListeners() function.
     */
    @Override
    public void onDestroy()
    {
        detachListeners();
        super.onDestroy();
    }

}


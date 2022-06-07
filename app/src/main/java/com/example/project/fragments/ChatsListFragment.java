package com.example.project.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import com.example.project.activities.ChatActivity;
import com.example.project.adapters.ChatAdapter;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.FirebaseManager;
import com.example.project.managers.UserManager;
import com.example.project.R;
import com.example.project.objects.Chat;
import com.example.project.objects.Message;
import com.example.project.objects.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * The class which the user can see his whole chats with other users.
 */
public class ChatsListFragment extends Fragment
{
    /**
     * The view of the screen.
     */
    private View view;
    /**
     * List view of chats (widget).
     */
    private ListView chatsList;
    /**
     * List of chats' adapter.
     */
    private ChatAdapter chatAdapter;
    /**
     * Actual array list of chat objects.
     */
    private ArrayList<Chat> chats;
    /**
     * List of all users id which the current user has chatted.
     */
    private List<String> usersIdList;
    /**
     * The toolbar - search.
     */
    private Toolbar toolbar;
    /**
     * The two interfaces for attaching and detaching the listeners.
     */
    private ListenerRegistration registration1 = null, registration2 = null;
    /**
     * Current search string (in search bar).
     */
    private String currentSearchString;


    /**
     * Initializes view and optionsMenu.
     * Calls init().
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.chats_list_fragment_layout, container, false);
        setHasOptionsMenu(true);
        init();
        return view;
    }

    /**
     * Initializes all widgets and variables.
     * Calls initToolBar(), setListOnClickListener() and initChatsList()
     */
    private void init()
    {
        chatsList = view.findViewById(R.id.clfl_chats_list_view);
        chats = new ArrayList<>();
        usersIdList = new ArrayList<>();
        chatAdapter = new ChatAdapter(getContext(), 0, 0, chats);
        chatsList.setAdapter(chatAdapter);
        currentSearchString = "";
        initToolBar();
        setListOnClickListener();
        initChatsList();
    }

    /**
     * Initializes the toolbar.
     */
    private void initToolBar()
    {
        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Chats");
    }

    /**
     * Initializes the chats list with onClick Method.
     */
    private void setListOnClickListener()
    {
        chatsList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                /**
                 * Goes the private chat with the specific chosen item.
                 */
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(ConstantsManager.USER, chats.get(i).getUser());
                startActivity(intent);
            }
        });
    }

    /**
     * Inflates the menu and handle the user searching action - updates the list and current search string.
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.search_bar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);

        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) MenuItemCompat.getActionView(menuItem);

        searchView.setQueryHint("Search...");
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String s)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s)
            {
                currentSearchString = s;
                chatAdapter.getFilter().filter(currentSearchString);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }


    /**
     * Finds all users that has a chat with current user with the help of the database.
     * Calls addUsersById().
     */
    private void initChatsList()
    {
        chats.clear();
        usersIdList.clear();
        FirebaseManager.getDBReference().collection(ConstantsManager.MESSAGES_COLLECTION).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                if(queryDocumentSnapshots == null) return;

                for(DocumentSnapshot ds : queryDocumentSnapshots)
                {
                    if(ds != null)
                    {
                        Message message = ds.toObject(Message.class);

                        if ((message.getSenderId().equals(UserManager.getCurrentUser().getUserId())) && !userIdExists(message.getReceiverId()))
                        {
                            usersIdList.add(message.getReceiverId());
                        }
                        else if((message.getReceiverId().equals(UserManager.getCurrentUser().getUserId())) && !userIdExists(message.getSenderId()))
                        {
                            usersIdList.add(message.getSenderId());
                        }
                    }
                }
                addUsersById();
            }
        });
    }

    /**
     * Builds one by one the chats list and calls setLastMessagesAndUnreadMessages().
     * Calls !userExists(user) - to check if user has already been added to the chats list.
     */
    private void addUsersById()
    {
        FirebaseManager.getDBReference().collection(ConstantsManager.USERS_COLLECTION).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                if(queryDocumentSnapshots == null) return;

                for(String id : usersIdList)
                {
                    for(DocumentSnapshot ds : queryDocumentSnapshots)
                    {
                        if(ds != null)
                        {
                            User user = ds.toObject(User.class);
                            if(user.getUserId().equals(id) && !userExists(user))
                            {
                                chats.add(new Chat(user, 0, null));
                                break;
                            }
                        }
                    }
                }
                setLastMessagesAndUnreadMessages();
            }
        });
    }

    /**
     * Calls setLastMessages().
     */
    private void setLastMessagesAndUnreadMessages()
    {
        setLastMessages();
    }

    /**
     * Sets all last messages of the chats.
     * Calls setUnreadMessages().
     */
    private void setLastMessages()
    {
        FirebaseManager.getDBReference().collection(ConstantsManager.MESSAGES_COLLECTION)
                .orderBy(ConstantsManager.MESSAGE_MILLISECONDS_FIELD, Query.Direction.DESCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                if(queryDocumentSnapshots == null) return;

                for(Chat chat : chats)
                {
                    for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                    {
                        if(documentSnapshot != null && documentSnapshot.exists())
                        {
                            Message message = documentSnapshot.toObject(Message.class);
                            if((message.getSenderId().equals(UserManager.getCurrentUser().getUserId()) &&
                                    message.getReceiverId().equals(chat.getUser().getUserId()))
                                    || (message.getReceiverId().equals(UserManager.getCurrentUser().getUserId()) &&
                                    message.getSenderId().equals(chat.getUser().getUserId())
                            ))
                            {
                                chat.setLastMessage(message);
                                break;
                            }
                        }
                    }
                }
                setUnreadMessages();
            }

        });
    }

    /**
     * Sets all unread messages for each chat.
     * Calls sortChatsListByMilliseconds().
     */
    private void setUnreadMessages()
    {
        FirebaseManager.getDBReference().collection(ConstantsManager.MESSAGES_COLLECTION)
                .whereEqualTo(ConstantsManager.MESSAGE_RECEIVER_ID_FIELD, UserManager.getCurrentUser().getUserId())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                if(queryDocumentSnapshots == null) return;

                for(Chat chat : chats)
                {
                    int count = 0;
                    for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                    {
                        if(documentSnapshot != null && documentSnapshot.exists())
                        {
                            Message message = documentSnapshot.toObject(Message.class);
                            if(!message.isRead() && chat.getUser().getUserId().equals(message.getSenderId())) count++;
                        }
                    }
                    chat.setUnreadMessages(count);
                }
                sortChatsListByMilliseconds();
                chatAdapter.updateObjectsAll();
                chatAdapter.getFilter().filter(currentSearchString);
            }
        });
    }

    /**
     * @param user
     * @return if the given user exists in the chats list.
     */
    private boolean userExists(User user)
    {
        for (Chat chat : chats)
        {
            User currUser = chat.getUser();
            if(currUser.getUserId().equals(user.getUserId())) return true;
        }
        return false;
    }

    /**
     *
     * @param userId
     * @return if the userId exists in the "usersIdList".
     */
    private boolean userIdExists(String userId)
    {
        for (String currUserId : usersIdList)
        {
            if(currUserId.equals(userId)) return true;
        }
        return false;
    }


    /**
     * An event listener for triggering the messages related to current user (sent and received).
     * Calls initChatsList() onEvent.
     */
    private final EventListener<QuerySnapshot> messagesEventListener = new EventListener<QuerySnapshot>()
    {
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error)
        {
            if(error != null) return;

            if(value != null)
            {
                initChatsList();
            }
        }
    };

    /**
     * Sorts the chats list that the latest message will appear at top.
     * Calls swap(...)
     */
    private void sortChatsListByMilliseconds()
    {
        for(int i = 0; i < chats.size() - 1; i++)
        {
            int maxIndex = i;
            for(int j = i + 1; j < chats.size(); j++)
            {
                if(chats.get(j).getLastMessage() == null || chats.get(maxIndex).getLastMessage() == null) return;

                if(chats.get(j).getLastMessage().getMilliseconds() > chats.get(maxIndex).getLastMessage().getMilliseconds())
                {
                    maxIndex = j;
                }
            }
            swap(maxIndex, i);
        }
    }

    /**
     * Takes two indexes and swaps between those two places in the current chats list.
     * @param i
     * @param maxIndex
     */
    private void swap(int i, int maxIndex)
    {
        Chat temp = chats.get(i);
        chats.set(i, chats.get(maxIndex));
        chats.set(maxIndex, temp);
    }

    /**
     * Attach the listeners for both received and sent messages of current user.
     */
    private void attachListeners()
    {
        if(registration1 == null)
        {
            registration1 = FirebaseManager.getDBReference().collection(ConstantsManager.MESSAGES_COLLECTION)
                    .whereEqualTo(ConstantsManager.MESSAGE_SENDER_ID_FIELD, UserManager.getCurrentUser().getUserId())
                    .addSnapshotListener(messagesEventListener);
        }

        if(registration2 == null)
        {
            registration2 = FirebaseManager.getDBReference().collection(ConstantsManager.MESSAGES_COLLECTION)
                    .whereEqualTo(ConstantsManager.MESSAGE_RECEIVER_ID_FIELD, UserManager.getCurrentUser().getUserId())
                    .addSnapshotListener(messagesEventListener);
        }
    }

    /**
     * Detaches the listeners.
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
     * Calls detachListeners().
     * Calls onPause() parent function.
     */
    @Override
    public void onPause()
    {
        detachListeners();
        super.onPause();
    }

    /**
     * Calls attachListeners().
     * Calls onResume() parent function.
     */
    @Override
    public void onResume()
    {
        attachListeners();
        super.onResume();
    }

    /**
     * Calls attachListeners().
     * Calls onDestroy() parent function.
     */
    @Override
    public void onDestroy()
    {
        detachListeners();
        super.onDestroy();
    }


}

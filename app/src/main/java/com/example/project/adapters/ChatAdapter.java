package com.example.project.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.example.project.managers.DateAndTimeManager;
import com.example.project.managers.ImageManager;
import com.example.project.objects.Category;
import com.example.project.objects.Chat;
import com.example.project.R;

import java.util.ArrayList;
import java.util.List;

/**
 * An adapter for handling the chats list.
 */
public class ChatAdapter extends ArrayAdapter<Chat> implements Filterable
{
    /**
     * The context from relevant activity
     */
    private Context context;
    /**
     * The current list of chats
     */
    private List<Chat> objects;
    /**
     * A copy of the full list of chats.
     */
    private List<Chat> objectsAll;
    /**
     * An object responsible for filtering the list.
     */
    private FilterObject filterObject;


    /**
     * Constructor - initializes the context, two lists and filter object.
     * Calls parent function - super(context, resource, textViewResourceId, objects).
     * @param context
     * @param resource
     * @param textViewResourceId
     * @param objects
     */
    public ChatAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<Chat> objects)
    {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.objects = objects;
        this.objectsAll = new ArrayList<>(objects);

        getFilter();
    }

    /**
     * inflates and returns the view of current chat item in the list.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.chat_item_layout, parent, false);


        /**
         * other user's name.
         */
        TextView username = (TextView)view.findViewById(R.id.user_name_text_view);
        /**
         * other user's image.
         */
        ImageView image = (ImageView)view.findViewById(R.id.user_image_image_view);
        /**
         * Chat's last message.
         */
        TextView lastMessage = (TextView)view.findViewById(R.id.cil_last_message_text_view);
        /**
         * Chat's unread messages - amount.
         */
        TextView unreadMessages = (TextView)view.findViewById(R.id.cil_unread_messages_text_view);
        /**
         * Chat's last unread messages "box".
         */
        CardView unreadMessagesLayout = (CardView) view.findViewById(R.id.cil_unread_messages_card_view);
        /**
         * Chat's last message date and time.
         */
        TextView lastMessageMilliseconds = (TextView)view.findViewById(R.id.cil_last_message_date_text_view);

        /**
         * Current chat item.
         */
        Chat temp = objects.get(position);

            username.setText(temp.getUser().getUsername());
            ImageManager.setImageViewString(context, temp.getUser().getUserImage(), image, R.drawable.profile_ic);

            if(temp.getLastMessage() != null)
            {
                lastMessage.setText(temp.getLastMessage().getMessage());
                unreadMessages.setText(temp.getUnreadMessages() + "");
                if(temp.getUnreadMessages() == 0)
                {
                    unreadMessagesLayout.setVisibility(View.INVISIBLE);
                }
                else
                {
                    lastMessage.setTextColor(context.getResources().getColor(R.color.light_green));
                    unreadMessagesLayout.setVisibility(View.VISIBLE);
                }
                lastMessageMilliseconds.setText(DateAndTimeManager.getDateAndTimeString(temp.getLastMessage().getMilliseconds()));

            }
        return view;
    }

    /**
     * Updates the list containing all chats to a new list which is the current list but that it's just got updated.
     */
    public void updateObjectsAll()
    {
        this.objectsAll = new ArrayList<>(objects);
    }


    /**
     *
     * @return the filter object.
     */
    @NonNull
    @Override
    public Filter getFilter()
    {
        if(filterObject == null)
        {
            filterObject = new FilterObject();
        }
        return filterObject;
    }

    /**
     * A class responsible for filtering a categories list.
     */
    private class FilterObject extends Filter
    {
        /**
         * Filters the list by the categories' titles.
         * @param charSequence - the user input text.
         * @return FilterResults object containing all relevant information - list and size.
         */
        @Override
        protected FilterResults performFiltering(CharSequence charSequence)
        {
            List<Chat> filteredList = new ArrayList<>();
            FilterResults filterResults = new FilterResults();

            if(charSequence.toString().isEmpty())
            {
                filteredList.addAll(objectsAll);
            }
            else
            {
                for(Chat chat : objectsAll)
                {
                    if(chat.getUser().getUsername().toLowerCase().contains(charSequence.toString().toLowerCase()))
                    {
                        filteredList.add(chat);
                    }
                }
            }
            filterResults.values = filteredList;
            filterResults.count = filteredList.size();
            return filterResults;
        }

        /**
         * Makes the changes and updates the adapter and the actual list.
         * @param charSequence - the user input text.
         * @param filterResults - the returned filterResults object.
         */
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults)
        {
            objects.clear();
            objects.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    }
}

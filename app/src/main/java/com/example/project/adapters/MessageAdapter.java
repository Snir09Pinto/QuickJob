package com.example.project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.managers.ConstantsManager;
import com.example.project.managers.DateAndTimeManager;
import com.example.project.managers.UserManager;
import com.example.project.objects.Message;
import com.example.project.R;

import java.util.List;

/**
 * An adapter for handling the actual messages (list) in a specific chat view.
 */
public class MessageAdapter extends RecyclerView.Adapter
{
    /**
     * The context of the relevant activity.
     */
    private Context context;
    /**
     * The list of messages.
     */
    private List<Message> messagesList;

    /**
     *
     * @param context - relevant context.
     * @param messagesList - the messages list (list object).
     * Initializes context and list.
     */
    public MessageAdapter(Context context, List<Message> messagesList)
    {
        this.context = context;
        this.messagesList = messagesList;
    }

    /**
     * @param parent - the view containing all child views.
     * @param viewType - the type of the view.
     * @return the correct view - received message view / sent message view.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(viewType == ConstantsManager.MSG_TYPE_RECEIVED)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.item_container_received_message_layout, parent, false);
            return new ReceivedMessageView(view);
        }
        else if(viewType == ConstantsManager.MSG_TYPE_SENT)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.item_container_sent_message_layout, parent, false);
            return new SentMessageView(view);
        }
        return null;
    }

    /**
     * Calls bind() function for the current item's view.
     * @param holder - the view.
     * @param position - current position of item in list.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        if(getItemViewType(position) == ConstantsManager.MSG_TYPE_SENT)
        {
            ((SentMessageView) holder).bind(position);
        }
        else if(getItemViewType(position) == ConstantsManager.MSG_TYPE_RECEIVED)
        {
            ((ReceivedMessageView) holder).bind(position);
        }
    }

    /**
     * @return the size of the messages list.
     */
    @Override
    public int getItemCount()
    {
        return messagesList.size();
    }


    /**
     *
     * @param position - current message position in list.
     * @return the correct view type of current message.
     */
    @Override
    public int getItemViewType(int position)
    {
        if(messagesList.get(position).getSenderId().equals(UserManager.getCurrentUser().getUserId())){
            return ConstantsManager.MSG_TYPE_SENT;
        }
        else{
            return ConstantsManager.MSG_TYPE_RECEIVED;
        }
    }

    /**
     * Class for handling the received messages view.
     */
    private class ReceivedMessageView extends RecyclerView.ViewHolder
    {
        /**
         * Message text.
         */
        private TextView messageText;
        /**
         * Message date.
         */
        private TextView messageDate;

        /**
         * Initializes the current item view widgets.
         * @param itemView - the view.
         */
        public ReceivedMessageView(@NonNull View itemView)
        {
            super(itemView);
            messageText = itemView.findViewById(R.id.icrml_received_message_text_text_view);
            messageDate = itemView.findViewById(R.id.icrml_received_message_date_text_view);
        }

        /**
         * @param position - current position of message in list.
         * Sets the information of the message (sets the view).
         */
        public void bind(int position)
        {
            messageText.setText(messagesList.get(position).getMessage());
            messageDate.setText(DateAndTimeManager.getDateAndTimeString(messagesList.get(position).getMilliseconds()));
        }
    }

    /**
     * Class for handling the sent messages view.
     */
    private class SentMessageView extends RecyclerView.ViewHolder
    {
        /**
         * Message text.
         */
        private TextView messageText;
        /**
         * Message date.
         */
        private TextView messageDate;

        /**
         * Initializes the current item view widgets.
         * @param itemView - the view.
         */
        public SentMessageView(@NonNull View itemView)
        {
            super(itemView);
            messageText = itemView.findViewById(R.id.icsml_sent_message_text_text_view);
            messageDate = itemView.findViewById(R.id.icsml_sent_message_date_text_view);
        }

        /**
         * @param position - current position of message in list.
         * Sets the information of the message (sets the view).
         */
        public void bind(int position)
        {
            messageText.setText(messagesList.get(position).getMessage());
            messageDate.setText(DateAndTimeManager.getDateAndTimeString(messagesList.get(position).getMilliseconds()));
        }
    }

    }

package edu.uga.cs.simplegroupmessaging;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MessagingRecyclerAdapter extends RecyclerView.Adapter<MessagingRecyclerAdapter.MessagesHolder> {

    private List<String> messagesList;

    // The adapter must have a ViewHolder class to "hold" one item to show.
    class MessagesHolder extends RecyclerView.ViewHolder {

        TextView message;

        public MessagesHolder( View itemView ) {
            super(itemView);

            message = itemView.findViewById(R.id.textView);
        }
    }

    public MessagingRecyclerAdapter( List<String> messagesList )
    {
        messagesList = new ArrayList<>();
        messagesList.add(0, "Message 1");
        messagesList.add(1, "Message 2");
        this.messagesList = messagesList;
    }

    @Override
    public MessagingRecyclerAdapter.MessagesHolder onCreateViewHolder(ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.message_item, parent, false );
        return new MessagingRecyclerAdapter.MessagesHolder( view );
    }

    @Override
    public void onBindViewHolder(MessagingRecyclerAdapter.MessagesHolder holder, int position ) {
        /*
        if( getItemCount() <= 0 && position >= getItemCount() ) {
            return;
        }
        */
        String messageItem = messagesList.get(position);

        holder.message.setText(messageItem);
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}

package edu.uga.cs.simplegroupmessaging;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatsRecyclerAdapter extends RecyclerView.Adapter<ChatsRecyclerAdapter.ChatsHolder> {

    private List<String> chatsList;

    // The adapter must have a ViewHolder class to "hold" one item to show.
    class ChatsHolder extends RecyclerView.ViewHolder {

        TextView title;

        public ChatsHolder( View itemView ) {
            super(itemView);

            title = itemView.findViewById(R.id.button);
        }
    }

    public ChatsRecyclerAdapter( List<String> chatsList )
    {
        chatsList = new ArrayList<>();
        chatsList.add(0, "Group 1");
        chatsList.add(1, "Group 2");
        this.chatsList = chatsList;
    }

    @Override
    public ChatsHolder onCreateViewHolder(ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.chat_item, parent, false );
        return new ChatsHolder( view );
    }

    @Override
    public void onBindViewHolder( ChatsHolder holder, int position ) {
        /*
        if( getItemCount() <= 0 && position >= getItemCount() ) {
            return;
        }
        */
        String chatItem = chatsList.get(position);

        holder.title.setText(chatItem);
    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }
}

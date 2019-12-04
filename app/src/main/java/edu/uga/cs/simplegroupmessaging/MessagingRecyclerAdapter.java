package edu.uga.cs.simplegroupmessaging;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessagingRecyclerAdapter extends RecyclerView.Adapter<MessagingRecyclerAdapter.MessagesHolder> {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference("message");

    private ArrayList<String> messagesList = new ArrayList<>();
    private ArrayList<String> emails = new ArrayList<>();
    private String data;

    // The adapter must have a ViewHolder class to "hold" one item to show.
    class MessagesHolder extends RecyclerView.ViewHolder {

        TextView message;

        public MessagesHolder( View itemView ) {
            super(itemView);

            message = itemView.findViewById(R.id.textView);
        }
    }

    public MessagingRecyclerAdapter(final String chatID)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String email = user.getEmail();

        dbRef = FirebaseDatabase.getInstance().getReference("Messages").child(chatID);

        //read from database to get chats
        ValueEventListener memberListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = dataSnapshot.getValue().toString();
                messagesList.clear();
                setMessages(data, email);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        dbRef.addValueEventListener(memberListener);
    }

    @Override
    public MessagingRecyclerAdapter.MessagesHolder onCreateViewHolder(ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.message_item, parent, false );
        return new MessagingRecyclerAdapter.MessagesHolder( view );
    }

    @Override
    public void onBindViewHolder(MessagingRecyclerAdapter.MessagesHolder holder, int position ) {
        String messageItem = messagesList.get(position);

        holder.message.setText(messageItem);
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    /**
     * setMessages: parses the data from the data snapshot and adds chat messages to the
     * messagesList arraylist; messagesList is then added to the recycler view and it is
     * updated
     *
     * @param data
     * @param email
     */
    private void setMessages(String data, String email) {
        int start = 0;
        for(int i = 0; i < data.length(); i++) {
            if(data.charAt(i) == '=') {
                start = i + 1;
            }
            if(data.charAt(i) == ',' && data.charAt(i+2) != '{') {
                messagesList.add(data.substring(start, i));
                notifyDataSetChanged();
            }
            if(data.charAt(i) == '}') {
                emails.add(data.substring(start, i));
            }
        }
    }
}

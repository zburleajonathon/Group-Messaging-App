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
    private String data;
    private String message;

    // The adapter must have a ViewHolder class to "hold" one item to show.
    class MessagesHolder extends RecyclerView.ViewHolder {

        TextView message;

        public MessagesHolder( View itemView ) {
            super(itemView);

            message = itemView.findViewById(R.id.textView);
        }
    }

    public MessagingRecyclerAdapter(String chatID)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String email = user.getEmail();

        dbRef = FirebaseDatabase.getInstance().getReference("Messages");


        //read from database to get chats
        ValueEventListener memberListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Messages could be empty if the chat is new
                try {

                    data = dataSnapshot.getValue().toString();
                    System.out.println("Messages Data: " + data);
                    setMessagesName(messageChecker(data, email));
                }
                catch(NullPointerException e) {}
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

    //method to check if the user is part of any chats
    private ArrayList<String> messageChecker(String data, String email) {
        ArrayList<String> chatIDs = new ArrayList<>();
        String chatID = "";
        int start = 1; //start after the {
        for(int i = 0; i < data.length(); i++) {
            if(data.charAt(i) == '=') {
                chatID = data.substring(start, i); //save chatID just in case
            }
            if(data.charAt(i) == '[') {
                start = i+1;
                i++;
            }
            if(data.charAt(i) == ',') {
                if(data.substring(start, i).equals(email)) {//check emails against the user
                    chatIDs.add(chatID);
                }
                start = i+2; //to skip the space
                i+=2;
            }
            if(data.charAt(i) == ']' && data.charAt(i+1) != '}') {
                start = i+3; //to get to the next member list
                i+=3;
            }
        }
        return chatIDs;
    }

    //method to read the json chat name from the database
    private void setMessagesName(ArrayList<String> chatIDs) {
        //read from chats data base to get the title of the chats
        for(int i =0; i < chatIDs.size(); i++) {
            dbRef = FirebaseDatabase.getInstance().getReference("Chats").child(chatIDs.get(i));

            //read from database to get chats
            ValueEventListener chatListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                    data = dataSnapshot.getValue().toString();
                    //System.out.println("Chat Data: " + data);
                    message = getMessage(data);
                    messagesList.add(message);
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            dbRef.addValueEventListener(chatListener);
        }
    }

    //method to get the title of the chat
    private String getMessage(String data) {
        int start = 0;
        String title = "";
        for(int i = 0; i < data.length(); i++) {
            if(data.charAt(i) == '=') {
                start = i + 1;
            }
            if(data.charAt(i) == '}') {
                title = data.substring(start, i);
            }
        }
        return title;
    }
}

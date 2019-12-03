package edu.uga.cs.simplegroupmessaging;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatsRecyclerAdapter extends RecyclerView.Adapter<ChatsRecyclerAdapter.ChatsHolder> {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference("message");

    private ArrayList<String> chats = new ArrayList<>();
    private String data;
    private String title;

    // The adapter must have a ViewHolder class to "hold" one item to show.
    class ChatsHolder extends RecyclerView.ViewHolder {

        TextView chatView;

        public ChatsHolder( View itemView ) {
            super(itemView);
            final Context context = itemView.getContext();

            chatView = itemView.findViewById(R.id.button);
            chatView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MessagingActivity.class);
                    intent.putExtra("chatID", chatView.getText().toString());
                    context.startActivity(intent);
                }
            });
        }
    }

    public ChatsRecyclerAdapter()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String email = user.getEmail();

        dbRef = FirebaseDatabase.getInstance().getReference("Members");

        //read from database to get chats
        ValueEventListener memberListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                data = dataSnapshot.getValue().toString();
                System.out.println("Member Data: "+ data);
                setChatName(chatChecker(data, email));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        dbRef.addValueEventListener(memberListener);
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
        String chatItem = chats.get(position);

        holder.chatView.setText(chatItem);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    //method to check if the user is part of any chats
    private ArrayList<String> chatChecker(String data, String email) {
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
    private void setChatName(ArrayList<String> chatIDs) {
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
                    title = getTitle(data);
                    chats.add(title);
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
    private String getTitle(String data) {
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

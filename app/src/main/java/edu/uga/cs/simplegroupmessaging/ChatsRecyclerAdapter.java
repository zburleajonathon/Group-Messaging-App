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
    private ArrayList<String> chatIDs = new ArrayList<>();
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
                    intent.putExtra("chatID", chatView.getHint().toString());
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
                //Chats could be empty if the user is new
                try {
                    chatIDs.clear();
                    chats.clear();
                    data = dataSnapshot.getValue().toString();
                    setChatName(chatChecker(data, email));
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
        String chatID = chatIDs.get(position);

        holder.chatView.setText(chatItem);
        holder.chatView.setHint(chatID);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    /**
     * chatChecker: checks if the current user is part of any chats in the database
     *
     * @param data
     * @param email
     * @return
     */
    private ArrayList<String> chatChecker(String data, String email) {
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
            if(data.charAt(i) == ']') {
                if(data.substring(start, i).equals(email)) {//check emails against the user
                    chatIDs.add(chatID);
                }
                if (data.charAt(i) == ']' && data.charAt(i + 1) != '}') {
                    start = i + 3; //to get to the next member list
                    i += 3;
                }
            }
        }
        return chatIDs;
    }

    /**
     * setChatName: reads chat titles from the database and sends them to an arraylist that
     * is then added to the recycler view and it is updated
     *
     * @param chatIDs
     */
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
                    title = getTitle(data);
                    chats.add(title);
                    System.out.println("Chats: " + chats);
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            dbRef.addValueEventListener(chatListener);
        }
    }

    /**
     * getTitle: a helper method to make parsing the title of the chat from the data snapshot
     * easy and separate it from the rest of the code
     *
     * @param data
     * @return
     */
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

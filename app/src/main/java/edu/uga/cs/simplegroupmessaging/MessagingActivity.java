package edu.uga.cs.simplegroupmessaging;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;

public class MessagingActivity extends AppCompatActivity {
    public static final String LOG_FILENAME = "Chat_Log.txt";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference("message");

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter recyclerAdapter;
    private EditText chatText;
    private String chatID;

    private int messageID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        chatID = intent.getStringExtra("chatID");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        recyclerView = findViewById(R.id.recyclerView);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new MessagingRecyclerAdapter(chatID);
        recyclerView.setAdapter(recyclerAdapter);

        chatText = findViewById(R.id.EditTextView);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessagingActivity.this, ChatsActivity.class);
                startActivity(intent);
            }
        });

        // Handle Send Button
        Button sendButton = findViewById(R.id.ButtonSendView);
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                messageID = recyclerAdapter.getItemCount();
                sendMessage();
                chatText.setText(null);
            }
        });
    }

    private void sendMessage() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();

        dbRef = FirebaseDatabase.getInstance().getReference("Messages").child(chatID).child(Integer.toString(messageID));

        //Create HashMap to store info into database with a single call
        HashMap<String, String> messagesHashMap = new HashMap<>();
        messagesHashMap.put("email", email);
        messagesHashMap.put("message", chatText.getText().toString());

        //add email and message to messages section of database
        dbRef.setValue(messagesHashMap);
    }
}

package edu.uga.cs.simplegroupmessaging;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateGroupActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference("message");

    EditText groupNameEdit;
    EditText groupMembersEdit;
    Button submitButton;

    private String groupName;
    private ArrayList<String> groupMembers = new ArrayList<>();
    private Long chatID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        groupNameEdit = findViewById(R.id.groupNameInsert);
        groupMembersEdit = findViewById(R.id.groupMembersInsert);
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupMembersString = groupMembersEdit.getText().toString();

                groupName = groupNameEdit.getText().toString();

                if(TextUtils.isEmpty(groupName) || TextUtils.isEmpty(groupMembersString)){
                    Toast.makeText(CreateGroupActivity.this, "Either the Group Name or Group Members fields are empty.", Toast.LENGTH_SHORT).show();
                }
                else {
                    //populate array list
                    int start = 0;
                    for(int i = 0; i < groupMembersString.length(); i++) {
                        if(groupMembersString.charAt(i) == ',') {
                            groupMembers.add(groupMembersString.substring(start, i));
                            start = i + 1;
                        }
                        if(i == groupMembersString.length() - 1) {
                            groupMembers.add(groupMembersString.substring(start, i + 1));
                        }
                    }

                    createGroup(groupName,groupMembers);
                }



                //Intent intent = new Intent(ChatsActivity.this, CreateGroupActivity.class);
                //startActivity(intent);
            }
        });
    }

    public void createGroup(String groupName, ArrayList<String> groupMembers){
        chatID = Math.round(Math.random() * 100000000);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        String userID = user.getUid();

        dbRef = FirebaseDatabase.getInstance().getReference("Chats").child(chatID.toString());

        //Create HashMap to store info into database with a single call
        HashMap<String, String> chatsHashMap = new HashMap<>();
        chatsHashMap.put("title", groupName);

        //add chatid and title to chat section of database
        dbRef.setValue(chatsHashMap);

        dbRef = FirebaseDatabase.getInstance().getReference("Members").child(chatID.toString());

        //Create HashMap to store info into database with a single call
        HashMap<String, String> membersHashMap = new HashMap<>();
        membersHashMap.put("0", email);
        for(Integer i = 1; i < groupMembers.size() + 1; i++) {
            membersHashMap.put(i.toString(), groupMembers.get(i-1));
        }

        //add members under chatid in members section of database
        dbRef.setValue(membersHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(CreateGroupActivity.this, MessagingActivity.class);
                    System.out.println("HashMap set task is successful.");
                    startActivity(intent);
                    finish();
                }
            }
        });

        dbRef = FirebaseDatabase.getInstance().getReference("Messages").child(chatID.toString()).child("0");

        HashMap<String, String> messagesHashMap = new HashMap<>();
        messagesHashMap.put("email", "system");
        messagesHashMap.put("message", "This is the beginning of the chat.");

        dbRef.setValue(messagesHashMap);
    }
}

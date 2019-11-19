package edu.uga.cs.simplegroupmessaging;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;

public class MessagingActivity extends AppCompatActivity {
    public static final String LOG_FILENAME = "Chat_Log.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        // Handle Send Button
        Button sendButton = findViewById(R.id.ButtonSendView);
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                logChatMessage();
                EditText chatText = findViewById(R.id.EditTextView);
                chatText.setText(null);
            }
        });
    }

    private void logChatMessage() {
        new Thread(){
            public void run(){
                EditText chatText = findViewById(R.id.EditTextView);
                String strChat = chatText.getText().toString();

                if (strChat.length() > 0) {
                    strChat = strChat+"\n\n";
                    try {
                        // Open the file
                        FileOutputStream fIO = openFileOutput(LOG_FILENAME, MODE_APPEND);
                        // Write our chat string
                        fIO.write(strChat.getBytes());
                        // Close
                        fIO.close();
                    } catch (Exception e) {
                        // Append failed. Handle error
                    }
                }
            }
        }.start();
    }

    private void uploadMessage() {
        new Thread() {
            public void run() {

            }
        }.start();
    }

    private void displayChatMessage() {

    }
}

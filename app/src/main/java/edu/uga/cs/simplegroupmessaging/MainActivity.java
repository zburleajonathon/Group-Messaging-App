package edu.uga.cs.simplegroupmessaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    Button signInButton, signUpButton;
    private FirebaseAuth mAuth;
    EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = findViewById(R.id.signUpButton);
        signUpButton = findViewById(R.id.button2);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.signUpEmailEditText);
        password = findViewById(R.id.signUpPassEditText);

        signUpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = email.getText().toString();
                String passText = password.getText().toString();

                if(TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passText)){
                    Toast.makeText(MainActivity.this, "Either the email or password fields are empty.", Toast.LENGTH_SHORT).show();
                }
                else if(passText.length() < 5){
                    Toast.makeText(MainActivity.this, "The password provided is too short.", Toast.LENGTH_SHORT).show();
                }
                else{
                    signIn(emailText, passText);
                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {

        //hideProgressDialog();
        if (user != null) {
        }
        else {
        }
    }

    public void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            Intent intent = new Intent(MainActivity.this, MessagingActivity.class);
                            startActivity(intent);
                        } else {

                            Toast.makeText(MainActivity.this, "User does not exist. Create an account!", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }


}

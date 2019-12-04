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
import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference("message");
    EditText email, password;
    Button signUpButton;
    private String emailText;
    private String passText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        // ...
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.signUpEmailEditText);
        password = findViewById(R.id.signUpPassEditText);
        signUpButton = findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailText = email.getText().toString();
                passText = password.getText().toString();

                if(TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passText)){
                    Toast.makeText(SignUpActivity.this, "Either the email or password fields are empty.", Toast.LENGTH_SHORT).show();
                }
                else if(passText.length() < 5){
                    Toast.makeText(SignUpActivity.this, "The password provided is too short.", Toast.LENGTH_SHORT).show();
                }
                else{
                    signIn(emailText,passText);
                }
            }
        });



    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {

        //hideProgressDialog();
        if (user != null) {
        }
        else {
        }
    }

    /**
     * signUp: adds the user's info to the firebase authentication for signing in and adds the
     * users info to the database
     *
     * @param email
     * @param pass
     */
    public void signUp(final String email, String pass){
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            String userID = user.getUid();
                            dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userID);

                            //Create HashMap to store info into database with a single call
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("userID", userID);
                            hashMap.put("username", email);

                            //add to database
                            dbRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                        System.out.println("HashMap set task is successful.");
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });

    }

    /**
     * signIn: signs the user in with Firebase Authentication if the user already has an account
     *
     * @param email
     * @param password
     */
    public void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in successful, let user know they already have an account
                            Toast.makeText(SignUpActivity.this, "You already have an account. Go to Sign In", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                            startActivity(intent);
                        } else {
                            signUp(emailText, passText);
                            Toast.makeText(SignUpActivity.this, "Thank you! Creating your account now!", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}

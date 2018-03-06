package com.example.ivan.crib2castle;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // setting widgets
        TextView tvLogin = (TextView) findViewById(R.id.tvLogin);
        TextView tvGuest = (TextView) findViewById(R.id.tvGuest);
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);

        // setting listeners
        tvLogin.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {

               if(etEmail.getText().toString().length() == 0 || etPassword.getText().toString().length() == 0 ) {
                   Toast.makeText(LoginActivity.this, "Email/password field is empty.",
                           Toast.LENGTH_SHORT).show();
               } else {
                    boolean isConnected = new Utils().checkForNetworkConnection(LoginActivity.this);
                   if(isConnected) {
                       authenticate(etEmail.getText().toString(), etPassword.getText().toString());
                   } else {
                       Toast.makeText(LoginActivity.this, "C2C needs access to internet. Please check your network connection",
                               Toast.LENGTH_SHORT).show();
                   }

               }




           }
        });

        tvGuest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                guestLogin();
            }
        });

    }

    public void authenticate(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent i = new Intent(LoginActivity.this, SearchActivity.class);
                            i.putExtra("UserId", user.getUid());
                            startActivity(i);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public void guestLogin() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent i = new Intent(LoginActivity.this, SearchActivity.class);
                            i.putExtra("UserId", "-1");
                            startActivity(i);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Error signing in as guest.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}

package com.example.ivan.crib2castle;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    protected ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = (ProgressBar) findViewById(R.id.pbLogin);
        progressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        // setting widgets
        loadWidgets();


    }

    public void loadWidgets() {
        TextView tvLogin = (TextView) findViewById(R.id.tvLogin);
        TextView tvGuest = (TextView) findViewById(R.id.tvGuest);
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final TextView tvSignup = (TextView) findViewById(R.id.tv_signuplink);
        // setting listeners
        tvLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if(etEmail.getText().toString().length() == 0 || etPassword.getText().toString().length() == 0 ) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Email/password field is empty.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    boolean isConnected = new Utils().checkForNetworkConnection(LoginActivity.this);
                    if(isConnected) {
                        authenticate(etEmail.getText().toString(), etPassword.getText().toString());
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "C2C needs access to internet. Please check your network connection",
                                Toast.LENGTH_SHORT).show();
                    }

                }




            }
        });

        tvGuest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                boolean isConnected = new Utils().checkForNetworkConnection(LoginActivity.this);
                if(isConnected) {
                    Intent i = new Intent(LoginActivity.this, SearchActivity.class);
                    i.putExtra("uId", "-1");
                    progressBar.setVisibility(View.GONE);
                    startActivity(i);
                } else {
                    Toast.makeText(LoginActivity.this, "C2C needs access to internet. Please check your network connection",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                boolean isConnected = new Utils().checkForNetworkConnection(LoginActivity.this);
                if(isConnected) {
                    Intent intent = new Intent(LoginActivity.this, RegisterUserActivity.class);
                    progressBar.setVisibility(View.GONE);
                    startActivity(intent);
                }
                else
                    Toast.makeText(LoginActivity.this, "C2C needs access to internet. Please check your network connection",
                            Toast.LENGTH_SHORT).show();

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
                            i.putExtra("uId", user.getUid());
                            startActivity(i);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
        progressBar.setVisibility(View.GONE);
    }
}

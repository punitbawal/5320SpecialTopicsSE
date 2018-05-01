package com.cse5320.c2c.crib2castle;

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

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    protected ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.cse5320.c2c.crib2castle.R.layout.activity_login);
        progressBar = (ProgressBar) findViewById(com.cse5320.c2c.crib2castle.R.id.pbLogin);
        progressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        // setting widgets
        loadWidgets();


    }

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
    }

    public void loadWidgets() {
        TextView tvLogin = (TextView) findViewById(com.cse5320.c2c.crib2castle.R.id.tvLogin);
        TextView tvGuest = (TextView) findViewById(com.cse5320.c2c.crib2castle.R.id.tvGuest);
        TextView tvForgotPassword = (TextView) findViewById(com.cse5320.c2c.crib2castle.R.id.tv_forgotpwdlink);
        final EditText etEmail = (EditText) findViewById(com.cse5320.c2c.crib2castle.R.id.etEmail);
        final EditText etPassword = (EditText) findViewById(com.cse5320.c2c.crib2castle.R.id.etPassword);
        final TextView tvSignup = (TextView) findViewById(com.cse5320.c2c.crib2castle.R.id.tv_signuplink);
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

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etEmail.getText().toString().length() == 0)
                {
                    Toast.makeText(LoginActivity.this,"Please enter your registered Email",Toast.LENGTH_SHORT).show();
                }else
                {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(etEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(LoginActivity.this,"New Password sent on :"+etEmail.getText().toString(),Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(LoginActivity.this,"Incorrect Email"+etEmail.getText().toString(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
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

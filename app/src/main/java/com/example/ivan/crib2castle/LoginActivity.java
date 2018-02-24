package com.example.ivan.crib2castle;

import android.content.Intent;
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
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);

        // setting listeners
        tvLogin.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {

               if(etEmail.getText().toString().length() == 0 || etPassword.getText().toString().length() == 0 ) {
                   Toast.makeText(LoginActivity.this, "Email/password field is empty.",
                           Toast.LENGTH_SHORT).show();
               } else {
                   mAuth.signInWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                           .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                               @Override
                               public void onComplete(@NonNull Task<AuthResult> task) {
                                   if (task.isSuccessful()) {
                                       // Sign in success, update UI with the signed-in user's information
                                       Log.d("C2C", "signInWithEmail:success");
                                       FirebaseUser user = mAuth.getCurrentUser();
                                       Intent i = new Intent("com.example.ivan.crib2castle.SearchActivity");
                                       startActivity(i);
                                   } else {
                                       // If sign in fails, display a message to the user.
                                       Log.w("C2C", "signInWithEmail:failure", task.getException());
                                       Toast.makeText(LoginActivity.this, "Authentication failed.",
                                               Toast.LENGTH_SHORT).show();
                                   }

                                   // ...
                               }
                           });
               }




           }
        });
    }
}

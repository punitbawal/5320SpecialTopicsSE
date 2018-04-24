package com.example.ivan.crib2castle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;
import static android.text.TextUtils.isEmpty;

public class RegisterUserActivity extends AppCompatActivity {

    boolean validflag = true;
    String uId;
    C2CUser user;
    private FirebaseAuth dbAuth;
    protected TextView tvRating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        user = new C2CUser();
        colorbar();
        activateButtons();
        dbAuth = FirebaseAuth.getInstance();
        tvRating = (TextView) findViewById(R.id.tvPasswordStrengthVal);
    }

    private void colorbar() {
        final EditText editPass = (EditText) findViewById(R.id.etPassword);
        editPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                int rating = getRating(editPass.getText().toString());
                //System.out.println("PasswordString_Rating : " +editPass.getText().toString()+"_"+rating);
                //RatingBar ratingBar = (RatingBar) findViewById(R.id.rbPassStrength);
                //ratingBar.setNumStars(rating);
                if(rating < 2)
                {
                    tvRating.setText(R.string.Weak);
                    tvRating.setTextColor(Color.RED);
                }else if(rating < 4)
                {
                    tvRating.setText(R.string.Normal);
                    tvRating.setTextColor(Color.BLUE);
                }else {
                    tvRating.setText(R.string.Strong);
                    tvRating.setTextColor(Color.GREEN);
                }
            }
        });
    }

    public boolean validateTextFields()
    {
        final EditText editEmail = (EditText) findViewById(R.id.etEmail);
        final EditText editPhone = (EditText) findViewById(R.id.etPhone);
        final EditText editPass = (EditText) findViewById(R.id.etPassword);
        final EditText editConfirmPass = (EditText) findViewById(R.id.etRePassword);
        final EditText editFirstName = (EditText) findViewById(R.id.etFirstName);
        final EditText editLastName = (EditText) findViewById(R.id.etLastName);
        final EditText editCity = (EditText) findViewById(R.id.etCity);
        final EditText editState = (EditText) findViewById(R.id.etState);

                String stringEmail = editEmail.getText().toString();
                if(!isEmpty(stringEmail) && Patterns.EMAIL_ADDRESS.matcher(stringEmail).matches())
                {
                    validflag = true;
                }else
                {
                    Context context = getApplicationContext();
                    Toast.makeText(context,"Invalid Email ID",Toast.LENGTH_SHORT).show();
                    validflag = false;
                    return validflag;
                }


                String stringPhone = editPhone.getText().toString();
                if(!isEmpty(stringPhone) && Patterns.PHONE.matcher(stringPhone).matches())
                {
                    validflag = true;
                }else
                {
                    Context context = getApplicationContext();
                    Toast.makeText(context,"Invalid Phone Number",Toast.LENGTH_SHORT).show();
                    validflag = false;
                    return validflag;
                }

                if(editPass.getText().toString().equals(editConfirmPass.getText().toString()))
                {
                    validflag = true;
                }else
                {
                    Context context = getApplicationContext();
                    Toast.makeText(context,"Password and Confirm Password do not match.",Toast.LENGTH_SHORT).show();
                    validflag = false;
                    return validflag;
                }

                if(editFirstName.getText().toString().equals(""))
                {
                    Context context = getApplicationContext();
                    Toast.makeText(context,"First Name cannot be empty.",Toast.LENGTH_SHORT).show();
                    validflag = false;
                    return validflag;
                }

                if(editLastName.getText().toString().equals(""))
                {
                    Context context = getApplicationContext();
                    Toast.makeText(context,"Last Name cannot be empty.",Toast.LENGTH_SHORT).show();
                    validflag = false;
                    return validflag;
                }

                if(editCity.getText().toString().equals(""))
                {
                    Context context = getApplicationContext();
                    Toast.makeText(context,"City cannot be empty.",Toast.LENGTH_SHORT).show();
                    validflag = false;
                    return validflag;
                }

                if(editState.getText().toString().equals(""))
                {
                    Context context = getApplicationContext();
                    Toast.makeText(context,"State cannot be empty.",Toast.LENGTH_SHORT).show();
                    validflag = false;
                    return validflag;
                }
                user.setCity(editCity.getText().toString());
                user.setState(editState.getText().toString());
                user.setFname(editFirstName.getText().toString());
                user.setLname(editLastName.getText().toString());
                user.setEmail(stringEmail);
                user.setPhone(stringPhone);

    return validflag;
    }


    private int getRating(String password) throws IllegalArgumentException {
        if (password == null) {throw new IllegalArgumentException();}
        int passwordStrength = 0;
        if (password.length() > 5) {passwordStrength++;} // minimal pw length of 6
        if (password.toLowerCase()!= password) {passwordStrength++;} // lower and upper case
        if (password.length() > 8) {passwordStrength++;} // good pw length of 9+
        int numDigits= getNumberDigits(password);
        if (numDigits > 0 && numDigits != password.length()) {passwordStrength++;} // contains digits and non-digits
        //System.out.println("IPString__PasswordStrength : " +password+"__"+passwordStrength);
        return passwordStrength;
    }

    public static int getNumberDigits(String inString){
        if (isEmpty(inString)) {
            return 0;
        }
        int numDigits= 0;
        int length= inString.length();
        for (int i = 0; i < length; i++) {
            if (Character.isDigit(inString.charAt(i))) {
                numDigits++;
            }
        }
        return numDigits;
    }

    protected void activateButtons()
    {
        final Button saveButton = (Button) findViewById(R.id.btSave);
        Button cancelButton = (Button) findViewById(R.id.btCancel);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateTextFields()) {

                    EditText uPass =(EditText) findViewById(R.id.etPassword);

                    dbAuth.createUserWithEmailAndPassword(user.getEmail(), uPass.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("FBAuth", "createUserWithEmail:success");
                                        user.setuID(dbAuth.getUid());
                                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                                        DatabaseReference dbref = db.getReference();
                                        Log.w("c2c--","User created is : " + user.getuID());
                                        Log.w("c2c--","User fname is : " + user.getFname());
                                        Log.w("c2c--","User lname is : " + user.getLname());
                                        Log.w("c2c--","User city is : " + user.getCity());
                                        Log.w("c2c--","User email is : " + user.getEmail());
                                        Log.w("c2c--","User state is : " + user.getState());
                                        Log.w("c2c--","User phone is : " + user.getPhone());
                                        dbref.child("c2cusers").child(user.getuID()).setValue(user);
                                        Context context = getApplicationContext();
                                        Toast.makeText(context, "User Created", Toast.LENGTH_SHORT).show();
                                        //saveButton.setEnabled(false);
                                        Intent intent = new Intent(RegisterUserActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("FBAuth", "createUserWithEmail:failure", task.getException());
                                        try
                                        {
                                            throw task.getException();
                                        }
                                        catch(FirebaseAuthWeakPasswordException e) {
                                            Toast.makeText(RegisterUserActivity.this, "Weak Password. Please use a stronger password.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                        catch(FirebaseAuthInvalidCredentialsException e) {
                                            Toast.makeText(RegisterUserActivity.this, "Invalid credentials",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                        catch(FirebaseAuthUserCollisionException e) {
                                            Toast.makeText(RegisterUserActivity.this, "User already exist",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                        catch (Exception e){
                                            Toast.makeText(RegisterUserActivity.this, "Something went wrong with Authentication. Please try again later.",
                                                    Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    // ...
                                }
                            });


                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterUserActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}

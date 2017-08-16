package com.example.sergi.fitelite;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    //declaring the elements used in this window
    private Button buttonSignUp;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextName;
    private EditText editTextSurname;
    private EditText editTextAge;
    private EditText editTextWeight;
    private EditText editTextHeight;
    private TextView textViewRegister;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();//gets the instance of the Firebase db

        progressDialog = new ProgressDialog(this);//creates a new ProgressDialog object
//creates objects for all the elements created in the xml files
        buttonSignUp = (Button) findViewById(R.id.registerButton);

        textViewRegister = (TextView) findViewById(R.id.loginText);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        editTextEmail = (EditText) findViewById(R.id.editTextEmailRegister);
        editTextPassword = (EditText) findViewById(R.id.editTextPasswordRegister);
        editTextName = (EditText) findViewById(R.id.editTextNameRegister);
        editTextSurname = (EditText) findViewById(R.id.editTextSurnameRegister);
        editTextAge = (EditText) findViewById(R.id.editTextAgeRegister);
        editTextWeight = (EditText) findViewById(R.id.editTextWeightRegister);
        editTextHeight = (EditText) findViewById(R.id.editTextHeightRegister);
//set the ActionListeners for the buttons
        buttonSignUp.setOnClickListener(this);
        textViewRegister.setOnClickListener(this);

        if (firebaseAuth.getCurrentUser()!= null){//if there is an account already connected
            finish();//ends the current activity
            startActivity(new Intent(getApplicationContext(), ProfileActivity2.class));//starts the ProfileActivity2 class, which is the first page seen after login
        }
    }

    private void saveUserInformation(){//method to save the user information and make it readable by using toString() method.
        String name = editTextName.getText().toString().trim();
        String surname = editTextSurname.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();
        String weight = editTextWeight.getText().toString().trim();
        String height = editTextHeight.getText().toString().trim();

        UserInformation user = new UserInformation(name, surname, age, weight, height);//creating the object which stores the data

        FirebaseUser appUser = firebaseAuth.getCurrentUser();//check if the user is signed in

        databaseReference.child("Users").child(appUser.getUid()).setValue(user);//save the data under the "Users" child in Firebase

        Toast.makeText(this, "Welcome!", Toast.LENGTH_LONG).show();//toast welcome message
    }

    private void registerUser(){
//variables to store the email and password as strings
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            //if the email is empty
            Toast.makeText(this, "Please enter Email.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)){
            //if the password is empty
            Toast.makeText(this, "Please enter Password", Toast.LENGTH_SHORT).show();
        }

        progressDialog.setMessage("Processing...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)//firebase method for the authentication system
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){//if the connection was successful
                            saveUserInformation();//saves the user's data
                            finish();//ends the current activity
                            startActivity(new Intent(getApplicationContext(), ProfileActivity2.class));//starts the ProfileActivity2 class
                        }else{//if the login is unsuccessful
                            Toast.makeText(RegisterActivity.this, "Failed!", Toast.LENGTH_SHORT).show();//display a toast message
                        }
                    }
                });

    }



    @Override
    public void onClick(View v) {

        if(v == buttonSignUp){
            registerUser();
        }

        if(v == textViewRegister) {
            startActivity(new Intent(this, MainActivity.class));
        }

    }
}

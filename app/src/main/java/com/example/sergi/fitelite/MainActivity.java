package com.example.sergi.fitelite;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    //declaring the variables used by this activity
    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignUp;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//set the correct layout for this window
//creates objects for all the elements created in the xml files
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonSignIn = (Button) findViewById(R.id.loginButton);
        textViewSignUp = (TextView) findViewById(R.id.registerText);
//set the button listeners
        buttonSignIn.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();//connect to the database

        if (firebaseAuth.getCurrentUser() != null) {//if a user is already connected
            finish();//the MainActivity class closes
            startActivity(new Intent(getApplicationContext(), ProfileActivity2.class));//the ProfileActivity2 class opens
        }
    }

    private void userLogin(){
        //store the email and password in String variables
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        progressDialog.setMessage("Processing...");
        progressDialog.show();//show the progress dialog

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {//Firebase authentication method
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();//after logging in the progress dialog is hidden
                        if(task.isSuccessful()){//if the login is successful
                            finish();//ends the current activity
                            startActivity(new Intent(getApplicationContext(), ProfileActivity2.class));//goes to the starting screen of the app
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {//when clicking the login button the userLogin() method is run
        if(v == buttonSignIn){
            userLogin();
        }

        if(v == textViewSignUp){//when the sign up button is pressed
            finish();//the current activity ends
            startActivity(new Intent(this, RegisterActivity.class));//starts the Register class.
        }
    }
}

package com.evento.evento;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    FirebaseAuth Auth;
    Button btnlogin,btnsignup;
    EditText etEmail,etPassword;
    ProgressDialog prog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        btnlogin = (Button)findViewById(R.id.button_1);
        btnsignup = (Button)findViewById(R.id.button_2);
        etEmail = (EditText)findViewById(R.id.etEmail_login);
        etPassword = (EditText)findViewById(R.id.etPassword_login);
        Auth = FirebaseAuth.getInstance();
        prog = new ProgressDialog(this);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = etEmail.getText().toString();
                if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches())
                {
                    etEmail.setError("Invalid Email Address");
                    etEmail.requestFocus();
                    return;
                }
                String Password = etPassword.getText().toString();
                if(Password.isEmpty())
                {
                    etPassword.setError("Enter Password");
                    etPassword.requestFocus();
                    return;
                }
                prog.setTitle("Login");
                prog.setMessage("Please wait");
                prog.show();
                Auth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            prog.cancel();
                            startActivity(new Intent(Login.this,MainPage.class));
                            finish();
                        }
                        else
                        {
                            prog.cancel();
                            Toast.makeText(Login.this, "Invalid Email or Password", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(Login.this,Signup.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Login.this,MainPage.class));
        finish();
    }
}

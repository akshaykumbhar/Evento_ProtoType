package com.evento.evento;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Signup extends AppCompatActivity {
    Button btnRigester;
    TextView btnchoose,btntake;
    ImageView ivProfile;
    EditText etName,etEmail,etPassword,etCol,etPhone;
    ProgressDialog prog;
    Uri filepath;
    StorageReference mStorageRef;
    DatabaseReference dbf;
    FirebaseAuth Auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        btnRigester = (Button)findViewById(R.id.button_3);
        btnchoose = (TextView) findViewById(R.id.button_1);
        btntake = (TextView) findViewById(R.id.button_2);
        ivProfile = (ImageView)findViewById(R.id.ivProfile_signup);
        etName =(EditText)findViewById(R.id.etName_signup);
        etEmail = (EditText)findViewById(R.id.etEmail_signup);
        etPassword =(EditText)findViewById(R.id.etPassword_signup);
        etCol = (EditText)findViewById(R.id.etCol_signup);
        etPhone =(EditText)findViewById(R.id.etPhone_signup);
        prog = new ProgressDialog(this);
        Auth= FirebaseAuth.getInstance();
        dbf = FirebaseDatabase.getInstance().getReference("Users");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.logo);
        ivProfile.setImageBitmap(bitmap);


        btnRigester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = etName.getText().toString();
                if(name.isEmpty())
                {
                    etName.setError("Enter Name");
                    etName.requestFocus();
                    return;
                }
                final String email = etEmail.getText().toString();
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    etEmail.setError("Enter Valid Email");
                    etEmail.requestFocus();
                    return;
                }
                final String password = etPassword.getText().toString();
                if(password.length()<8)
                {
                    etPassword.setError("Minimum 8 character");
                    etPassword.requestFocus();
                    return;
                }
                final String Col = etCol.getText().toString();
                if(Col.isEmpty())
                {
                    etCol.setError("Enter College name");
                    etCol.requestFocus();
                    return;
                }
                final String phone = etPhone.getText().toString();
                if(phone.length()>10)
                {
                    etPhone.setError("Enter Valid Phone no.");
                    etPhone.requestFocus();
                    return;
                }
                if(filepath == null)
                {
                    Toast.makeText(Signup.this, "Please Choose or Take profile photo", Toast.LENGTH_LONG).show();
                    return;
                }

                prog.setTitle("Sign-up");
                prog.setMessage("Please wait");
                prog.show();
                dbf.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren() )
                        {
                            User u = ds.getValue(User.class);
                            if(u.getEmail().equals(email))
                            {
                                prog.cancel();
                                etEmail.setText("Email Address already Exist");
                                etEmail.requestFocus();
                                return;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                StorageReference sf = mStorageRef.child("Userprofile/"+email+".jpg");
                sf.putFile(filepath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            final String url = "Userprofile/"+email+".jpg";
                            User user = new User(name,email,Col,phone,url);
                            dbf.child(phone).setValue(user);
                            Auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        prog.cancel();
                                        startActivity(new Intent(Signup.this,MainPage.class));
                                        finish();
                                    }
                                    else
                                    {
                                        prog.cancel();
                                        Toast.makeText(Signup.this, "Sign-up Failed", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            });
                        }
                        else
                        {
                           prog.cancel();
                            Toast.makeText(Signup.this, "Image failed to upload", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
            }
        });
        btnchoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i,123);
            }
        });
        btntake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),124);
            }
        });

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123 && resultCode== RESULT_OK)
        {
            filepath = data.getData();
            if(filepath!=null)
            {

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
                    RoundedBitmapDrawable rbd= RoundedBitmapDrawableFactory.create(getResources(),bitmap);
                    rbd.setCircular(true);
                    ivProfile.setImageDrawable(rbd);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(requestCode == 124 && resultCode == RESULT_OK)
        {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            RoundedBitmapDrawable rbd= RoundedBitmapDrawableFactory.create(getResources(),bitmap);
            rbd.setCircular(true);
            ivProfile.setImageDrawable(rbd);
            File f = new File(getExternalCacheDir(),"user.jpeg");
            try {
                FileOutputStream fos = new FileOutputStream(f);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                filepath = Uri.fromFile(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
       

    }
}

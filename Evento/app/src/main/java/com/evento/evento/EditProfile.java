package com.evento.evento;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class EditProfile extends AppCompatActivity {
    Button btnSave;
    TextView btngallery,btntake;
    EditText etname,etphone,etcol;
    ImageView ivprofile;
    StorageReference sf;
    DatabaseReference dbf;
    Uri filepath;
    ProgressDialog prog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        btnSave = (Button)findViewById(R.id.button_3);
        btngallery = (TextView) findViewById(R.id.button_2);
        prog = new ProgressDialog(this);
        btntake = (TextView) findViewById(R.id.button_1);
        ivprofile = (ImageView)findViewById(R.id.ivProfile_signup);
        etname =(EditText)findViewById(R.id.etName_signup);

        etcol = (EditText)findViewById(R.id.etCol_signup);
        etphone =(EditText)findViewById(R.id.etPhone_signup);
        dbf = FirebaseDatabase.getInstance().getReference("Users");
        sf = FirebaseStorage.getInstance().getReference();
        final Intent i = getIntent();
        etname.setText(i.getStringExtra("name"));

        etcol.setText(i.getStringExtra("col"));
        etphone.setText(i.getStringExtra("phone"));
        String uri = i.getStringExtra("uri");
        StorageReference ref = sf.child("Userprofile/" + i.getStringExtra("email") + ".jpg");
        try {
            final File localFile = File.createTempFile("images", "jpg");
            ref.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        filepath =  Uri.fromFile(localFile);
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                            RoundedBitmapDrawable rbd= RoundedBitmapDrawableFactory.create(getResources(),bitmap);
                            rbd.setCircular(true);
                            ivprofile.setImageDrawable(rbd);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        btntake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i,123);
            }
        });
        btngallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),124);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = etname.getText().toString();
                if(name.isEmpty())
                {
                    etname.setError("Enter Name");
                    etname.requestFocus();
                    return;
                }

                final String Col = etcol.getText().toString();
                if(Col.isEmpty())
                {
                    etcol.setError("Enter College name");
                    etcol.requestFocus();
                    return;
                }
                final String phone = etphone.getText().toString();
                if(phone.length()>10)
                {
                    etphone.setError("Enter Valid Phone no.");
                    etphone.requestFocus();
                    return;
                }
                if(filepath == null)
                {
                    Toast.makeText(EditProfile.this, "Please Choose or Take profile photo", Toast.LENGTH_LONG).show();
                    return;
                }

                prog.setTitle("Saving");
                prog.setMessage("Please wait");
                prog.show();

                StorageReference msf = sf.child("Userprofile/"+i.getStringExtra("email")+".jpg");
                msf.putFile(filepath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            final String url = "Userprofile/"+i.getStringExtra("email")+".jpg";
                            User user = new User(name,i.getStringExtra("email"),Col,phone,url);
                            dbf.child(phone).setValue(user);
                            prog.cancel();
                            startActivity(new Intent(EditProfile.this,MainPage.class));
                            finish();
                        }
                        else
                        {
                            prog.cancel();
                            Toast.makeText(EditProfile.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
                    ivprofile.setImageDrawable(rbd);
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
            ivprofile.setImageDrawable(rbd);
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

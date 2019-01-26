package com.evento.evento;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class AddEventActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_add_event);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        btnSave = (Button)findViewById(R.id.button_3);
        btngallery = (TextView) findViewById(R.id.button_2);
        prog = new ProgressDialog(this);
        btntake = (TextView) findViewById(R.id.button_1);
        ivprofile = (ImageView)findViewById(R.id.ivProfile_signup);
        etname =(EditText)findViewById(R.id.etName_signup);

        etcol = (EditText)findViewById(R.id.etCol_signup);
        etphone =(EditText)findViewById(R.id.etPhone_signup);
        dbf = FirebaseDatabase.getInstance().getReference("Events");
        sf = FirebaseStorage.getInstance().getReference();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = etname.getText().toString();
                if(name.isEmpty())
                {
                    etname.setError("Enter Event Name");
                    etname.requestFocus();
                    return;
                }

                final String Col = etcol.getText().toString();
                if(Col.isEmpty())
                {
                    etcol.setError("Enter Subtitle");
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
                    Toast.makeText(AddEventActivity.this, "Please Choose or Take profile photo", Toast.LENGTH_LONG).show();
                    return;
                }

                prog.setTitle("Posting");
                prog.setMessage("Please wait");
                prog.show();
                final int[] num = new int[1];
                final int[] numb = new int[1];
                dbf.addValueEventListener(new ValueEventListener() {
                    int count=0;
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Events e = ds.getValue(Events.class);
                            if (count == 0) {
                                num[0] = Integer.parseInt(e.getEventno());
                                num[0] = num[0] - 1;
                                final String n = String.valueOf(num[0]);
                                StorageReference msf = sf.child("EventImage/" + n + ".jpg");
                                msf.putFile(filepath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {


                                            final String url = "EventImage/" + n + ".jpg";
                                            Events e = new Events(name, Col, phone, n, url);
                                            dbf.child(n).setValue(e);

                                            prog.cancel();
                                            startActivity(new Intent(AddEventActivity.this, MainPage.class));
                                            finish();
                                        } else {
                                            prog.cancel();
                                            Toast.makeText(AddEventActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                                count++;
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

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

    @Override
    public void onBackPressed() {
            super.onBackPressed();
        startActivity(new Intent(AddEventActivity.this,MainPage.class));
        finish();
    }
}
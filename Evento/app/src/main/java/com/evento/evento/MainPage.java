package com.evento.evento;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static com.evento.evento.R.id.tvName;

public class MainPage extends AppCompatActivity {

    FirebaseAuth Auth;
    FirebaseUser user;
    StorageReference sf;
    DatabaseReference dbf;
    User us;
    Fragment f;
    ImageView btnProfile,btnEvent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Auth = FirebaseAuth.getInstance();
        user = Auth.getCurrentUser();
        btnEvent = (ImageView)findViewById(R.id.btnEvent);
        btnProfile = (ImageView) findViewById(R.id.btnProfile);
        final FragmentManager fmr = getFragmentManager();
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user == null)
                {
                    startActivity(new Intent(MainPage.this,Login.class));
                    finish();
                }
                FragmentTransaction ft = fmr.beginTransaction();
                if(f!=null) {
                    ft.remove(f);
                }
                f = new ProfileFragment();
                ft.replace(R.id.frag,f);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        btnEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = fmr.beginTransaction();
                if(f!=null) {
                    ft.remove(f);
                }
                 f = new EventFragment();
                ft.replace(R.id.frag,f);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }
}

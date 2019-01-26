package com.evento.evento;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.io.File;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    ImageView ivprofile;
    TextView tvname,tvcol,tvemail,tvphone;
    Button btnwallet,btnEdit,btnLogout,btnadd;
    FirebaseAuth Auth;
    FirebaseUser user;
    StorageReference sf;
    DatabaseReference dbf;
    User us;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


       View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Auth =FirebaseAuth.getInstance();
        user = Auth.getCurrentUser();
        ivprofile =(ImageView) view.findViewById(R.id.imageView2);
        tvname = (TextView)view.findViewById(R.id.tvName);
        tvcol = (TextView) view.findViewById(R.id.tvcol);
        tvemail = (TextView) view.findViewById(R.id.tvemail);
        tvphone = (TextView) view.findViewById(R.id.tvphone);
        btnEdit = (Button) view.findViewById(R.id.btnEdit);
        btnLogout = (Button) view.findViewById(R.id.btnlogout);
        btnwallet = (Button)view.findViewById(R.id.btnwallet);
        btnadd = (Button) view.findViewById(R.id.btnadd);

        sf = FirebaseStorage.getInstance().getReference();
        if(user != null)
        {
            dbf = FirebaseDatabase.getInstance().getReference("Users");
            dbf.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        us = ds.getValue(User.class);
                        if (us.getEmail().equals(user.getEmail())) {
                            tvname.setText(us.getName());
                            tvcol.setText(tvcol.getText().toString()+us.getCol());
                            tvemail.setText(tvemail.getText().toString()+us.getEmail());
                            tvphone.setText(tvphone.getText().toString()+us.getPhone());
                            StorageReference ref = sf.child("Userprofile/" + us.getEmail() + ".jpg");
                            try {
                                final File localFile = File.createTempFile("images", "jpg");
                                ref.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            Uri filepath = Uri.fromFile(localFile);
                                            try {
                                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filepath);
                                                RoundedBitmapDrawable rbd= RoundedBitmapDrawableFactory.create(getResources(),bitmap);
                                                rbd.setCircular(true);
                                                ivprofile.setImageDrawable(rbd);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                                break;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tvname.getText().toString().equals("----"))
                {
                    Toast.makeText(getActivity(), "Wait", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent i = new Intent(getActivity(),EditProfile.class);
                i.putExtra("name",us.getName());
                i.putExtra("email",us.getEmail());
                i.putExtra("col",us.getCol());
                i.putExtra("phone",us.getPhone());
                i.putExtra("uri",us.getProuri());
                startActivity(i);

            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.signOut();
                startActivity(new Intent(getActivity(),MainPage.class));
                getActivity().finish();
            }
        });
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),AddEventActivity.class));
                getActivity().finish();
            }
        });
        return view;
    }


}

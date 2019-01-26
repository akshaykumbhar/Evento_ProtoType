package com.evento.evento;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.ArrayList;

import static com.evento.evento.EventFragment.act;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {


    StorageReference sf;
    DatabaseReference dbf;
    static FirebaseAuth Auth;
  static  FirebaseUser user;
    Events e;
    static ArrayList<String> Org;
    static ArrayList<String> Event ;
    static ArrayList<String> imag ;
    static Context cont;
    static Activity act;
    ListView lv;
   static Resources res;

    public EventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        //cont= getContext();
        act = getActivity();
        res = getResources();

        lv = (ListView) view.findViewById(R.id.listView);
        sf = FirebaseStorage.getInstance().getReference();
        dbf = FirebaseDatabase.getInstance().getReference("Events");
        dbf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Org = new ArrayList<String>();
                Event = new ArrayList<String>();
                imag = new ArrayList<String>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    e = ds.getValue(Events.class);
                    Org.add(e.getName());
                    Event.add(e.getSub());
                    imag.add(e.getImgurl());

                }
                CustomEvent cs = new CustomEvent();
                lv.setAdapter(cs);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return view;
    }

}

class CustomEvent extends BaseAdapter
        {
            StorageReference sf;
            @Override
            public int getCount() {
                return EventFragment.Org.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(final int i, View view, ViewGroup parent) {
                LayoutInflater inflater = LayoutInflater.from(EventFragment.cont);
                sf = FirebaseStorage.getInstance().getReference();
                view = inflater.inflate(R.layout.custevent,null);
                TextView tvname = (TextView) view.findViewById(R.id.tvname);
                TextView tvemail = (TextView) view.findViewById(R.id.tvemail);
                final ImageView ivprofile = (ImageView) view.findViewById(R.id.ivpro);
                Button btn = (Button)view.findViewById(R.id.btnbook);

                tvname.setText(EventFragment.Org.get(i));
                tvemail.setText(EventFragment.Event.get(i));
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventFragment.Auth =FirebaseAuth.getInstance();
                        EventFragment.user = EventFragment.Auth.getCurrentUser();
                        if(EventFragment.user == null)
                        {
                            Toast.makeText(act, "Please Login", Toast.LENGTH_SHORT).show();
                        }
                        else{


                        Toast.makeText(act, "Your Event "+EventFragment.Org.get(i)+" is Booked", Toast.LENGTH_SHORT).show();
                    }}
                });
                StorageReference ref = sf.child(EventFragment.imag.get(i));
                try {
                    final File localFile = File.createTempFile("images"+String.valueOf(i), "jpg");
                    ref.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                Uri filepath = Uri.fromFile(localFile);
                                try {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(act.getContentResolver(), filepath);
                                    ivprofile.setImageBitmap(bitmap);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }


                return view;
            }
        }

package com.example.nitdurgapur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    ImageView profileImage, profileLogout;
    TextView profileName, regNo, rollNo, instituteEmail, contactNumber, department, course, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        profileImage = (ImageView) findViewById(R.id.profile_image);
        profileLogout = (ImageView) findViewById(R.id.profile_logout);
        profileName = (TextView) findViewById(R.id.profile_name);
        regNo = (TextView) findViewById(R.id.profile_regNo);
        rollNo = (TextView) findViewById(R.id.profile_rollNo);
        instituteEmail = (TextView) findViewById(R.id.profile_institute_email);
        contactNumber = (TextView) findViewById(R.id.profile_contact_number);
        department = (TextView) findViewById(R.id.profile_department);
        course = (TextView) findViewById(R.id.profile_course);
        year = (TextView) findViewById(R.id.profile_year);

        String user_id = user.getUid();

        DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("users");

        StorageReference reference = FirebaseStorage.getInstance().getReference().child("profile_images").child(user_id+".png");

        current_user_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                profileName.setText(dataSnapshot.child(user_id).child("Name").getValue(String.class));
                regNo.setText(dataSnapshot.child(user_id).child("RegNo").getValue(String.class));
                rollNo.setText(dataSnapshot.child(user_id).child("RollNo").getValue(String.class));
                instituteEmail.setText(dataSnapshot.child(user_id).child("InstituteEmail").getValue(String.class));
                contactNumber.setText(dataSnapshot.child(user_id).child("ContactNumber").getValue(String.class));
                department.setText(dataSnapshot.child(user_id).child("Department").getValue(String.class));
                course.setText(dataSnapshot.child(user_id).child("Course").getValue(String.class));
                year.setText(dataSnapshot.child(user_id).child("Year").getValue(String.class));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(ProfileActivity.this)
                                .load(uri)
                                .into(profileImage);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        profileImage.setImageResource(R.drawable.ic_user);
                    }
                });

        profileLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(ProfileActivity.this,"User Logged Out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProfileActivity.this, HomePage.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notification_bell,menu);
        return super.onCreateOptionsMenu(menu);
    }
}
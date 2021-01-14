package com.example.nitdurgapur;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    ImageView profileImage, profileLogout;
    TextView profileName, regNo, rollNo, instituteEmail, contactNumber, department, course, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(toolbar.getNavigationIcon()).setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        profileImage = findViewById(R.id.profile_image);
        profileLogout = findViewById(R.id.profile_logout);
        profileName = findViewById(R.id.profile_name);
        regNo = findViewById(R.id.profile_regNo);
        rollNo = findViewById(R.id.profile_rollNo);
        instituteEmail = findViewById(R.id.profile_institute_email);
        contactNumber = findViewById(R.id.profile_contact_number);
        department = findViewById(R.id.profile_department);
        course = findViewById(R.id.profile_course);
        year = findViewById(R.id.profile_year);

        assert user != null;
        String user_id = user.getUid();

        DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);

        StorageReference reference = FirebaseStorage.getInstance().getReference().child("profile_images").child(user_id + ".png");

        current_user_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                profileName.setText(dataSnapshot.child("Name").getValue(String.class));
                regNo.setText(dataSnapshot.child("RegNo").getValue(String.class));
                rollNo.setText(dataSnapshot.child("RollNo").getValue(String.class));
                instituteEmail.setText(dataSnapshot.child("InstituteEmail").getValue(String.class));
                contactNumber.setText(dataSnapshot.child("ContactNumber").getValue(String.class));
                department.setText(dataSnapshot.child("Department").getValue(String.class));
                course.setText(dataSnapshot.child("Course").getValue(String.class));
                year.setText(dataSnapshot.child("Year").getValue(String.class));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
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

}
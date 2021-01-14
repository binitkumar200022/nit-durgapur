package com.example.nitdurgapur.nitdgpchat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nitdurgapur.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class PeopleFragment extends Fragment {

    private View peopleView;
    private RecyclerView peopleList;
    private DatabaseReference usersRef;
    private StorageReference storageReference;

    public PeopleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        peopleView = inflater.inflate(R.layout.fragment_people, container, false);

        peopleList = peopleView.findViewById(R.id.people_recycler_view);
        peopleList.setLayoutManager(new LinearLayoutManager(getContext()));

        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        storageReference = FirebaseStorage.getInstance().getReference().child("profile_images");

        return peopleView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<People>()
                .setQuery(usersRef, People.class)
                .build();

        final FirebaseRecyclerAdapter<People, PeopleViewHolder> adapter =
                new FirebaseRecyclerAdapter<People, PeopleViewHolder>(options) {

                    @NonNull
                    @Override
                    public PeopleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout, parent, false);
                        return new PeopleViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull PeopleViewHolder peopleViewHolder, int position, @NonNull People people) {
                        String userIDs = getRef(position).getKey();

                        usersRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String name = snapshot.child("Name").getValue().toString();
                                String department = snapshot.child("Department").getValue().toString();

                                peopleViewHolder.userName.setText(name);
                                peopleViewHolder.userDept.setText(department);

                                storageReference.child(userIDs + ".png").getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Picasso.get().load(uri).into(peopleViewHolder.profileImage);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                peopleViewHolder.profileImage.setImageResource(R.drawable.ic_user);
                                            }
                                        });

                                peopleViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                        chatIntent.putExtra("visit_user_id", userIDs);
                                        chatIntent.putExtra("visit_user_name", name);
                                        startActivity(chatIntent);
                                    }
                                });

                                if (snapshot.child("userState").hasChild("state")) {
                                    String state = snapshot.child("userState").child("state").getValue().toString();

                                    if (state.equals("online")) {
                                        peopleViewHolder.onlineIcon.setVisibility(View.VISIBLE);
                                    } else if (state.equals("offline")) {
                                        peopleViewHolder.onlineIcon.setVisibility(View.INVISIBLE);
                                    }
                                } else {
                                    peopleViewHolder.onlineIcon.setVisibility(View.INVISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                };
        peopleList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class PeopleViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userDept;
        ImageView profileImage, onlineIcon;

        public PeopleViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.people_user_name);
            userDept = itemView.findViewById(R.id.people_user_department);
            profileImage = itemView.findViewById(R.id.people_user_image);
            onlineIcon = itemView.findViewById(R.id.people_user_online_status);
        }
    }
}
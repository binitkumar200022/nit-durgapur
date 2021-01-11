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

import com.bumptech.glide.Glide;
import com.example.nitdurgapur.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ChatsFragment extends Fragment {

    private View chatsView;
    private RecyclerView chatsList;
    private DatabaseReference chatsRef, usersRef;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private String currentUserID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        chatsView = inflater.inflate(R.layout.fragment_chats, container, false);

        chatsList = (RecyclerView) chatsView.findViewById(R.id.chats_list);
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));

        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        chatsRef = FirebaseDatabase.getInstance().getReference().child("private").child(currentUserID);
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        storageReference = FirebaseStorage.getInstance().getReference().child("profile_images");

        return chatsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<People> options = new FirebaseRecyclerOptions.Builder<People>()
                .setQuery(chatsRef, People.class)
                .build();

        FirebaseRecyclerAdapter<People, chatsViewHolder> adapter = new FirebaseRecyclerAdapter<People, chatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull chatsViewHolder chatsViewHolder, int position, @NonNull People people) {
                final String userIDs = getRef(position).getKey();

                usersRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.child("Name").getValue().toString();

                        chatsViewHolder.userName.setText(name);

                        storageReference.child(userIDs + ".png").getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Glide.with(getActivity())
                                                .load(uri)
                                                .into(chatsViewHolder.profileImage);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        chatsViewHolder.profileImage.setImageResource(R.drawable.ic_user);
                                    }
                                });

                        chatsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                chatIntent.putExtra("visit_user_id", userIDs);
                                chatIntent.putExtra("visit_user_name", name);
                                startActivity(chatIntent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public chatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_display_layout, parent, false);
                return new chatsViewHolder(view);
            }
        };

        chatsList.setAdapter(adapter);
        adapter.startListening();

    }

    public static class chatsViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImage;
        TextView userName;

        public chatsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.message_user_name);
            profileImage = itemView.findViewById(R.id.message_user_image);
        }
    }
}
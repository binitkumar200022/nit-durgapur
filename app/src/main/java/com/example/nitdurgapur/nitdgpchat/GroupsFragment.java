package com.example.nitdurgapur.nitdgpchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.nitdurgapur.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GroupsFragment extends Fragment {

    private final ArrayList<String> arrayList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private View groupFragmentView;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private DatabaseReference groupRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        groupFragmentView = inflater.inflate(R.layout.fragment_groups, container, false);

        groupRef = FirebaseDatabase.getInstance().getReference().child("groups");

        InitializeFields();

        RetriveAndDisplayGroups();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentGroupName = parent.getItemAtPosition(position).toString();

                Intent groupChatIntent = new Intent(getContext(), GroupChatActivity.class);
                groupChatIntent.putExtra("groupName", currentGroupName);
                startActivity(groupChatIntent);
            }
        });

        return groupFragmentView;
    }

    private void InitializeFields() {

        listView = groupFragmentView.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);

    }

    private void RetriveAndDisplayGroups() {
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String user_id = currentUser.getUid();

                Set<String> set = new HashSet<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String group_name = dataSnapshot.getKey();
                    if (dataSnapshot.child("users").hasChild(user_id)) {
                        set.add(group_name);
                    }
                }

                arrayList.clear();
                arrayList.addAll(set);

                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
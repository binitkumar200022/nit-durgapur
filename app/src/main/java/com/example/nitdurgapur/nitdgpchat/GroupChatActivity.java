package com.example.nitdurgapur.nitdgpchat;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nitdurgapur.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView sendMessageButton;
    private EditText userMessageInput;
    private ScrollView scrollView;
    private TextView displayTextMessage;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef, groupMessageRef, groupMessageKeyRef;

    private String currentGroupName, currentUserID, currentUserName, currentDate, currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGroupName = getIntent().getExtras().get("groupName").toString();

        InitializeFields();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        groupMessageRef = FirebaseDatabase.getInstance().getReference().child("groups").child(currentGroupName).child("messages");

        GetsUserInfo();

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveMessageToDatabase();

                userMessageInput.setText("");

                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        groupMessageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    DisplayMessages(snapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    DisplayMessages(snapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void InitializeFields() {
        toolbar = findViewById(R.id.toolbar_group_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentGroupName);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);

        sendMessageButton = (ImageView) findViewById(R.id.group_chat_send_message);
        userMessageInput = (EditText) findViewById(R.id.input_group_message);
        scrollView = (ScrollView) findViewById(R.id.group_chat_scroll_view);
        displayTextMessage = (TextView) findViewById(R.id.group_chat_text_view);
    }

    private void GetsUserInfo() {
        userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUserName = snapshot.child("Name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SaveMessageToDatabase() {
        String message = userMessageInput.getText().toString();
        String messageKey = groupMessageRef.push().getKey();

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Please write a message first!", Toast.LENGTH_SHORT).show();
        } else {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormat.format(calendar.getTime());

            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calendar.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            groupMessageRef.updateChildren(groupMessageKey);

            groupMessageKeyRef = groupMessageRef.child(messageKey);

            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("Name", currentUserName);
            messageInfoMap.put("UID", currentUserID);
            messageInfoMap.put("Message", message);
            messageInfoMap.put("Date", currentDate);
            messageInfoMap.put("Time", currentTime);

            groupMessageKeyRef.updateChildren(messageInfoMap);
        }
    }

    private void DisplayMessages(DataSnapshot snapshot) {
        String chatDate = snapshot.child("Date").getValue().toString();
        String chatMessage = snapshot.child("Message").getValue().toString();
        String chatName = snapshot.child("Name").getValue().toString();
        String chatTime = snapshot.child("Time").getValue().toString();

        displayTextMessage.append(chatName + "\n" + chatMessage + "\n" + chatTime + "\n" + chatDate + "\n\n\n");

        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }
}
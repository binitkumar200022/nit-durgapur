package com.example.nitdurgapur.nitdgpchat;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nitdurgapur.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private final List<Messages> messagesList = new ArrayList<>();
    private String messageReceiverID, messageReceiverName, messageSenderID;
    private Toolbar toolbar;
    private TextView userName, userLastSeen;
    private FirebaseAuth auth;
    private StorageReference storageReference;
    private DatabaseReference RootRef;
    private ImageView userImage;
    private ImageButton sendMessageButton, sendFilesButton;
    private EditText messageInputText;
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;

    private String saveCurrentTime, saveCurrentDate;
    private String checker = "", myUrl = "";
    private StorageTask uploadTask;
    private Uri fileUri;
    private ProgressDialog loadingBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        auth = FirebaseAuth.getInstance();
        messageSenderID = auth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("visit_user_name").toString();

        InitializeFields();

        userName.setText(messageReceiverName);

        RootRef.child("users").child(messageReceiverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("userState").hasChild("state")) {
                    String date = snapshot.child("userState").child("date").getValue().toString();
                    String state = snapshot.child("userState").child("state").getValue().toString();
                    String time = snapshot.child("userState").child("time").getValue().toString();

                    if (state.equals("online")) {
                        userLastSeen.setText("online");
                    } else if (state.equals("offline")) {
                        String lastSeenMessage = "last seen on " + date + " at " + time;
                        userLastSeen.setText(lastSeenMessage);
                    }
                } else {
                    userLastSeen.setText(R.string.offline);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        storageReference.child(messageReceiverID + ".png").getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(userImage);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        userImage.setImageResource(R.drawable.ic_user);
                    }
                });

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        sendFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence[] options = new CharSequence[]
                        {
                                "Image",
                                "Document",
                                "Audio"
                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Select the File type:");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            checker = "image";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "Select Image"), 438);
                        }
                        if (which == 1) {
                            checker = "document";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/*");
                            startActivityForResult(Intent.createChooser(intent, "Select Document"), 438);
                        }
                        if (which == 2) {
                            checker = "audio";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("audio/*");
                            startActivityForResult(Intent.createChooser(intent, "Select Audio"), 438);
                        }
                    }
                });
                builder.show();
            }
        });

    }

    private void InitializeFields() {
        toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);

        storageReference = FirebaseStorage.getInstance().getReference().child("profile_images");

        userImage = findViewById(R.id.custom_chat_bar_user_image);
        userName = findViewById(R.id.custom_chat_bar_user_name);
        userLastSeen = findViewById(R.id.custom_chat_bar_user_last_seen);
        sendMessageButton = findViewById(R.id.chat_activity_send_message);
        sendFilesButton = findViewById(R.id.chat_activity_send_files_btn);
        messageInputText = findViewById(R.id.chat_activity_message);

        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = findViewById(R.id.private_messages_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);

        loadingBar = new ProgressDialog(this);

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd MMM yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 438 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            loadingBar.setTitle("Sending File");
            loadingBar.setMessage("Please wait, send in progress");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            fileUri = data.getData();

            if (checker.equals("image") || checker.equals("document") || checker.equals("audio")) {
                StorageReference storageReference = null;

                switch (checker) {
                    case "image":
                        storageReference = FirebaseStorage.getInstance().getReference().child("image_files");
                        break;
                    case "document":
                        storageReference = FirebaseStorage.getInstance().getReference().child("document_files");
                        break;
                    case "audio":
                        storageReference = FirebaseStorage.getInstance().getReference().child("audio_files");
                        break;
                }

                final String messageSenderRef = "private/" + messageSenderID + "/" + messageReceiverID;
                final String messageReceiverRef = "private/" + messageReceiverID + "/" + messageSenderID;

                DatabaseReference userMessageKeyRef = RootRef.child("private").child(messageSenderID).child(messageReceiverID).push();

                final String messagePushID = userMessageKeyRef.getKey();

                ContentResolver cR = getApplicationContext().getContentResolver();
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                String ttype = mime.getExtensionFromMimeType(cR.getType(fileUri));

                StorageReference filePath = storageReference.child(messagePushID + "." + ttype);

                uploadTask = filePath.putFile(fileUri);

                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUrl = task.getResult();
                            myUrl = downloadUrl.toString();

                            Map messageImageBody = new HashMap();
                            messageImageBody.put("Message", myUrl);
                            messageImageBody.put("name", fileUri.getLastPathSegment());
                            messageImageBody.put("type", checker);
                            messageImageBody.put("extension", ttype);
                            messageImageBody.put("from", messageSenderID);
                            messageImageBody.put("to", messageReceiverID);
                            messageImageBody.put("messageID", messagePushID);
                            messageImageBody.put("Time", saveCurrentTime);
                            messageImageBody.put("Date", saveCurrentDate);

                            Map messageBodyDetails = new HashMap();
                            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageImageBody);
                            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageImageBody);

                            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        loadingBar.dismiss();
                                        Toast.makeText(ChatActivity.this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        loadingBar.dismiss();
                                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            messageInputText.setText("");
                        }
                    }
                });
            } else {
                loadingBar.dismiss();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        RootRef.child("private").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Messages messages = snapshot.getValue(Messages.class);

                        messagesList.add(messages);

                        messageAdapter.notifyDataSetChanged();

                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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

    private void sendMessage() {
        String messageText = messageInputText.getText().toString();
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "Please write a message first", Toast.LENGTH_SHORT).show();
        } else {
            String messageSenderRef = "private/" + messageSenderID + "/" + messageReceiverID;
            String messageReceiverRef = "private/" + messageReceiverID + "/" + messageSenderID;

            DatabaseReference userMessageKeyRef = RootRef.child("private").child(messageSenderID).child(messageReceiverID).push();

            String messagePushID = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("Message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);
            messageTextBody.put("to", messageReceiverID);
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("Time", saveCurrentTime);
            messageTextBody.put("Date", saveCurrentDate);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChatActivity.this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            messageInputText.setText("");
        }
    }
}

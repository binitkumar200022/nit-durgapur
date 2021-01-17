package com.example.nitdurgapur.nitdgpchat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nitdurgapur.R;
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
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final List<Messages> userMessagesList;
    private FirebaseAuth auth;
    private DatabaseReference usersRef;
    private StorageReference storageReference;

    public MessageAdapter(List<Messages> userMessagesList) {
        this.userMessagesList = userMessagesList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_messages_layout, parent, false);

        auth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, final int position) {
        String messageSenderID = auth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(position);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(fromUserID);
        storageReference = FirebaseStorage.getInstance().getReference().child("profile_images");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                storageReference.child(fromUserID + ".png").getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(holder.receiverProfileImage);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                holder.receiverProfileImage.setImageResource(R.drawable.ic_user);
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.receiverMessageText.setVisibility(View.GONE);
        holder.receiverProfileImage.setVisibility(View.GONE);
        holder.senderMessageText.setVisibility(View.GONE);
        holder.messageSenderPicture.setVisibility(View.GONE);
        holder.messageReceiverPicture.setVisibility(View.GONE);

        if (fromMessageType.equals("text")) {
            if (fromUserID.equals(messageSenderID)) {
                holder.senderMessageText.setVisibility(View.VISIBLE);

                holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                holder.senderMessageText.setText(messages.getMessage() + "\n\n" + messages.getTime() + " - " + messages.getDate());
            } else {
                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverProfileImage.setVisibility(View.VISIBLE);

                holder.receiverProfileImage.setBackgroundResource(R.drawable.receiver_messages_layout);
                holder.receiverMessageText.setText(messages.getMessage() + "\n\n" + messages.getTime() + " - " + messages.getDate());
            }
        } else if (fromMessageType.equals("image")) {
            if (fromUserID.equals(messageSenderID)) {
                holder.messageSenderPicture.setVisibility(View.VISIBLE);

                Picasso.get().load(messages.getMessage()).into(holder.messageSenderPicture);
            } else {
                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);

                Picasso.get().load(messages.getMessage()).into(holder.messageReceiverPicture);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(userMessagesList.get(position).getMessage()), "image/*");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    PackageManager manager = holder.itemView.getContext().getPackageManager();
                    List<ResolveInfo> list = manager.queryIntentActivities(intent, 0);
                    if (list != null && list.size() > 0) {
                        holder.itemView.getContext().startActivity(intent);
                    } else {
                        Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        holder.itemView.getContext().startActivity(intent1);
                    }
                }
            });
        } else {
            String fromMessageExtension = messages.getExtension();
            if (fromMessageType.equals("audio")) {
                if (fromUserID.equals(messageSenderID)) {
                    holder.messageSenderPicture.setVisibility(View.VISIBLE);

                    holder.messageSenderPicture.setBackgroundResource(R.drawable.audio);
                } else {
                    holder.receiverProfileImage.setVisibility(View.VISIBLE);
                    holder.messageReceiverPicture.setVisibility(View.VISIBLE);

                    holder.messageReceiverPicture.setBackgroundResource(R.drawable.audio);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(userMessagesList.get(position).getMessage()), "audio/*");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        PackageManager manager = holder.itemView.getContext().getPackageManager();
                        List<ResolveInfo> list = manager.queryIntentActivities(intent, 0);
                        if (list != null && list.size() > 0) {
                            holder.itemView.getContext().startActivity(intent);
                        } else {
                            Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            holder.itemView.getContext().startActivity(intent1);
                        }
                    }
                });
            } else if (fromMessageExtension.equals("pdf")) {
                if (fromUserID.equals(messageSenderID)) {
                    holder.messageSenderPicture.setVisibility(View.VISIBLE);

                    holder.messageSenderPicture.setBackgroundResource(R.drawable.pdf);
                } else {
                    holder.receiverProfileImage.setVisibility(View.VISIBLE);
                    holder.messageReceiverPicture.setVisibility(View.VISIBLE);

                    holder.messageReceiverPicture.setBackgroundResource(R.drawable.pdf);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(userMessagesList.get(position).getMessage()), "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        PackageManager manager = holder.itemView.getContext().getPackageManager();
                        List<ResolveInfo> list = manager.queryIntentActivities(intent, 0);
                        if (list != null && list.size() > 0) {
                            holder.itemView.getContext().startActivity(intent);
                        } else {
                            Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            holder.itemView.getContext().startActivity(intent1);
                        }
                    }
                });
            } else if (fromMessageExtension.equals("doc") || fromMessageExtension.equals("docx") || fromMessageExtension.equals("odt")) {
                if (fromUserID.equals(messageSenderID)) {
                    holder.messageSenderPicture.setVisibility(View.VISIBLE);

                    holder.messageSenderPicture.setBackgroundResource(R.drawable.word);
                } else {
                    holder.receiverProfileImage.setVisibility(View.VISIBLE);
                    holder.messageReceiverPicture.setVisibility(View.VISIBLE);

                    holder.messageReceiverPicture.setBackgroundResource(R.drawable.word);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(userMessagesList.get(position).getMessage()), "application/msword");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        PackageManager manager = holder.itemView.getContext().getPackageManager();
                        List<ResolveInfo> list = manager.queryIntentActivities(intent, 0);
                        if (list != null && list.size() > 0) {
                            holder.itemView.getContext().startActivity(intent);
                        } else {
                            Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            holder.itemView.getContext().startActivity(intent1);
                        }
                    }
                });
            } else {
                if (fromUserID.equals(messageSenderID)) {
                    holder.messageSenderPicture.setVisibility(View.VISIBLE);

                    holder.messageSenderPicture.setBackgroundResource(R.drawable.file);
                } else {
                    holder.receiverProfileImage.setVisibility(View.VISIBLE);
                    holder.messageReceiverPicture.setVisibility(View.VISIBLE);

                    holder.messageReceiverPicture.setBackgroundResource(R.drawable.file);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        holder.itemView.getContext().startActivity(intent);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView senderMessageText, receiverMessageText;
        public ImageView receiverProfileImage, messageSenderPicture, messageReceiverPicture;


        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText = itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage = itemView.findViewById(R.id.custom_messages_layout_user_image);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
        }
    }
}

package com.example.nitdurgapur.nitdgpchat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nitdurgapur.NitDgpChat;
import com.example.nitdurgapur.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
        holder.messageSenderLinearLayout.setVisibility(View.GONE);
        holder.messageSenderPicture.setVisibility(View.GONE);
        holder.messageSenderTextView.setVisibility(View.GONE);
        holder.messageReceiverLinearLayout.setVisibility(View.GONE);
        holder.messageReceiverPicture.setVisibility(View.GONE);
        holder.messageReceiverTextView.setVisibility(View.GONE);

        if (fromMessageType.equals("text")) {
            if (fromUserID.equals(messageSenderID)) {
                holder.senderMessageText.setVisibility(View.VISIBLE);

                holder.senderMessageText.setText(messages.getMessage() + "\n\n" + messages.getTime() + " - " + messages.getDate());
            } else {
                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverProfileImage.setVisibility(View.VISIBLE);

                holder.receiverMessageText.setText(messages.getMessage() + "\n\n" + messages.getTime() + " - " + messages.getDate());
            }
        } else if (fromMessageType.equals("image")) {
            if (fromUserID.equals(messageSenderID)) {
                holder.messageSenderLinearLayout.setVisibility(View.VISIBLE);
                holder.messageSenderPicture.setVisibility(View.VISIBLE);
                holder.messageSenderTextView.setVisibility(View.VISIBLE);

                Picasso.get().load(messages.getMessage()).into(holder.messageSenderPicture);
                holder.messageSenderTextView.setText(messages.getTime() + " - " + messages.getDate());
            } else {
                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverLinearLayout.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);
                holder.messageReceiverTextView.setVisibility(View.VISIBLE);

                Picasso.get().load(messages.getMessage()).into(holder.messageReceiverPicture);
                holder.messageReceiverTextView.setText(messages.getTime() + " - " + messages.getDate());
            }
        } else if (fromMessageType.equals("audio")) {
            if (fromUserID.equals(messageSenderID)) {
                holder.messageSenderLinearLayout.setVisibility(View.VISIBLE);
                holder.messageSenderPicture.setVisibility(View.VISIBLE);
                holder.messageSenderTextView.setVisibility(View.VISIBLE);

                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/nit-durgapur-2204.appspot.com/o/icon_files%2Faudio.png?alt=media&token=a2fbab13-e28f-4458-88e1-6f15d65b1037").into(holder.messageSenderPicture);
                holder.messageSenderTextView.setText(messages.getTime() + " - " + messages.getDate());
            } else {
                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverLinearLayout.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);
                holder.messageReceiverTextView.setVisibility(View.VISIBLE);

                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/nit-durgapur-2204.appspot.com/o/icon_files%2Faudio.png?alt=media&token=a2fbab13-e28f-4458-88e1-6f15d65b1037").into(holder.messageReceiverPicture);
                holder.messageReceiverTextView.setText(messages.getTime() + " - " + messages.getDate());
            }
        } else if (fromMessageType.equals("document")) {
            String fromMessageExtension = messages.getExtension();
            if (fromMessageExtension.equals("pdf")) {
                if (fromUserID.equals(messageSenderID)) {
                    holder.messageSenderLinearLayout.setVisibility(View.VISIBLE);
                    holder.messageSenderPicture.setVisibility(View.VISIBLE);
                    holder.messageSenderTextView.setVisibility(View.VISIBLE);

                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/nit-durgapur-2204.appspot.com/o/icon_files%2Fpdf.png?alt=media&token=8bb36568-e8ff-4df4-be69-4e5691f05497").into(holder.messageSenderPicture);
                    holder.messageSenderTextView.setText(messages.getTime() + " - " + messages.getDate());
                } else {
                    holder.receiverProfileImage.setVisibility(View.VISIBLE);
                    holder.messageReceiverLinearLayout.setVisibility(View.VISIBLE);
                    holder.messageReceiverPicture.setVisibility(View.VISIBLE);
                    holder.messageReceiverTextView.setVisibility(View.VISIBLE);

                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/nit-durgapur-2204.appspot.com/o/icon_files%2Fpdf.png?alt=media&token=8bb36568-e8ff-4df4-be69-4e5691f05497").into(holder.messageReceiverPicture);
                    holder.messageReceiverTextView.setText(messages.getTime() + " - " + messages.getDate());
                }
            } else if (fromMessageExtension.equals("doc") || fromMessageExtension.equals("docx") || fromMessageExtension.equals("odt")) {
                if (fromUserID.equals(messageSenderID)) {
                    holder.messageSenderLinearLayout.setVisibility(View.VISIBLE);
                    holder.messageSenderPicture.setVisibility(View.VISIBLE);
                    holder.messageSenderTextView.setVisibility(View.VISIBLE);

                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/nit-durgapur-2204.appspot.com/o/icon_files%2Fword.png?alt=media&token=0bc184a6-129a-4b10-9c9b-2766c48c0437").into(holder.messageSenderPicture);
                    holder.messageSenderTextView.setText(messages.getTime() + " - " + messages.getDate());
                } else {
                    holder.receiverProfileImage.setVisibility(View.VISIBLE);
                    holder.messageReceiverLinearLayout.setVisibility(View.VISIBLE);
                    holder.messageReceiverPicture.setVisibility(View.VISIBLE);
                    holder.messageReceiverTextView.setVisibility(View.VISIBLE);

                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/nit-durgapur-2204.appspot.com/o/icon_files%2Fword.png?alt=media&token=0bc184a6-129a-4b10-9c9b-2766c48c0437").into(holder.messageReceiverPicture);
                    holder.messageReceiverTextView.setText(messages.getTime() + " - " + messages.getDate());
                }
            } else {
                if (fromUserID.equals(messageSenderID)) {
                    holder.messageSenderLinearLayout.setVisibility(View.VISIBLE);
                    holder.messageSenderPicture.setVisibility(View.VISIBLE);
                    holder.messageSenderTextView.setVisibility(View.VISIBLE);

                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/nit-durgapur-2204.appspot.com/o/icon_files%2Ffile.png?alt=media&token=ec68114a-2ef7-4e92-8c18-5dd44060aa92").into(holder.messageSenderPicture);
                    holder.messageSenderTextView.setText(messages.getTime() + " - " + messages.getDate());
                } else {
                    holder.receiverProfileImage.setVisibility(View.VISIBLE);
                    holder.messageReceiverLinearLayout.setVisibility(View.VISIBLE);
                    holder.messageReceiverPicture.setVisibility(View.VISIBLE);
                    holder.messageReceiverTextView.setVisibility(View.VISIBLE);

                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/nit-durgapur-2204.appspot.com/o/icon_files%2Ffile.png?alt=media&token=ec68114a-2ef7-4e92-8c18-5dd44060aa92").into(holder.messageReceiverPicture);
                    holder.messageReceiverTextView.setText(messages.getTime() + " - " + messages.getDate());
                }
            }
        }

        if (fromUserID.equals(messageSenderID)) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userMessagesList.get(position).getType().equals("document")) {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Download and View This Document",
                                        "Delete for me",
                                        "Delete for everyone",
                                        "Cancel"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Select Action: ");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    String fromMessageExtension = messages.getExtension();
                                    if (fromMessageExtension.equals("pdf")) {
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
                                    } else if (fromMessageExtension.equals("doc") || fromMessageExtension.equals("docx") || fromMessageExtension.equals("odt")) {
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
                                    } else {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        holder.itemView.getContext().startActivity(intent);
                                    }
                                } else if (which == 1) {
                                    deleteSentMessage(position, holder);

                                } else if (which == 2) {
                                    deleteMessageForEveryone(position, holder);
                                }
                            }
                        });
                        builder.show();
                    } else if (userMessagesList.get(position).getType().equals("audio")) {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Download and Listen This Audio",
                                        "Delete for me",
                                        "Delete for everyone",
                                        "Cancel"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Select Action: ");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
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
                                } else if (which == 1) {
                                    deleteSentMessage(position, holder);
                                } else if (which == 2) {
                                    deleteMessageForEveryone(position, holder);
                                }
                            }
                        });
                        builder.show();
                    } else if (userMessagesList.get(position).getType().equals("image")) {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Download and View This Image",
                                        "Delete for me",
                                        "Delete for everyone",
                                        "Cancel"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Select Action: ");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
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
                                } else if (which == 1) {
                                    deleteSentMessage(position, holder);
                                } else if (which == 2) {
                                    deleteMessageForEveryone(position, holder);
                                }
                            }
                        });
                        builder.show();
                    } else if (userMessagesList.get(position).getType().equals("text")) {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Delete for me",
                                        "Delete for everyone",
                                        "Cancel"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Select Action: ");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    deleteSentMessage(position, holder);
                                } else if (which == 1) {
                                    deleteMessageForEveryone(position, holder);
                                }
                            }
                        });
                        builder.show();
                    }
                }
            });
        } else {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userMessagesList.get(position).getType().equals("document")) {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Download and View This Document",
                                        "Delete for me",
                                        "Cancel"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Select Action: ");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    String fromMessageExtension = messages.getExtension();
                                    if (fromMessageExtension.equals("pdf")) {
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
                                    } else if (fromMessageExtension.equals("doc") || fromMessageExtension.equals("docx") || fromMessageExtension.equals("odt")) {
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
                                    } else {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        holder.itemView.getContext().startActivity(intent);
                                    }
                                } else if (which == 1) {
                                    deleteReceivedMessage(position, holder);
                                }
                            }
                        });
                        builder.show();
                    } else if (userMessagesList.get(position).getType().equals("audio")) {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Download and Listen This Audio",
                                        "Delete for me",
                                        "Cancel"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Select Action: ");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
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
                                } else if (which == 1) {
                                    deleteReceivedMessage(position, holder);
                                }
                            }
                        });
                        builder.show();
                    } else if (userMessagesList.get(position).getType().equals("image")) {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Download and View This Image",
                                        "Delete for me",
                                        "Cancel"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Select Action: ");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
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
                                } else if (which == 1) {
                                    deleteReceivedMessage(position, holder);
                                }
                            }
                        });
                        builder.show();
                    } else if (userMessagesList.get(position).getType().equals("text")) {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Delete for me",
                                        "Cancel"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Select Action: ");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    deleteReceivedMessage(position, holder);
                                    holder.receiverMessageText.setVisibility(View.GONE);
                                }
                            }
                        });
                        builder.show();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

    private void deleteSentMessage(final int position, final MessageViewHolder holder) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("private")
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(holder.itemView.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), NitDgpChat.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    private void deleteReceivedMessage(final int position, final MessageViewHolder holder) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("private")
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(holder.itemView.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), NitDgpChat.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    private void deleteMessageForEveryone(final int position, final MessageViewHolder holder) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("private")
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    rootRef.child("private")
                            .child(userMessagesList.get(position).getTo())
                            .child(userMessagesList.get(position).getFrom())
                            .child(userMessagesList.get(position).getMessageID())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(holder.itemView.getContext(), "Deleted Successfully for both", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(holder.itemView.getContext(), "Deleted Successfully for you", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), NitDgpChat.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView senderMessageText, receiverMessageText, messageReceiverTextView, messageSenderTextView;
        public ImageView receiverProfileImage, messageSenderPicture, messageReceiverPicture;
        public RelativeLayout messageReceiverLinearLayout, messageSenderLinearLayout;


        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText = itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage = itemView.findViewById(R.id.custom_messages_layout_user_image);
            messageSenderLinearLayout = itemView.findViewById(R.id.message_sender_linear_layout);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
            messageSenderTextView = itemView.findViewById(R.id.message_sender_text_view);
            messageReceiverLinearLayout = itemView.findViewById(R.id.message_receiver_linear_layout);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
            messageReceiverTextView = itemView.findViewById(R.id.message_receiver_text_view);
        }
    }
}

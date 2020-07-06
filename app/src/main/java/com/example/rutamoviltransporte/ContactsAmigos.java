package com.example.rutamoviltransporte;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAmigos extends AppCompatActivity {
    private RecyclerView mis_amigos;
    private Toolbar toolbaramigos;
    private DatabaseReference usersContactsref, data, ChatRequest, ContactoRef;
    //private Dialog dialog;
    private String receiverID, senderUserId, Current_state, visitar;
    private CircleImageView imageprofile;
    private TextView userprofile;
    private Button buttomprofile, buttomprofiledecline;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_amigos);

        //dialog = new Dialog(this);

        usersContactsref = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        mis_amigos = findViewById(R.id.mis_amigos);
        mis_amigos.setLayoutManager(new LinearLayoutManager(this));

        getWindow().setStatusBarColor(getColor(R.color.colorVioletDark));
        toolbaramigos = findViewById(R.id.contacts_toolbar);
        setSupportActionBar(toolbaramigos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Mis Amigos");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<ModelContacts> options = new FirebaseRecyclerOptions.Builder<ModelContacts>()
                .setQuery(usersContactsref, ModelContacts.class)
                .build();
        FirebaseRecyclerAdapter<ModelContacts, AmigosViewHolder> adapter = new FirebaseRecyclerAdapter<ModelContacts, AmigosViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AmigosViewHolder holder, int position, @NonNull ModelContacts model)
            {
                holder.userName.setText(model.getNombre());
                holder.userEmail.setText(model.getEmail());
                //Picasso.get().load(model.getPhoto()).placeholder(R.drawable.perfil).into(holder.profileImage);
                Glide.with(ContactsAmigos.this).load(model.getPhoto()).placeholder(R.drawable.perfil).into(holder.profileImage);



                //Clicks de Items
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        visitar = getRef(position).getKey();
                        //SolRespuesta(visitar);
                        RespuestaRequest(visitar);

                    }
                });
            }

            @NonNull
            @Override
            public AmigosViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_contact, viewGroup, false);
                AmigosViewHolder viewHolder = new AmigosViewHolder(view);
                return viewHolder;
            }
        };
        mis_amigos.setAdapter(adapter);
        adapter.startListening();
    }

    private void RespuestaRequest(String visitar) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ContactsAmigos.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_solrespuesta, null);

        imageprofile = mView.findViewById(R.id.ImageContactdialogvisit);
        userprofile = mView.findViewById(R.id.textdialogvisit);
        buttomprofile = mView.findViewById(R.id.sendmessage);
        buttomprofiledecline = mView.findViewById(R.id.declinemessage);
        Current_state ="new";

        mAuth = FirebaseAuth.getInstance();
        data = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        ChatRequest = FirebaseDatabase.getInstance().getReference().child("ChatMessageSolid");
        ContactoRef = FirebaseDatabase.getInstance().getReference().child("Contactos");
        informations();

        receiverID = visitar;

        senderUserId = mAuth.getCurrentUser().getUid();
        Toast.makeText(this, "UserID: "+ receiverID, Toast.LENGTH_SHORT).show();

        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void informations() {
        data.child(visitar).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Photo"))){
                    String userimage = dataSnapshot.child("Photo").getValue().toString();
                    String username = dataSnapshot.child("Nombre").getValue().toString();

                    Glide.with(getApplicationContext()).load(userimage).placeholder(R.drawable.perfil).into(imageprofile);
                    userprofile.setText(username);

                    ManageChatRequest();


                }else {
                    String username = dataSnapshot.child("Nombre").getValue().toString();
                    userprofile.setText(username);
                    ManageChatRequest();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ManageChatRequest() {

        ChatRequest.child(senderUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(receiverID)){
                            String request_type = dataSnapshot.child(receiverID).child("tipo de Solicitud").getValue().toString();
                            if (request_type.equals("Sent")){
                                Current_state = "request_Sent";
                                buttomprofile.setText("Cancelar Solicitud");
                            }
                            else if (request_type.equals("Recibido")){
                                Current_state = "request_Recibido";
                                buttomprofile.setText("Aceptar Solicitud");
                                buttomprofiledecline.setVisibility(View.VISIBLE);
                                buttomprofiledecline.setEnabled(true);
                                buttomprofiledecline.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        CancelChatRequest();
                                    }
                                });
                            }
                        }else {
                            ContactoRef.child(senderUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(receiverID)){
                                                Current_state = "Friends";
                                                buttomprofile.setText("Eliminar este Contacto");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        if (!senderUserId.equals(receiverID)){

            buttomprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    buttomprofile.setEnabled(false);
                    if (Current_state.equals("new")){
                        SendChatRequest();
                    }
                    if (Current_state.equals("request_Sent")){
                        CancelChatRequest();
                    }
                    if (Current_state.equals("request_Recibido")){
                        AcceptChatRequest();
                    }
                    if (Current_state.equals("Friends")){
                        RemoveContact();
                    }
                }
            });
        }
        else {
            buttomprofile.setVisibility(View.INVISIBLE);
        }
    }

    private void RemoveContact() {
        ContactoRef.child(senderUserId).child(receiverID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            ContactoRef.child(receiverID).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                buttomprofile.setEnabled(true);
                                                Current_state = "new";
                                                buttomprofile.setText("Enviar Solicitud");

                                                buttomprofiledecline.setVisibility(View.INVISIBLE);
                                                buttomprofiledecline.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptChatRequest() {
        ContactoRef.child(senderUserId).child(receiverID)
                .child("Contactos").setValue("Guardado")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            ContactoRef.child(receiverID).child(senderUserId)
                                    .child("Contactos").setValue("Guardado")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                ChatRequest.child(senderUserId).child(receiverID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    ChatRequest.child(receiverID).child(senderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    buttomprofile.setEnabled(true);
                                                                                    Current_state = "Friends";
                                                                                    buttomprofile.setText("Eliminar este Contacto");

                                                                                    buttomprofiledecline.setVisibility(View.INVISIBLE);
                                                                                    buttomprofiledecline.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void CancelChatRequest() {
        ChatRequest.child(senderUserId).child(receiverID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            ChatRequest.child(receiverID).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                buttomprofile.setEnabled(true);
                                                Current_state = "new";
                                                buttomprofile.setText("Enviar Solicitud");

                                                buttomprofiledecline.setVisibility(View.INVISIBLE);
                                                buttomprofiledecline.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void SendChatRequest() {
        ChatRequest.child(senderUserId).child(receiverID)
                .child("tipo de Solicitud").setValue("Sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            ChatRequest.child(receiverID).child(senderUserId)
                                    .child("tipo de Solicitud").setValue("Recibido")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                buttomprofile.setEnabled(true);
                                                Current_state = "request_Sent";
                                                buttomprofile.setText("Cancelar Solicitud");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
/*
    private void SolRespuesta(String visitar) {

        String receiverID, senderUserID, current_state;
        CircleImageView imageprofile;
        TextView userprofile;
        Button buttomprofile;
        DatabaseReference data, chatuserID;
        FirebaseAuth firebaseAuth;

        dialog.setContentView(R.layout.dialog_solrespuesta);

        imageprofile = dialog.findViewById(R.id.ImageContactdialogvisit);
        userprofile = dialog.findViewById(R.id.textdialogvisit);
        buttomprofile = dialog.findViewById(R.id.sendmessage);
        current_state ="new";

        firebaseAuth = FirebaseAuth.getInstance();
        data = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        informations(visitar, data, imageprofile, userprofile, buttomprofile);
        receiverID = visitar;
        senderUserID = firebaseAuth.getCurrentUser().getUid();
        Toast.makeText(this, "UserID: "+ receiverID, Toast.LENGTH_SHORT).show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void informations(String visitar, DatabaseReference data, CircleImageView imageprofile, TextView userprofile, Button buttomprofile) {
        data.child(visitar).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Photo"))){
                    String userimage = dataSnapshot.child("Photo").getValue().toString();
                    String username = dataSnapshot.child("Nombre").getValue().toString();

                    Glide.with(ContactsAmigos.this).load(userimage).placeholder(R.drawable.perfil).into(imageprofile);
                    userprofile.setText(username);


                }else {
                    String username = dataSnapshot.child("Nombre").getValue().toString();
                    userprofile.setText(username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }*/

    public static class AmigosViewHolder extends RecyclerView.ViewHolder{

        TextView userName, userEmail;
        CircleImageView profileImage;
        public AmigosViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.NameContact);
            userEmail = itemView.findViewById(R.id.EmailContact);
            profileImage =itemView.findViewById(R.id.ImageContact);
        }
    }
}

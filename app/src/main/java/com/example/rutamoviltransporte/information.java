package com.example.rutamoviltransporte;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class information extends Fragment {
    private View informationView;
    private RecyclerView myinformationList;
    private DatabaseReference requestRef, usersref, contactsref;
    private FirebaseAuth mAuth;
    private String currentUserID;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);

        informationView = inflater.inflate(R.layout.fragment_information, container, false);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        usersref = FirebaseDatabase.getInstance().getReference("Usuarios");
        requestRef = FirebaseDatabase.getInstance().getReference().child("ChatMessageSolid");
        contactsref = FirebaseDatabase.getInstance().getReference().child("Contactos");

        myinformationList = informationView.findViewById(R.id.list_information_message);
        myinformationList.setLayoutManager(new LinearLayoutManager(getContext()));

        return informationView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<ModelContacts> options =
                new FirebaseRecyclerOptions.Builder<ModelContacts>()
                .setQuery(requestRef.child(currentUserID), ModelContacts.class)
                .build();
        FirebaseRecyclerAdapter<ModelContacts, RequestViewHolder> adapter = new FirebaseRecyclerAdapter<ModelContacts, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RequestViewHolder holder, int position, @NonNull ModelContacts model) {
                holder.itemView.findViewById(R.id.request_button_accept).setVisibility(View.VISIBLE);
                final String list_user_id = getRef(position).getKey();
                DatabaseReference gettyperef = getRef(position).child("tipo de Solicitud").getRef();
                gettyperef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String type = dataSnapshot.getValue().toString();
                            if (type.equals("Recibido")){
                                usersref.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild("Photo")){
                                            //final String requestEmail = dataSnapshot.child("Email").getValue().toString();
                                            //final String requestName = dataSnapshot.child("Nombre").getValue().toString();
                                            final String requestprofileImage = dataSnapshot.child("Photo").getValue().toString();

                                            //holder.userEmail.setText(requestEmail);
                                            //holder.userName.setText(requestName);
                                            if (isAdded()){
                                                Glide.with(getContext()).load(requestprofileImage).placeholder(R.drawable.perfil).into(holder.profileImage);
                                            }
                                        }

                                        final String requestName = dataSnapshot.child("Nombre").getValue().toString();
                                        final String requestEmail = dataSnapshot.child("Email").getValue().toString();

                                        holder.userEmail.setText("Quiere conectarse contigo");
                                        holder.userName.setText(requestName);


                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                CharSequence options[]= new CharSequence[]{
                                                        "Aceptar", "Cancelar"
                                                };
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle(requestName +"  ChatMessageSolid");
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        if (i == 0 ){
                                                            contactsref.child(currentUserID).child(list_user_id).child("Contactos")
                                                                    .setValue("Guardado").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        contactsref.child(list_user_id).child(currentUserID).child("Contactos")
                                                                                .setValue("Guardado").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()){
                                                                                    Toast.makeText(getContext(), "Contacto Guardado", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });
                                                        }
                                                        if (i== 1){

                                                            requestRef.child(currentUserID).child(list_user_id)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()){
                                                                                requestRef.child(list_user_id).child(currentUserID)
                                                                                        .removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()){
                                                                                                    Toast.makeText(getContext(), "Contacto Eliminado", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            else if (type.equals("Sent")){
                                Button request_sent = holder.itemView.findViewById(R.id.request_button_accept);
                                request_sent.setText("Solicitud Enviada");

                                usersref.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild("Photo")){
                                            //final String requestEmail = dataSnapshot.child("Email").getValue().toString();
                                            //final String requestName = dataSnapshot.child("Nombre").getValue().toString();
                                            final String requestprofileImage = dataSnapshot.child("Photo").getValue().toString();

                                            //holder.userEmail.setText(requestEmail);
                                            //holder.userName.setText(requestName);
                                            if (isAdded()){
                                                Glide.with(getContext()).load(requestprofileImage).placeholder(R.drawable.perfil).into(holder.profileImage);
                                            }
                                        }

                                        final String requestName = dataSnapshot.child("Nombre").getValue().toString();
                                        final String requestEmail = dataSnapshot.child("Email").getValue().toString();

                                        holder.userEmail.setText("Ha enviado una solicitud a " + requestName);
                                        holder.userName.setText(requestName);


                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                CharSequence options[]= new CharSequence[]{
                                                        "Cancelar Solicitud"
                                                };
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle("Ya envié la solicitud");
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        if (i== 0){
                                                            requestRef.child(currentUserID).child(list_user_id)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()){
                                                                                requestRef.child(list_user_id).child(currentUserID)
                                                                                        .removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()){
                                                                                                    Toast.makeText(getContext(), "Has Cancelado la Solicitud para Contacto", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_contact, viewGroup, false);
                RequestViewHolder holder = new RequestViewHolder(view);
                return holder;
            }
        };
        myinformationList.setAdapter(adapter);
        adapter.startListening();
    }
    public static class RequestViewHolder extends RecyclerView.ViewHolder{

        TextView userName, userEmail;
        CircleImageView profileImage;
        Button Accept;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.NameContact);
            userEmail = itemView.findViewById(R.id.EmailContact);
            profileImage =itemView.findViewById(R.id.ImageContact);
            Accept =itemView.findViewById(R.id.request_button_accept);
        }
    }

}

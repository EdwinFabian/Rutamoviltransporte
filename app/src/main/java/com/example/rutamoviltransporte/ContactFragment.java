package com.example.rutamoviltransporte;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {

    private View ContactView;
    private RecyclerView myContactList;
    private DatabaseReference Contactref, userRef;
    private FirebaseAuth mAuth;
    private String currentUserID;


    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ContactView= inflater.inflate(R.layout.fragment_contact, container, false);
        //Init RecyclerView
        myContactList = ContactView.findViewById(R.id.contacts);
        //Actualizar propiedad
        myContactList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        Contactref = FirebaseDatabase.getInstance().getReference().child("Contactos").child(currentUserID);
        userRef = FirebaseDatabase.getInstance().getReference().child("Usuarios");

        //getAllContacts();
        return ContactView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<ModelContacts>()
                .setQuery(Contactref, ModelContacts.class)
                .build();
        FirebaseRecyclerAdapter<ModelContacts, ContactsViewHolder> adapter
                = new FirebaseRecyclerAdapter<ModelContacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ContactsViewHolder holder, int position, @NonNull ModelContacts model) {
                String userID = getRef(position).getKey();
                userRef.child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("Photo")){
                            String userimage = dataSnapshot.child("Photo").getValue().toString();
                            String profileName = dataSnapshot.child("Nombre").getValue().toString();
                            String profileEmail = dataSnapshot.child("Email").getValue().toString();

                            holder.userName.setText(profileName);
                            holder.emailName.setText(profileEmail);

                            if (isAdded()){
                                Glide.with(getContext()).load(userimage).placeholder(R.drawable.perfil).into(holder.profileImage);
                            }
                        }else {
                            String profileName = dataSnapshot.child("Nombre").getValue().toString();
                            String profileEmail = dataSnapshot.child("Email").getValue().toString();

                            holder.userName.setText(profileName);
                            holder.emailName.setText(profileEmail);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_contact, viewGroup, false);
                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                return viewHolder;
            }
        };

        myContactList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder{

        TextView userName, emailName;
        CircleImageView profileImage;

        public ContactsViewHolder(@NonNull View itemView){
            super(itemView);

            userName = itemView.findViewById(R.id.NameContact);
            emailName = itemView.findViewById(R.id.EmailContact);
            profileImage = itemView.findViewById(R.id.ImageContact);
        }
    }


    /*
    private void getAllContacts() {
        final FirebaseUser fragmentuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contactsList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelContacts modelContacts = ds.getValue(ModelContacts.class);
                    if (!modelContacts.getCurrentUserId().equals(fragmentuser.getUid())){
                        contactsList.add(modelContacts);
                    }

                    adapterContacts = new AdapterContacts(getActivity(), contactsList);
                    recyclerView.setAdapter(adapterContacts);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/

}

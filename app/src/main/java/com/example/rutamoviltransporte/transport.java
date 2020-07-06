package com.example.rutamoviltransporte;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class transport extends AppCompatActivity {
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    TabAdapter tabAdapter;
    TabItem tabChats;
    TabItem tabStatus;
    TabItem tabCalls;
    private String[] colors = {"Contactos", "Peticiones", "Reporte"};
    private DatabaseReference reference;
    private String currentUserID;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.Title_toolbar));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setStatusBarColor(getColor(R.color.t));
        tabLayout = findViewById(R.id.TabLayout);
        tabChats = findViewById(R.id.tabChats);
        tabStatus = findViewById(R.id.tabStatus);
        tabCalls = findViewById(R.id.tabCalls);
        viewPager = findViewById(R.id.viewPager_id);
        //Referenciar datos a grupo.
        reference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        tabAdapter = new TabAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(tabAdapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 1) {
                    toolbar.setBackgroundColor(ContextCompat.getColor(transport.this,
                            R.color.f));
                    tabLayout.setBackgroundColor(ContextCompat.getColor(transport.this,
                            R.color.f));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().setStatusBarColor(ContextCompat.getColor(transport.this,
                                R.color.f));
                    }
                } else if (tab.getPosition() == 2) {
                    toolbar.setBackgroundColor(ContextCompat.getColor(transport.this,
                            R.color.s));
                    tabLayout.setBackgroundColor(ContextCompat.getColor(transport.this,
                            R.color.s));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().setStatusBarColor(ContextCompat.getColor(transport.this,
                                R.color.s));
                    }
                } else {
                    toolbar.setBackgroundColor(ContextCompat.getColor(transport.this,
                            R.color.t));
                    tabLayout.setBackgroundColor(ContextCompat.getColor(transport.this,
                            R.color.t));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().setStatusBarColor(ContextCompat.getColor(transport.this,
                                R.color.t));
                    }
                }
                toolbar.setTitle(colors[tab.getPosition()]);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.configuration, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        switch (id){
            case R.id.action_add_contact:
                Intent intent = new Intent(this, ContactsAmigos.class); startActivity(intent);
                //Toast.makeText(transport.this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_settings:
                Toast.makeText(transport.this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_about_us:
                Toast.makeText(transport.this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_group:
                //addgroup();
                Toast.makeText(transport.this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    /*
    private void addgroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(transport.this, R.style.AlertDialog);
        builder.setTitle("Crear nombre de un grupo :");

        final EditText groupNameField = new EditText(transport.this);
        groupNameField.setHint("Nombre del Grupo");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String namegrupo = groupNameField.getText().toString();
                if (TextUtils.isEmpty(namegrupo)){
                    Toast.makeText(transport.this, "por favor escribe un grupo", Toast.LENGTH_SHORT).show();
                }else {
                    CreateNewGroup(namegrupo);
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    private void CreateNewGroup(String namegrupo) {
        reference.child("Grupos").child(namegrupo).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(transport.this, namegrupo+ "creado exitosamente.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser != null ){
            updateuserstate("online");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (currentUser != null){
            updateuserstate("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (currentUser != null){
            updateuserstate("offline");
        }

    }

    private void updateuserstate(String state){
        String saveCurrentTime, saveCurrentDate;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String,Object> OnlineState = new HashMap<>();
        OnlineState.put("time", saveCurrentTime);
        OnlineState.put("date", saveCurrentDate);
        OnlineState.put("state", state);

        currentUserID = mAuth.getCurrentUser().getUid();
        reference.child("Usuarios").child(currentUserID).child("Estado")
                .updateChildren(OnlineState);
    }
}

package com.example.rutamoviltransporte;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatRatingBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
//facebook

public class Perfil extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private CircleImageView ImageViewPhoto, ImageViewPhotoEdit;
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView idTextView;

    private GoogleApiClient googleApiClient;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private String currentUserID, UserId;
    private DatabaseReference reference;
    private ImageView PhotoGallery, dialogvaloration, cerrardialog;
    private static int PReqCode = 1;
    private static final int REQUESCODE = 1;
    private Uri pickedImageUri;
    private Button logout, opinion, information;
    private TextView valoracionn, textcontent, rating, calificacion;
    private EditText descripcion;
    private AppCompatRatingBar ratingBar;

    private LinearLayout linearLayout;
    private AnimationDrawable animationDrawable;

    private StorageReference Userprofileimageref;
    private ProgressDialog loadingbar;

    //image and text Edit
    private CircleImageView ImageViewPhotoEditar;
    private EditText input_name_edit;
    private Button guardar_editar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        ImageViewPhoto = findViewById(R.id.ImageViewPhoto);
        ImageViewPhotoEdit = findViewById(R.id.ImageViewPhotoEdit);
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        information = findViewById(R.id.Information);
        idTextView = findViewById(R.id.idTextView);
        PhotoGallery = findViewById(R.id.PhotoGallery);
        logout = findViewById(R.id.button);

        dialogvaloration = findViewById(R.id.dialogvaloration);
        loadingbar = new ProgressDialog(this);

        /* configurar imagen de perfil.*/
/*
        ImageViewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent();
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, REQUESCODE);
            }
        });*/

        information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ajuste_perfil();
            }
        });

//--------------------------------
        //facebook
//--------------------------------

        dialogvaloration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(Perfil.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_valoration, null);
                ratingBar = mView.findViewById(R.id.ratingBar);
                valoracionn = mView.findViewById(R.id.textvaloration);
                textcontent = mView.findViewById(R.id.textcontent);
                rating = mView.findViewById(R.id.valoracionn);
                opinion = mView.findViewById(R.id.opinion);
                descripcion = mView.findViewById(R.id.input_name);
                calificacion = mView.findViewById(R.id.calificacion);

                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                        calificacion.setVisibility(View.VISIBLE);
                        rating.setText("Rating: "+ v);
                        calificacion.setText(String.valueOf(v));
                        switch ((int) ratingBar.getRating()) {
                            case 1:
                                calificacion.setText("Muy malo");
                                break;
                            case 2:
                                calificacion.setText("Necesita alguna mejora");
                                break;
                            case 3:
                                calificacion.setText("Bueno");
                                break;
                            case 4:
                                calificacion.setText("Excelente");
                                break;
                            case 5:
                                calificacion.setText("Excelente. Me encanta");
                                break;
                            default:
                                calificacion.setText("");
                        }
                        //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios").child(currentUserID).child("Servicio").child("Valoracion");
                        double dbrating = v;
                        //databaseReference.setValue(dbrating);
                        opinion.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (descripcion.getText().toString().isEmpty()) {
                                    Toast.makeText(Perfil.this, "Por favor complete el cuadro de texto", Toast.LENGTH_LONG).show();
                                } else {
                                    //descripcion.setText("");
                                    ratingBar.setRating(0);
                                    Toast.makeText(Perfil.this, "Gracias su opinion es importante", Toast.LENGTH_SHORT).show();

                                    String puntuacion = calificacion.getText().toString();
                                    reference.child("Usuarios").child(currentUserID).child("Servicio").child("Valoracion")
                                            .setValue(dbrating + puntuacion).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(Perfil.this, "", Toast.LENGTH_SHORT).show();
                                            }else {
                                                String message = task.getException().toString();
                                                Toast.makeText(Perfil.this, "Error"+ message, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    String input_name = descripcion.getText().toString();
                                    reference.child("Usuarios").child(currentUserID).child("Servicio").child("Descripcion").setValue(input_name)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(Perfil.this,"Creado exitosamente", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                    }

                });
                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        //finishAfterTransition();

        ImageViewPhotoEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (Build.VERSION.SDK_INT >= 22){
                    checkAndRequestForPermission();
                }else {
                    openGallery();
                }
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference();
        Userprofileimageref = FirebaseStorage.getInstance().getReference().child("Imagenes");
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    setUserData(user);
                    //RetriveUserInfo();
                }else {
                    goLogInScreen();
                }
            }
        };

        // init LinerLayout
        linearLayout = findViewById(R.id.LinerLayout);

        // initializing animation drawable by getting background from constraint layout
        animationDrawable = (AnimationDrawable) linearLayout.getBackground();

        // setting enter fade animation duration to 5 seconds
        animationDrawable.setEnterFadeDuration(5000);

        // setting exit fade animation duration to 2 seconds
        animationDrawable.setExitFadeDuration(2000);
    }

    private void ajuste_perfil() {
        AlertDialog.Builder ajuste = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_information, null);

        ImageViewPhotoEditar = mView.findViewById(R.id.ImageViewPhotoEditar);
        input_name_edit = mView.findViewById(R.id.input_name_edit);
        guardar_editar = mView.findViewById(R.id.guardar_editar);
        cerrardialog = mView.findViewById(R.id.cerrardialog);
        recuperarperfil();

        guardar_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               actualizarperfil();
            }
        });
        ajuste.setCancelable(false);
        ajuste.setView(mView);
        AlertDialog dialog = ajuste.create();
        cerrardialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void actualizarperfil() {
        String setName = input_name_edit.getText().toString();
        if (TextUtils.isEmpty(setName)){
            Toast.makeText(this, "Actualice su nombre por aqui.", Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String, String> profilemap = new HashMap<>();
            profilemap.put("Id", currentUserID );
            profilemap.put("Nombre", setName);
            reference.child("Actualizacion").child(currentUserID).setValue(profilemap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(Perfil.this, "Perfil Actualizado.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        String message = task.getException().toString();
                        Toast.makeText(Perfil.this, "Error..." + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void recuperarperfil() {
        reference.child("Actualizacion").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Nombre"))){
                    String receiveText = dataSnapshot.child("Nombre").getValue().toString();
                    input_name_edit.setText(receiveText);
                }
                else {
                    Toast.makeText(Perfil.this, "No se pudo iniciar con el servicio de Perfil", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.child("Usuarios").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Photo"))){
                    String receivePhoto = dataSnapshot.child("Photo").getValue().toString();
                    if (TextUtils.isEmpty(receivePhoto)){
                        ImageViewPhotoEditar.setImageResource(R.drawable.perfil);
                    }else {
                        Glide.with(getApplicationContext()).load(receivePhoto).placeholder(R.drawable.perfil).into(ImageViewPhotoEditar);
                    }
                }
                else {
                    Toast.makeText(Perfil.this, "No se pudo iniciar con el servicio de Perfil", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //----------------------------------------
    //facebook
//-------------------------------------------

    @Override
    protected void onResume() {
        super.onResume();
        if (animationDrawable != null && !animationDrawable.isRunning()) {
            // start the animation
            animationDrawable.start();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (animationDrawable != null && animationDrawable.isRunning()) {
            // stop the animation
            animationDrawable.stop();
        }
    }
    private void openGallery() {
        CropImage.startPickImageActivity(this);
        //Intent galleryIntent = new Intent();
        //galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        //galleryIntent.setType("image/*");
        //startActivityForResult(galleryIntent,REQUESCODE);
    }
    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(Perfil.this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(Perfil.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(Perfil.this,"Por fabor acepte o no tendrá acceso.", Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(this)
                        .setTitle("Necesita un Permiso.")
                        .setMessage("Este permiso es necesario para acceder a imagenes")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(Perfil.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        PReqCode);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create().show();
            }else {
                ActivityCompat.requestPermissions(Perfil.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        }
        else {
            openGallery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PReqCode){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permison Concedido", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this,"Permison Denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK   && data != null){
            pickedImageUri = data.getData();
            CropImage.activity(pickedImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
            //ImageViewPhoto.setImageURI(pickedImageUri);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK){
                loadingbar.setTitle("Modificando Perfil de Imagen");
                loadingbar.setMessage("Por fabor espere unos segundo ya casi termina o asegurece de estar Conectado a Internet");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();
                Uri resultUri = result.getUri();
                StorageReference filepath = Userprofileimageref.child(currentUserID + ".jpg");
                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!urlTask.isSuccessful());
                        Uri downloadUrl = urlTask.getResult();
                        Toast.makeText(Perfil.this, "Imagen subido correctamente", Toast.LENGTH_SHORT).show();
                        final String sdownload_url = String.valueOf(downloadUrl);
                        reference.child("Usuarios").child(currentUserID).child("Photo")
                                .setValue(sdownload_url)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(Perfil.this, "Imagen guardada, espere un rato a que visualice su imagen.", Toast.LENGTH_SHORT).show();
                                            loadingbar.dismiss();
                                        }else {
                                            String message = task.getException().toString();
                                            Toast.makeText(Perfil.this, "Error: "+ message, Toast.LENGTH_SHORT).show();
                                            loadingbar.dismiss();
                                        }
                                    }
                                });
                    }
                });
                /*
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Perfil.this, "Imagen subido correctamente", Toast.LENGTH_SHORT).show();
                            final String hola = filepath.getDownloadUrl().toString();
                            //final String descargarImagen = task.getResult().getStorage().getDownloadUrl().toString();
                            /*task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    System.out.println(uri.toString());
                                }
                            });   //

                            reference.child("Usuarios").child(currentUserID).child("Photo")
                                    .setValue(hola)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(Perfil.this, "Imagen guardada correctamente", Toast.LENGTH_SHORT).show();
                                                loadingbar.dismiss();
                                            }else {
                                                String message = task.getException().toString();
                                                Toast.makeText(Perfil.this, "Error: "+ message, Toast.LENGTH_SHORT).show();
                                                loadingbar.dismiss();
                                            }
                                        }
                                    });
                        }else {
                            String message = task.getException().toString();
                            Toast.makeText(Perfil.this, "Error..."+ message, Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                        }
                    }
                });*/
            }
        }
    }
    private void setUserData(FirebaseUser user) {
        nameTextView.setText(user.getDisplayName());
        emailTextView.setText(user.getEmail());
        idTextView.setText(user.getUid());
        Glide.with(this).load(user.getPhotoUrl()).into(ImageViewPhoto);
        reference.child("Usuarios").child(currentUserID)//.child("Photo")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Photo"))){
                            String retriviewImage = dataSnapshot.child("Photo").getValue().toString();
                            //String retriviewName = dataSnapshot.child("Nombre").getValue().toString();
                            String retriviewEmail = dataSnapshot.child("Email").getValue().toString();
                            if (TextUtils.isEmpty(retriviewImage)){

                                ImageViewPhotoEdit.setImageResource(R.drawable.perfil);
                            }else {
                                //nameTextView.setText(retriviewName);
                                emailTextView.setText(retriviewEmail);
                                //Picasso.get().load(retriviewImage).into(ImageViewPhotoEdit);
                                Glide.with(getApplicationContext())
                                        .load(retriviewImage)
                                        .apply(new RequestOptions()
                                        .placeholder(R.drawable.perfil)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                                        .thumbnail(.5f)
                                        .into(ImageViewPhotoEdit);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
        reference.child("Actualizacion").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Nombre"))){
                    String receiveText = dataSnapshot.child("Nombre").getValue().toString();
                    nameTextView.setText(receiveText);
                }
                else {
                    Toast.makeText(Perfil.this, "No se pudo iniciar con el servicio de Perfil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
/*
    private void RetriveUserInfo() {
        reference.child("Usuarios").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.exists())  && (dataSnapshot.hasChild("Nombre")) && (dataSnapshot.hasChild("Photo"))){

                            String retriviewName = dataSnapshot.child("Nombre").getValue().toString();
                            String retriviewEmail = dataSnapshot.child("Email").getValue().toString();
                            String retriviewImage = dataSnapshot.child("Photo").getValue().toString();

                            nameTextView.setText(retriviewName);
                            emailTextView.setText(retriviewEmail);
                            //Picasso.get().load(retriviewImage).into(PhotoGallery);
                        }
                        else if((dataSnapshot.exists())  && (dataSnapshot.hasChild("Nombre"))){

                            String retriviewName = dataSnapshot.child("Nombre").getValue().toString();
                            String retriviewEmail = dataSnapshot.child("Email").getValue().toString();

                            nameTextView.setText(retriviewName);
                            emailTextView.setText(retriviewEmail);
                        }
                        else {
                            Toast.makeText(Perfil.this, "Porfabor cambie su perfil", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }*/
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }
    private void goLogInScreen() {

        Intent intent = new Intent(Perfil.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void logOut(View view) {
        Toast.makeText(this, "Presione de nuevo", Toast.LENGTH_SHORT).show();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder Salida = new AlertDialog.Builder(Perfil.this);
                Salida.setMessage("Desea salir su aplicacion")
                        .setCancelable(false)
                        .setPositiveButton("De acuerdo", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth.getInstance().signOut();
                                LoginManager.getInstance().logOut();
                                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(@NonNull Status status) {
                                        if (status.isSuccess()){
                                            goLogInScreen();
                                        }else {
                                            Toast.makeText(getApplicationContext(), getString(R.string.not_close_session), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog titulo = Salida.create();
                titulo.setTitle("Alerta a Usuario Ruta Movil");
                titulo.show();
            }
        });
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener !=null){
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }
}

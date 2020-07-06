package com.example.rutamoviltransporte;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Arrays;
import java.util.HashMap;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    //animationSplash
    ImageView imagedraw, imageView;
    LinearLayout Splash, SplashLogin, Boton;
    Animation frombottom, frombottomup;

    //generar Firebase
    private GoogleApiClient googleApiClient;
    //Login Google
    private SignInButton signInButton;
    public static final int SIG_IN_CODE = 7777;
    //Login Facebook
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    //iniciamos firebase
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //splashLogin
        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        frombottomup = AnimationUtils.loadAnimation(this, R.anim.frombottomup);

        imagedraw = findViewById(R.id.imagedraw);
        imageView = findViewById(R.id.imageView);
        Splash = findViewById(R.id.Splash);
        SplashLogin = findViewById(R.id.SplashLogin);
        Boton = findViewById(R.id.Boton);

        imagedraw.animate().translationY(-1900).setDuration(1200).setStartDelay(1200);
        imageView.animate().alpha(0).setDuration(1000).setStartDelay(1000);
        Splash.animate().translationY(140).alpha(0).setDuration(1200).setStartDelay(1200);

        SplashLogin.startAnimation(frombottom);
        Boton.setAnimation(frombottomup);
        //Iniciamos Google.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso )
                .build();
        signInButton = findViewById(R.id.signInButton);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        //signInButton.setColorScheme(SignInButton.COLOR_DARK);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIG_IN_CODE);
            }
        });

        //Iniciamos Facebook
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.loginButton);
        loginButton.setReadPermissions("email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                //goMainScreen();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), R.string.cancel_login, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), R.string.error_login, Toast.LENGTH_SHORT).show();
            }
        });
        // inizializamos en el metodo onCreate firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    goMainScreen();
                    Toast.makeText(Login.this, "Bienvenido: "+user.getDisplayName()+" Correo: "+user.getEmail(), Toast.LENGTH_LONG).show();
                }
            }
        };
        progressBar = findViewById(R.id.progress_circular);
    }
    //facebook firebase.
    private void handleFacebookAccessToken(AccessToken accessToken) {
        progressBar.setVisibility(View.VISIBLE);
        signInButton.setVisibility(View.GONE);
        loginButton.setVisibility(View.GONE);
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                loginButton.setVisibility(View.VISIBLE);
                if (task.getResult().getAdditionalUserInfo().isNewUser()){
                    databaseUser();
                }
                if (!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),getString(R.string.not_firebase_auth), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIG_IN_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()){
            //goMainScreen();
            firebaseAuthWhitGoogle(result.getSignInAccount());
        }else {
            Toast.makeText(this, getString(R.string.not_long_in), Toast.LENGTH_SHORT).show();
        }
    }
    private void firebaseAuthWhitGoogle(GoogleSignInAccount signInAccount) {

        progressBar.setVisibility(View.VISIBLE);
        signInButton.setVisibility(View.GONE);
        loginButton.setVisibility(View.GONE);
        AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                progressBar.setVisibility(View.GONE);
                signInButton.setVisibility(View.VISIBLE);
                databaseUsusario();
                if (task.getResult().getAdditionalUserInfo().isNewUser()){
                    databaseUser();
                }
                if (!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),getString(R.string.not_firebase_auth),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void databaseUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String name = user.getDisplayName();
        String email = user.getEmail();
        String currentUserId = user.getUid();
        //String photo = user.getPhotoUrl();

        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("Nombre", name);
        hashMap.put("Email", email);
        hashMap.put("Photo", "");
        hashMap.put("Id", currentUserId );
        databaseReference.child("Usuarios").child(currentUserId).setValue(hashMap);
    }

    private void databaseUsusario() {

    }

    private void goMainScreen() {
        Intent intent= new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null){
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }
}

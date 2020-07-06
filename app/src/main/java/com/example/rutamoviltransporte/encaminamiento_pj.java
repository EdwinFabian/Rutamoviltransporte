package com.example.rutamoviltransporte;

import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.mapbox.api.directions.v5.models.BannerInstructions;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.listeners.BannerInstructionsListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.InstructionListListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.RouteListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.SpeechAnnouncementListener;
import com.mapbox.services.android.navigation.ui.v5.map.NavigationMapboxMap;
import com.mapbox.services.android.navigation.ui.v5.voice.SpeechAnnouncement;
import com.mapbox.services.android.navigation.v5.instruction.Instruction;
import com.mapbox.services.android.navigation.v5.milestone.Trigger;
import com.mapbox.services.android.navigation.v5.milestone.TriggerProperty;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.offroute.OffRouteListener;
import com.mapbox.services.android.navigation.v5.milestone.RouteMilestone;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;

public class encaminamiento_pj extends AppCompatActivity implements OnNavigationReadyCallback,
        NavigationListener, RouteListener, ProgressChangeListener, SpeechAnnouncementListener, OffRouteListener, BannerInstructionsListener, InstructionListListener  {

    private NavigationView navigationView;
    private boolean dropoffDialogShown;
    private Location lastKnownLocation;
    private MapboxNavigation navigation;
    private static final int BEGIN_ROUTE_MILESTONE = 1001;
    private TextView textVelocidad, result, result1;
    private FloatingActionButton fab;
    private View spacer;
    private boolean bottomSheetVisible = true;
    private boolean instructionListShown = false;
    private RippleBackground findCoins;
    private ImageView centerImageBut, detect, cerrardialog;
    private Animation animation;
    private TextToSpeech textToSpeech;
    private CountDownTimer countDownTimer;

    private List<Point> points = new ArrayList<>();
    private static final int INITIAL_ZOOM = 16;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encaminamientopj);

        setTheme(R.style.Theme_AppCompat_NoActionBar);
        initNightMode();

        points.add(Point.fromLngLat(-70.034176, -15.815640));
        points.add(Point.fromLngLat(-70.033420, -15.785867));
        points.add(Point.fromLngLat(-70.041023, -15.775349));
        points.add(Point.fromLngLat(-70.055674, -15.741082));
        points.add(Point.fromLngLat(-70.067813, -15.696983));
        points.add(Point.fromLngLat(-70.103971, -15.560636));
        points.add(Point.fromLngLat(-70.110891, -15.544599));

        getWindow().setStatusBarColor(getColor(R.color.mapbox_navigation_view_color_banner_background));
        CameraPosition position = new CameraPosition.Builder().zoom(INITIAL_ZOOM).build();
        navigationView = findViewById(R.id.navigationView);
        navigationView.onCreate(savedInstanceState);
        navigationView.initialize(this, position);
        fab = findViewById(R.id.fab);
        spacer = findViewById(R.id.spacer);
        textVelocidad = findViewById(R.id.speed_limit);
        setSpeedWidgetAnchor(R.id.summaryBottomSheet);

        navigation = new MapboxNavigation(this, Mapbox.getAccessToken());
        navigation.addMilestone(new RouteMilestone.Builder()
                .setIdentifier(BEGIN_ROUTE_MILESTONE)
                .setInstruction(new encaminamiento_pj.BeginRouteInstruction())
                .setTrigger(
                        Trigger.all(
                                Trigger.lt(TriggerProperty.STEP_INDEX, 3),
                                Trigger.gt(TriggerProperty.STEP_DISTANCE_TOTAL_METERS, 200),
                                Trigger.gte(TriggerProperty.STEP_DISTANCE_TRAVELED_METERS, 75)
                        )
                ).build());

        //Text to Speech
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS){
                    Locale spanish = new Locale("spa", "MEX");
                    //textToSpeech.setLanguage(spanish);
                    //int language = textToSpeech.setLanguage(Locale.ENGLISH);
                    int languaje = textToSpeech.setLanguage(spanish);
                }
            }
        });
        //Progrecion de cuenta.
        countDownTimer = new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long l) {
                result1.setText(l/1000 + " segundos");
            }

            @Override
            public void onFinish() {
                //Text to Speech
                result1.setText("Se informa, Tramo recta o curva como Ruta peligrosa.");
                String texto = result1.getText().toString();
                int speech = textToSpeech.speak(texto, TextToSpeech.QUEUE_ADD, null);
            }
        };
    }

    private static class BeginRouteInstruction extends Instruction {

        @Override
        public String buildInstruction(RouteProgress routeProgress) {
            return "¡Ten un viaje seguro!";
        }
    }
    @Override
    public SpeechAnnouncement willVoice(SpeechAnnouncement announcement){
        return SpeechAnnouncement.builder().announcement("Cuidado Punto Peligroso").build();
    }
    @Override
    public BannerInstructions willDisplay(BannerInstructions instructions) {
        return instructions;
    }

    @Override
    public void onStart() {
        super.onStart();
        navigationView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        navigationView.onLowMemory();
    }

    @Override
    public void onBackPressed() {
        // If the navigation view didn't need to do anything, call super
        if (!navigationView.onBackPressed()) {
            super.onBackPressed();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        navigationView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        navigationView.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        navigationView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        navigationView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        navigationView.onDestroy();
        if (isFinishing()) {
            saveNightModeToPreferences(AppCompatDelegate.MODE_NIGHT_AUTO);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
        }
    }

    @Override
    public void onNavigationReady(boolean isRunning) {
        fetchRoute(points.remove(0), points.remove(0));
    }

    @Override
    public void onCancelNavigation() {
        // Navigation canceled, finish the activity
        finish();
    }

    @Override
    public void onNavigationFinished() {
// Intentionally empty
    }

    @Override
    public void onNavigationRunning() {
// Intentionally empty
    }

    @Override
    public boolean allowRerouteFrom(Point offRoutePoint) {
        return true;
    }

    @Override
    public void onOffRoute(Point offRoutePoint) {
        //Point.fromLngLat(offRoutePoint.longitude(), offRoutePoint.latitude());
        Toast.makeText(this, "Estas Fuera de Ruta por favor acercate a la ruta.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void userOffRoute(Location location) {
        //Point.fromLngLat(location.getLongitude(),location.getLatitude());
        Toast.makeText(this, "llamada fuera de ruta", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRerouteAlong(DirectionsRoute directionsRoute) {

    }

    @Override
    public void onFailedReroute(String errorMessage) {

    }

    @Override
    public void onArrival() {
        if (!dropoffDialogShown && !points.isEmpty()) {
            showDropoffDialog();
            dropoffDialogShown = true; // Accounts for multiple arrival
            //SpeechAnnouncement.builder().announcement("Hola has llegado ¿desea continuar?").build();
            Toast.makeText(this, "Hola has llegado ¿desea continuar?", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProgressChange(Location location, RouteProgress routeProgress) {
        lastKnownLocation = location;
        setSpeed(location);
    }

    private void startNavigation(DirectionsRoute directionsRoute) {
        NavigationViewOptions navigationViewOptions = setupOptions(directionsRoute);
        navigationView.startNavigation(navigationViewOptions);
    }
    private void showDropoffDialog (){

        AlertDialog.Builder builderdialog = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_alert, null);
        findCoins = mView.findViewById(R.id.findCoins);
        detect = mView.findViewById(R.id.detect);
        centerImageBut = mView.findViewById(R.id.centerImageBut);
        cerrardialog = mView.findViewById(R.id.cerrardialog);
        result = mView.findViewById(R.id.result);
        result1 = mView.findViewById(R.id.result1);
        builderdialog
                .setCancelable(false)
                .setView(mView);
        AlertDialog dialog = builderdialog.create();
        animation = AnimationUtils.loadAnimation( this,R.anim.alertanim);
        detect.startAnimation(animation);
        findCoins.startRippleAnimation();
        result1.startAnimation(animation);
        centerImageBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchRoute(getLastKnownLocation(),points.remove(0));
                dialog.dismiss();
                mediaPlayer.stop();
                countDownTimer.cancel();
            }
        });
        cerrardialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(encaminamiento_pj.this, "Ruta Finalizada", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alert);
        mediaPlayer.start();
        //InitCount
        countDownTimer.start();
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        //Crea handler, em 10 segundos cierra el dialogo.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()){
                    fetchRoute(getLastKnownLocation(),points.remove(0));
                    dialog.dismiss();
                    mediaPlayer.stop();
                }
            }
        }, 8000);
    }
    private void fetchRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .alternatives(true)
                .build()
                .getRoute(new SimplifiedCallback() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        startNavigation(response.body().routes().get(0));
                    }
                });
    }

    private NavigationViewOptions setupOptions(DirectionsRoute directionsRoute) {
        NavigationMapboxMap map = navigationView.retrieveNavigationMapboxMap();
        map.updateLocationLayerRenderMode(RenderMode.GPS);
        dropoffDialogShown = false;
        NavigationViewOptions.Builder options = NavigationViewOptions.builder();
        options.directionsRoute(directionsRoute)
                //.speechAnnouncementListener(this)
                //.bannerInstructionsListener(this)
                .navigationListener(this)
                .progressChangeListener(this)
                .routeListener(this)
                .shouldSimulateRoute(false);
        setBottomSheetCallback(options);
        setupNightModeFab();
        return options.build();
    }

    private Point getLastKnownLocation() {
        return Point.fromLngLat(lastKnownLocation.getLongitude(), lastKnownLocation.getLatitude());
    }

    private void setSpeedWidgetAnchor(@IdRes int res) {
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) spacer.getLayoutParams();
        layoutParams.setAnchorId(res);
        spacer.setLayoutParams(layoutParams);
    }
    @Override
    public void onInstructionListVisibilityChanged(boolean shown) {
        instructionListShown = shown;
        textVelocidad.setVisibility(shown ? View.GONE : View.VISIBLE);
        if (instructionListShown) {
            fab.hide();
        } else if (bottomSheetVisible) {
            fab.show();
        }
    }
    private void setBottomSheetCallback(NavigationViewOptions.Builder options) {
        options.bottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        bottomSheetVisible = false;
                        fab.hide();
                        setSpeedWidgetAnchor(R.id.recenterBtn);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        bottomSheetVisible = true;
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        if (!bottomSheetVisible) {
                            fab.show();
                            setSpeedWidgetAnchor(R.id.summaryBottomSheet);
                        }
                        break;
                    default: return;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }
    private void setupNightModeFab() {
        fab.setOnClickListener(view -> toggleNightMode());
    }

    private void toggleNightMode() {
        int currentNightMode = getCurrentNightMode();
        alternateNightMode(currentNightMode);
    }

    private void initNightMode() {
        int nightMode = retrieveNightModeFromPreferences();
        AppCompatDelegate.setDefaultNightMode(nightMode);
    }

    private int getCurrentNightMode() {
        return getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
    }

    private void alternateNightMode(int currentNightMode) {
        int newNightMode;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            newNightMode = AppCompatDelegate.MODE_NIGHT_NO;
        } else {
            newNightMode = AppCompatDelegate.MODE_NIGHT_YES;
        }
        saveNightModeToPreferences(newNightMode);
        recreate();
    }

    private int retrieveNightModeFromPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getInt(getString(R.string.current_night_mode), AppCompatDelegate.MODE_NIGHT_AUTO);
    }
    private void saveNightModeToPreferences(int nightMode) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(getString(R.string.current_night_mode), nightMode);
        editor.apply();
    }

    private void setSpeed(Location location) {
        String string = String.format("%d\nMPH", (int) (location.getSpeed() * 2.2369));
        int mphTextSize = getResources().getDimensionPixelSize(R.dimen.mph_text_size);
        int speedTextSize = getResources().getDimensionPixelSize(R.dimen.speed_text_size);

        SpannableString spannableString = new SpannableString(string);
        spannableString.setSpan(new AbsoluteSizeSpan(mphTextSize),
                string.length() - 4, string.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        spannableString.setSpan(new AbsoluteSizeSpan(speedTextSize),
                0, string.length() - 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        textVelocidad.setText(spannableString);
        if (!instructionListShown) {
            textVelocidad.setVisibility(View.VISIBLE);
        }
    }
}

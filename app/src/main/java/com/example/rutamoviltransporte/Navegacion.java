package com.example.rutamoviltransporte;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
//import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.core.utils.TextUtils;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
//import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.OnCameraTrackingChangedListener;
import com.mapbox.mapboxsdk.location.OnLocationClickListener;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
//import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;
import com.mapbox.mapboxsdk.plugins.traffic.TrafficPlugin;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.listeners.RouteListener;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.mapboxsdk.plugins.building.BuildingPlugin;

import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;


import android.annotation.SuppressLint;
import android.location.Location;
// Classes needed to add the location engine
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import java.lang.ref.WeakReference;
import android.content.DialogInterface;
//import com.example.rutamoviltransporte.R;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

import com.mapbox.services.android.navigation.ui.v5.listeners.SpeechAnnouncementListener;
import com.mapbox.services.android.navigation.ui.v5.voice.SpeechAnnouncement;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.json.JSONObject;


public class Navegacion extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, OnNavigationReadyCallback, OnLocationClickListener, OnCameraTrackingChangedListener,
        MapboxMap.OnMapClickListener, PermissionsListener, RouteListener, SpeechAnnouncementListener, ProgressChangeListener {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;

    //private route
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity", TAC = "OffNavigation";
    private NavigationMapRoute navigationMapRoute;
    private Button button, iniciarruta, modoNavigationEmuler, modoNavigationGPS;
    private boolean isInTrackingMode;
    private BuildingPlugin buildingPlugin;

    // Variables needed to add the location engine
    private LocationEngine locationEngine;
    private long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    // Variables needed to listen to location updates
    private MainActivityLocationCallback callback = new MainActivityLocationCallback(this);
    private boolean dropoffDialogShown;
    private List<Point> points = new ArrayList<>();
    private Switch clickSwitch;
    private Location lastKnownLocation;
    private FloatingActionButton ejemplo;

    //speech
    private CircleImageView perfilprofile;
    private DatabaseReference referencedata;
    private FirebaseAuth firebaseAuth;
    private String currentUserID;
    // Traffic
    private TrafficPlugin trafficPlugin;

    //RadioButton
    private RadioButton jp, pj;
    private ImageView cerrardialogo;

    //Search
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private String geojsonSourceLayerId = "geojsonSourceLayerId";

    //Offline
    private OfflineManager offlineManager;
    private OfflineRegion offlineRegion;
    private boolean isEndNotified;
    private int regionSelected;
    private CircularProgressBar circularProgressBar;
    private TextView count_porcentaje;
    //JSON
    public static final String JSON_CHARSET = "UTF-8";
    public static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_navegacion);
        ejemplo = findViewById(R.id.Empesar);
        ejemplo.setOnClickListener(this);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        points.add(Point.fromLngLat(-70.1990742527136, -14.919541313041952));
        points.add(Point.fromLngLat(-70.19905708877116, -14.919798418057606));
        clickSwitch = findViewById(R.id.switchfunction);
        getWindow().setStatusBarColor(getColor(R.color.colorVioletDark));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        perfilprofile = findViewById(R.id.perfilnavigation);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();
        referencedata = FirebaseDatabase.getInstance().getReference();

        dataprofile();

    }

    private void dataprofile() {
        referencedata.child("Usuarios").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && dataSnapshot.hasChild("Photo")){
                    String receivePhoto = dataSnapshot.child("Photo").getValue().toString();
                    if (TextUtils.isEmpty(receivePhoto)){
                        perfilprofile.setImageResource(R.drawable.perfil);
                    }else {
                        Glide.with(getApplicationContext()).load(receivePhoto).placeholder(R.drawable.perfil).into(perfilprofile);
                    }
                }else {
                    Toast.makeText(Navegacion.this, "No se pudo iniciar con el servicio de Perfil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //speech
    @Override
    public SpeechAnnouncement willVoice(SpeechAnnouncement announcement) {
        return SpeechAnnouncement.builder().announcement("A 50 metros hay una curva !!cuidado").build();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Navegación");
        }
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {


        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(getString(R.string.navigation_guidance_day), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                buildingPlugin = new BuildingPlugin(mapView, mapboxMap, style );
                buildingPlugin.setMinZoomLevel(15f);
                buildingPlugin.setVisibility(true);
                enableLocationComponent(style);
                addDestinationIconSymbolLayer(style);
                mapboxMap.addOnMapClickListener(Navegacion.this);

                //Add-bottom
                button = findViewById(R.id.startButton);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dropoffDialogShown = true;
                        button.setVisibility(View.INVISIBLE);
                        //boolean simulateRoute = false;
                        //Dialog Desicion.
                        AlertDialog.Builder mensajedesicion = new AlertDialog.Builder(Navegacion.this);
                        View mView = getLayoutInflater().inflate(R.layout.dialogmodo_navegation, null);
                        modoNavigationEmuler = mView.findViewById(R.id.modoNavigationEmuler);
                        modoNavigationGPS = mView.findViewById(R.id.modoNavigationGPS);
                        mensajedesicion
                                .setCancelable(true)
                                .setView(mView);
                        modoNavigationEmuler.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                        .directionsRoute(currentRoute)
                                        .shouldSimulateRoute(true)
                                        .build();
                                NavigationLauncher.startNavigation(Navegacion.this, options);
                            }
                        });
                        modoNavigationGPS.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                        .directionsRoute(currentRoute)
                                        .shouldSimulateRoute(false)
                                        .build();
                                NavigationLauncher.startNavigation(Navegacion.this, options);
                            }
                        });
                        AlertDialog titulo = mensajedesicion.create();
                        titulo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        titulo.show();
                    }
                });

                // Enable the traffic view by default
                trafficPlugin = new TrafficPlugin(mapView, mapboxMap, style);
                trafficPlugin.setVisibility(false);
                findViewById(R.id.trafico).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mapboxMap != null ){
                            trafficPlugin.setVisibility(!trafficPlugin.isVisible());
                        }
                    }
                });

                //Search Map
                setUpSource(style);

                //Iniciamos offline
                //Optener OfflineManager en esta activity
                offlineManager = OfflineManager.getInstance(Navegacion.this);
                circularProgressBar = findViewById(R.id.progress_circular);
                count_porcentaje = findViewById(R.id.count_porcentaje);

            }
        });
    }

    @Override
    public void onArrival() {
        if (!dropoffDialogShown && !points.isEmpty()) {
            showDropoffDialog();
            dropoffDialogShown = true; // Accounts for multiple arrival events
            Toast.makeText(this, "¡Has llegado!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean allowRerouteFrom(Point offRoutePoint) {
        return true;
    }

    @Override
    public void onOffRoute(Point offRoutePoint) {

    }
    @Override
    public void onRerouteAlong(DirectionsRoute directionsRoute) {

    }

    @Override
    public void onFailedReroute(String errorMessage) {

    }
    @Override
    public void onNavigationReady(boolean isRunning) {
        getRoute(points.remove(0), points.remove(0));
    }

    private void showDropoffDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage(getString(R.string.dropoff_dialog_text));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.dropoff_dialog_positive_text),
                (dialogInterface, in) -> getRoute(getLastKnownLocation(), points.remove(0)));
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.dropoff_dialog_negative_text),
                (dialogInterface, in) -> {
// Do nothing
                });

        alertDialog.show();
    }
    private Point getLastKnownLocation() {
        return Point.fromLngLat(lastKnownLocation.getLongitude(), lastKnownLocation.getLatitude());
    }

    private void addDestinationIconSymbolLayer(Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(
                        Navegacion.this.getResources(), R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id", Feature.fromGeometry(
                Point.fromLngLat(-70.1961600, -14.9084300)));
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer  = new SymbolLayer("destination-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }
    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)){
            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(this)
                    .elevation(5)
                    .accuracyAlpha(.6f)
                    //.foregroundTintColor(getColor(R.color.mapbox_navigation_route_layer_blue))
                    .backgroundTintColor(getColor(R.color.white))
                    .accuracyColor(getColor(R.color.mapbox_navigation_location_shield_layer_color))
                    //.bearingTintColor(getColor(R.color.mapbox_navigation_route_layer_blue))
                    .build();
            locationComponent = mapboxMap.getLocationComponent();
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions
                            .builder(this, loadedMapStyle)
                            .locationComponentOptions(customLocationComponentOptions)
                            .useDefaultLocationEngine(true)
                            .build();
            locationComponent.activateLocationComponent(locationComponentActivationOptions);
            //locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.addOnLocationClickListener(this);
            locationComponent.addOnCameraTrackingChangedListener(this);
            locationComponent.setRenderMode(RenderMode.COMPASS);
            clickSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b == true){
                        locationComponent.setRenderMode(RenderMode.COMPASS);
                        locationComponent.setCameraMode(CameraMode.TRACKING_COMPASS);
                        locationComponent.setRenderMode(RenderMode.GPS);
                    }else {
                        locationComponent.setRenderMode(RenderMode.COMPASS);
                        locationComponent.setCameraMode(CameraMode.NONE);
                    }
                }
            });
            initLocationEngine();
            findViewById(R.id.back_to_camera_tracking_mode).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if (!isInTrackingMode) {
                        isInTrackingMode = true;
                        locationComponent.setCameraMode(CameraMode.TRACKING);
                        locationComponent.zoomWhileTracking(16f);
                        Snackbar.make(view,getString(R.string.tracking_enabled), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        Snackbar.make(view, getString(R.string.tracking_already_enabled), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            });

        }
        else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }
    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Empesar :
                dialogempesar(); break;
            //speech
            default: break;
        }
    }

    private void dialogempesar() {
        AlertDialog.Builder mensajeruta = new AlertDialog.Builder(Navegacion.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_optionbutton, null);
        jp = mView.findViewById(R.id.jp);
        pj = mView.findViewById(R.id.pj);
        iniciarruta = mView.findViewById(R.id.iniciarruta);
        cerrardialogo = mView.findViewById(R.id.cerrardialog);
        mensajeruta
                .setCancelable(false)
                .setView(mView);
        iniciarruta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jp.isChecked()){
                    Intent intent = new Intent(getApplicationContext(), encaminamiento_jp.class); startActivity(intent);
                }else if (pj.isChecked()){
                    Intent intent = new Intent(getApplicationContext(), encaminamiento_pj.class); startActivity(intent);
                    //Toast.makeText(Navegacion.this, "Selecciono: " + pj.getText(), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(Navegacion.this, "Se requiere una seleccion", Toast.LENGTH_SHORT).show();
                }
            }
        });
        AlertDialog titulo = mensajeruta.create();
        cerrardialogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titulo.dismiss();
            }
        });
        titulo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        titulo.show();
    }

    private static class MainActivityLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<Navegacion> activityWeakReference;
        private DatabaseReference databaseReference;
        private FirebaseAuth firebaseAuth;
        private String currentUserID;


        MainActivityLocationCallback(Navegacion activity) {
            firebaseAuth = FirebaseAuth.getInstance();
            currentUserID = firebaseAuth.getCurrentUser().getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference();



            this.activityWeakReference = new WeakReference<>(activity);
        }
        /*
         El método de la interfaz LocationEngineCallback que se activa cuando la ubicación del dispositivo ha cambiado.
         @param result el objeto LocationEngineResult que tiene la última ubicación conocida dentro de él.
         */
        @Override
        public void onSuccess(LocationEngineResult result) {

            Navegacion activity = activityWeakReference.get();
            if (activity != null) {
                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }
                // Create a Toast which displays the new location's coordinates
                //Toast.makeText(activity, String.format(activity.getString(R.string.new_location),
                        //String.valueOf(result.getLastLocation().getLatitude()), String.valueOf(result.getLastLocation().getLongitude())),
                        //Toast.LENGTH_SHORT).show();

                databaseReference.child("Usuarios").child(currentUserID).child("Ubicacion").child("Latitud")
                        .setValue(result.getLastLocation().getLatitude()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(activity, String.format(activity.getString(R.string.new_location),
                                    String.valueOf(result.getLastLocation().getLatitude()), String.valueOf(result.getLastLocation().getLongitude())),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                databaseReference.child("Usuarios").child(currentUserID).child("Ubicacion").child("Longitud")
                        .setValue(result.getLastLocation().getLongitude()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(activity, String.format(activity.getString(R.string.new_location),
                                    String.valueOf(result.getLastLocation().getLatitude()), String.valueOf(result.getLastLocation().getLongitude())),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                // Pass the new location to the Maps SDK's LocationComponent
                if (activity.mapboxMap != null && result.getLastLocation() != null) {
                    activity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
                }
            }
        }

        /*
         El método de la interfaz LocationEngineCallback que se activa cuando no se puede capturar la ubicación del dispositivo
         @param exception el mensaje de excepcion.
         */
        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.d("LocationChangeActivity", exception.getLocalizedMessage());
            Navegacion activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    @SuppressWarnings( {"MissingPermission"})
    @Override
    public void onLocationComponentClick() {
        if (locationComponent.getLastKnownLocation() != null) {
            Toast.makeText(this, String.format(getString(R.string.current_location),
                    locationComponent.getLastKnownLocation().getLatitude(),
                    locationComponent.getLastKnownLocation().getLongitude()), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCameraTrackingDismissed() {
        isInTrackingMode = false;
    }

    @Override
    public void onCameraTrackingChanged(int currentMode) {
        // Empty on purpose
    }

    @SuppressWarnings({"MissingPermission"})
    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        Point destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                locationComponent.getLastKnownLocation().getLatitude());
        GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
        if (source != null){
            source.setGeoJson(Feature.fromGeometry(destinationPoint));
        }
        getRoute(originPoint, destinationPoint);

        button.setEnabled(true);
        button.setVisibility(View.VISIBLE);
        //button.setBackgroundResource(R.drawable.custom_circlesix);
        return false;
    }
    private void getRoute(Point originPoint, Point destinationPoint) {
        Toast.makeText(this, "Calculando Ruta...", Toast.LENGTH_SHORT).show();
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(originPoint)
                .destination(destinationPoint)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        currentRoute = response.body().routes().get(0);

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }

                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.e(TAG, "Error: " + t.getMessage());

                    }
                });
    }

    public static boolean isOnline(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null){
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null){
                return networkInfo.isConnected();
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isOnline(this)){
            mapView.onResume();
        }else {
            Toast.makeText(this, "Se requiere una conexion a Internet", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Toast.makeText(this, "Holasss", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }
    @Override
    public void onProgressChange(Location location, RouteProgress routeProgress) {
        lastKnownLocation = location;
    }

    //Creacion de Menus de Estilo.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_style_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_search:
                initSearch();
                return true;
            case R.id.item1:
                return true;
            case R.id.menu_streets:
                mapboxMap.setStyle(Style.MAPBOX_STREETS);
                return true;
            case R.id.menu_dark:
                mapboxMap.setStyle(Style.DARK);
                return true;
            case R.id.menu_light:
                mapboxMap.setStyle(Style.LIGHT);
                return true;
            case R.id.menu_outdoors:
                mapboxMap.setStyle(Style.OUTDOORS);
                return true;
            case R.id.menu_satellite:
                mapboxMap.setStyle(Style.SATELLITE);
                return true;
            case R.id.menu_satellite_streets:
                mapboxMap.setStyle(Style.SATELLITE_STREETS);
                return true;
            case R.id.item2:
                return true;
            case R.id.Registrar:
                descargarRegionDialog();
                Toast.makeText(this, "Registro de mapas offline", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.Listar:
                listarRegion();
                Toast.makeText(this, "Listado de mapas offline", Toast.LENGTH_SHORT).show();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //offline
    private void listarRegion() {

        regionSelected = 0;
        offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
            @Override
            public void onList(final OfflineRegion[] offlineRegions) {
                if (offlineRegions == null || offlineRegions.length == 0) {
                    Toast.makeText(getApplicationContext(), "No existe Regiones", Toast.LENGTH_SHORT).show();
                    return;
                }

                ArrayList<String> offlineRegionsNames = new ArrayList<>();
                for (OfflineRegion offlineRegion : offlineRegions) {
                    offlineRegionsNames.add(getRegionName(offlineRegion));
                }
                final CharSequence[] items = offlineRegionsNames.toArray(new CharSequence[offlineRegionsNames.size()]);

                AlertDialog dialog = new AlertDialog.Builder(Navegacion.this)
                        .setTitle("Listao de Regiones")
                        .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                regionSelected = which;
                            }
                        })
                        .setPositiveButton("Navegar Region", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                Toast.makeText(Navegacion.this, items[regionSelected], Toast.LENGTH_LONG).show();

                                LatLngBounds bounds = (offlineRegions[regionSelected].getDefinition()).getBounds();
                                double regionZoom = (offlineRegions[regionSelected].getDefinition()).getMinZoom();

                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(bounds.getCenter())
                                        .zoom(regionZoom)
                                        .build();
                                mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                            }
                        })
                        .setNeutralButton("Eliminar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                circularProgressBar.setIndeterminateMode(true);
                                circularProgressBar.setVisibility(View.VISIBLE);

                                offlineRegions[regionSelected].delete(new OfflineRegion.OfflineRegionDeleteCallback() {
                                    @Override
                                    public void onDelete() {
                                        circularProgressBar.setVisibility(View.INVISIBLE);
                                        circularProgressBar.setIndeterminateMode(false);
                                        Toast.makeText(getApplicationContext(), "Eliminar Region", Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onError(String error) {
                                        circularProgressBar.setVisibility(View.INVISIBLE);
                                        circularProgressBar.setIndeterminateMode(false);
                                        Timber.e( "Error: %s", error);
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        }).create();
                dialog.show();

            }

            @Override
            public void onError(String error) {
                Timber.e( "Error: %s", error);
            }
        });

    }
    private String getRegionName(OfflineRegion offlineRegion) {
// Get the region name from the offline region metadata
        String regionName;

        try {
            byte[] metadata = offlineRegion.getMetadata();
            String json = new String(metadata, JSON_CHARSET);
            JSONObject jsonObject = new JSONObject(json);
            regionName = jsonObject.getString(JSON_FIELD_REGION_NAME);
        } catch (Exception exception) {
            Timber.e("Failed to decode metadata: %s", exception.getMessage());
            regionName = String.format("Region %1$d", offlineRegion.getID());
        }
        return regionName;
    }

    private void descargarRegionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Navegacion.this);
        final TextInputEditText nameRegion = new TextInputEditText(Navegacion.this);
        nameRegion.setHint("Nombre de Region");
        builder
                .setTitle("Descargar Region")
                .setView(nameRegion)
                .setMessage("Descargar una Region Existente en dicho mapa")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String RegionNombre = nameRegion.getText().toString();
                        if (RegionNombre.length() == 0){
                            Toast.makeText(Navegacion.this, "Existe esa Region", Toast.LENGTH_SHORT).show();
                        }else {
                            downloadRegion(RegionNombre);
                        }
                    }
                })
                .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        builder.show();
    }

    private void downloadRegion(String regionNombre) {

        startCircularprogress();

        mapboxMap.getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                String urlStyle = style.getUri();
                LatLngBounds bounds = mapboxMap.getProjection().getVisibleRegion().latLngBounds;
                double minZoom = mapboxMap.getCameraPosition().zoom;
                double maxZoom = mapboxMap.getMaxZoomLevel();
                float pixelRatio = Navegacion.this.getResources().getDisplayMetrics().density;
                OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                        urlStyle, bounds, minZoom, maxZoom, pixelRatio);
                byte[] metadata;
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(JSON_FIELD_REGION_NAME, regionNombre);
                    String json = jsonObject.toString();
                    metadata = json.getBytes(JSON_CHARSET);
                } catch (Exception exception) {
                    Timber.e("Failed to encode metadata: %s", exception.getMessage());
                    metadata = null;
                }

                offlineManager.createOfflineRegion(definition, metadata, new OfflineManager.CreateOfflineRegionCallback() {
                    @Override
                    public void onCreate(OfflineRegion offlineRegion) {
                        Timber.d( "Offline region created: %s" , regionNombre);
                        Navegacion.this.offlineRegion = offlineRegion;
                        launchDownload();
                    }

                    @Override
                    public void onError(String error) {
                        Timber.e( "Error: %s" , error);
                    }
                });
            }
        });
    }

    private void startCircularprogress() {
        isEndNotified = false;
        //offlineDownload.setEnabled(false);
        circularProgressBar.setIndeterminateMode(true);
        circularProgressBar.setVisibility(View.VISIBLE);
        count_porcentaje.setVisibility(View.VISIBLE);
    }

    private void launchDownload() {
        offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
            @Override
            public void onStatusChanged(OfflineRegionStatus status) {
                double percentage = status.getRequiredResourceCount() >= 0 ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) : 0.0;
                if (status.isComplete()){
                    endProgress("Region Completado");
                    return;
                }else if (status.isRequiredResourceCountPrecise()){
                    setPercentage((int) Math.round(percentage));
                }

                Timber.d("%s/%s resources; %s bytes downloaded.",
                        String.valueOf(status.getCompletedResourceCount()),
                        String.valueOf(status.getRequiredResourceCount()),
                        String.valueOf(status.getCompletedResourceSize()));
            }

            @Override
            public void onError(OfflineRegionError error) {
                Timber.e("onError reason: %s", error.getReason());
                Timber.e("onError message: %s", error.getMessage());
            }

            @Override
            public void mapboxTileCountLimitExceeded(long limit) {
                Timber.e("Mapbox tile count limit exceeded: %s", limit);
            }
        });
        offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);
    }

    private void setPercentage(final int round) {
        circularProgressBar.setIndeterminateMode(false);
        circularProgressBar.setProgress(round);
        count_porcentaje.setText(round);
    }

    private void endProgress(final String region_completado) {
        if (isEndNotified){
            return;
        }
        isEndNotified = true;
        //offlineDownload.setEnabled(true);
        circularProgressBar.setIndeterminateMode(false);
        circularProgressBar.setVisibility(View.GONE);
        count_porcentaje.setVisibility(View.GONE);
        Toast.makeText(this, region_completado, Toast.LENGTH_SHORT).show();
    }

    //Search
    private void initSearch() {
        Intent intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.access_token))
                .placeOptions(PlaceOptions.builder()
                        .backgroundColor(Color.parseColor("#6D302D2D"))
                        .limit(10)
                        .build(PlaceOptions.MODE_CARDS))
                .build(Navegacion.this);
        startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
    }
    private void setUpSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(geojsonSourceLayerId));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {

            // Retrieve selected location's CarmenFeature
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

            // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above.
            // Then retrieve and update the source designated for showing a selected location's symbol layer icon
            if (mapboxMap != null) {
                Style style = mapboxMap.getStyle();
                if (style != null) {
                    GeoJsonSource source = style.getSourceAs(geojsonSourceLayerId);
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(
                                new Feature[] {Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }

                    // Movimiento Camara de Mapa de Ubicacion
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                            ((Point) selectedCarmenFeature.geometry()).longitude()))
                                    .zoom(13)
                                    .build()), 4000);
                }
            }
        }
    }
}

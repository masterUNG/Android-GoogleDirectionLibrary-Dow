package com.akexorcist.googledirection.sample;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

public class AlternativeDirectionActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, DirectionCallback {
    private Button btnRequestDirection;
    private GoogleMap googleMap;
    private String serverKey = "AIzaSyD_6HZwKgnxSOSkMWocLs4-2AViQuPBteQ";
    private LatLng camera;
    private LatLng origin;
    private LatLng destination;
    private String[] colors = {"#7fff7272", "#7f31c7c5", "#7fff8a00"};
    private Double startLatADouble = 0.0, startLngADouble = 0.0;
    private Double endLatADouble, endLngADouble;
    private LocationManager locationManager;
    private Criteria criteria;
    private EditText originEditText, destinationEditText;
    private String originString, destinationString;
    private MyManage myManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternative_direction);

        btnRequestDirection = (Button) findViewById(R.id.btn_request_direction);
        btnRequestDirection.setOnClickListener(this);

        //My Setup
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        myManage = new MyManage(AlternativeDirectionActivity.this);

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

    }   // Main Method

    @Override
    protected void onResume() {
        super.onResume();

        afterResume();

    }

    private void afterResume() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(locationListener);

        Location networkLocation = myFindLocation(LocationManager.NETWORK_PROVIDER);
        if (networkLocation != null) {
            startLatADouble = networkLocation.getLatitude();
            startLngADouble = networkLocation.getLongitude();
        }

        Location gpsLocation = myFindLocation(LocationManager.GPS_PROVIDER);
        if (gpsLocation != null) {
            startLatADouble = gpsLocation.getLatitude();
            startLngADouble = gpsLocation.getLongitude();
        }

        Log.d("29janV1", "Lat ==> " + startLatADouble);
        Log.d("29janV1", "Lng ==> " + startLngADouble);


    }   // afterResume

    @Override
    protected void onStop() {
        super.onStop();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(locationListener);

    }

    public Location myFindLocation(String strProvider) {

        Location location = null;

        if (locationManager.isProviderEnabled(strProvider)) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            locationManager.requestLocationUpdates(strProvider, 1000, 10, locationListener);
            location = locationManager.getLastKnownLocation(strProvider);

        }

        return location;
    }


    public LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            startLatADouble = location.getLatitude();
            startLngADouble = location.getLongitude();

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };



    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        //Setup Center Map
        camera = new LatLng(startLatADouble, startLngADouble);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera, 15));

        //Create Marker of User
        createMarkerUser();

        //Get Even from click Map
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                googleMap.clear();

                createMarkerUser();

                createMarkerDestination(latLng);

            }   // onMapClick
        });


    }   // onMapReady

    private void createMarkerDestination(LatLng latLng) {

        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        endLatADouble = latLng.latitude;
        endLngADouble = latLng.longitude;
        Log.d("30janV1", "Destination Lat ==> " + endLatADouble);
        Log.d("30janV1", "Destination Lng ==> " + endLngADouble);
        destination = new LatLng(endLatADouble, endLngADouble);

    }   // createMarkerDestination

    private void createMarkerUser() {
        origin = new LatLng(startLatADouble, startLngADouble);
        googleMap.addMarker(new MarkerOptions().position(origin));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_request_direction) {
            requestDirection();
        }
    }

    public void requestDirection() {
        Snackbar.make(btnRequestDirection, "Direction Requesting...", Snackbar.LENGTH_SHORT).show();

        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .alternativeRoute(true)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {

        Snackbar.make(btnRequestDirection, "Success with status : " + direction.getStatus(), Snackbar.LENGTH_SHORT).show();

        Polyline[] polylines = new Polyline[direction.getRouteList().size()];

        if (direction.isOK()) {
//            googleMap.addMarker(new MarkerOptions().position(origin));
//            googleMap.addMarker(new MarkerOptions().position(destination));

            Log.d("30janV2", "จำนวนเส้นทางที่ google แนะนำมา ==> " + direction.getRouteList().size());



            for (int i = 0; i < direction.getRouteList().size(); i++) {

                Route route = direction.getRouteList().get(i);
                String color = colors[i % colors.length];
                ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                polylines[i] = googleMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 10, Color.parseColor(color)));
                polylines[i].setClickable(true);
                polylines[i].setZIndex(i);

            }   // for

            //btnRequestDirection.setVisibility(View.GONE);
        }   // if


        googleMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                Log.d("30janV2", "click PolyLine OK");
                Log.d("30janV2", "index ==> " + polyline.getZIndex());
                int index = (int) polyline.getZIndex();

//                Toast.makeText(AlternativeDirectionActivity.this,
//                        "คุณเลือกเส้นทางที่ " + Integer.toString(index), Toast.LENGTH_SHORT).show();

                myAlertDialog(index);

            }
        });


    }   // onDirectionSuccess

    private void myAlertDialog(int index) {

        AlertDialog.Builder builder = new AlertDialog.Builder(AlternativeDirectionActivity.this);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.doremon48);
        builder.setTitle("ข้อมูลที่ต้องการบันทึก");

        LayoutInflater layoutInflater = AlternativeDirectionActivity.this.getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.my_layout, null);
        builder.setView(view);
        builder.setMessage("คุณเลือกเส้นทาง " + Integer.toString(index));

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Bind Widget
                originEditText = (EditText) view.findViewById(R.id.editText);
                destinationEditText = (EditText) view.findViewById(R.id.editText2);

                originString = originEditText.getText().toString().trim();
                destinationString = destinationEditText.getText().toString().trim();

                Log.d("30janV2", "Origin ==> " + originString);
                Log.d("30janV2", "Destination ==> " + destinationString);

                dialogInterface.dismiss();
            }
        });
        builder.show();


    }   // myAlertDialog

    @Override
    public void onDirectionFailure(Throwable t) {
        Snackbar.make(btnRequestDirection, t.getMessage(), Snackbar.LENGTH_SHORT).show();
    }
}

package com.example.sergi.fitelite;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;
import static com.example.sergi.fitelite.R.id.start;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, android.location.LocationListener {
    /***
     * Declaration of the elements. As you can see, the permissions have proven to be tricky because of the fact that
     * the user of the device needs to allow any kind of access starting from Android Marshmallow.
     */
    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;
    LocationManager locationManager;
    ArrayList<LatLng> points;
    TextView speedTxt;
    TextView distanceTxt;
    TextView timeTxt;


    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_map, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /***
         * Here, we initialize the elements and set them to their corresponding xml item.
         */

        mMapView = (MapView) mView.findViewById(R.id.map);
        speedTxt = (TextView) mView.findViewById(R.id.tvSpeed);
        distanceTxt = (TextView) mView.findViewById(R.id.tvDistance);
        timeTxt = (TextView) mView.findViewById(R.id.tvDuration);
        points = new ArrayList<>();
        locationManager = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);

        /***
         * Permissions, permissions, permissions.
         */
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionGranted()) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
        } else requestLocation();
        if (!isLocationEnabled()) showAlert(1);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    /***
     * This method checks and displays if the user has the permission to access the location settings.
     * @param status
     */
    private void showAlert(final int status) {
        String message, title, btnText;
        if (status == 1) {
            message = "Enable location to use the app!";
            title = "Enable location";
            btnText = "Location Settings";
        } else {
            message = "Allow the app to access location!";
            title = "Permission access";
            btnText = "Grant";
        }
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this.getContext());
        dialog.setCancelable(false);
        dialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(btnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        if (status == 1) {
                            Intent aIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(aIntent);
                        } else {
                            requestPermissions(PERMISSIONS, PERMISSION_ALL);
                        }

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
            }
        });
        dialog.show();
    }

    /***
     * As the name suggests, the method checks if the location is enabled.
     * @return
     */
    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /***
     * Again, permissions.
     * @return
     */
    private boolean isPermissionGranted() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.v("mylog", "Permission Granted!");
                return true;
            } else {
                Log.v("mylog", "Permission refused!");
                return false;
            }
        }
        return true;
    }


    /***
     * In the onMapReady method, we initialize the map and we set the location button which zoomz the camera
     * to the current location.
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //User has previously accepted this permission
            if (ActivityCompat.checkSelfPermission(this.getContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
    }

    /***
     * In the onLocationChanged method, we declare a new location each time the location changes and we add the points
     * of the polyline in a list that is called "points".
     * On top of the previous statement, it declares the distance parameter and the speed.
     * The distance is retrieved while moving by calculating the points of the list using google map utils.
     * If the device has speed, the application shows the current speed, if it does not have speed it just shows
     * "0.00".
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        LatLng myCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
        points.add(myCoordinates);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(myCoordinates));
        redrawLine();
        double distance = SphericalUtil.computeLength(points);
        String convertedDistance = String.format("%.2f",distance);
        distanceTxt.setText(convertedDistance);
        double currentSpeed;
        if(location.hasSpeed()){
            currentSpeed = (location.getSpeed() * 3.6);
            String convertedSpeed = String.format("%.2f",currentSpeed);
            speedTxt.setText(convertedSpeed);
        }else{
            currentSpeed = 0.0;
            String convertedSpeed = String.format("%.2f",currentSpeed);
            speedTxt.setText(convertedSpeed);
        }

        System.out.println(currentSpeed + "@@@@@@@@@@@@@@@@@@@@ km/h ");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /***
     * This method draws the polyline on the map. It basically adds the coordinates to the point list ad then adds each point
     * to the polyline options.
     */
    private void redrawLine() {
        mGoogleMap.clear();

        PolylineOptions options = new PolylineOptions().width(10).color(Color.GREEN).geodesic(true);
        for (int i = 0; i < points.size(); ++i) {
            LatLng point = points.get(i);
            options.add(point);
        }
        mGoogleMap.addPolyline(options);

    }

    /***
     * Again, as the name suggests, the methods requests the location by declaring a criteria which has fine accuracy and uses
     * high power in order to update the location accordingly.
     * Then it gets the best provider, and after there is another permission check to request the location updates.
     * When requesting the location, we specify the provider used, the interval in ms and the error distance in meters.
     */
    private void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(provider, 500, 2, this);
    }
}

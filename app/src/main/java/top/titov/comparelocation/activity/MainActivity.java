package top.titov.comparelocation.activity;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import top.titov.comparelocation.MyApp;
import top.titov.comparelocation.R;
import top.titov.comparelocation.utils.CONST;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker mNetworkMarker;
    private Marker mGpsMarker;
    private Circle mNetworkCircle;
    private Circle mGpsCircle;

    private LocationManager mLocationManager;
    private LocationListener mNetworkLocListener;
    private LocationListener mGpsLocListener;

    private AppCompatButton mWifiButton;
    private boolean isWifiEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mWifiButton = (AppCompatButton) findViewById(R.id.wifi_button);
        setButtonColor();
        createLocationListeners();
    }

    private void createLocationListeners(){
       mNetworkLocListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                setNetworkMarker(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        mGpsLocListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                setGpsMarker(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
    }

    public void onLocateWifiClick(View v){
        if (!isWifiEnabled) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mNetworkLocListener);
        } else {
            mLocationManager.removeUpdates(mNetworkLocListener);
            clearNetworkMarkers();
        }
        isWifiEnabled = !isWifiEnabled;
        setButtonColor();
    }

    private void clearNetworkMarkers(){
        mNetworkMarker.remove();
        mNetworkCircle.remove();
        mNetworkMarker = null;
        mNetworkCircle = null;
    }

    private void setButtonColor(){
        int buttonColorId = isWifiEnabled ? R.color.enabled_color : R.color.disabled_color;
        mWifiButton.setBackgroundColor(MyApp.getColorFromRes(buttonColorId));
    }

    @Override
    public void onMapReady(GoogleMap pGoogleMap) {
        mMap = pGoogleMap;
    }

    private void setNetworkMarker(Location pLocation) {
        if (mNetworkMarker == null) {
            mNetworkMarker = addMarker(pLocation, R.string.network_title, R.drawable.ic_network_wifi_black_24dp);
            mNetworkCircle = addCircle(pLocation, R.color.network_circle_color);
            moveCamera(pLocation);
        } else setMarker(pLocation, mNetworkMarker, mNetworkCircle);
    }

    private void setGpsMarker(Location pLocation) {
        if (mGpsMarker == null) {
            mGpsMarker = addMarker(pLocation, R.string.gps_title, R.drawable.ic_gps_location);
            mGpsCircle = addCircle(pLocation, R.color.gps_circle_color);
            moveCamera(pLocation);
        }
        else setMarker(pLocation, mGpsMarker, mGpsCircle);
    }

    private Marker addMarker(Location pLocation, int pTitleId, int pDrawableId) {
        double lat = pLocation.getLatitude();
        double lng = pLocation.getLongitude();
        return mMap.addMarker(
                new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .title(MyApp.getStringFromRes(pTitleId))
                        .anchor(0.5F, 1F)
                        .icon(BitmapDescriptorFactory.fromResource(pDrawableId))
        );
    }

    private Circle addCircle(Location pLocation, int pCircleColorId){
        double lat = pLocation.getLatitude();
        double lng = pLocation.getLongitude();
        return mMap.addCircle(new CircleOptions()
                .center(new LatLng(lat, lng))
                .radius(pLocation.getAccuracy() / 0.68f)
                .strokeColor(MyApp.getColorFromRes(android.R.color.transparent))
                .fillColor(MyApp.getColorFromRes(pCircleColorId)));
    }

    private void setMarker(Location pLocation, Marker pMarker, Circle pCircle){
        pMarker.setPosition(new LatLng(pLocation.getLatitude(), pLocation.getLongitude()));
        pCircle.setCenter(new LatLng(pLocation.getLatitude(), pLocation.getLongitude()));
        pCircle.setRadius(pLocation.getAccuracy()/0.68f);
    }

    private void moveCamera(Location pLocation){
        LatLng position = new LatLng(pLocation.getLatitude(), pLocation.getLongitude());

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position)
                .zoom(CONST.DEFAULT_ZOOM)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mLocationManager.removeUpdates(mNetworkLocListener);
        mLocationManager.removeUpdates(mGpsLocListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mGpsLocListener);
        if (isWifiEnabled) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mNetworkLocListener);
        }
    }
}

package com.aig.science.damagedetection.controllers;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.aig.science.damagedetection.utilities.GPSTracker;
import com.aig.science.damagedetection.utilities.ItemViewHolder;
import com.aig.science.damagedetection.R;
import com.aig.science.damagedetection.adaptor.ShopsListViewAdapter;
import com.aig.science.damagedetection.models.Place;
import com.aig.science.damagedetection.services.PlacesService;
import com.aig.science.damagedetection.utilities.NetworkingChecker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class NearByShopsActivity extends Activity {

    private final static String TAG = "NearByShopsActivity";
    private final static String PLACE_TYPE = "auto+repair";
    //private final static String API_KEY = "AIzaSyBPMbNtP_kZwFwL5mV8AymPlCIVmQIGLTg";
    private final static String API_KEY = "AIzaSyD_rmd_ZOA1zcCy57oynLYeC-zI4w9QIQ8";

    private Dialog dialog;
    private Dialog infoDialog;
    private ViewSwitcher shopViewSwitcher;
    private Animation slide_in_left, slide_out_right;
    private boolean isListView = true;
    private List<Place> placesList = new ArrayList<Place>();
    private ArrayList<Place> resultPalcesList;
    private Place placeDetails;
    private ShopsListViewAdapter shopAdapter;
    private ListView shopsListView;
    private GoogleMap mMap;
    private LatLng currentLocation;
    private GPSTracker gpsTracker;
    private LinearLayout networkStatusLayout;
    private Button refreshNetworkBtn;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_shops);

        networkStatusLayout = (LinearLayout) findViewById(R.id.network_status_layout);
        refreshNetworkBtn = (Button) findViewById(R.id.refresh_network_btn);
        refreshNetworkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initLayout();
            }
        });
        context = this;
        shopViewSwitcher = (ViewSwitcher) findViewById(R.id.shops_viewswitcher);
        slide_in_left = AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left);
        slide_out_right = AnimationUtils.loadAnimation(this,
                android.R.anim.slide_out_right);
        shopViewSwitcher.setInAnimation(slide_in_left);
        shopViewSwitcher.setOutAnimation(slide_out_right);
        shopsListView = (ListView) findViewById(R.id.nearby_shop_listview);
        getCurrentLocation();
        initLayout();
    }

    public void initLayout() {
        if (NetworkingChecker.isNetworkAvailable(this)) {
            new GetPlaces(NearByShopsActivity.this, PLACE_TYPE).execute();
            networkStatusLayout.setVisibility(View.GONE);
        }
    }

    private void setUpMapIfNeeded() {
        if (mMap != null) {
            return;
        }
        mMap = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMap();

        if (mMap == null) {
            return;
        }

        for (Place place : resultPalcesList) {
            mMap.addMarker(new MarkerOptions()
                            .position(place.getLatLng())
                            .anchor(0.5f, 0.5f)
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.ic_repair_man))
            );
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Place tempPlace = null;
                    // When clicked, show a toast with the TextView text
                    for (Place place : resultPalcesList) {
                        if (marker.getPosition().latitude == place.getLatLng().latitude && marker.getPosition().longitude == place.getLatLng().longitude) {
                            tempPlace = place;
                        }
                    }
                    infoDialog = new Dialog(context);
                    infoDialog.getWindow();
                    // dialog.setTitle("Please take a photo.");
                    infoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    infoDialog.setContentView(R.layout.place_clicked_options_window);
                    TextView clickedShopNameTxtview = (TextView) infoDialog.findViewById(R.id.clicked_shop_name_txtview);
                    TextView clickedPhoneNumberTxtview = (TextView) infoDialog.findViewById(R.id.clicked_phone_number_txtview);
                    TextView clickedShopAddressTxtview = (TextView) infoDialog.findViewById(R.id.clicked_shop_address_txtview);
                    TextView clickedDistanceTxtview = (TextView) infoDialog.findViewById(R.id.clicked_distance_txtview);

                    clickedShopNameTxtview.setText(tempPlace.getsName());
                    clickedPhoneNumberTxtview.setText(tempPlace.getsPhoneNumber());
                    clickedShopAddressTxtview.setText(tempPlace.getsVicinity());
                    clickedDistanceTxtview.setText(String.valueOf(tempPlace.getDistance()));
                    Button callShopBtn = (Button) infoDialog.findViewById(R.id.call_shop_btn);
                    //homeScreenIntent.putExtra("Claim",);
                    final Place temPlace = tempPlace;
                    callShopBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + temPlace.getsPhoneNumber()));
                            startActivity(callIntent);
                        }
                    });
                    Button navigateToShopBtn = (Button) infoDialog.findViewById(R.id.navigate_to_shop_btn);
                    navigateToShopBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            final Intent navigateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?" + "saddr=" + currentLocation.latitude + "," + currentLocation.longitude + "&daddr=" + temPlace.getLatLng().latitude + "," + temPlace.getLatLng().longitude));
                            navigateIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                            startActivity(navigateIntent);
                        }
                    });
                    infoDialog.show();
                }
            });
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    View convertView = null;
                    Place tempPlace = new Place();
                    final ItemViewHolder viewHolder;
                    for (Place place : resultPalcesList) {
                        if (marker.getPosition().latitude == place.getLatLng().latitude && marker.getPosition().longitude == place.getLatLng().longitude) {
                            tempPlace = place;
                        }
                    }
                    if (convertView == null) {
                        convertView = inflater.inflate(R.layout.custom_infowindow, null);
                        viewHolder = new ItemViewHolder();
                        viewHolder.setShopNameTextView((TextView) convertView.findViewById(R.id.shop_info_name_txtview));
                        viewHolder.setShopAddressTextView((TextView) convertView.findViewById(R.id.shop_address_info_txtview));
                        viewHolder.setPhoneNumTextView((TextView) convertView.findViewById(R.id.phone_number_info_txtview));
                        viewHolder.setDistanceTextView((TextView) convertView.findViewById(R.id.distance_info_txtview));
                        viewHolder.getShopNameTextView().setText(tempPlace.getsName());
                        viewHolder.getPhoneNumTextView().setText(tempPlace.getsPhoneNumber());
                        viewHolder.getShopAddressTextView().setText(tempPlace.getsVicinity());
                        viewHolder.getDistanceTextView().setText(String.valueOf(tempPlace.getDistance()) + " miles");
                        convertView.setTag(viewHolder);
                    } else {
                        viewHolder = (ItemViewHolder) convertView.getTag();
                    }
                    convertView.setBackgroundColor(getResources().getColor(R.color.aig_blue));
                    return convertView;
                }

                @Override
                public View getInfoContents(Marker arg0) {
                    return null;
                }
            });
        }

        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (currentLocation != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));
        }
    }

    public void init() {

        shopAdapter = new ShopsListViewAdapter(this, resultPalcesList);
        shopsListView.setAdapter(shopAdapter);
        shopsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View view, final int position, long id) {
                // When clicked, show a toast with the TextView text

                dialog = new Dialog(view.getContext());
                dialog.getWindow();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.place_clicked_options_window);
                TextView clickedShopNameTxtview = (TextView) dialog.findViewById(R.id.clicked_shop_name_txtview);
                TextView clickedPhoneNumberTxtview = (TextView) dialog.findViewById(R.id.clicked_phone_number_txtview);
                TextView clickedShopAddressTxtview = (TextView) dialog.findViewById(R.id.clicked_shop_address_txtview);
                TextView clickedDistanceTxtview = (TextView) dialog.findViewById(R.id.clicked_distance_txtview);

                clickedShopNameTxtview.setText(resultPalcesList.get(position).getsName());
                clickedPhoneNumberTxtview.setText(resultPalcesList.get(position).getsPhoneNumber());
                clickedShopAddressTxtview.setText(resultPalcesList.get(position).getsVicinity());
                clickedDistanceTxtview.setText(String.valueOf(resultPalcesList.get(position).getDistance()));
                Button callShopBtn = (Button) dialog.findViewById(R.id.call_shop_btn);
                //homeScreenIntent.putExtra("Claim",);
                callShopBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + resultPalcesList.get(position).getsPhoneNumber()));
                        startActivity(callIntent);
                    }
                });
                Button navigateToShopBtn = (Button) dialog.findViewById(R.id.navigate_to_shop_btn);
                navigateToShopBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        final Intent navigateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?" + "saddr=" + currentLocation.latitude + "," + currentLocation.longitude + "&daddr=" + resultPalcesList.get(position).getLatLng().latitude + "," + resultPalcesList.get(position).getLatLng().longitude));
                        navigateIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                        startActivity(navigateIntent);
                    }
                });
                dialog.show();
            }
        });
    }

    public void getCurrentLocation() {
        gpsTracker = new GPSTracker(NearByShopsActivity.this);
        // Check if GPS enabled
        if (gpsTracker.canGetLocation()) {
            currentLocation = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
            double longitude = gpsTracker.getLongitude();
            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + currentLocation.latitude + "\nLong: " + currentLocation.longitude, Toast.LENGTH_SHORT).show();
        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gpsTracker.showSettingsAlert();
        }
    }

    private class GetPlaces extends AsyncTask<Void, Void, ArrayList<Place>> {

        private ProgressDialog dialog;
        private Context context;
        private String placeType;

        public GetPlaces(Context context, String placeType) {
            this.context = context;
            this.placeType = placeType;
        }

        @Override
        protected void onPostExecute(ArrayList<Place> palcesList) {
            super.onPostExecute(palcesList);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            //android.os.Debug.waitForDebugger();
            resultPalcesList = palcesList;
            Collections.sort(resultPalcesList);
            init();
            setUpMapIfNeeded();
/*            for (int i = 0; i < palcesList.size(); i++) {
                mMap.addMarker(new MarkerOptions()
                        .title(palcesList.get(i).getsName())
                        .position(palcesList.get(i).getLatLng())
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.ic_repair_shop))
                        .snippet(palcesList.get(i).getsVicinity()));
            }
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(palcesList.get(0).getLatLng()) // Sets the center of the map to
                            // Mountain View
                    .zoom(14) // Sets the zoom
                    .tilt(30) // Sets the tilt of the camera to 30 degrees
                    .build(); // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));*/
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage("Loading Repair Shops");
            dialog.isIndeterminate();
            dialog.show();
        }

        @Override
        protected ArrayList<Place> doInBackground(Void... arg0) {
            //android.os.Debug.waitForDebugger();
            PlacesService service = new PlacesService(API_KEY);
            ArrayList<Place> findPlaces = service.findPlaces(currentLocation, placeType);

            for (int i = 0; i < findPlaces.size(); i++) {
                Place placeDetail = findPlaces.get(i);
                //Log.e(TAG, "places : " + placeDetail.getsName());
            }
            return findPlaces;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nearby_shops, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                break;
            case R.id.switch_view_btn:
                if (isListView) {
                    shopViewSwitcher.showPrevious();
                    item.setIcon(R.drawable.ic_action_view_as_list);
                    isListView = false;
                } else {
                    shopViewSwitcher.showPrevious();
                    item.setIcon(R.drawable.ic_action_map);
                    isListView = true;
                }
                break;
            default:
                break;
        }
        return true;
    }
}

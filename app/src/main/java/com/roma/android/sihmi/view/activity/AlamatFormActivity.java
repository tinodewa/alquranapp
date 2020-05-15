package com.roma.android.sihmi.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Alamat;
import com.roma.android.sihmi.model.database.entity.Master;
import com.roma.android.sihmi.model.database.interfaceDao.AlamatDao;
import com.roma.android.sihmi.model.database.interfaceDao.MasterDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.roma.android.sihmi.view.adapter.AlamatMasterAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlamatFormActivity extends BaseActivity implements OnMapReadyCallback {
    public static String IS_EDIT = "is_edit";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_simpan)
    Button btnSimpan;
    @BindView(R.id.etNamaKomisariat)
    EditText etNamaKomisariat;
    @BindView(R.id.llInfo)
    LinearLayout llInfo;
    @BindView(R.id.llDetail)
    LinearLayout llDetail;
    @BindView(R.id.tv_nama)
    TextView tvNama;
    @BindView(R.id.tv_lokasi)
    TextView tvLokasiDetail;
    @BindView(R.id.actv_lokasi)
    AutoCompleteTextView actvLokasi;

    public static final int PICK_LOCATION = 0;
    private static int REQUEST_CODE = 0;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    GoogleMap map;
    LatLng latLng;

    SupportMapFragment mapFragment;

    FusedLocationProviderClient fusedLocationProviderClient;
    private Boolean mLocationPermissionsGranted = false;

    String id_address, judul, address="";
    Alamat alamat;
    boolean isEdit = false;

    double lat, lng;

    MasterService service;
    AppDb appDb;
    AlamatDao alamatDao;
    MasterDao masterDao;
    UserDao userDao;
    Master master;
    private List<String> locationList;

    private PlacesClient placesClient;
    private List<String> placeIds;
    private ArrayAdapter<String> locationListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alamat_form);
        ButterKnife.bind(this);

        service = ApiClient.getInstance().getApi();
        appDb = AppDb.getInstance(this);
        alamatDao = appDb.alamatDao();
        masterDao = appDb.masterDao();
        userDao = appDb.userDao();

        location();
        getLocationPermission();


        id_address = getIntent().getStringExtra("id");
        isEdit = getIntent().getBooleanExtra(IS_EDIT, false);

        if (id_address != null){
            alamat = alamatDao.getAlamatById(id_address);

            if (isEdit){
                judul = getString(R.string.perbarui_alamat);
                btnSimpan.setVisibility(View.VISIBLE);
                address = alamat.getAlamat();
                lat = Double.parseDouble(alamat.getLatitude());
                lng = Double.parseDouble(alamat.getLongitude());
                llDetail.setVisibility(View.GONE);
                llInfo.setVisibility(View.VISIBLE);
            } else {
                judul = alamat.getNama();
                btnSimpan.setVisibility(View.GONE);
                llDetail.setVisibility(View.VISIBLE);
                llInfo.setVisibility(View.GONE);
                tvNama.setText(alamat.getNama());
                tvLokasiDetail.setText(alamat.getAlamat());
            }
        } else {
            judul = getString(R.string.tambah_alamat);
            llDetail.setVisibility(View.GONE);
            llInfo.setVisibility(View.VISIBLE);
        }

        toolbar.setTitle(judul);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (!Tools.isSuperAdmin()){
            etNamaKomisariat.setEnabled(false);
            etNamaKomisariat.setText(userDao.getUser().getKomisariat());
            etNamaKomisariat.setBackgroundColor(getResources().getColor(R.color.colorTextLight));
        }

        // Initialize Places.
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

        // Create a new Places client instance.
        placesClient = Places.createClient(this);


        locationList = new ArrayList<>();
        placeIds = new ArrayList<>();
        locationListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locationList);

        actvLokasi.setAdapter(locationListAdapter);

        Handler handler = new Handler();

        actvLokasi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                handler.removeCallbacksAndMessages(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.postDelayed(() -> showPlaceAutoComplete(s.toString()), 1000);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        actvLokasi.setOnItemClickListener((parent, view, position, id) -> {
            handler.removeCallbacksAndMessages(null);
            address = locationList.get(position);

            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

            String placeId = placeIds.get(position);
            FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

            placesClient.fetchPlace(request).addOnSuccessListener(response -> {
                Place place = response.getPlace();

                LatLng latLngPlace = place.getLatLng();
                if (latLngPlace != null) {
                    lat = latLngPlace.latitude;
                    lng = latLngPlace.longitude;

                    map.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngPlace, 15f));
                }

                Log.i("LOCATION", "Place wirh id " + placeId + " retrieved");
            }).addOnFailureListener(exception -> {
                if (exception instanceof ApiException) {
                    // Handle error with given status code.
                    Log.e("LOCAtiON", "Place not found: " + exception.getMessage());
                }
            });
        });
    }

    private void showPlaceAutoComplete(String query) {
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setCountry("ID")
                .setSessionToken(token)
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            locationList.clear();

            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                locationList.add(prediction.getFullText(null).toString());

                Log.i("LOCATION", prediction.getPlaceId());
                Log.i("LOCATION", prediction.getPrimaryText(null).toString());

                placeIds.add(prediction.getPlaceId());
            }

            locationListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locationList);

            actvLokasi.setAdapter(locationListAdapter);
            locationListAdapter.notifyDataSetChanged();
        }).addOnFailureListener(exception -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e("LOCATION API", "Place not found: " + apiException.getStatusCode());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (id_address != null){
            if (isEdit){
                etNamaKomisariat.setFocusable(true);

            } else {
                etNamaKomisariat.setFocusable(false);
            }
            etNamaKomisariat.setText(alamat.getNama());
            actvLokasi.setText(alamat.getAlamat());
            latLng = new LatLng(Double.parseDouble(alamat.getLatitude()), Double.parseDouble(alamat.getLongitude()));
            map.addMarker(new MarkerOptions().position(latLng).title(alamat.getNama()));

            if (mLocationPermissionsGranted) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
            }

        } else {
            if (mLocationPermissionsGranted) {
//                getDeviceLocation();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            }
        }

    }

    private void getLocationPermission(){
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                location();
            }
        }else{
            location();
        }
    }

    private void initMap(){
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getDeviceLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            Task location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
//                        Toast.makeText(AlamatFormActivity.this, "Found Location", Toast.LENGTH_SHORT).show();
                        try {
                            Location currentLocation = (Location) task.getResult();

                            latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

//                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15f));
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                            Log.d("latlng", "onComplete: "+latLng);
                        } catch (NullPointerException e){
                            Log.d("latlng", "onComplete: catch "+latLng);
                        }
                    } else {
                        Toast.makeText(AlamatFormActivity.this, "Current Location is Null", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (SecurityException e){
            Toast.makeText(this, "Security Exception "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.etNamaKomisariat)
    public void choose(){
//        if (Tools.isSuperAdmin()) {
            LayoutInflater inflater = LayoutInflater.from(this);
            View dialogView = inflater.inflate(R.layout.dialog_master, null);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setView(dialogView)
                    .create();
            dialog.show();
            List<Master> list = new ArrayList<>();
            list.add(new Master("0", "PBHMI"));
            list.addAll(masterDao.getAllMaster());
            AlamatMasterAdapter adapter = new AlamatMasterAdapter(this, list, master -> {
                this.master = master;
                if (master.isAvailableAddres()) {
                    Tools.showToast(this, getString(R.string.alamat_sudah_ada));
                } else {
                    etNamaKomisariat.setText(master.getValue());
                    dialog.dismiss();
                }
            });
            RecyclerView recyclerView = dialogView.findViewById(R.id.rv_list_master);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
//        }
    }

    @OnClick(R.id.btn_simpan)
    public void tambah(){
        if (isEdit){
            updateAddress();
        } else {
            addAddres();
        }
    }

    private void addAddres(){
        String type;
        if (etNamaKomisariat.getText().toString().equals("PBHMI")) {
            type = "0-PB HMI";
        } else {
            type = masterDao.getTypeMasterByValue(etNamaKomisariat.getText().toString()) + "-" + etNamaKomisariat.getText().toString();
        }
        Call<GeneralResponse> call = service.addAddress(Constant.getToken(), etNamaKomisariat.getText().toString(), address, lat, lng, type, "");
        if (!etNamaKomisariat.getText().toString().isEmpty() && !address.isEmpty() && lat != 0 && lng != 0) {
            if (Tools.isOnline(this)) {
                Tools.showProgressDialog(this, getString(R.string.menambah_alamat));
                call.enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.body().getStatus().equalsIgnoreCase("ok")) {
                                Toast.makeText(AlamatFormActivity.this, getString(R.string.berhasil_tambah), Toast.LENGTH_SHORT).show();
                                setResult(Activity.RESULT_OK);
                                finish();
                            } else {
                                Toast.makeText(AlamatFormActivity.this, getString(R.string.gagal_tambah), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AlamatFormActivity.this, getString(R.string.gagal_tambah), Toast.LENGTH_SHORT).show();
                        }
                        Tools.dissmissProgressDialog();
                    }

                    @Override
                    public void onFailure(Call<GeneralResponse> call, Throwable t) {
                        Toast.makeText(AlamatFormActivity.this, getString(R.string.gagal_tambah), Toast.LENGTH_SHORT).show();
                        Tools.dissmissProgressDialog();
                    }
                });
            } else {
                Toast.makeText(this, getString(R.string.tidak_ada_internet), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.field_mandatory), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateAddress(){
        String type;
        if (etNamaKomisariat.getText().toString().equals("PBHMI")) {
            type = "0-PB HMI";
        } else {
            type = masterDao.getTypeMasterByValue(etNamaKomisariat.getText().toString()) + "-" + etNamaKomisariat.getText().toString();
        }
        Call<GeneralResponse> call = service.updateAddress(Constant.getToken(), id_address, etNamaKomisariat.getText().toString(), address, lat, lng, type, "");
        if (Tools.isOnline(this)) {
            Tools.showProgressDialog(this, getString(R.string.update_alamat));
            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("ok")) {
                            Toast.makeText(AlamatFormActivity.this, getString(R.string.berhasil_update), Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(AlamatFormActivity.this, AlamatActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            setResult(Activity.RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(AlamatFormActivity.this, getString(R.string.berhasil_update), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AlamatFormActivity.this, getString(R.string.berhasil_update), Toast.LENGTH_SHORT).show();
                    }
                    Tools.dissmissProgressDialog();

                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Toast.makeText(AlamatFormActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Tools.dissmissProgressDialog();
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.tidak_ada_internet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

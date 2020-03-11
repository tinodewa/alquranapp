package com.roma.android.sihmi.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.google.android.libraries.places.compat.AutocompleteFilter;
import com.google.android.libraries.places.compat.Place;
import com.google.android.libraries.places.compat.ui.PlaceAutocomplete;
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
    @BindView(R.id.tvLokasi)
    TextView tvLokasi;
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

        tvLokasi.setOnClickListener(v -> showPlaceAutoComplete(PICK_LOCATION));

        if (!Tools.isSuperAdmin()){
            etNamaKomisariat.setEnabled(false);
            etNamaKomisariat.setText(userDao.getUser().getKomisariat());
            etNamaKomisariat.setBackgroundColor(getResources().getColor(R.color.colorTextLight));
        }

    }

    private void showPlaceAutoComplete(int type) {
        REQUEST_CODE = type;

        //FIlter
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setCountry("ID").build();

        try {
            Intent i = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(typeFilter)
                    .build(this);
            startActivityForResult(i, REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Place place = PlaceAutocomplete.getPlace(this, data);
            if (place.isDataValid()) {
                String placeAddress = place.getAddress().toString();
                LatLng placeLatLng = place.getLatLng();
                String placeName = place.getName().toString();

                address = placeAddress;
                latLng = place.getLatLng();
                lat = place.getLatLng().latitude;
                lng = place.getLatLng().longitude;

                switch (REQUEST_CODE) {
                    case PICK_LOCATION:
                        tvLokasi.setText(placeAddress);

                        latLng = placeLatLng;

                        map.clear();
                        map.addMarker(new MarkerOptions().position(placeLatLng).title("test"));
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 15f));
                        break;
                }
            } else {
                Toast.makeText(this, "Invalid Place!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (id_address != null){
            if (isEdit){
                etNamaKomisariat.setFocusable(true);
                tvLokasi.setClickable(true);

            } else {
                etNamaKomisariat.setFocusable(false);
                tvLokasi.setClickable(false);
            }
            etNamaKomisariat.setText(alamat.getNama());
            tvLokasi.setText(alamat.getAlamat());
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
        if (!etNamaKomisariat.getText().toString().isEmpty()) {
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

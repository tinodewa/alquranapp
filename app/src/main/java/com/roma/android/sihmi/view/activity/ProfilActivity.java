//package com.roma.android.sihmi.view.activity;
//
//import android.Manifest;
//import android.animation.Animator;
//import android.animation.ValueAnimator;
//import android.app.Activity;
//import android.app.DatePickerDialog;
//import android.app.ProgressDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.StrictMode;
//import android.provider.MediaStore;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.DatePicker;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.RelativeLayout;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.widget.Toolbar;
//import androidx.cardview.widget.CardView;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.roma.android.sihmi.R;
//import com.roma.android.sihmi.core.CoreApplication;
//import com.roma.android.sihmi.repository.model.entity.Contact;
//import com.roma.android.sihmi.repository.model.entity.Job;
//import com.roma.android.sihmi.repository.model.entity.Medsos;
//import com.roma.android.sihmi.repository.model.entity.Pendidikan;
//import com.roma.android.sihmi.repository.model.entity.Training;
//import com.roma.android.sihmi.repository.model.entity.User;
//import com.roma.android.sihmi.repository.model.entity.tempat.Provinsi;
//import com.roma.android.sihmi.repository.network.MasterService;
//import com.roma.android.sihmi.repository.response.GeneralResponse;
//import com.roma.android.sihmi.repository.response.JobResponse;
//import com.roma.android.sihmi.repository.response.MedsosResponse;
//import com.roma.android.sihmi.repository.response.PendidikanResponse;
//import com.roma.android.sihmi.repository.response.ProfileResponse;
//import com.roma.android.sihmi.repository.response.TrainingResponse;
//import com.roma.android.sihmi.repository.response.UploadFileResponse;
//import com.roma.android.sihmi.repository.response.tempat.ProvinsiResponse;
//import com.roma.android.sihmi.utils.Constant;
//import com.roma.android.sihmi.utils.Tools;
//import com.roma.android.sihmi.view.adapter.JobAdapter;
//import com.roma.android.sihmi.view.adapter.MedsosAdapter;
//import com.roma.android.sihmi.view.adapter.PendidikanAdapter;
//import com.roma.android.sihmi.view.adapter.TempatAdapter;
//import com.roma.android.sihmi.view.adapter.TrainingAdapter;
//import com.google.android.material.textfield.TextInputLayout;
//import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageView;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Objects;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import id.zelory.compressor.Compressor;
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import pub.devrel.easypermissions.EasyPermissions;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class ProfilActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
//    private static final String TAG = "Fabric";
//
//    public static final String ID_ROLES = "ID_ROLES";
//    public static final String UPDATE_ADMIN = "UPDATE_ADMIN";
//    public static final String ORIGIN_DETAIL = "ORIGIN_DETAIL"; // true = profil user, false = profile leader
//
//    private static boolean isFromProfile = true;
//
//    @BindView(R.id.toolbar)
//    Toolbar toolbar;
//    @BindView(R.id.iv_photo)
//    ImageView ivPhoto;
//    @BindView(R.id.tv_name)
//    TextView tvName;
//    @BindView(R.id.tv_admin)
//    TextView tvAdmin;
//    @BindView(R.id.etNamaDepan)
//    EditText etNamaDepan;
//    @BindView(R.id.etNamaBelakang)
//    EditText etNamaBelakang;
//    @BindView(R.id.et_panggilan)
//    EditText etPanggilan;
//    @BindView(R.id.et_jenis_kelamin)
//    EditText etJenisKelamin;
//    @BindView(R.id.et_hp)
//    EditText etHp;
//    @BindView(R.id.et_alamat_domisili)
//    EditText etAlamatDomisili;
//    @BindView(R.id.et_alamat_komisariat)
//    EditText etAlamatKomisariat;
//    @BindView(R.id.et_berkas)
//    EditText etBerkas;
//    @BindView(R.id.et_password)
//    EditText etPassword;
//    @BindView(R.id.et_password_baru)
//    EditText etPasswordBaru;
//    @BindView(R.id.et_confirm_password)
//    EditText etConfirmPassword;
//    @BindView(R.id.tv_pilihan)
//    TextView tvPilihan;
//    @BindView(R.id.spinner_pilihan)
//    Spinner spinnerPilihan;
//    @BindView(R.id.btn_simpan_profile)
//    Button btnSimpanProfile;
//    @BindView(R.id.btn_batal)
//    Button btnBatal;
//    @BindView(R.id.btn_simpan)
//    Button btnSimpan;
//    @BindView(R.id.btn_ubah)
//    Button btnUbah;
//    @BindView(R.id.btn_tolak)
//    Button btnTolak;
//    @BindView(R.id.btn_setuju)
//    Button btnSetuju;
//    @BindView(R.id.btn_pengajuan)
//    Button btnPengajuan;
//    @BindView(R.id.til_password)
//    TextInputLayout tillPassword;
//    @BindView(R.id.til_password_baru)
//    TextInputLayout tillPasswordBaru;
//    @BindView(R.id.til_confirm_password)
//    TextInputLayout tilConfirmPassword;
//
//    @BindView(R.id.rlformpengajuan)
//    RelativeLayout rlformpengajuan;
//
//    @BindView(R.id.et_tempat_lahir)
//    EditText etTempatLahir;
//    @BindView(R.id.rl_tnggal)
//    RelativeLayout rlTanggal;
//    @BindView(R.id.et_tanggal)
//    EditText etTanggal;
//    @BindView(R.id.iv_tanggal)
//    ImageView iv_tanggal;
//    @BindView(R.id.et_status)
//    EditText etStatus;
//    @BindView(R.id.til_status)
//    TextInputLayout tilStatus;
//    @BindView(R.id.et_birthplace)
//    EditText etBirthplace;
//    @BindView(R.id.til_birthplace)
//    TextInputLayout tilBirthplace;
//    @BindView(R.id.et_birthdate)
//    EditText etBirthdate;
//    @BindView(R.id.til_birthdate)
//    TextInputLayout tilBirthdate;
//
//    @BindView(R.id.tv_pengajuan)
//    TextView tvPengajuan;
//    @BindView(R.id.header)
//    LinearLayout mLinearLayoutHeader;
//    @BindView(R.id.expandable)
//    LinearLayout mLinearLayoutExpand;
//
//
//    @BindView(R.id.tv_pendidikan)
//    TextView tvPendidikan;
//    @BindView(R.id.rv_pendidikan)
//    RecyclerView rvPendidikan;
//    @BindView(R.id.add_pendidikan)
//    TextView addPendidikan;
//    @BindView(R.id.tv_training)
//    TextView tvTraining;
//    @BindView(R.id.rv_training)
//    RecyclerView rvTraining;
//    @BindView(R.id.add_training)
//    TextView addTraining;
//    @BindView(R.id.tv_pekerjaan)
//    TextView tvPekerjaan;
//    @BindView(R.id.rv_pekerjaan)
//    RecyclerView rvPekerjaan;
//    @BindView(R.id.add_pekerjaan)
//    TextView addPekerjaan;
//
//    @BindView(R.id.tv_medsos)
//    TextView tvMedsos;
//    @BindView(R.id.rv_medsos)
//    RecyclerView rvMedsos;
//    @BindView(R.id.add_medsos)
//    TextView addMedsos;
//    @BindView(R.id.cv_lokasi)
//    CardView cvLokasi;
//    @BindView(R.id.tv_lokasi_LK)
//    TextView tvLokasiLk;
//
//    @BindView(R.id.llPengajuanLK1)
//    LinearLayout llPengajuanLK1;
//    @BindView(R.id.sp_bulan)
//    Spinner spBulan;
//    @BindView(R.id.sp_tahun)
//    Spinner spTahun;
//
//    @BindView(R.id.et_lokasi)
//    EditText etLokasi;
//
//    @BindView(R.id.ll_approve)
//    LinearLayout llApprove;
//    @BindView(R.id.ll_4thn)
//    LinearLayout ll4thn;
//
//    @BindView(R.id.radio_gender)
//    RadioGroup rgGender;
//    @BindView(R.id.radio_status)
//    RadioGroup rgStatus;
//
//    List<Medsos> medsos = new ArrayList();
//    List<Pendidikan> pendidikans = new ArrayList();
//    List<Training> trainings = new ArrayList();
//    List<Job> jobs = new ArrayList();
//
//    MedsosAdapter medsosAdapter;
//    PendidikanAdapter pendidikanAdapter;
//    TrainingAdapter trainingAdapter;
//    JobAdapter jobAdapter;
//
//    boolean isEdit, isChange, isUpdateAdmin;
//    String title;
//    int tujuan_pengajuan;
//    int id_prov, id_kota, id_wilayah;
//
//    MasterService service;
//
//    ProgressDialog dialog;
//
//    User user;
//    String id_roles="";
//    String namaKota="";
//    String urlImage;
//
//    private Uri uri;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_profil);
//        ButterKnife.bind(this);
//
//        Log.d("Check", "onCreate: "+CoreApplication.get().getConstant().getToken()+" - "+Calendar.getInstance().get(Calendar.YEAR));
//        Constant.setDataProfile(false);
//
//        service = CoreApplication.get().getClient().create(MasterService.class);
//        initProfile();
//        toolbar.setTitle(title.toUpperCase());
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//
//
//        //set visibility to GONE
////        mLinearLayoutExpand.setVisibility(View.GONE);
////
////        mLinearLayoutHeader.setOnClickListener(new View.OnClickListener() {
////
////            @Override
////            public void onClick(View v) {
////                if (mLinearLayoutExpand.getVisibility()==View.GONE){
////                    tvPengajuan.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down, 0);
////                    expand();
////                }else{
////                    tvPengajuan.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_right, 0);
////                    collapse();
////                }
////            }
////        });
//
//        initViewBulan();
//        initViewTahun();
//
//        medsosAdapter = new MedsosAdapter(this, medsos, new MedsosAdapter.ItemClickListener() {
//            @Override
//            public void onItemClickId(String id) {
//                dialogKonfirmasi(1, id);
//            }
//        });
//        rvMedsos.setLayoutManager(new LinearLayoutManager(this));
//        rvMedsos.setAdapter(medsosAdapter);
//
//        pendidikanAdapter = new PendidikanAdapter(this, pendidikans, new PendidikanAdapter.itemClickListener() {
//            @Override
//            public void onItemClickId(String id) {
//                dialogKonfirmasi(2, id);
//            }
//        });
//        rvPendidikan.setLayoutManager(new LinearLayoutManager(this));
//        rvPendidikan.setAdapter(pendidikanAdapter);
//
//        trainingAdapter = new TrainingAdapter(this, trainings, new TrainingAdapter.ItemClickListener() {
//            @Override
//            public void onItemClickId(String id) {
//                dialogKonfirmasi(3, id);
//            }
//        });
//        rvTraining.setLayoutManager(new LinearLayoutManager(this));
//        rvTraining.setAdapter(trainingAdapter);
//
//        jobAdapter = new JobAdapter(this, jobs, new JobAdapter.ItemClickListener() {
//            @Override
//            public void onItemClickId(String id) {
//                dialogKonfirmasi(4, id);
//            }
//        });
//        rvPekerjaan.setLayoutManager(new LinearLayoutManager(this));
//        rvPekerjaan.setAdapter(jobAdapter);
//
//
//        cvLokasi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivityForResult(new Intent(ProfilActivity.this, LokasiActivity.class), LokasiActivity.REQUEST_LOKASI);
//            }
//        });
//
//        ivPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectImage();
//            }
//        });
//
////        getData();
//
//    }
//
//    /*
//    * TODO : Remove Dialog Perhatian Dari Click Menu Leader
//    * */
//    private void initProfile(){
//        isFromProfile = getIntent().getBooleanExtra(ProfilActivity.ORIGIN_DETAIL, true);
//        isUpdateAdmin = getIntent().getBooleanExtra(ProfilActivity.UPDATE_ADMIN, false);
//        if (getIntent().getStringExtra(ID_ROLES) != null){
//            Log.d(TAG, "onCreate: "+getIntent().getStringExtra(ID_ROLES));
//            Log.d(TAG, "onCreate: "+Constant.getUser().getId_roles());
//            Log.d(TAG, "onCreate: !kosong");
//            Contact contact = CoreApplication.get().getAppDb().interfaceDao().getContactById(getIntent().getStringExtra(ID_ROLES));
//            user = new User();
//            user.setId_roles(contact.getId_roles());
//            user.set_id(contact.get_id());
//            user.setNama_depan(contact.getNama_depan());
//            user.setNama_belakang(contact.getNama_belakang());
//            user.setNama_panggilan(contact.getNama_panggilan());
//            user.setJenis_kelamin(contact.getJenis_kelamin());
//            user.setNomor_hp(contact.getNomor_hp());
//            user.setAlamat(contact.getAlamat());
//            urlImage = contact.getImage();
//        } else {
//            getData();
//            Log.d(TAG, "onCreate: kosong");
//            user = CoreApplication.get().getAppDb().interfaceDao().getUser();
//            urlImage = user.getImage();
//        }
//        profile(user);
//
//        if (user.getImage() != null){
//            urlImage = user.getImage();
//        }
//
//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null){
//            Log.d(TAG, "romaakbar: bundle ! null");
//            namaKota = bundle.getString("kota");
//        }
//
//        isEdit = getIntent().getBooleanExtra("isEdit", false);
//        isChange = getIntent().getBooleanExtra("isChange", false);
//        getDataMedsos();
//        getDataPendidikan();
//        getDataTraining();
////        getDataPekerjaan();
//        if (isEdit){
//
//            title = "Edit Profile";
//            viewEditProfile();
//            if (Constant.getLevelUser() == Constant.LEVEL_NON_LK){
//                llApprove.setVisibility(View.GONE);
//                ll4thn.setVisibility(View.GONE);
//                dialogtanya();
//            } else {
//                rlformpengajuan.setVisibility(View.VISIBLE);
//                llApprove.setVisibility(View.VISIBLE);
////                ll4thn.setVisibility(View.VISIBLE);
//            }
//        } else if (isChange){
//            title = "Ganti Password";
//            viewChangePassword();
//        } else {
//            if (getIntent().getStringExtra(ID_ROLES) != null){
//                title = CoreApplication.get().getAppDb().interfaceDao().getContactById(getIntent().getStringExtra(ID_ROLES)).getFullName();
//            } else {
//                title = "Profile";
//            }
//
//            viewProfile();
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.profil, menu);
//        MenuItem edit_profile = menu.findItem(R.id.action_profil);
//        MenuItem change_pass = menu.findItem(R.id.action_change_password);
//
//        if (getIntent().getStringExtra(ID_ROLES)!= null){
//            edit_profile.setVisible(false);
//            change_pass.setVisible(false);
//        }
//
//        if (isEdit) {
//            edit_profile.setVisible(false);
//        } else if (isChange){
//            edit_profile.setVisible(false);
//            change_pass.setVisible(false);
//        } else {
//            change_pass.setVisible(false);
//        }
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        switch (id){
//            case R.id.action_profil:
//                startActivityForResult(new Intent(ProfilActivity.this, ProfilActivity.class).putExtra("isEdit", true), Constant.REQUEST_GANTI_POTO);
//                break;
//            case R.id.action_change_password:
//                startActivity(new Intent(ProfilActivity.this, ProfilActivity.class).putExtra("isChange", true));
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void viewProfile(){
//        etAlamatKomisariat.setVisibility(View.GONE);
//        etBerkas.setVisibility(View.GONE);
////        etPassword.setVisibility(View.GONE);
//        etPasswordBaru.setVisibility(View.GONE);
//        etConfirmPassword.setVisibility(View.GONE);
//        btnBatal.setVisibility(View.GONE);
//        btnSimpanProfile.setVisibility(View.GONE);
//        btnSimpan.setVisibility(View.GONE);
//        btnUbah.setVisibility(View.GONE);
//        btnTolak.setVisibility(View.GONE);
//        btnSetuju.setVisibility(View.GONE);
//        rgGender.setVisibility(View.GONE);
//
//        ivPhoto.setEnabled(false);
//
//        btnPengajuan.setVisibility(View.GONE);
//
//        tillPassword.setVisibility(View.GONE);
//        tillPasswordBaru.setVisibility(View.GONE);
//        tilConfirmPassword.setVisibility(View.GONE);
//
//
//        if (Constant.getLevelUser() != Constant.LEVEL_NON_LK){
//            if (((user.getTanggal_lahir() != null && user.getTanggal_lahir().isEmpty()) || (user.getTempat_lahir() != null && user.getTempat_lahir().isEmpty()) || (user.getStatus_perkawinan() != null && user.getStatus_perkawinan().isEmpty())) && !isEdit){
//                dialogProfile(true);
//                tilBirthdate.setVisibility(View.GONE);
//                tilBirthplace.setVisibility(View.GONE);
//                tilStatus.setVisibility(View.GONE);
//            } else {
//                tilBirthdate.setVisibility(View.VISIBLE);
//                tilBirthplace.setVisibility(View.VISIBLE);
//                tilStatus.setVisibility(View.VISIBLE);
//            }
//        } else {
//            tilBirthdate.setVisibility(View.GONE);
//            tilBirthplace.setVisibility(View.GONE);
//            tilStatus.setVisibility(View.GONE);
//        }
//
//        if (getIntent().getStringExtra(ID_ROLES) != null){
//            tilBirthdate.setVisibility(View.GONE);
//            tilBirthplace.setVisibility(View.GONE);
//            tilStatus.setVisibility(View.GONE);
//        }
//
//        if (isUpdateAdmin){
//            tvPilihan.setVisibility(View.VISIBLE);
//            spinnerPilihan.setVisibility(View.VISIBLE);
//            btnBatal.setVisibility(View.VISIBLE);
//            btnUbah.setVisibility(View.VISIBLE);
//
//            defaultSpinner(CoreApplication.get().getAppDb().interfaceDao().getNamaLevel(user.getId_roles()));
//        } else {
//            tvPilihan.setVisibility(View.GONE);
//            spinnerPilihan.setVisibility(View.GONE);
//            btnUbah.setVisibility(View.GONE);
//        }
//
//        etNamaDepan.setFocusable(false);
//        etNamaBelakang.setFocusable(false);
//        etPanggilan.setFocusable(false);
//        etJenisKelamin.setFocusable(false);
//        etHp.setFocusable(false);
//        etAlamatDomisili.setFocusable(false);
//        etTanggal.setFocusable(false);
//        etTempatLahir.setFocusable(false);
//        etStatus.setFocusable(false);
//        etBirthplace.setFocusable(false);
//        etBirthdate.setFocusable(false);
//        rlformpengajuan.setVisibility(View.GONE);
//    }
//
//    private void viewEditProfile(){
//        tvPilihan.setVisibility(View.GONE);
//        spinnerPilihan.setVisibility(View.GONE);
//        etAlamatKomisariat.setVisibility(View.GONE);
//        etBerkas.setVisibility(View.GONE);
//        etConfirmPassword.setVisibility(View.GONE);
//        btnUbah.setVisibility(View.GONE);
//        btnTolak.setVisibility(View.GONE);
//        btnSetuju.setVisibility(View.GONE);
//
//        btnBatal.setVisibility(View.GONE);
//        btnSimpan.setVisibility(View.GONE);
//
//        tillPassword.setVisibility(View.GONE);
//        tillPasswordBaru.setVisibility(View.GONE);
//        tilConfirmPassword.setVisibility(View.GONE);
//
//        etJenisKelamin.setVisibility(View.GONE);
//        rgGender.setVisibility(View.VISIBLE);
//
//        tilBirthdate.setVisibility(View.GONE);
//        tilBirthplace.setVisibility(View.GONE);
//        tilStatus.setVisibility(View.GONE);
//
//        if (Constant.getLevelUser() != Constant.LEVEL_NON_LK){
//            llApprove.setVisibility(View.GONE);
//            ll4thn.setVisibility(View.GONE);
////            llSplit.setVisibility(View.GONE);
//        } else {
//            llApprove.setVisibility(View.VISIBLE);
////            ll4thn.setVisibility(View.VISIBLE);
//        }
//    }
//
//    private void viewChangePassword(){
//        etNamaDepan.setVisibility(View.GONE);
//        etNamaBelakang.setVisibility(View.GONE);
//        etPanggilan.setVisibility(View.GONE);
//        etJenisKelamin.setVisibility(View.GONE);
//        etHp.setVisibility(View.GONE);
//        etAlamatDomisili.setVisibility(View.GONE);
//
//        ivPhoto.setEnabled(false);
//
//        tvPilihan.setVisibility(View.GONE);
//        spinnerPilihan.setVisibility(View.GONE);
//        etAlamatKomisariat.setVisibility(View.GONE);
//        etBerkas.setVisibility(View.GONE);
//        btnUbah.setVisibility(View.GONE);
//        btnTolak.setVisibility(View.GONE);
//        btnSetuju.setVisibility(View.GONE);
//
//        btnSimpanProfile.setVisibility(View.GONE);
//        btnPengajuan.setVisibility(View.GONE);
//
//        tilBirthdate.setVisibility(View.GONE);
//        tilBirthplace.setVisibility(View.GONE);
//        tilStatus.setVisibility(View.GONE);
//
//        rgGender.setVisibility(View.GONE);
//        rlformpengajuan.setVisibility(View.GONE);
////        tillPassword.setVisibility(View.VISIBLE);
////        tillPasswordBaru.setVisibility(View.VISIBLE);
////        tilConfirmPassword.setVisibility(View.VISIBLE);
//    }
//
//    private void profile(User user){
//
//        urlImage = user.getImage();
//        if (urlImage != null){
//            Glide.with(ProfilActivity.this)
//                    .load(Uri.parse(urlImage))
//                    .into(ivPhoto);
//        }
//
//        String namaLevel = CoreApplication.get().getAppDb().interfaceDao().getNamaLevel(user.getId_roles());
//        tujuan_pengajuan = Constant.getLevelUser()+1;
//
//        tvName.setText(user.getNama_depan()+" "+user.getNama_belakang());
//        tvAdmin.setText(namaLevel);
//        etNamaDepan.setText(user.getNama_depan());
//        etNamaBelakang.setText(user.getNama_belakang());
//        etPanggilan.setText(user.getNama_panggilan());
//        etJenisKelamin.setText(user.getGender());
//        etHp.setText(user.getNomor_hp());
//        etAlamatDomisili.setText(user.getAlamat());
//        etTempatLahir.setText(user.getTempat_lahir());
//        etTanggal.setText(user.getTanggal_lahir());
//        etStatus.setText(user.getStatus_perkawinan());
//        etBirthplace.setText(user.getTempat_lahir());
//        etBirthdate.setText(user.getTanggal_lahir());
//
////        rgGender.check(rgGender.getChildAt(user.getJenis_kelamin()).getId());
//
//        String status = user.getStatus_perkawinan();
//        RadioButton rbSudah =(RadioButton)findViewById(R.id.radiosudah);
//        RadioButton rbBelum =(RadioButton)findViewById(R.id.radiobelum);
//
//        if (status != null) {
//            if (status.toLowerCase().contains("belum")) {
//                rbBelum.setChecked(true);
//            } else if (status.toLowerCase().contains("sudah")) {
//                rbSudah.setChecked(true);
//            } else {
//                rbBelum.setChecked(true);
//            }
//        }
//
//
//        if (isEdit) {
//            btnPengajuan.setText("Pengajuan "+CoreApplication.get().getAppDb().interfaceDao().getNamaLevel(tujuan_pengajuan));
//            tvPengajuan.setText("Pengajuan "+CoreApplication.get().getAppDb().interfaceDao().getNamaLevel(tujuan_pengajuan));
//        }
//    }
//
//    private void getData(){
//        Call<ProfileResponse> call = service.getProfile(CoreApplication.get().getConstant().getToken());
//        call.enqueue(new Callback<ProfileResponse>() {
//            @Override
//            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
//                if (response.isSuccessful()){
//                    if (response.body().getStatus().equalsIgnoreCase("ok")){
//                        profile(response.body().getData());
//                        CoreApplication.get().getAppDb().interfaceDao().insertUser(response.body().getData());
//                        saveProfiletoContact();
//                    } else {
//                        Toast.makeText(ProfilActivity.this, "Gagal Load Data", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(ProfilActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ProfileResponse> call, Throwable t) {
//                Toast.makeText(ProfilActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//    private void saveProfiletoContact(){
//        User user = Constant.getUser();
//
//        int id_level = CoreApplication.get().getAppDb().interfaceDao().getPengajuanLevel(user.getId_roles());
//        Contact contact = new Contact(user.get_id(), user.getBadko(), user.getCabang(), user.getKorkom(), user.getKomisariat(),
//                user.getId_roles(), user.getImage(), user.getNama_depan(), user.getNama_belakang(), user.getNama_panggilan(),
//                user.getJenis_kelamin(), user.getNomor_hp(), user.getAlamat(), user.getUsername(), user.getTanggal_daftar(),
//                user.getTempat_lahir(), user.getTanggal_lahir(), user.getStatus_perkawinan(), user.getKeterangan(), user.getDevice_name(),
//                user.getLast_login(), user.getStatus_online(), id_level);
//
////        Contact contact= new Contact(user.get_id(), user.getNama_depan(), user.getNama_belakang(), user.getNama_panggilan(),
////                user.getNomor_hp(), user.getAlamat(), user.getId_fakultas(), user.getId_kampus(), user.getId_kota(), user.getId_provinsi(),
////                user.getId_roles(), user.getId_wilayah(),  user.getImage(), user.getJenis_kelamin(),
////                CoreApplication.get().getAppDb().interfaceDao().getPengajuanLevel(user.getId_roles()),
////                user.getUsername(), user.getTanggal_daftar(), user.getLast_login(), user.getDevice_name(),
////                user.getStatus_online(), user.getTempat_lahir(), user.getTanggal_lahir(), user.getStatus_perkawinan(),
////                user.getKeterangan());
////        contact.set_id(user.get_id());
//
//        CoreApplication.get().getAppDb().interfaceDao().insertContact(contact);
//    }
//
//    @OnClick(R.id.btn_simpan_profile)
//    public void goSimpanProfile(){
//        int selectId = rgStatus.getCheckedRadioButtonId();
//        RadioButton rb = (RadioButton) findViewById(selectId);
//
//        String status = rb.getText().toString();
//        int gender = rgGender.indexOfChild(findViewById(rgGender.getCheckedRadioButtonId()));
//        String namaDepan = etNamaDepan.getText().toString();
//        String namaBelakang = etNamaBelakang.getText().toString();
//        String namaPanggilan = etPanggilan.getText().toString();
//        String noHp = etHp.getText().toString();
//        String alamat = etAlamatDomisili.getText().toString();
//
//        if (namaDepan.isEmpty() || namaBelakang.isEmpty() || namaPanggilan.isEmpty() || noHp.isEmpty() || alamat.isEmpty()){
//            Toast.makeText(this, "Field tidak boleh kosong!", Toast.LENGTH_SHORT).show();
//        } else {
//            if (Constant.getLevelUser() == Constant.LEVEL_NON_LK){
//                update(namaDepan, namaBelakang, namaPanggilan, noHp, alamat, String.valueOf(gender), "");
//            } else {
//                if (etTempatLahir.getText().toString().isEmpty() || etTanggal.getText().toString().isEmpty()){
//                    Toast.makeText(this, "Field tidak boleh kosong!", Toast.LENGTH_SHORT).show();
//                } else {
//                    update(namaDepan, namaBelakang, namaPanggilan, noHp, alamat, String.valueOf(gender), status);
//                }
//            }
//        }
//    }
//
//    @OnClick(R.id.btn_simpan)
//    public void goSimpan(){
//        String password = etPassword.getText().toString();
//        String password_baru = etPasswordBaru.getText().toString();
//        String confirm_passsword_baru = etConfirmPassword.getText().toString();
//
//        if (isEdit){
////            testPengajuan();
//        } else if (isChange) {
//            if (password_baru.length() >= 6) {
//                if (password_baru.equals(confirm_passsword_baru)) {
//                    changePassword(password, password_baru);
//                } else {
//                    Toast.makeText(this, "Password tidak sesuai", Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//    }
//
//    private void update (final String namaDepan, final String namaBelakang, final String namaPanggilan, final String noHp, final String alamat, final String jenis_kelamin, final String status){
//        if (Tools.isOnline(this)) {
//            dialog = new ProgressDialog(ProfilActivity.this);
//            dialog.setMessage("Update Profile...");
//            dialog.setIndeterminate(true);
//            dialog.setCancelable(false);
//            dialog.show();
//
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    User user = Constant.getUser();
//
//                    Call<ProfileResponse> call = service.updateProfile(CoreApplication.get().getConstant().getToken(), user.getId_provinsi(), user.getId_kota(), user.getId_kampus(), user.getId_fakultas(),
//                            user.getId_roles(), namaDepan, namaBelakang,
//                            namaPanggilan, jenis_kelamin, noHp, alamat, urlImage, etTempatLahir.getText().toString(), etTanggal.getText().toString(), status, "");
//                    call.enqueue(new Callback<ProfileResponse>() {
//                        @Override
//                        public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
//                            dialog.dismiss();
//                            if (response.isSuccessful()) {
//                                if (response.body().getStatus().equalsIgnoreCase("ok")) {
//                                    Toast.makeText(ProfilActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                                    setResult(Activity.RESULT_OK);
//
//                                    new Handler().postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            finish();
//                                        }
//                                    }, 1000);
//
//                                } else {
//                                    Toast.makeText(ProfilActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            } else {
//                                Toast.makeText(ProfilActivity.this, "" + response.message(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<ProfileResponse> call, Throwable t) {
//                            dialog.dismiss();
//                            Toast.makeText(ProfilActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            }, 3000);
//
//
//        } else {
//            Toast.makeText(this, "Tidak Ada Internet!", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void updatePhoto (String url){
//        if (Tools.isOnline(this)) {
//            dialog = new ProgressDialog(ProfilActivity.this);
//            dialog.setMessage("Mengganti Foto Profil...");
//            dialog.setIndeterminate(true);
//            dialog.setCancelable(false);
//            dialog.show();
//
//            User user = Constant.getUser();
//
//            Call<ProfileResponse> call = service.updateProfile(CoreApplication.get().getConstant().getToken(), user.getId_provinsi(), user.getId_kota(), user.getId_kampus(), user.getId_fakultas(),
//                    user.getId_roles(), user.getNama_depan(), user.getNama_belakang(),
//                    user.getNama_panggilan(), user.getGender(), user.getNomor_hp(), user.getAlamat(), url, user.getTempat_lahir(), user.getTanggal_lahir(), user.getStatus_perkawinan(), user.getKeterangan());
//            call.enqueue(new Callback<ProfileResponse>() {
//                @Override
//                public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
//                    dialog.dismiss();
//                    if (response.isSuccessful()) {
//                        Log.d(TAG, "onResponse: " + response.body().getStatus());
//                        if (response.body().getStatus().equalsIgnoreCase("ok")) {
////                            CoreApplication.get().getAppDb().interfaceDao().insertUser(response.body().getData());
//                            setResult(Activity.RESULT_OK);
//
//                        } else {
//                            Toast.makeText(ProfilActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(ProfilActivity.this, "" + response.message(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ProfileResponse> call, Throwable t) {
//                    dialog.dismiss();
//                    Toast.makeText(ProfilActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        } else {
//            Toast.makeText(this, "Tidak Ada Internet!", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void changePassword(String old_pass, String new_pass){
//        if (Tools.isOnline(this)) {
//            dialog = new ProgressDialog(ProfilActivity.this);
//            dialog.setMessage("Ganti Password...");
//            dialog.setIndeterminate(true);
//            dialog.setCancelable(false);
//            dialog.show();
//
//            Call<ProfileResponse> call = service.changePassword(CoreApplication.get().getConstant().getToken(), new_pass, old_pass);
//            call.enqueue(new Callback<ProfileResponse>() {
//                @Override
//                public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
//                    dialog.dismiss();
//                    if (response.isSuccessful()) {
//                        if (response.body().getStatus().equalsIgnoreCase("ok")) {
//                            Toast.makeText(ProfilActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                            CoreApplication.get().getConstant().logout();
//                            CoreApplication.get().getAppDb().interfaceDao().deleteAlamat();
//                            CoreApplication.get().getAppDb().interfaceDao().deleteAgenda();
//                            startActivity(new Intent(ProfilActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                            finish();
//                        } else {
//                            Toast.makeText(ProfilActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(ProfilActivity.this, "" + response.message(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ProfileResponse> call, Throwable t) {
//                    dialog.dismiss();
//                    Toast.makeText(ProfilActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//            });
//        } else {
//            Toast.makeText(this, "Tidak Ada Internet!", Toast.LENGTH_SHORT).show();
//        }
//
//    }
//
//    @OnClick({R.id.btn_batal})
//    public void batal(){
////        testPengajuan();
//        finish();
//    }
//
//    @OnClick(R.id.btn_ubah)
//    public void goUbah(){
//        Log.d(TAG, "goUbah: "+spinnerPilihan.getSelectedItem().toString()+" - "+spinnerPilihan.getSelectedItemPosition());
//        int id_level;
//        switch (spinnerPilihan.getSelectedItemPosition()){
//            case 0: //Non Aktif / User Biasa
//                id_level = 1;
//                break;
//            case 1: // Admin 1
//                id_level = 5;
//                break;
//            case 2: // Admin 2
//                id_level = 8;
//                break;
//            case 3: // Admin 3
//                id_level = 11;
//                break;
//            case 4: // Low Admin 1
//                id_level = 13;
//                break;
//            case 5: // Low Admin 2
//                id_level = 16;
//                break;
//            case 6: // Second Admin
//                id_level = 19;
//                break;
//            case 7: // Super Admin
//                id_level = 20;
//                break;
//            default:
//                id_level = 1;
//                break;
//
//        }
//
//        if (Tools.isOnline(this)){
//            Tools.showProgressDialog(this, "Update Admin");
//            Call<GeneralResponse> call = service.updateUserLevel(CoreApplication.get().getConstant().getToken(), user.get_id(), id_level);
//            call.enqueue(new Callback<GeneralResponse>() {
//                @Override
//                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
//                    if (response.isSuccessful()) {
//                        if (response.body().getStatus().equalsIgnoreCase("success")) {
//                            Tools.dissmissProgressDialog();
//                        } else {
//                            Toast.makeText(ProfilActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(ProfilActivity.this, "" + response.message(), Toast.LENGTH_SHORT).show();
//                    }
//                    Tools.dissmissProgressDialog();
//                    finish();
//                }
//
//                @Override
//                public void onFailure(Call<GeneralResponse> call, Throwable t) {
//
//                }
//            });
//
//        } else{
//            Toast.makeText(this, "Tidak Ada Internet!", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @OnClick(R.id.btn_pengajuan)
//    public void pengajuan(){
//        final AlertDialog dialog = new AlertDialog.Builder(this)
//                .setTitle("Pengajuan "+CoreApplication.get().getAppDb().interfaceDao().getNamaLevel(Constant.getUser().getId_roles()))
//                .setMessage("Apakah Anda Yakin ?")
//                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        int month = spBulan.getSelectedItemPosition() + 1;
//                        String bulan;
//                        if (month < 10){
//                            bulan = "0"+month;
//                        } else {
//                            bulan = String.valueOf(month);
//                        }
//                        String tahun = spTahun.getSelectedItem().toString();
//                        String tahunLK = bulan+"-"+tahun;
//                        if (!etLokasi.getText().toString().isEmpty() || !tvLokasiLk.getText().toString().toLowerCase().contains("lokasi")){
//                            pengajuanLK(String.valueOf(month), tahun, tvLokasiLk.getText().toString());
//                        } else {
//                            Toast.makeText(ProfilActivity.this, "Lokasi LK tidak boleh kosong!", Toast.LENGTH_SHORT).show();
//                        }
////                        dialogLokasi("", "", "", "");
//                        dialog.dismiss();
//                    }
//                })
//                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
////                        sendMessage();
//                        dialog.dismiss();
//                    }
//                })
//                .setCancelable(false)
//                .create();
//
//        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface arg) {
//                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorTextDark));
//                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
//            }
//        });
//        dialog.show();
//    }
//
//    @OnClick(R.id.add_medsos)
//    public void addMedos(){
//        View dialogView = getLayoutInflater().inflate(R.layout.dialog_medsos, null);
//        final Spinner spMedsos = dialogView.findViewById(R.id.sp_medsos);
//        final EditText etUsername = dialogView.findViewById(R.id.et_username);
//
//        final AlertDialog dialog = new AlertDialog.Builder(this)
//                .setView(dialogView)
//                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        addDataMedsos(String.valueOf(spMedsos.getSelectedItem().toString()), etUsername.getText().toString());
//                    }
//                })
//                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .setCancelable(false)
//                .create();
//
//        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface arg) {
//                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorTextDark));
//                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
//            }
//        });
//        dialog.show();
//    }
//
//    @OnClick(R.id.add_pendidikan)
//    public void addPendidikan(){
//        View dialogView = getLayoutInflater().inflate(R.layout.dialog_pendidikan, null);
//        final EditText etTahun = dialogView.findViewById(R.id.et_thn_masuk);
//        final EditText etJenjang = dialogView.findViewById(R.id.et_jenjang);
//        final Spinner spJenjang = dialogView.findViewById(R.id.sp_jenjang);
//        final Spinner spUniversitas = dialogView.findViewById(R.id.sp_universitas);
//        final Spinner spFakultas = dialogView.findViewById(R.id.sp_fakultas);
////        final EditText etNama = dialogView.findViewById(R.id.et_nama);
////        final EditText etJurusan = dialogView.findViewById(R.id.et_jurusan);
//
//        final AlertDialog dialog = new AlertDialog.Builder(this)
//                .setView(dialogView)
//                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        addDataPendidikan(etTahun.getText().toString(), String.valueOf(spJenjang.getSelectedItem()), spUniversitas.getSelectedItem().toString(), spFakultas.getSelectedItem().toString());
////                        addDataPendidikan(etTahun.getText().toString(), String.valueOf(spJenjang.getSelectedItem()), etNama.getText().toString(), etJurusan.getText().toString());
//                    }
//                })
//                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .setCancelable(false)
//                .create();
//
//        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface arg) {
//                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorTextDark));
//                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
//            }
//        });
//        dialog.show();
//    }
//
//    @OnClick({R.id.add_training})
//    public void addTraining(){
//        View dialogView = getLayoutInflater().inflate(R.layout.dialog_training, null);
//        final EditText etTipe = dialogView.findViewById(R.id.etTipe);
//        final Spinner spTipe = dialogView.findViewById(R.id.sp_training);
//        final EditText etTahun = dialogView.findViewById(R.id.etTahun);
//        final EditText etNama = dialogView.findViewById(R.id.etNama);
//        final EditText etLokasi = dialogView.findViewById(R.id.etLokasi);
//        final AlertDialog dialog = new AlertDialog.Builder(this)
//                .setView(dialogView)
//                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        addDataTraining(String.valueOf(spTipe.getSelectedItem()), etTahun.getText().toString(), etNama.getText().toString(), etLokasi.getText().toString());
////                        trainings.add(new Training(trainings.size(), etTipe.getText().toString(), etTahun.getText().toString(), etNama.getText().toString(), etLokasi.getText().toString()));
////                        Log.d("tessss", "onClick: "+trainings.get(0).getNama());
////                        trainingAdapter.updateData(trainings);
//                        dialog.dismiss();
//                    }
//                })
//                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .setCancelable(false)
//                .create();
//
//        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface arg) {
//                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorTextDark));
//                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
//            }
//        });
//        dialog.show();
//    }
//
//    @OnClick({R.id.add_pekerjaan})
//    public void addPekerjaan(){
//        View dialogView = getLayoutInflater().inflate(R.layout.dialog_pekerjaan, null);
//        final EditText etThnMsk = dialogView.findViewById(R.id.etThnMsk);
//        final EditText etThnKluar = dialogView.findViewById(R.id.etThnKluar);
//        final EditText etNama = dialogView.findViewById(R.id.etNama);
//        final EditText etJabatan = dialogView.findViewById(R.id.etJabatan);
//        final EditText etAlamat = dialogView.findViewById(R.id.etAlamat);
//        final AlertDialog dialog = new AlertDialog.Builder(this)
//                .setView(dialogView)
//                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        addDataPekerjaan(etNama.getText().toString(), etJabatan.getText().toString(), etAlamat.getText().toString(), etThnMsk.getText().toString(), etThnKluar.getText().toString());
////                        dialog.dismiss();
//                    }
//                })
//                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .setCancelable(false)
//                .create();
//
//        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface arg) {
//                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorTextDark));
//                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
//            }
//        });
//        dialog.show();
//    }
//
//    private void expand() {
//        //set Visible
//        mLinearLayoutExpand.setVisibility(View.VISIBLE);
//
//        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//        mLinearLayoutExpand.measure(widthSpec, heightSpec);
//
//        ValueAnimator mAnimator = slideAnimator(0, mLinearLayoutExpand.getMeasuredHeight());
//        mAnimator.start();
//    }
//
//    private void collapse() {
//        int finalHeight = mLinearLayoutExpand.getHeight();
//
//        ValueAnimator mAnimator = slideAnimator(finalHeight, 0);
//
//        mAnimator.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                mLinearLayoutExpand.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
//
//        mAnimator.start();
//    }
//
//    private ValueAnimator slideAnimator(int start, int end) {
//
//        ValueAnimator animator = ValueAnimator.ofInt(start, end);
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                //Update Height
//                int value = (Integer) valueAnimator.getAnimatedValue();
//                ViewGroup.LayoutParams layoutParams = mLinearLayoutExpand.getLayoutParams();
//                layoutParams.height = value;
//                mLinearLayoutExpand.setLayoutParams(layoutParams);
//            }
//        });
//        return animator;
//    }
//
//    private void pengajuanLK(String bulanLk, String tahunLk, String lokasiLk){
//        if (Tools.isOnline(this)) {
//            Tools.showProgressDialog(this, "Sedang melakukan pengajuan...");
//
//            HashMap<String, String> params = new HashMap<>();
//            params.put("bulan_lk", bulanLk);
//            params.put("tahun_lk", tahunLk);
//            params.put("tempat_lk", lokasiLk);
//            params.put("tujuan_pengajuan", String.valueOf(tujuan_pengajuan));
//            params.put("status", "1");
//
//            Call<GeneralResponse> call = service.pengajuanUser(CoreApplication.get().getConstant().getToken(), params);
//            call.enqueue(new Callback<GeneralResponse>() {
//                @Override
//                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
//                    Tools.dissmissProgressDialog();
//                    if (response.isSuccessful()) {
//                        Toast.makeText(ProfilActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(ProfilActivity.this, "" + response.message(), Toast.LENGTH_SHORT).show();
//                    }
//                    finish();
//                }
//
//                @Override
//                public void onFailure(Call<GeneralResponse> call, Throwable t) {
//                    Tools.dissmissProgressDialog();
//                    Toast.makeText(ProfilActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//            });
//        } else {
//            Toast.makeText(this, "Tidak Ada Internet!", Toast.LENGTH_SHORT).show();
//        }
//    }
//
////    private void testPengajuan(){
////        JsonObject object;
////        JsonArray pendidikan = new JsonArray();
////
////        for (int i = 0; i < 3; i++) {
////            object = new JsonObject();
////            object.addProperty("tahun", "2017");
////            object.addProperty("strata", "S1");
////            object.addProperty("kampus", "kampus "+i);
////            pendidikan.add(object);
////        }
////
////        JsonObject req = new JsonObject();
////        req.addProperty("nama_lengkap", "romaa");
////        req.addProperty("nama_panggilan", "samamor");
////        req.add("kuliah", pendidikan);
////
////        HashMap<String, String> params = new HashMap<>();
////        params.put("nama_lengkap", "romaa");
////        params.put("nama_panggilan", "samamor");
////        params.put("kuliah", pendidikan.toString());
////
////        Call<GeneralResponse> call = service.pengajuanUser(CoreApplication.get().getConstant().getToken(), params);
////        call.enqueue(new Callback<GeneralResponse>() {
////            @Override
////            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
////                if (response.isSuccessful()){
////                    Log.d("testPengajuan", "onResponse: sukses "+response.body().getMessage());
////                } else {
////                    Log.d("testPengajuan", "onResponse: !sukses "+response.message());
////                }
////            }
////
////            @Override
////            public void onFailure(Call<GeneralResponse> call, Throwable t) {
////                Log.d("testPengajuan", "onResponse: failure "+t.getMessage());
////            }
////        });
////    }
//
//    @OnClick(R.id.et_tanggal)
//    public void goTanggal(){
//        showDateDialog();
//    }
//
//    private void showDateDialog(){
//
//        /**
//         * Calendar untuk mendapatkan tanggal sekarang
//         */
//        Calendar newCalendar = Calendar.getInstance();
//
//        /**
//         * Initiate DatePicker dialog
//         */
//        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//
//                /**
//                 * Method ini dipanggil saat kita selesai memilih tanggal di DatePicker
//                 */
//
//                /**
//                 * Set Calendar untuk menampung tanggal yang dipilih
//                 */
//                Calendar newDate = Calendar.getInstance();
//                newDate.set(year, monthOfYear, dayOfMonth);
//
//                /**
//                 * Update TextView dengan tanggal yang kita pilih
//                 */
//                etTanggal.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(newDate.getTime()));
//            }
//
//        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
//
//        /**
//         * Tampilkan DatePicker dialog
//         */
//        datePickerDialog.show();
//    }
//
//    private void dialogLokasi (final String badko, final String cabang, final String korkom, final String nama){
//        View dialogView = getLayoutInflater().inflate(R.layout.dialog_komisariat, null);
//        final CardView cvBadko = dialogView.findViewById(R.id.cv_badko);
//        final CardView cvCabang = dialogView.findViewById(R.id.cv_cabang);
//        final CardView cvKorkom = dialogView.findViewById(R.id.cv_korkom);
//        final CardView cvNama = dialogView.findViewById(R.id.cv_nama);
//
//        if (badko.isEmpty()){
//            cvCabang.setVisibility(View.GONE);
//            cvKorkom.setVisibility(View.GONE);
//            cvNama.setVisibility(View.GONE);
//        } else if (cabang.isEmpty()){
//            cvKorkom.setVisibility(View.GONE);
//            cvNama.setVisibility(View.GONE);
//        } else if (korkom.isEmpty()){
//            cvNama.setVisibility(View.GONE);
//        }
//
//        final AlertDialog dialog = new AlertDialog.Builder(this)
//                .setTitle("Dialog Lokasi")
//                .setView(dialogView)
//                .setPositiveButton("Simpan", null)
//                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .setCancelable(false)
//                .show();
//
//        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
//        positiveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (badko.isEmpty() || cabang.isEmpty() || korkom.isEmpty() || nama.isEmpty()){
//                    Toast.makeText(ProfilActivity.this, "Masih ada field kosong", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(ProfilActivity.this, "Oke", Toast.LENGTH_SHORT).show();
//                    dialog.dismiss();
//                }
//            }
//        });
//
//
//
//        cvBadko.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogProvinsi();
//                dialog.dismiss();
//            }
//        });
//        cvCabang.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogCabang();
//                dialog.dismiss();
//            }
//        });
//        cvKorkom.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogKorkom();
//                dialog.dismiss();
//            }
//        });
//        cvNama.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogNama();
//                dialog.dismiss();
//            }
//        });
//    }
//
//    private void dialogProvinsi(){
//        final List<String> stringList = new ArrayList<>();
//        View view=getLayoutInflater().inflate(R.layout.dialog_list_scroll, null);
//        final EditText etSearch = view.findViewById(R.id.et_search);
//        final RecyclerView rvList = view.findViewById(R.id.rv_list);
//        final ProgressBar progressBar = view.findViewById(R.id.progress_bar);
//        progressBar.setVisibility(View.VISIBLE);
//
//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setTitle("Dialog Provinsi")
//                .setView(view)
//                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialogLokasi("Jawa Timur", "","","");
//                        dialog.dismiss();
//                    }
//                })
//                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .setCancelable(false)
//                .create();
//        dialog.show();
//
////        Rect displayRectangle = new Rect();
////        Window window = getWindow();
////
////        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
////        dialog.getWindow().setLayout((int)(displayRectangle.width() *
////                0.9f), (int)(displayRectangle.height() * 0.5f));
//
//        Call<ProvinsiResponse> call = service.getProvinsi();
//        call.enqueue(new Callback<ProvinsiResponse>() {
//            @Override
//            public void onResponse(Call<ProvinsiResponse> call, final Response<ProvinsiResponse> response) {
//                progressBar.setVisibility(View.GONE);
//                if (response.isSuccessful()){
//                    if (response.body().getStatus().equalsIgnoreCase("ok")) {
//                        if (response.body().getData().size() > 0) {
//                            for (int i = 0; i < response.body().getData().size(); i++) {
//                                stringList.add(response.body().getData().get(i).getNama());
//                            }
//                            final TempatAdapter adapter = new TempatAdapter(getApplicationContext(), stringList, new TempatAdapter.itemClickListener() {
//                                @Override
//                                public void onItemClick(String nama) {
//                                    Toast.makeText(ProfilActivity.this, ""+nama, Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                            rvList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//                            rvList.setAdapter(adapter);
//                            etSearch.addTextChangedListener(new TextWatcher() {
//                                @Override
//                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                                }
//
//                                @Override
//                                public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                                }
//
//                                @Override
//                                public void afterTextChanged(Editable s) {
//                                    adapter.updateData(filter(s.toString(), response.body().getData()));
//                                }
//                            });
//                        }
//                    } else {
//                        Toast.makeText(ProfilActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(ProfilActivity.this, ""+response.message(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ProvinsiResponse> call, Throwable t) {
//                Toast.makeText(ProfilActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private List<String> filter(String text, List<Provinsi> list){
//        ArrayList<Provinsi> filterList = new ArrayList<>();
//        ArrayList<String> filStrings = new ArrayList<>();
//        for (Provinsi item : list){
//            if (item.getNama().toLowerCase().contains(text.toLowerCase())){
//                filterList.add(item);
//                filStrings.add(item.getNama());
//            }
//        }
//        return filStrings;
//    }
//
//    private void dialogCabang(){
//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setTitle("Dialog Provinsi")
//                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialogLokasi("Jawa Timur", "Malang","","");
//                        dialog.dismiss();
//                    }
//                })
//                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .setCancelable(false)
//                .create();
//        dialog.show();
//    }
//
//    private void dialogKorkom(){
//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setTitle("Dialog Korkom")
//                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialogLokasi("Jawa Timur", "Malang","Blimbing","");
//                        dialog.dismiss();
//                    }
//                })
//                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .setCancelable(false)
//                .create();
//        dialog.show();
//    }
//
//    private void dialogNama(){
//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setTitle("Dialog Nama")
//                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialogLokasi("Jawa Timur", "Malang","Blimbing","Nama");
//                        dialog.dismiss();
//                    }
//                })
//                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .setCancelable(false)
//                .create();
//        dialog.show();
//    }
//
//    private void dialogtanya(){
//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setTitle("Apakah anda telah LK1 ?")
//                .setPositiveButton("Sudah", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(ProfilActivity.this, "Silakan lakukan pengajuan LK1", Toast.LENGTH_SHORT).show();
//                        rlformpengajuan.setVisibility(View.VISIBLE);
//                        llPengajuanLK1.setVisibility(View.VISIBLE);
//                        btnPengajuan.setVisibility(View.VISIBLE);
//                        Log.d(TAG, "onClick: tujuanPengajuan "+tujuan_pengajuan);
//                        btnPengajuan.setText("Pengajuan "+CoreApplication.get().getAppDb().interfaceDao().getNamaLevel(tujuan_pengajuan));
//                    }
//                })
//                .setNegativeButton("Belum", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        llPengajuanLK1.setVisibility(View.GONE);
//                        tvPengajuan.setVisibility(View.GONE);
//                    }
//                }).show();
//    }
//
//    private void sendMessage(String apiKey, String token, String caption, String params){
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//
//        try {
//            Log.d(TAG, "sendMessage: "+params);
//            JSONObject mainObject = new JSONObject();
//            JSONObject notifObject = new JSONObject();
//            JSONObject dataObject = new JSONObject();
//
//            notifObject.put("body", caption);
//            notifObject.put("title", "testDevice");
//            notifObject.put("priority", "high");
//
//            dataObject.put("params", "testDeviceBoss");
//
//
//            mainObject.put("to", "djumT9FqkC4:APA91bFXStmAPs2fI1kDwS2HDhMobEoCBIUnhZSdtxIzAlIJlKR_Ej2EwO9T5oe9y2ue4Ubqoo8ne-sjefxfTlYOHnJ5XaGX2o61w4PWWyVkMKfrEJUHwxoOfwu0jpz9x3kNyEmq9S7j");
//            mainObject.put("notification", notifObject);
//            mainObject.put("data", dataObject);
//            mainObject.put("priority", "high");
//
//            Log.d(TAG, "initContent: "+mainObject.toString());
//
//
//
//            OkHttpClient client = new OkHttpClient();
//
//            MediaType mediaType = MediaType.parse("application/json");
//            RequestBody requestBody = RequestBody.create(mediaType, mainObject.toString());
//
//            Request request = new Request.Builder()
//                    .url("https://fcm.googleapis.com/fcm/send")
//                    .post(requestBody)
//                    .addHeader("Content-Type", "application/json")
//                    .addHeader("Authorization", "key=AIzaSyAm6PtzRM_vlc8x_bxEJB8rh-LMlN6c0tw")
//                    .build();
//            okhttp3.Response response = client.newCall(request).execute();
//            Log.d(TAG, "initContent: "+response.body().string());
//        } catch (JSONException | IOException e) {
//            e.printStackTrace();
//        }
//    }
//
////    private void sendMessage(){
////        Log.d(TAG, "sendMessage: ");
////        SendNotifService sendNotifService = CoreApplication.get().getClient("https://fcm.googleapis.com/").create(SendNotifService.class);
////        Data data = new Data("Coba_Kirim","Kirim melalui device");
////
////        Sender sender = new Sender(data, "eMtu7CYBU6w:APA91bG4IpyiP9ebG4KsZKyBRMdBhi938B8S3QdPUzdEwpisKgatP3qAZBGKq8hzxbnrTcKU_MglJhjL3xYSsvXOKEo9q8uyywSq-UOV2bYds3dZxPoSok0-8nm9_gSHsK5FtTCWeE57");
////
////        sendNotifService.sendNotification(sender)
////                .enqueue(new Callback<SendNotifResponse>() {
////                    @Override
////                    public void onResponse(Call<SendNotifResponse> call, Response<SendNotifResponse> response) {
////                        Log.d(TAG, "onResponse: "+response.code());
////                        if (response.code() == 200){
////                            if (response.body().success != 1){
////                                Toast.makeText(ProfilActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
////                            } else {
////                                Toast.makeText(ProfilActivity.this, "Sukses", Toast.LENGTH_SHORT).show();
////                            }
////                        }
////                    }
////
////                    @Override
////                    public void onFailure(Call<SendNotifResponse> call, Throwable t) {
////                        Log.d(TAG, "onFailure: "+t.getMessage());
////                        Toast.makeText(ProfilActivity.this, "Failure", Toast.LENGTH_SHORT).show();
////
////                    }
////                });
////    }
//
//    private void initViewBulan(){
//        String[] bulan= new String[]{
//                "Jan",
//                "Feb",
//                "Mar",
//                "Apr",
//                "May",
//                "Jun",
//                "Jul",
//                "Aug",
//                "Sep",
//                "Oct",
//                "Nov",
//                "Dec"
//        };
//        final List<String> list = new ArrayList<>(Arrays.asList(bulan));
//        final ArrayAdapter<String> adapterSpinnerBulan = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, list);
//        spBulan.setAdapter(adapterSpinnerBulan);
//    }
//
//    private void initViewTahun(){
//        List<String> list = new ArrayList<>();
//        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
//        for (int i = thisYear; i>= 1947; i--) {
//            list.add(String.valueOf(i));
//        }
//        final ArrayAdapter<String> adapterSpinnerTahun = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, list);
//        spTahun.setAdapter(adapterSpinnerTahun);
//    }
//
//    // change photo
//
//    private void selectImage(){
//        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
//        openGalleryIntent.setType("image/*");
//        startActivityForResult(openGalleryIntent, Constant.REQUEST_GALLERY_CODE);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, ProfilActivity.this);
//    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == Constant.REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
//            uri = data.getData();
//            if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                String filePath = getRealPathFromURIPath(uri, ProfilActivity.this);
//                File file = new File(filePath);
//                Uri imageUri = CropImage.getPickImageResultUri(this, data);
//                if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
//                    uri = imageUri;
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
//                    }
//                }
//                startCropImageActivity(imageUri);
//
//            } else {
//                EasyPermissions.requestPermissions(this, "Aplikasi ini butuh Akses Penyimpanan", Constant.READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
//            }
//        } if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                File compFile = null;
//                try {
//                    compFile = new Compressor(this).compressToFile(new File(Objects.requireNonNull(result.getUri().getPath())));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                Uri comUri = Uri.fromFile(compFile);
//                String filePath = getRealPathFromURIPath(comUri, ProfilActivity.this);
//                File file = new File(filePath);
//                Log.d(TAG, "onActivityResult: halloword uri 6 "+comUri);
//                uploadImage(file);
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
//            }
//        }
//
//        if (requestCode == LokasiActivity.REQUEST_LOKASI){
//            Log.d(TAG, "onActivityResult: lokasi "+resultCode);
//            if (resultCode == Activity.RESULT_OK){
//                String result=data.getStringExtra(LokasiActivity.NAMA_LOKASI);
//                if (!result.isEmpty()){
//                    tvLokasiLk.setText(result);
//                }
//                Log.d(TAG, "onActivityResult: "+result);
//            }
//        }
//
//        if (requestCode == Constant.REQUEST_GANTI_POTO && resultCode == Activity.RESULT_OK){
//            getData();
//        }
//
//
//    }
//
//    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
//        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
//        if (cursor == null) {
//            return contentURI.getPath();
//        } else {
//            cursor.moveToFirst();
//            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//            return cursor.getString(idx);
//        }
//    }
//
//    @Override
//    public void onPermissionsGranted(int requestCode, List<String> perms) {
//        if(uri != null){
//            String filePath = getRealPathFromURIPath(uri, ProfilActivity.this);
//            File file = new File(filePath);
//            RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
//            Log.d(TAG, "onPermissionsGranted: halloword "+file.getName()+" - "+mFile);
////            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
////            RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
////            Retrofit retrofit = new Retrofit.Builder()
////                    .baseUrl(SERVER_PATH)
////                    .addConverterFactory(GsonConverterFactory.create())
////                    .build();
////            UploadImageInterface uploadImage = retrofit.create(UploadImageInterface.class);
////            Call<UploadObject> fileUpload = uploadImage.uploadFile(fileToUpload, filename);
////            fileUpload.enqueue(new Callback<UploadObject>() {
////                @Override
////                public void onResponse(Call<UploadObject> call, Response<UploadObject> response) {
////                    Toast.makeText(ProfilActivity.this, "Success " + response.message(), Toast.LENGTH_LONG).show();
////                    Toast.makeText(ProfilActivity.this, "Success " + response.body().toString(), Toast.LENGTH_LONG).show();
////                }
////                @Override
////                public void onFailure(Call<UploadObject> call, Throwable t) {
////                    Log.d(TAG, "Error " + t.getMessage());
////                }
////            });
//        }
//    }
//    @Override
//    public void onPermissionsDenied(int requestCode, List<String> perms) {
//        Log.d(TAG, "Permission has been denied");
//    }
//
//    private void startCropImageActivity(Uri imageUri) {
//        CropImage.activity(imageUri)
//                .setGuidelines(CropImageView.Guidelines.ON)
//                .setAspectRatio(1, 1)
//                .setMultiTouchEnabled(true)
//                .start(this);
//    }
//
//    private void uploadImage(File file) {
//        if (Tools.isOnline(this)) {
//            Tools.showProgressDialog(ProfilActivity.this, "Mengungah Foto...");
//
//            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
//            Map<String, RequestBody> map = new HashMap<>();
//            map.put("files\"; filename=\"" + file.getName() + "\"", requestBody);
//
//            // finally, execute the request
//            Call<UploadFileResponse> call = service.uploadFile(CoreApplication.get().getConstant().getToken(), map);
//            call.enqueue(new Callback<UploadFileResponse>() {
//                @Override
//                public void onResponse(Call<UploadFileResponse> call, Response<UploadFileResponse> response) {
//                    if (response.isSuccessful()) {
//                        if (response.body().getStatus().equalsIgnoreCase("ok")) {
//                            Log.d("uploadFile", "onResponse: ok");
//                            Toast.makeText(ProfilActivity.this, "Sukses Upload File", Toast.LENGTH_SHORT).show();
//                            String url = response.body().getData().get(0).getUrl();
//
//                            urlImage = url;
//                            Glide.with(ProfilActivity.this)
//                                    .load(Uri.parse(url))
//                                    .into(ivPhoto);
//
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    updatePhoto(urlImage);
//                                }
//                            }, 500);
//                        } else {
//                            Toast.makeText(ProfilActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(ProfilActivity.this, "" + response.message(), Toast.LENGTH_SHORT).show();
//                    }
//                    Tools.dissmissProgressDialog();
//                }
//
//                @Override
//                public void onFailure(Call<UploadFileResponse> call, Throwable t) {
//                    Tools.dissmissProgressDialog();
//                    Toast.makeText(ProfilActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        } else {
//            Toast.makeText(this, "Tidak Ada Internet!", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void getDataMedsos(){
//        Call<MedsosResponse> call = service.getMedsos(CoreApplication.get().getConstant().getToken());
//        call.enqueue(new Callback<MedsosResponse>() {
//            @Override
//            public void onResponse(Call<MedsosResponse> call, Response<MedsosResponse> response) {
//                if (response.isSuccessful()) {
//                    if (response.body().getStatus().equalsIgnoreCase("success")) {
//                        Log.d(TAG, "onResponse: success medsos");
//                        medsosAdapter.updateData(response.body().getData());
//                        if (response.body().getData().size() == 0 && !isEdit){
//                            dialogProfile(true);
//                        }
//                    }
//                } else {
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<MedsosResponse> call, Throwable t) {
//
//            }
//        });
//    }
//
//    private void getDataPendidikan(){
//        Call<PendidikanResponse> call = service.getPendidikan(CoreApplication.get().getConstant().getToken());
//        call.enqueue(new Callback<PendidikanResponse>() {
//            @Override
//            public void onResponse(Call<PendidikanResponse> call, Response<PendidikanResponse> response) {
//                if (response.isSuccessful()) {
//                    if (response.body().getStatus().equalsIgnoreCase("success")) {
//                        Log.d(TAG, "onResponse: success pendidikan");
//                        pendidikanAdapter.updateData(response.body().getData());
//                        if (response.body().getData().size() == 0 && !isEdit){
//                            dialogProfile(true);
//                        } else {
//                            for (int i = 0; i < response.body().getData().size(); i++) {
//                                int maxYear = Calendar.getInstance().get(Calendar.YEAR)-4;
//                                int pos = response.body().getData().size()+1;
//                                Log.d(TAG, "onResponse: "+response.body().getData().get(0).getTahun()+" - "+maxYear);
//                                if (Integer.valueOf(response.body().getData().get(0).getTahun()) <= maxYear){
//                                    getDataPekerjaan();
////                                    dialogAlumni();
//                                } else {
//                                    ll4thn.setVisibility(View.GONE);
//                                }
//                            }
//                        }
//                    }
//                } else {
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<PendidikanResponse> call, Throwable t) {
//
//            }
//        });
//    }
//
//    private void getDataTraining(){
//        Call<TrainingResponse> call = service.getTraining(CoreApplication.get().getConstant().getToken());
//        call.enqueue(new Callback<TrainingResponse>() {
//            @Override
//            public void onResponse(Call<TrainingResponse> call, Response<TrainingResponse> response) {
//                if (response.isSuccessful()) {
//                    if (response.body().getStatus().equalsIgnoreCase("success")) {
//                        Log.d(TAG, "onResponse: success training");
//                        trainingAdapter.updateData(response.body().getData());
//                        if (response.body().getData().size() == 0 && !isEdit){
//                            dialogProfile(true);
//                        }
//                    }
//                } else {
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<TrainingResponse> call, Throwable t) {
//
//            }
//        });
//    }
//
//    private void getDataPekerjaan(){
//        Call<JobResponse> call = service.getJob(CoreApplication.get().getConstant().getToken());
//        call.enqueue(new Callback<JobResponse>() {
//            @Override
//            public void onResponse(Call<JobResponse> call, Response<JobResponse> response) {
//                if (response.isSuccessful()) {
//                    if (response.body().getStatus().equalsIgnoreCase("success")) {
//                        Log.d(TAG, "onResponse: success pekerjaan");
//                        jobAdapter.updateData(response.body().getData());
//                        if (response.body().getData().size() == 0){
//                            if (!isEdit) {
//                                dialogProfile(true);
//                            } else {
//                                dialogAlumni();
//                            }
//                        } else {
//                            ll4thn.setVisibility(View.VISIBLE);
//                        }
//                    }
//                } else {
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JobResponse> call, Throwable t) {
//
//            }
//        });
//    }
//
//    private void addDataMedsos(String medsos, String username){
//        if (!medsos.isEmpty() && !username.isEmpty()) {
//            Call<GeneralResponse> call = service.addMedsos(CoreApplication.get().getConstant().getToken(), medsos, username);
//            call.enqueue(new Callback<GeneralResponse>() {
//                @Override
//                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
//                    if (response.isSuccessful()) {
//                        if (response.body().getStatus().equalsIgnoreCase("success")) {
//                            Log.d(TAG, "onResponse: addDataMedsos sukses");
//                            getDataMedsos();
//                        }
//                    } else {
//
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<GeneralResponse> call, Throwable t) {
//
//                }
//            });
//        } else {
//            Toast.makeText(this, "Gagal Input Data, Field harus diisi semua", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void addDataPendidikan(String tahun, String strata, String kampus, String jurusan){
//        if (!tahun.isEmpty() && !strata.isEmpty() && !kampus.isEmpty() && !jurusan.isEmpty()) {
//            user.setId_kampus(kampus);
//            CoreApplication.get().getAppDb().interfaceDao().insertUser(user);
//            Call<GeneralResponse> call = service.addPendidikan(CoreApplication.get().getConstant().getToken(), tahun, strata, kampus, jurusan);
//            call.enqueue(new Callback<GeneralResponse>() {
//                @Override
//                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
//                    if (response.isSuccessful()) {
//                        if (response.body().getStatus().equalsIgnoreCase("success")) {
//                            Log.d(TAG, "onResponse: addPendidiakn sukses");
//                            getDataPendidikan();
//                        }
//                    } else {
//
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<GeneralResponse> call, Throwable t) {
//
//                }
//            });
//        } else {
//            Toast.makeText(this, "Gagal Input Data, Field harus diisi semua", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void addDataTraining(String tipe, String tahun, String nama, String lokasi){
//        if (!tipe.isEmpty() && !tahun.isEmpty() && !nama.isEmpty() && !lokasi.isEmpty()) {
//            Call<GeneralResponse> call = service.addTraining(CoreApplication.get().getConstant().getToken(), tipe, tahun, nama, lokasi);
//            call.enqueue(new Callback<GeneralResponse>() {
//                @Override
//                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
//                    if (response.isSuccessful()) {
//                        if (response.body().getStatus().equalsIgnoreCase("success")) {
//                            Log.d(TAG, "onResponse: addTraining sukses");
//                            getDataTraining();
//                        }
//                    } else {
//
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<GeneralResponse> call, Throwable t) {
//
//                }
//            });
//        } else {
//            Toast.makeText(this, "Gagal Input Data, Field harus diisi semua", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void addDataPekerjaan(String nama, String jabatan, String alamat, String tahun_mulai, String tahun_berakhir){
//        if (!nama.isEmpty() && !jabatan.isEmpty() && !alamat.isEmpty() && !tahun_mulai.isEmpty() && !tahun_berakhir.isEmpty()) {
//            Call<GeneralResponse> call = service.addJob(CoreApplication.get().getConstant().getToken(), nama, jabatan, alamat, tahun_mulai, tahun_berakhir);
//            call.enqueue(new Callback<GeneralResponse>() {
//                @Override
//                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
//                    if (response.isSuccessful()) {
//                        if (response.body().getStatus().equalsIgnoreCase("success")) {
//                            Log.d(TAG, "onResponse: addPekerjaan sukses");
//                            getDataPekerjaan();
//                        }
//                    } else {
//
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<GeneralResponse> call, Throwable t) {
//
//                }
//            });
//        } else {
//            Toast.makeText(this, "Gagal Input Data, Field harus diisi semua", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void deleteDataMedsos(String id){
//        Call<GeneralResponse> call = service.deleteMedsos(CoreApplication.get().getConstant().getToken(), id);
//        call.enqueue(new Callback<GeneralResponse>() {
//            @Override
//            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
//                if (response.isSuccessful()) {
//                    if (response.body().getStatus().equalsIgnoreCase("success")) {
//                        Log.d(TAG, "onResponse: deleteDataMedsos sukses");
//                        getDataMedsos();
//                    }
//                } else {
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GeneralResponse> call, Throwable t) {
//
//            }
//        });
//    }
//
//    private void dialogKonfirmasi(final int type, final String id){
//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setTitle("Hapus Data Ini ?")
////                .setMessage("Lengkapi Data Profile Anda!")
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (type == 1){  //medsos
//                            deleteDataMedsos(id);
//                        } else if (type == 2){ //pendidikan
//                            deleteDataPendidikan(id);
//                        } else if (type == 3) { // training
//                            deleteDataTraining(id);
//                        } else if (type == 4){ //pekerjaan
//                            deleteDataPekerjaan(id);
//                        }
//                    }
//                })
//                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                })
//                .setCancelable(false)
//                .show();
//    }
//
//    private void deleteDataPendidikan(String id){
//        Call<GeneralResponse> call = service.deletePendidikan(CoreApplication.get().getConstant().getToken(), id);
//        call.enqueue(new Callback<GeneralResponse>() {
//            @Override
//            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
//                if (response.isSuccessful()) {
//                    if (response.body().getStatus().equalsIgnoreCase("success")) {
//                        Log.d(TAG, "onResponse: deleteDataPendidikan sukses");
//                        getDataPendidikan();
//                    }
//                } else {
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GeneralResponse> call, Throwable t) {
//
//            }
//        });
//    }
//
//    private void deleteDataTraining(String id){
//        Call<GeneralResponse> call = service.deleteTraining(CoreApplication.get().getConstant().getToken(), id);
//        call.enqueue(new Callback<GeneralResponse>() {
//            @Override
//            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
//                if (response.isSuccessful()) {
//                    if (response.body().getStatus().equalsIgnoreCase("success")) {
//                        Log.d(TAG, "onResponse: deleteDataTraining sukses");
//                        getDataTraining();
//                    }
//                } else {
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GeneralResponse> call, Throwable t) {
//
//            }
//        });
//    }
//
//    private void deleteDataPekerjaan(String id){
//        Call<GeneralResponse> call = service.deleteJob(CoreApplication.get().getConstant().getToken(), id);
//        call.enqueue(new Callback<GeneralResponse>() {
//            @Override
//            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
//                if (response.isSuccessful()) {
//                    if (response.body().getStatus().equalsIgnoreCase("success")) {
//                        Log.d(TAG, "onResponse: deleteDataPekerjaan sukses");
//                        getDataPekerjaan();
//                    }
//                } else {
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GeneralResponse> call, Throwable t) {
//
//            }
//        });
//    }
//
//    private void dialogProfile(boolean dataProfile){
//        if (!isFromProfile){
//            return;
//        }
//        if (Constant.isDataProfile() == dataProfile || Constant.getLevelUser() == Constant.LEVEL_NON_LK){
//            return;
//        }
//        Constant.setDataProfile(dataProfile);
//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setTitle("Perhatian!")
//                .setMessage("Lengkapi Data Profile Anda!")
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Constant.setDataProfile(false);
//                    }
//                })
//                .setCancelable(false)
//                .show();
//    }
//
//    private void dialogAlumni(){
//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setTitle("Apakah Anda Alumni ?")
////                .setMessage("Apakah Anda Alumni ?")
//                .setPositiveButton("Iya", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        ll4thn.setVisibility(View.VISIBLE);
//                    }
//                })
//                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                })
//                .setCancelable(false)
//                .show();
//    }
//
//
//    private void defaultSpinner(String level){
//        Log.d(TAG, "defaultSpinner: "+level);
//        String myString = level; //the value you want the position for
//
//        ArrayAdapter myAdap = (ArrayAdapter) spinnerPilihan.getAdapter(); //cast to an ArrayAdapter
//
//        int spinnerPosition = myAdap.getPosition(myString);
//
////set the default according to value
//        spinnerPilihan.setSelection(spinnerPosition);
//    }
//}
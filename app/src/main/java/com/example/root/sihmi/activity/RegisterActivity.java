package com.example.root.sihmi.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.sihmi.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {
    @BindView(R.id.tvLogin)
    TextView tvLogin;
    @BindView(R.id.btnRegister)
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnRegister)
    public void goRegister(){
        Toast.makeText(this, "Register Successful", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.tvLogin)
    public void goLogin(){
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }
}

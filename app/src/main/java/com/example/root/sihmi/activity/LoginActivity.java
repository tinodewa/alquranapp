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

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.tvRegister)
    TextView tvRegister;
    @BindView(R.id.tvForget)
    TextView tvForget;
    @BindView(R.id.btnLogin)
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnLogin)
    public void goLogin(){
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.tvRegister)
    public void goRegister(){
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }
}

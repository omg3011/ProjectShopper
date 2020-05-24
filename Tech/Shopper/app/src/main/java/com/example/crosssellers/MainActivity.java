package com.example.crosssellers;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {
    // Views: Cache references
    Button btn_register, btn_login;

    // Save
    String mallName;

    //-------------------------------------------------------------------------------------------------------------------------------------------//
    //
    // Built-In Function(s)
    //
    //-------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //-- Load Data
        LoadData(savedInstanceState);

        //----------------------------------------------------------------------//
        // Action bar                                                           //
        //----------------------------------------------------------------------//
        //-- Add built-in "Actionbar" and it's "Actionbar"->title
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Main");

        //-- Enable "Actionbar"->back button
        actionbar.setDisplayHomeAsUpEnabled(true);

        //-- Cache references
        btn_register = findViewById(R.id.register_btn);
        btn_login = findViewById(R.id.login_btn);

        //-- Register btn onClick event
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //-- Click Register Btn -> From MainActivity(page) Go to RegisterActivity(another page)
                SaveData_And_GoToNextActivity(RegisterActivity.class);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //-- Click Login Btn -> From MainActivity(page) Go to LoginActivity(another page)
                SaveData_And_GoToNextActivity(LoginActivity.class);
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        //-- Go to previous activity
        onBackPressed();

        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MainActivity.this, SelectMallActivity.class);
        startActivity(intent);
        finish();
    }

    void LoadData(final Bundle savedInstanceState){
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                mallName= null;
            } else {
                mallName= extras.getString("mall");
            }
        } else {
            mallName= (String) savedInstanceState.getSerializable("mall");
        }
    }


    void SaveData_And_GoToNextActivity(Class activity)
    {
        Intent intent = new Intent(MainActivity.this, activity);
        intent.putExtra("mall", mallName);
        startActivity(intent);
        finish();
    }

}

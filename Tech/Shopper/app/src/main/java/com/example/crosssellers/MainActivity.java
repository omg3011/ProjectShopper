package com.example.crosssellers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    // Views: Cache references
    Button btn_register, btn_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //-- Cache references
        btn_register = findViewById(R.id.register_btn);
        btn_login = findViewById(R.id.login_btn);

        //-- Register btn onClick event
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //-- Click Register Btn -> From MainActivity(page) Go to RegisterActivity(another page)
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });
    }
}

package com.example.crosssellers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    // Views: Cache references
    Button btn_register, btn_login;

    //-------------------------------------------------------------------------------------------------------------------------------------------//
    //
    // Built-In Function(s)
    //
    //-------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //-- Cache references
        btn_register = findViewById(R.id.register_btn);
        btn_login = findViewById(R.id.login_btn);

        //-- Register btn onClick event
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //-- Get mall
                String mallName;
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


                //-- Click Register Btn -> From MainActivity(page) Go to RegisterActivity(another page)
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                intent.putExtra("mall", mallName);
                startActivity(intent);
                finish();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //-- Get mall
                String mallName;
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

                //-- Click Login Btn -> From MainActivity(page) Go to LoginActivity(another page)
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("mall", mallName);
                startActivity(intent);
                finish();
            }
        });
    }
}

package com.example.proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class Inicio extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 5000; // tiempo en milisegundos
    private Handler handler;
    private Runnable runnable;
    private boolean isClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        // Cambia el título del Action Bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Inicio");
        }

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (!isClicked) {
                    Intent i = new Intent(Inicio.this, login_register.class);
                    startActivity(i);
                    finish();
                }
            }
        };
        handler.postDelayed(runnable, SPLASH_TIME_OUT);

        ImageView image1 = findViewById(R.id.perro);
        ImageView image2 = findViewById(R.id.entrar);

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClicked = true;
                handler.removeCallbacks(runnable);
                Intent intent = new Intent(Inicio.this, login_register.class);
                startActivity(intent);
                finish();
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClicked = true;
                handler.removeCallbacks(runnable);
                Intent intent = new Intent(Inicio.this, login_register.class);
                startActivity(intent);
                finish();
            }
        });
    }
}

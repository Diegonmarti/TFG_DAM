package com.example.proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class instrucciones_dueno extends AppCompatActivity {
    private String email;
    TextView emailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrucciones_dueno);
        email = getIntent().getStringExtra("email");
        // buscar el textview por su emaol, estamos pasando el dato
        if (email.contains("dueño")) {
            // Eliminar la palabra "cuidador" del correo electrónico
            email = email.replaceAll("dueño", "");
        }
        // buscar el textview por su emaol
        emailTextView = findViewById(R.id.emaildueno);
        emailTextView.setText(email.split("@")[0]);
    }

    public void pasar(View view) {
        // obtenemos el correo electrónico del usuario
        String userEmail = emailTextView.getText().toString();

        // refirigimos a la clase menu_dueno y le mandamos el email
        Intent intent = new Intent(instrucciones_dueno.this, menu_dueno.class);
        intent.putExtra("email", userEmail);

        startActivity(intent);
    }


}
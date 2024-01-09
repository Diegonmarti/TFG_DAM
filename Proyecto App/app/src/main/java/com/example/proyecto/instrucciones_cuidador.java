package com.example.proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class instrucciones_cuidador extends AppCompatActivity {
    private TextView edtemail;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrucciones_cuidador);
        // Obtener el correo electrónico del usuario del intent recibido en onCreate
        userEmail = getIntent().getStringExtra("userEmail");

        // Comprobar si la palabra "cuidador" está presente en el correo electrónico
        if (userEmail.contains("cuidador")) {
            // Eliminar la palabra "cuidador" del correo electrónico
            userEmail = userEmail.replaceAll("cuidador", "");
        }

        // Asignar el correo electrónico del usuario al elemento edtemail
        edtemail = findViewById(R.id.emailcuidador);
        edtemail.setText(userEmail.split("@")[0]);
    }

    public void pasar(View view) {
        // Obtener el correo electrónico del usuario
        String userEmail = edtemail.getText().toString();

        // Redirigir a la clase menu_dueno
        Intent intent = new Intent(instrucciones_cuidador.this, perfil_cuidador.class);
        intent.putExtra("userEmail", userEmail);

        startActivity(intent);
    }
}
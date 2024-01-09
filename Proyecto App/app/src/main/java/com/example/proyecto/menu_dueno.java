package com.example.proyecto;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class menu_dueno extends AppCompatActivity {
    Button btnvolver, btnlista, btncasa, btnperfil, btnidioma;
    TextView emailTextView;
    TextView nombredueno;
    private String email;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_dueno);
        // cambiamos el título del action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Menu Dueño");
        }

        btnvolver = findViewById(R.id.volver);
        btnlista = findViewById(R.id.listado);
        btncasa = findViewById(R.id.casa);
        btnidioma = findViewById(R.id.idioma);
        btnperfil = findViewById(R.id.perfil);
        // ibtiene el email del Intent ya que lo recibe porque en la otra clase se llama email
        email = getIntent().getStringExtra("email");
        if (email.contains("dueño")) {
            // eliminar la palabra "dueño" del correo electrónico
            email = email.replaceAll("dueño", "");
        }
        // busca el textview por su email
        emailTextView = findViewById(R.id.emaildueno);
        emailTextView.setText(email.split("@")[0]);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        nombredueno = findViewById(R.id.email);
        DatabaseReference carpetaRef = mDatabase.child("Datos Dueños").child("dueñito").child(email);
        carpetaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean contienePasear = false;
                    boolean contiene24Horas = false;
                    boolean contieneVeterinario = false;
                    boolean contienePeluqueria = false;

                    StringBuilder builder = new StringBuilder();
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        // Obtén el valor de cada "issue"
                        String data = issue.getValue().toString();

                        // Verifica si el dato contiene las palabras clave
                        if (data.contains("pasear")) {
                            contienePasear = true;
                        }
                        if (data.contains("24 horas")) {
                            contiene24Horas = true;
                        }
                        if (data.contains("veterinario")) {
                            contieneVeterinario = true;
                        }
                        if (data.contains("peluqueria")) {
                            contienePeluqueria = true;
                        }

                        builder.append(data + "\n");
                    }
                    ImageView imageView = findViewById(R.id.fotoreserva);

                    // carga la imagen según los datos
                    if (contienePasear) {
                        imageView.setImageResource(R.drawable.paseante);
                    } else if (contiene24Horas) {
                        imageView.setImageResource(R.drawable.perro24horas);
                    } else if (contieneVeterinario) {
                        imageView.setImageResource(R.drawable.veterinario);
                    } else if (contienePeluqueria) {
                        imageView.setImageResource(R.drawable.peluqueria);
                    } else {
                        // asigna la imagen perro por defecto si no coincide con ninguna
                        imageView.setImageResource(R.drawable.perro);
                    }
                    TextView datosCuidadorTextView = findViewById(R.id.datosreserva);
                    datosCuidadorTextView.setText(builder.toString());
                } else {
                    Toast.makeText(menu_dueno.this, "No hay ninguna reserva para este dueño", Toast.LENGTH_SHORT).show();
                }
            }


        @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(menu_dueno.this, "Error al cargar los datos.", Toast.LENGTH_SHORT).show();
            }
        });

        btnperfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email;
                // obtener la instancia de la clase datos
                datos datosInstance = datos.getInstance();
                // guardar el correo electrónico en la clase datos utilizando el setter
                datosInstance.setEmail(userEmail);
                // redirigir a la clase perfil_dueno.class
                Intent intent = new Intent(menu_dueno.this, perfil_dueno.class);
                startActivity(intent);
                finish();
            }
        });

        btncasa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        btnidioma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent googleIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://perruna-cd211.firebaseapp.com/" ));
                startActivity(googleIntent);
            }
        });

        btnlista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // obtener el correo electrónico del textviewq
                String userEmail = emailTextView.getText().toString();
                // redirigir a la clase Userlist.class
                Intent intent = new Intent(menu_dueno.this, Userlist.class);
                // pasar el correo electrónico como extra en el Intent
                intent.putExtra("email", userEmail);
                startActivity(intent);
                finish();
            }
        });
    }
    public void btnvolver(View view) {
        super.onBackPressed();
    }
}
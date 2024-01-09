package com.example.proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class informacion_cuidadores extends AppCompatActivity {
    private TextView nombreTextView, telefonocuida, direccionTextView, fechaiTextView, fechafTextView;
    private EditText descripcio;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_cuidadores);

        // Cambia el título del Action Bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Informacion Cuidadores");
        }

        // Obtener referencias a los TextView y EditText en el layout
        nombreTextView = findViewById(R.id.nombre);
        telefonocuida = findViewById(R.id.telefono);
        direccionTextView = findViewById(R.id.direccion);
        fechaiTextView = findViewById(R.id.fecha_inicio);
        fechafTextView = findViewById(R.id.fecha_fin);
        descripcio = findViewById(R.id.descripcion);

        // Obtener el Intent que inició esta actividad y extraer los datos
        Intent intent = getIntent();
        String nombre = intent.getStringExtra("nombre");
        String telefon = intent.getStringExtra("telefonocuidador");
        String direccion = intent.getStringExtra("direccion");
        String fechaini = intent.getStringExtra("fechaini");
        String fechafin = intent.getStringExtra("fechafin");
        String descrip = intent.getStringExtra("descripcion");

        // Mostrar los datos en los TextView correspondientes
        nombreTextView.setText(nombre);
        telefonocuida.setText(telefon);
        direccionTextView.setText(direccion);
        fechaiTextView.setText(fechaini);
        fechafTextView.setText(fechafin);
        descripcio.setText(descrip);

        // Obtener la URL de la imagen
        String imageURL = getIntent().getStringExtra("imageURL");

        // Mostrar la imagen en un ImageView
        ImageView imageView = findViewById(R.id.foto);
        Glide.with(this).load(imageURL).into(imageView);

        // Inicializar la referencia a la base de datos
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Referencia a la descripción del cuidador en la base de datos
        DatabaseReference cuidadorReference = mDatabase.child("Datos Cuidadores").child(nombreTextView.getText().toString());

        // Obtener el valor de la descripción
        cuidadorReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String descripcion = dataSnapshot.child("descripcion").getValue(String.class);
                    if (descripcion != null) {
                        descripcio.setText(descripcion);
                    } else {
                        descripcio.setText("No se encontró ninguna descripción para este cuidador.");
                    }
                } else {
                    descripcio.setText("No se encontró ninguna descripción para este cuidador.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejo de errores de la consulta
                descripcio.setText("Error al obtener la descripción del cuidador.");
            }
        });
    }

    public void btnvolver(View view) {
        // Regresar a la pantalla anterior
        super.onBackPressed();
    }
}

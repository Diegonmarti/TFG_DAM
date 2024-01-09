package com.example.proyecto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class perfil_dueno extends AppCompatActivity {

    EditText nombredueno, emaildueno, direcciondueno,  nombreperro, edadperro, razaperro;
    ImageView foto;
    Button btnguardar;
    DatabaseReference databaseUsers;
    StorageReference storageReference;
    private static final int PICK_IMAGE = 1;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // cambiamos el título del action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Perfil Dueño");
        }

        setContentView(R.layout.activity_perfil_dueno);
        btnguardar = findViewById(R.id.guardardatos);
        nombredueno = findViewById(R.id.edtnamedueno);
        emaildueno = findViewById(R.id.edtemaildueno);
        direcciondueno = findViewById(R.id.edtdireccion);
        foto = findViewById(R.id.imagenfoto2);
        nombreperro = findViewById(R.id.edtnamemascota);
        razaperro = findViewById(R.id.edtrazamascota);
        edadperro = findViewById(R.id.edtededadmascota);

        // obtenemos la instancia de la clase datos
        datos datosInstance = datos.getInstance();
        // obtenemos el correo electrónico de la clase datos utilizando el getter
        String userEmail = datosInstance.getEmail();
        emaildueno.setText(userEmail);

        databaseUsers = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "Selecciona una imagen"), PICK_IMAGE);
            }
        });

        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertData();
            }
        });
    }

    private void InsertData() {
        String nombredue = nombredueno.getText().toString();
        String maildueno = emaildueno.getText().toString();
        String telefono = direcciondueno.getText().toString();
        String nombreperr = nombreperro.getText().toString();
        String raza = razaperro.getText().toString();
        String edad = edadperro.getText().toString();

        if (nombredue.isEmpty() || maildueno.isEmpty() || telefono.isEmpty() || nombreperr.isEmpty() || raza.isEmpty() || edad.isEmpty() || bitmap == null) {
            Toast.makeText(perfil_dueno.this, "Por favor completa todos los campos e inserta una imagen", Toast.LENGTH_SHORT).show();
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            StorageReference reference = storageReference.child("dueño/" + nombredue + ".jpeg");
            UploadTask uploadTask = reference.putBytes(data);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri downloadUri = uriTask.getResult();

                DatabaseReference usersRef = databaseUsers.child("Datos Dueños");

                usersRef.child(nombredue).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            DataSnapshot snapshot = task.getResult();
                            if (snapshot.exists()) {
                                Toast.makeText(perfil_dueno.this, "Ya existe un registro con este nombre de usuario", Toast.LENGTH_SHORT).show();
                            } else {
                                datos dato = new datos(nombredue, maildueno, telefono, nombreperr, raza, edad);
                                dato.setRutaImagen(nombredue + ".jpeg"); // Se guarda la ruta de la imagen en la clase datos
                                usersRef.child(nombredue).setValue(dato).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(perfil_dueno.this, "Detalles Usuarios Insertados", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(perfil_dueno.this, "Error al insertar los datos del usuario", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(perfil_dueno.this, "Error al consultar los datos del usuario", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }).addOnFailureListener(e -> Toast.makeText(perfil_dueno.this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show());
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                foto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}
    public void btnvolver(View view) {
        String userEmail = emaildueno.getText().toString();
        Intent intent = new Intent(perfil_dueno.this, menu_dueno.class);
        intent.putExtra("email", userEmail);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        String userEmail = emaildueno.getText().toString();
        Intent intent = new Intent(perfil_dueno.this, menu_dueno.class);
        intent.putExtra("email", userEmail);
        startActivity(intent);

        finish();
    }
}
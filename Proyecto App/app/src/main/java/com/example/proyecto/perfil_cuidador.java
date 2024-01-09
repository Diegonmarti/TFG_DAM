package com.example.proyecto;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class perfil_cuidador extends AppCompatActivity {

    Button btnInsert, fechinicio, fechfin;
    EditText name, email, direccion, descripcion, telefono;
    ImageView foto;

    DatabaseReference databaseUsers;
    StorageReference storageReference;
    private static final int PICK_IMAGE = 1;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_cuidador);
        // cambiamos el título del action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Perfil Cuidador");
        }
        String userEmail = getIntent().getStringExtra("userEmail");

        // comprobamos si la palabra "cuidador" está para quitarla
        if (userEmail.contains("cuidador")) {
            // Eliminar la palabra "cuidador" del correo electrónico
            userEmail = userEmail.replaceAll("cuidador", "");
        }

        // asignamos el correo electrónico del usuario al elemento edtemail
        EditText edtemail = findViewById(R.id.edtname);
        edtemail.setText(userEmail.split("@")[0]);

        btnInsert = findViewById(R.id.btninsert);
        name = findViewById(R.id.edtname);
        email = findViewById(R.id.edtemail);
        telefono = findViewById(R.id.edttelefonocuidador);
        direccion = findViewById(R.id.edtdireccion);
        foto = findViewById(R.id.imagenfoto);
        fechinicio = findViewById(R.id.fehca_inicio);
        fechfin =findViewById(R.id.fehca_fin);
        descripcion = findViewById(R.id.descripcion);

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


        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fechaini = fechinicio.getText().toString();
                String fechafi = fechfin.getText().toString();

                if (fechaini.isEmpty() || fechafi.isEmpty()) {
                    Toast.makeText(perfil_cuidador.this, "Por favor ingresa la fecha de inicio y la fecha de fin", Toast.LENGTH_SHORT).show();
                } else {
                    List<String> fechasEntre = obtenerFechasEntre(fechaini, fechafi);
                    String fechas = TextUtils.join(", ", fechasEntre);
                    Toast.makeText(perfil_cuidador.this, "Fechas entre la fecha de inicio y la fecha de fin: " + fechas, Toast.LENGTH_SHORT).show();

                    InsertData();
                }
            }
        });
    }
    private List<String> obtenerFechasEntre(String fechaInicio, String fechaFin) {
        List<String> fechasList = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            Date dateInicio = sdf.parse(fechaInicio);
            Date dateFin = sdf.parse(fechaFin);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateInicio);

            while (!calendar.getTime().after(dateFin)) {
                String fecha = sdf.format(calendar.getTime());
                fechasList.add(fecha);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return fechasList;
    }
    public static String selectedOption = "";
    private void InsertData() {
        String usernombre = name.getText().toString();
        String usermail = email.getText().toString();
        String telefon = telefono.getText().toString();
        String userdireccion = direccion.getText().toString();
        String fechaini= fechinicio.getText().toString();
        String fechafi = fechfin.getText().toString();
        String descripcio = descripcion.getText().toString();

        RadioGroup radioGroup = findViewById(R.id.radio_group);
        int selectedOptionId = radioGroup.getCheckedRadioButtonId();
        if (selectedOptionId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedOptionId);
            selectedOption = selectedRadioButton.getText().toString();
        }


        if (usernombre.isEmpty() || usermail.isEmpty() || telefon.isEmpty() || userdireccion.isEmpty() || fechaini.isEmpty() || fechafi.isEmpty()|| descripcio.isEmpty() || bitmap == null ) {
            Toast.makeText(perfil_cuidador.this, "Por favor completa todos los campos e inserta una imagen", Toast.LENGTH_SHORT).show();
        } else {
            // cogemos fecha de inicio y fecha de fin como objetos calendar
            Calendar fechaInicio = Calendar.getInstance();
            String[] partesFechaInicio = fechaini.split("/");
            fechaInicio.set(Integer.parseInt(partesFechaInicio[2]), Integer.parseInt(partesFechaInicio[1]) - 1, Integer.parseInt(partesFechaInicio[0]));
            Calendar fechaFin = Calendar.getInstance();
            String[] partesFechaFin = fechafi.split("/");
            fechaFin.set(Integer.parseInt(partesFechaFin[2]), Integer.parseInt(partesFechaFin[1]) - 1, Integer.parseInt(partesFechaFin[0]));

            // comprobamos que la fecha de fin es posterior a la fecha de inicio
            if (fechaFin.compareTo(fechaInicio) <= 0) {
                Toast.makeText(getApplicationContext(), "La fecha de fin debe ser posterior a la fecha de inicio", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> fechasList = obtenerFechasEntre(fechaini, fechafi);

            // verificar si ya existe un registro con el mismo nombre de usuario
            databaseUsers.child("Datos Cuidadores").child(usernombre).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        if (snapshot.exists()) {
                            Toast.makeText(perfil_cuidador.this, "Ya existe un registro con este nombre de usuario", Toast.LENGTH_SHORT).show();
                        } else {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();

                            StorageReference reference = storageReference.child("cuidador/" + usernombre + ".jpeg");
                            UploadTask uploadTask = reference.putBytes(data);
                            uploadTask.addOnSuccessListener(taskSnapshot -> {
                                reference.getDownloadUrl().addOnSuccessListener(uri -> {
                                    datos dato = new datos(usernombre, usermail,telefon, userdireccion, fechaini, fechafi, descripcio);
                                    dato.setRutaImagen(usernombre + ".jpeg"); // Se guarda la ruta de la imagen en la clase datos
                                    dato.setOpcion(selectedOption); // guardar la opción seleccionada en la clase datos

                                    // agregar las fechas al objeto datos
                                    for (String fecha : fechasList) {
                                        dato.addFecha(fecha);
                                    }

                                    databaseUsers.child("Datos Cuidadores").child(usernombre).setValue(dato).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(perfil_cuidador.this, "Detalles Usuarios Insertados", Toast.LENGTH_SHORT).show();
                                                // Redirigir a la página de inicio
                                                Intent intent = new Intent(perfil_cuidador.this, login_register.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                                });
                            }).addOnFailureListener(e -> Toast.makeText(perfil_cuidador.this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(perfil_cuidador.this, "Error al buscar el registro en la base de datos", Toast.LENGTH_SHORT).show();
                    }
                }
            });
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

    public void fechaini(View view) {

            // Obtener fecha actual
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            // Crear DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(perfil_cuidador.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            // Actualizar EditText con fecha seleccionada
                            fechinicio.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                        }
                    }, year, month, dayOfMonth);

            // Configurar fecha mínima y máxima permitida en el calendario si es necesario
            // datePickerDialog.getDatePicker().setMinDate(...);
            // datePickerDialog.getDatePicker().setMaxDate(...);

            // Mostrar DatePickerDialog
            datePickerDialog.show();

    }

    public void fechafi(View view) {
            // Obtener fecha actual
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            // Crear DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(perfil_cuidador.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            // Actualizar EditText con fecha seleccionada
                            fechfin.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                        }
                    }, year, month, dayOfMonth);

            // Configurar fecha mínima y máxima permitida en el calendario si es necesario
            // datePickerDialog.getDatePicker().setMinDate(...);
            // datePickerDialog.getDatePicker().setMaxDate(...);

            // Mostrar DatePickerDialog
            datePickerDialog.show();
    }
    public void btnvolver(View view) {
        // Regresar a la pantalla anterior
        super.onBackPressed();
    }
}
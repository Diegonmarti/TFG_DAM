package com.example.proyecto;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class carrito extends AppCompatActivity {

    private DatabaseReference mDatabase;

    TextView name, direccion2, telefonocuidador, servicio, emailDueno2;

    private Spinner opcion;
    private String nombreCuidador, emailDueno;
    private DatabaseReference carpetaRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        // Cambia el título del Action Bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Carrito");
        }


        setContentView(R.layout.activity_carrito);
        emailDueno = getIntent().getStringExtra("email");
        String telefonocuida = getIntent().getStringExtra("telefonocuidador");
        nombreCuidador = getIntent().getStringExtra("nombreCuidador");
        String direccionCuidador = getIntent().getStringExtra("direccionCuidador");
        String servic = getIntent().getStringExtra("opcionseleccionada");
        String imageURL = getIntent().getStringExtra("imageURL");
// Mostrar la imagen en un ImageView
        ImageView imageView = findViewById(R.id.imagen2);
        Glide.with(this).load(imageURL).into(imageView);

        emailDueno2 = findViewById(R.id.nombredueno2);
        // Establece el nombre del cuidador en el campo "name"
        name = findViewById(R.id.nombre);
        telefonocuidador = findViewById(R.id.telefonocuida);
        direccion2 = findViewById(R.id.direccioncar);
        servicio = findViewById(R.id.servicios);

        emailDueno2.setText(emailDueno);
        name.setText(nombreCuidador);
        telefonocuidador.setText(telefonocuida);
        direccion2.setText(direccionCuidador);
        servicio.setText(servic);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        opcion = findViewById(R.id.opcion);

        // Obtén la referencia a la base de datos
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Obtiene la referencia a la ruta de Firebase que contiene las fechas
        Query query = database.getReference("Datos Cuidadores").child(nombreCuidador).child("fechas");

        // Realiza la consulta a la base de datos
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> fechasList = new ArrayList<>();

                // Itera sobre los datos obtenidos de la consulta
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Obtén el valor de la fecha y añádelo a la lista
                    String fecha = snapshot.getValue(String.class);
                    fechasList.add(fecha);
                }

                // Crea un adaptador para el Spinner y añade las fechas
                ArrayAdapter<String> adapter = new ArrayAdapter<>(carrito.this,
                        android.R.layout.simple_spinner_item, fechasList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                opcion.setAdapter(adapter);

                if (fechasList.isEmpty()) {
                    // La carpeta de fechas está vacía, elimina al cuidador de Firebase
                    DatabaseReference cuidadorRef = mDatabase.child("Datos Cuidadores").child(nombreCuidador);
                    cuidadorRef.removeValue();

                    // Muestra un Toast o una notificación para informar al usuario que el cuidador ha sido eliminado
                    Toast.makeText(getApplicationContext(), "El cuidador ha sido eliminado debido a la falta de fechas disponibles.", Toast.LENGTH_SHORT).show();

                    // Otras acciones que desees realizar cuando el cuidador se elimina

                    // Regresar a la pantalla anterior o realizar otra acción
                    // super.onBackPressed();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejo de error en caso de fallo en la consulta
            }
        });
    }


    private void mostrarPopup(View view) {
        name = findViewById(R.id.nombre);

        // Crea un objeto PopupWindow con el ancho y alto deseados
        PopupWindow popup = new PopupWindow(this);
        popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setFocusable(true);
        popup.setOutsideTouchable(true);

        // Infla la vista del popup desde un archivo XML
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_puntuacion, null);
        popup.setContentView(popupView);

        // Busca los elementos de la vista del popup
        final RatingBar simpleRatingBar = popupView.findViewById(R.id.simpleRatingBar);
        Button enviarButton = popupView.findViewById(R.id.botonaceptar);

        // Agrega un OnClickListener al botón "enviar"
        enviarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtiene los valores del RatingBar y los muestra en un Toast
                String totalStars = "Total Stars: " + simpleRatingBar.getNumStars();
                String rating = "Rating: " + simpleRatingBar.getRating();
                Toast.makeText(getApplicationContext(), totalStars + "\n" + rating, Toast.LENGTH_LONG).show();

                // Obtiene la fecha seleccionada del Spinner
                final String fechaSeleccionada = opcion.getSelectedItem().toString();

                // Muestra los detalles de la fecha seleccionada
                Toast.makeText(getApplicationContext(), "Fecha seleccionada: " + fechaSeleccionada, Toast.LENGTH_SHORT).show();

                // Cierra el popup
                popup.dismiss();

                // Obtiene la referencia a la ruta de Firebase que contiene las fechas
                DatabaseReference fechasRef = mDatabase.child("Datos Cuidadores").child(nombreCuidador).child("fechas");

                // Realiza una consulta para buscar la fecha seleccionada
                fechasRef.orderByValue().equalTo(fechaSeleccionada).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot fechaSnapshot : snapshot.getChildren()) {
                            // Elimina la fecha seleccionada de Firebase
                            fechaSnapshot.getRef().removeValue();
                        }

                        // Redirige a la clase Listado
                        Intent listadoIntent = new Intent(carrito.this, menu_dueno.class);
                        listadoIntent.putExtra("email", emailDueno); // Pasar el email a la clase carrito
                        startActivity(listadoIntent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Manejo de error en caso de fallo en la consulta
                    }
                });
            }
        });

        // Muestra el popup en la pantalla
        popup.showAtLocation((View) view.getParent(), Gravity.CENTER, 0, 0);
    }
    public void comprar(View view) {
        // Obtén el servicio seleccionado
        String servicioSeleccionado = servicio.getText().toString().toLowerCase();

        // Crea un AlertDialog para mostrar la información de la fecha seleccionada
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Detalles de la compra");
        String mensaje = "Servicio: " + servicioSeleccionado + "\n"
                + "Telefono: " + telefonocuidador + "\n"
                + "Nombre: " + nombreCuidador;

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Redirigir a Google y realizar una búsqueda si el servicio coincide con "pasear"
                if (servicioSeleccionado.equals("pasear")) {
                    carpetaRef = mDatabase.child("Datos Dueños").child("dueñito").child(emailDueno2.getText().toString());
                    carpetaRef.child("telefono").setValue(telefonocuidador.getText().toString());
                    carpetaRef.child("nombre").setValue(nombreCuidador);
                    carpetaRef.child("servicio").setValue(servicioSeleccionado);
                    Intent googleIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://buy.stripe.com/test_14k5mG3A75AA08g001" ));
                    startActivity(googleIntent);

                    // Programa una tarea con un retraso de 10 segundos para mostrar el popup
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mostrarPopup(view);
                        }
                    }, 10000); // 10 segundos en milisegundos
                    } else {
                    // Agrega los casos correspondientes a los demás servicios
                    if (servicioSeleccionado.contains("24horas")) {
                        carpetaRef = mDatabase.child("Datos Dueños").child("dueñito").child(emailDueno2.getText().toString());
                        carpetaRef.child("telefono").setValue(telefonocuidador.getText().toString());
                        carpetaRef.child("nombre").setValue(nombreCuidador);
                        carpetaRef.child("servicio").setValue(servicioSeleccionado);

                        // Abre la página durante 10 segundos
                        Intent googleIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://buy.stripe.com/test_14k5mG3A75AA08g001"));
                        startActivity(googleIntent);

                        // Programa una tarea con un retraso de 10 segundos para mostrar el popup
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mostrarPopup(view);
                            }
                        }, 10000); // 10 segundos en milisegundos
                } else if (servicioSeleccionado.contains("veterinario")) {
                        // Crear un diálogo personalizado con opciones de lista
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(carrito.this);
                        dialogBuilder.setTitle("Seleccionar opción");

                        String[] opciones = {"Importe predeterminado", "Poner importe"};
                        dialogBuilder.setItems(opciones, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        carpetaRef = mDatabase.child("Datos Dueños").child("dueñito").child(emailDueno2.getText().toString());
                                        carpetaRef.child("telefono").setValue(telefonocuidador.getText().toString());
                                        carpetaRef.child("nombre").setValue(nombreCuidador);
                                        carpetaRef.child("servicio").setValue(servicioSeleccionado);
                                        // Redirigir a la opción 1
                                        Intent opcion1Intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://buy.stripe.com/test_cN2bL41rZbYYcV2aEJ"));
                                        startActivity(opcion1Intent);
                                        // Programa una tarea con un retraso de 10 segundos para mostrar el popup
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                mostrarPopup(view);
                                            }
                                        }, 10000); // 10 segundos en milisegundos
                                        break;
                                    case 1:
                                        carpetaRef = mDatabase.child("Datos Dueños").child("dueñito").child(emailDueno2.getText().toString());
                                        carpetaRef.child("telefono").setValue(telefonocuidador.getText().toString());
                                        carpetaRef.child("nombre").setValue(nombreCuidador);
                                        carpetaRef.child("servicio").setValue(servicioSeleccionado);
                                        // Redirigir a la opción 2
                                        Intent opcion2Intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://buy.stripe.com/test_eVa6qK0nV6EEcV2eUX"));
                                        startActivity(opcion2Intent);
                                        // Programa una tarea con un retraso de 10 segundos para mostrar el popup
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                mostrarPopup(view);
                                            }
                                        }, 10000); // 10 segundos en milisegundos
                                        break;
                                }
                            }
                        });

                        dialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Acciones a realizar al hacer clic en el botón "Cancelar"
                                dialog.dismiss();
                            }
                        });

                        // Mostrar el diálogo personalizado
                        AlertDialog dialog2 = dialogBuilder.create();
                        dialog2.show();
                } else if (servicioSeleccionado.contains("peluqueria")) {
                        carpetaRef = mDatabase.child("Datos Dueños").child("dueñito").child(emailDueno2.getText().toString());
                        carpetaRef.child("telefono").setValue(telefonocuidador.getText().toString());
                        carpetaRef.child("nombre").setValue(nombreCuidador);
                        carpetaRef.child("servicio").setValue(servicioSeleccionado);
                        // Redireccionar a la página web de peluquería
                        Intent peluqueriaIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://buy.stripe.com/test_14kcP8daHfba08g5ko"));
                        startActivity(peluqueriaIntent);
                        // Programa una tarea con un retraso de 10 segundos para mostrar el popup
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mostrarPopup(view);
                            }
                        }, 10000); // 10 segundos en milisegundos
                    }
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Acciones a realizar al hacer clic en el botón "Cancelar"
                dialog.dismiss();
            }
        });

        // Muestra el AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void btnvolver(View view) {
        // Regresar a la pantalla anterior
        super.onBackPressed();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tarjeta, menu);

        // Obtén una referencia al ítem de menú de compartir
        MenuItem compartirMenuItem = menu.findItem(R.id.compartir);

        // Agrega un listener al ítem de menú de compartir
        compartirMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Obtén todos los datos necesarios para compartir
                String nombre = name.getText().toString();
                String telefono = telefonocuidador.getText().toString();
                String direccionCuidador = direccion2.getText().toString();
                String servicioSeleccionado = servicio.getText().toString();

                // Crea el texto para compartir
                String textoCompartir = "Detalles del carrito:\n\n" +
                        "Nombre: " + nombre + "\n" +
                        "Email: " + telefono + "\n" +
                        "Dirección: " + direccionCuidador + "\n" +
                        "Servicio: " + servicioSeleccionado;

                // Crea el intent de compartir
                Intent compartirIntent = new Intent(Intent.ACTION_SEND);
                compartirIntent.setType("text/plain");
                compartirIntent.putExtra(Intent.EXTRA_TEXT, textoCompartir);

                // Inicia la actividad para compartir
                startActivity(Intent.createChooser(compartirIntent, "Compartir detalles del carrito"));

                return true;
            }
        });

        return true;
    }

}
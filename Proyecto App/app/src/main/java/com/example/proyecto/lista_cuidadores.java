package com.example.proyecto;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Random;

public class lista_cuidadores extends RecyclerView.Adapter<lista_cuidadores.MyViewHolder> {
    Context context;
    ArrayList<datos> List;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private String email2;
    private String telefon;
    private String imageURL;

    public lista_cuidadores(Context context, ArrayList<datos> dataList, String email) {
        this.context = context;
        this.List = dataList;
        this.email2 = email;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_lista_cuidadores, parent, false);

        // cambiar el título del action bar
        ActionBar actionBar = ((AppCompatActivity) context).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Lista Cuidadores");
        }

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        datos dato = List.get(position);
        // ...
        holder.name.setText(dato.getNombre());
        holder.telefon.setText(dato.getTelefono());
        holder.direccion.setText(dato.getDireccion());
        holder.fechai.setText(dato.getFechaini());
        holder.fechaf.setText(dato.getFechafin());
        holder.opcion.setText(dato.getOpcion());

        StorageReference storageRef = storage.getReferenceFromUrl("gs://perruna-cd211.appspot.com/cuidador/" + holder.name.getText().toString() + ".jpeg");

        // obtener la url de descarga de la imagen y cargarla en el imageview
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).into(holder.foto);
                imageURL = uri.toString();
            }
        });



        // genera un color rgb aleatorio y establece como fondo del botón "elegir"
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        int color = Color.rgb(red, green, blue);
        holder.button.setBackgroundColor(color);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Información del usuario");
                String message = "¿Qué quieres hacer?";
                builder.setMessage(message);
                holder.cardView.setCardBackgroundColor(color);

                builder.setPositiveButton("Reservar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (context != null && holder != null) {
                            // obtener la ruta de la imagen en Firebase Storage
                            String nombreCuidador = holder.name.getText().toString();
                            String rutaImagen = "cuidador/" + nombreCuidador + ".jpeg";

                            // pbtener la referencia al archivo de imagen
                            StorageReference storageRef = storage.getReference().child(rutaImagen);

                            // obtener la URL de descarga de la imagen
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Agrega la URL como un extra en el Intent
                                    Intent intent = new Intent(context, carrito.class);
                                    intent.putExtra("nombreCuidador", holder.name.getText().toString());
                                    intent.putExtra("telefonocuidador", holder.telefon.getText().toString());
                                    intent.putExtra("direccionCuidador", holder.direccion.getText().toString());
                                    intent.putExtra("opcionseleccionada", holder.opcion.getText().toString());
                                    intent.putExtra("imageURL", uri.toString());
                                    intent.putExtra("email", email2); // Pasar el email a la clase carrito
                                    context.startActivity(intent);
                                }
                            });
                        }
                    }
                });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        holder.cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        holder.mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://perruna-cd211.firebaseapp.com/ubicacion/mapa.html";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            }
        });


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StorageReference storageRef = storage.getReferenceFromUrl("gs://perruna-cd211.appspot.com/cuidador/" + holder.name.getText().toString() + ".jpeg");
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Intent intent = new Intent(context, informacion_cuidadores.class);
                        intent.putExtra("nombre", holder.name.getText().toString());
                        intent.putExtra("telefonocuidador", holder.telefon.getText().toString());
                        intent.putExtra("direccion", holder.direccion.getText().toString());
                        intent.putExtra("fechaini", holder.fechai.getText().toString());
                        intent.putExtra("fechafin", holder.fechaf.getText().toString());
                        intent.putExtra("imageURL", uri.toString());
                        intent.putExtra("email", email2); // pasar el email a la clase informacion_cuidadores, como hacemos en todas las clases
                        context.startActivity(intent);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return List.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, direccion, fechai, fechaf, opcion, telefon;
        Button button;
        ImageButton mapa;
        CardView cardView;
        ImageView foto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textnombre);
            telefon = itemView.findViewById(R.id.texttelefono);
            direccion = itemView.findViewById(R.id.textdireccion);
            button = itemView.findViewById(R.id.button);
            cardView = itemView.findViewById(R.id.tarjeta);
            foto = itemView.findViewById(R.id.imagen2);
            fechai = itemView.findViewById(R.id.fehca_inicio);
            fechaf = itemView.findViewById(R.id.fehca_fin);
            opcion = itemView.findViewById(R.id.opcion);
            mapa =itemView.findViewById(R.id.mapa);
        }
    }

}

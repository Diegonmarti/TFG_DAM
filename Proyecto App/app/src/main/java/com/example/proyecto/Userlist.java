package com.example.proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Userlist extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    private String email;
    TextView emailTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlist);
        // cogemos el email del Intent
        email = getIntent().getStringExtra("email");

        emailTextView = findViewById(R.id.emaildueno);
        emailTextView.setText(email);
        recyclerView = findViewById(R.id.recycleview);
        databaseReference = FirebaseDatabase.getInstance().getReference("Datos Cuidadores");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<datos> list = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    datos dato = dataSnapshot.getValue(datos.class);
                    dato.setEmail(email);
                    list.add(dato);
                }
                recyclerView.setAdapter(new lista_cuidadores(Userlist.this, list, email));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    @Override
    public void onBackPressed() {
        String userEmail = emailTextView.getText().toString();
        Intent intent = new Intent(Userlist.this, menu_dueno.class);
        intent.putExtra("email", userEmail);
        startActivity(intent);
        finish();
    }

}

package com.example.proyecto;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class register extends Fragment {

    private EditText emailEditText;
    private EditText phoneEditText, usuario;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button signUpButton;

    private FirebaseAuth mAuth;
    RadioButton btndueño;
    RadioButton btncuidador;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_register, container, false);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        Context context = activity.getApplicationContext();

        // cambiamos el título del action bar
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Login-Registro");
        }

        usuario = view.findViewById(R.id.user);
        emailEditText = view.findViewById(R.id.email);
        phoneEditText = view.findViewById(R.id.numero);
        passwordEditText = view.findViewById(R.id.contraseña);
        confirmPasswordEditText = view.findViewById(R.id.confcontraseña);
        btndueño = view.findViewById(R.id.dueño);
        btncuidador = view.findViewById(R.id.cuidador);
        signUpButton = view.findViewById(R.id.registrarse);

        mAuth = FirebaseAuth.getInstance();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String use = usuario.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                // agregamos la palabra dueño o perfil_cuidador al correo electrónico
                String correo_con_prefijo = "";
                if (btndueño.isChecked()) {
                    correo_con_prefijo = "dueño" + email;
                } else if (btncuidador.isChecked()) {
                    correo_con_prefijo = "cuidador" + email;
                }

                // validaciones de email y demás campos
                String emai = correo_con_prefijo;
                Pattern pattern = Pattern.compile("^([\\w-\\.]+@gmail\\.com)$");
                Matcher matcher = pattern.matcher(emai);
                if (!matcher.find()) {
                    Toast.makeText(getContext(), "Por favor, ingrese una dirección de correo electrónico de Gmail válida", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (emai.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(getContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    return;
                }

                // validacion de 9 dígitos para el campo de teléfono
                if (phone.length() != 9) {
                    Toast.makeText(getContext(), "El número de teléfono debe tener 9 dígitos", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(emai, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        String userId = mAuth.getCurrentUser().getUid();

                                        Map<String, Object> userData = new HashMap<>();
                                        userData.put("email", emai);
                                        userData.put("phone", phone);

                                        db.collection("Datos Usuarios").document(usuario.getText().toString().trim()).set(userData)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();

                                                        // Mostrar el popup de éxito y redirigir a la pantalla de login_cardview
                                                        mostrarPopup();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();
                                                        // Mostrar el popup de éxito y redirigir a la pantalla de login_cardview
                                                        mostrarPopup();                                                    }
                                                });

                                    } else {
                                        Toast.makeText(getContext(), "Error al registrarse: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
        });
        return view;
    }
    private void mostrarPopup() {
        if (btndueño.isChecked()) {
            // mostramos el popup para dueños
            final android.app.Dialog ventanaRegister2 = new android.app.Dialog(getActivity());
            ventanaRegister2.setContentView(R.layout.popup_register2);
            ventanaRegister2.setCancelable(false);
            // asignamos el listener al botón de aceptar
            Button aceptarButton = ventanaRegister2.findViewById(R.id.boton);
            aceptarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // redirigimos a la pantalla de login_cardview
                    Intent intent = new Intent(getActivity(), login_register.class);
                    startActivity(intent);
                    ventanaRegister2.dismiss();
                }
            });
            ventanaRegister2.show();
        } else if (btncuidador.isChecked()) {
            // mostramos el popup para cuidadores
            final android.app.Dialog ventanaRegister = new android.app.Dialog(getActivity());
            ventanaRegister.setContentView(R.layout.popup_register);
            ventanaRegister.setCancelable(false);

            // listener al botón de aceptar
            Button aceptarButton = ventanaRegister.findViewById(R.id.boton);
            aceptarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), login_register.class);
                    startActivity(intent);
                    ventanaRegister.dismiss();
                }
            });
            ventanaRegister.show();
        }
    }


}
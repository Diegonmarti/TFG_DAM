package com.example.proyecto;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class login extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 9001;
    private login_cardview loginAdapter;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private FloatingActionButton fabGoogle, btnpag, fabmusica;
    public void setLoginAdapter(login_cardview loginAdapter) {
        this.loginAdapter = loginAdapter;
    }

    public static login newInstance() {
        return new login();
    }

    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login, container, false);
        ActionBar actionBar2 = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar2 != null) {
            actionBar2.setTitle("Login");
        }

        usernameEditText = view.findViewById(R.id.email);
        passwordEditText = view.findViewById(R.id.contra);
        loginButton = view.findViewById(R.id.buttonlog);
        fabGoogle = view.findViewById(R.id.google_btn);
        fabmusica = view.findViewById(R.id.musica);
        btnpag = view.findViewById(R.id.btnpagina);
        mAuth = FirebaseAuth.getInstance();
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.prueba);


        mediaPlayer.start();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getContext(), "Nombre de usuario o contraseña vacíos", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(username, password)
                            .addOnCompleteListener(getActivity(), task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(getContext(), "¡Enhorabuena!", Toast.LENGTH_SHORT).show();
                                    mostrarPopup();
                                } else {
                                    Toast.makeText(getContext(), "Nombre de usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        fabmusica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    fabmusica.setImageResource(R.drawable.mueto); // Cambiar imagen a "muteo.png" cuando está en pausa
                } else {
                    mediaPlayer.start();
                    fabmusica.setImageResource(R.drawable.musica); // Cambiar imagen a "musica.png" cuando se reanuda la reproducción
                }
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        fabGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        btnpag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent googleIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://perruna-cd211.firebaseapp.com/"));
                startActivity(googleIntent);
            }
        });

        return view;
    }

    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(getContext(), "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(getContext(), "Inicio de sesión con Google exitoso", Toast.LENGTH_SHORT).show();
                        mostrarPopup();
                    } else {
                        Toast.makeText(getContext(), "Error al autenticar con Firebase", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void mostrarPopup() {
        final android.app.Dialog ventanaLogin = new android.app.Dialog(getActivity());
        ventanaLogin.setContentView(R.layout.popup_login);
        ventanaLogin.setCancelable(false);
        Button aceptarButton = ventanaLogin.findViewById(R.id.boton);
        aceptarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();

                if (username.startsWith("cuidador")) {
                    String userEmail = username;
                    Intent intent = new Intent(getActivity(), instrucciones_cuidador.class);
                    intent.putExtra("userEmail", userEmail);
                    startActivity(intent);
                } else if (username.startsWith("dueño")) {
                    String userEmail = username;
                    Intent intent = new Intent(getActivity(), instrucciones_dueno.class);
                    intent.putExtra("email", userEmail);

                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "Correo electrónico inválido", Toast.LENGTH_SHORT).show();
                }
                ventanaLogin.dismiss();
            }
        });
        ventanaLogin.show();
    }
}

package com.example.proyecto;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class login_register extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    FloatingActionButton fb, google, twitter;
    private View view;
    private PopupWindow popup;

    float v=0;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        tabLayout = findViewById(R.id.table_layout);
        viewPager=findViewById(R.id.view_pager);
        fb = findViewById(R.id.fab_facebook);
        google = findViewById(R.id.fab_google);
        twitter = findViewById(R.id.fab_twiter);


        tabLayout.addTab(tabLayout.newTab().setText("Login"));
        tabLayout.addTab(tabLayout.newTab().setText("Register"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final login_cardview adapter = new login_cardview(getSupportFragmentManager(), this, tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        fb.setTranslationY(300);
        google.setTranslationY(300);
        twitter.setTranslationY(300);
        tabLayout.setTranslationY(300);

        fb.setAlpha(v);
        google.setAlpha(v);
        twitter.setAlpha(v);
        tabLayout.setAlpha(v);

        fb.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();
        google.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        twitter.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(800).start();
        tabLayout.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(800).start();

        ImageView bola = findViewById(R.id.bola);
        bola.startAnimation(AnimationUtils.loadAnimation(this, R.anim.animacion_inicio_dueno));
        ImageView bola2 = findViewById(R.id.bola2);
        bola2.startAnimation(AnimationUtils.loadAnimation(this, R.anim.animacion_inicio_dueno2));
    }

    public void cerrarImagen(View view) {
        if (popup != null && popup.isShowing()) {
            popup.dismiss();
        }
    }

    public void cambiocontraseña(View view) {
        mostrarPopup(view);
    }
    private void mostrarPopup(View view) {
        popup = new PopupWindow(this);
        popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setFocusable(true);
        popup.setOutsideTouchable(true);

        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_resetearcontra, null);
        popup.setContentView(popupView);

        TextView emailTextView = popupView.findViewById(R.id.correo);
        Button enviarButton = popupView.findViewById(R.id.botonaceptar);

        enviarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailTextView.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    emailTextView.setError("Ingrese su correo electrónico");
                    return;
                }
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(login_register.this, "Se ha enviado un correo electrónico para resetear la contraseña", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(login_register.this, "No se pudo enviar el correo electrónico de reseteo de contraseña", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                popup.dismiss();
            }
        });
        popup.showAtLocation((View) view.getParent(), Gravity.CENTER, 0, 0);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inicio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:")); // Solo aplicaciones de correo electrónico deben manejar esto
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contactar Mi-Mascota Perruna");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Cuerpo del correo");
            startActivity(Intent.createChooser(emailIntent, "Enviar correo electrónico"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

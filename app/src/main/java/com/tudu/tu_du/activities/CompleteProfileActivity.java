package com.tudu.tu_du.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tudu.tu_du.R;
import com.tudu.tu_du.models.Usuarios;
import com.tudu.tu_du.providers.AuthProvider;
import com.tudu.tu_du.providers.UsuariosPrivider;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class CompleteProfileActivity extends AppCompatActivity {

     /*argumento 1 es el tipo de campo en el diseño y el argumento 2 es el nombre que le asiganmos*/
    TextView mtextViewiniciar;
    TextInputEditText mTextInputnombreUsuario;
    TextInputEditText getmTextInputNumero;

    Button mbtnRegister;
    AuthProvider mAuthProvider;
    FirebaseFirestore mFirestore;
    UsuariosPrivider musuariosProvider;
    AlertDialog mDialog;
    CheckBox maceptaTerminos;
    private Spinner mSpinnerComuna;
    private Spinner mSpinnerRegion;
    private String opcionSeleccionadaComuna;
    private String opcionSeleccionadaRegion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        mTextInputnombreUsuario = findViewById(R.id.textInputnombreUsuario);
        getmTextInputNumero = findViewById(R.id.textInputNumero);
        mbtnRegister = findViewById(R.id.btnRegister);
        // Referencias a los Spinners en el diseño
        mSpinnerComuna = findViewById(R.id.spinnerComuna);
        mSpinnerRegion = findViewById(R.id.spinnerRegion);
        // Configuración del Adapter y Listener para el Spinner Comuna
        ArrayAdapter<CharSequence> adapterComuna = ArrayAdapter.createFromResource(this, R.array.opcionescomuna, android.R.layout.simple_spinner_item);
        adapterComuna.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerComuna.setAdapter(adapterComuna);
        mSpinnerComuna.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                opcionSeleccionadaComuna = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Manejar el caso en que no se seleccione ninguna comuna.
            }
        });
        // Configuración del Adapter y Listener para el Spinner Región
        ArrayAdapter<CharSequence> adapterRegion = ArrayAdapter.createFromResource(this, R.array.opciones, android.R.layout.simple_spinner_item);
        adapterRegion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerRegion.setAdapter(adapterRegion);
        mSpinnerRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                opcionSeleccionadaRegion = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Manejar el caso en que no se seleccione ninguna región.
            }
        });



        mAuthProvider = new AuthProvider();
        musuariosProvider = new UsuariosPrivider();
        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere Un Momemnto")
                .setCancelable(false).build();
        maceptaTerminos=  findViewById(R.id.checkBoxTerms);
        CheckBox checkBoxTerms = findViewById(R.id.checkBoxTerms);

        String fullText = "Estoy de acuerdo con los Términos y condiciones";

        SpannableString spannableString = new SpannableString(fullText);

// Cambia el color del texto "Estoy de acuerdo con los"
        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(getResources().getColor(R.color.white));
        spannableString.setSpan(colorSpan1, 0, 24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

// Cambia el color del texto "Términos y condiciones"
        ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(getResources().getColor(R.color.OPCION));
        spannableString.setSpan(colorSpan2, 24, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        checkBoxTerms.setText(spannableString);

        mbtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

    }
    private void register() {
        String nombreUsuario = mTextInputnombreUsuario.getText().toString();
        String Numero = getmTextInputNumero.getText().toString();
        boolean aceptaTerminos = maceptaTerminos.isChecked();


        if (!nombreUsuario.isEmpty() && !Numero.isEmpty() && opcionSeleccionadaComuna != null && opcionSeleccionadaRegion != null) {
            if (aceptaTerminos) {
                updateUsuario(nombreUsuario, Numero, opcionSeleccionadaComuna, opcionSeleccionadaRegion);
            } else {
                Toast.makeText(this, "Debes aceptar los Términos y Condiciones para continuar", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Para continuar inserta todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUsuario(final String nombreUsuario, String Numero, String comunaSeleccionada, String regionSeleccionada) {

        String id = mAuthProvider.getUid();

        Usuarios usuario = new Usuarios();
        usuario.setId(id);
        usuario.setNombre(nombreUsuario);
        usuario.setTelefono(Numero);
        usuario.setComuna(comunaSeleccionada);
        usuario.setRegion(regionSeleccionada);
        usuario.setTimestamp(new Date().getTime());

        mDialog.show();

        musuariosProvider.completarInfo(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDialog.dismiss();
                if (task.isSuccessful()) {
                    Intent intent = new Intent(CompleteProfileActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish(); // Para cerrar la actividad actual y evitar que el usuario regrese a ella con el botón "Atrás".
                } else {
                    Toast.makeText(CompleteProfileActivity.this, "No se pudo almacenar el usuario en la base de datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

package com.tudu.tu_du.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tudu.tu_du.R;
import com.tudu.tu_du.models.Usuarios;
import com.tudu.tu_du.providers.AuthProvider;
import com.tudu.tu_du.providers.UsuariosPrivider;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class registerActivity extends AppCompatActivity {

    CircleImageView mcircleImageBack; /*argumento 1 es el tipo de campo en el diseño y el argumento 2 es el nombre que le asiganmos*/
    TextView mtextViewiniciar;
    TextInputEditText mTextInputnombreUsuario;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    TextInputEditText mTextInputconfirmarPassword;
    TextInputEditText mTextInputNumero;
    Button mbtnRegister;
    AuthProvider mAuthPrivider;
    UsuariosPrivider mUsuariosProvider;
    AlertDialog mDialog;
    CheckBox maceptaTerminos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mTextInputnombreUsuario = findViewById(R.id.textInputnombreUsuario);
        mTextInputEmail= findViewById(R.id.textInputEmail);
        mTextInputNumero = findViewById(R.id.textInputNumero);
        mTextInputPassword = findViewById(R.id.textInputPassword);
        mTextInputconfirmarPassword = findViewById(R.id.textInputconfirmarPassword);
        mbtnRegister = findViewById(R.id.btnRegister);
        mAuthPrivider = new AuthProvider();
        mUsuariosProvider = new UsuariosPrivider();
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
            public void onClick(View v) {
                register();
            }
        });

        mcircleImageBack = findViewById(R.id.circleImageBack);
        mcircleImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });
        mtextViewiniciar = findViewById(R.id.textViewiniciar);
        mtextViewiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void register() {

        String email = mTextInputEmail.getText().toString();

        String password = mTextInputPassword.getText().toString();
        String confirmarPassword = mTextInputconfirmarPassword.getText().toString();
        boolean aceptaTerminos = maceptaTerminos.isChecked();

        if ( !email.isEmpty()  && !password.isEmpty() && !confirmarPassword.isEmpty()) {
            if (isEmailValid(email)) {
                if (password.equals(confirmarPassword)) {
                    if (password.length() >= 6) {
                        if (aceptaTerminos) {
                            crearUsuario( email, password);
                        } else {
                            Toast.makeText(this, "Debes aceptar los Términos y Condiciones", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "La Contraseña debe tener al menos 6 Caracteres", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "Las Contraseñas No Coinciden", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "El Correo Ingresado es Inválido", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Todos Los Campos Son Obligatorios", Toast.LENGTH_LONG).show();
        }
    }

    private void crearUsuario(String Email,String Password){
        mDialog.show();
        mAuthPrivider.registrar(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = mAuthPrivider.getUid();
                    Usuarios usuario = new Usuarios();
                    usuario.setId(id);
                    usuario.setEmail(Email);
                    usuario.setTimestamp(new Date().getTime());

                    mUsuariosProvider.crearUsuario(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            mDialog.dismiss();
                            if (task.isSuccessful()){
                                Intent intent = new Intent(registerActivity.this,HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }else{
                                Toast.makeText(registerActivity.this, "El Usuario no se almaceno ", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    Toast.makeText(registerActivity.this, "El Usuario se registro Correctamente", Toast.LENGTH_LONG).show();

                }
                else {
                    mDialog.dismiss();
                    Toast.makeText(registerActivity.this, "Email ya Registrado ", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    /*Verificar Email Valido*/
    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
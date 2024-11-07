package com.tudu.tu_du.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.tudu.tu_du.R;

import com.tudu.tu_du.models.Usuarios;
import com.tudu.tu_du.providers.AuthProvider;
import com.tudu.tu_du.providers.ImageProvider;
import com.tudu.tu_du.providers.UsuariosPrivider;
import com.tudu.tu_du.utils.FileUtil;
import com.tudu.tu_du.utils.ViewedMensajesHelper;


import java.io.File;
import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;



public class EditProfileActivity extends AppCompatActivity {

    CircleImageView mCircleImageViewback;
    CircleImageView mCircleImageViewProfile;
    ImageView mImageViewCover;

    TextInputEditText mTextInputnombreUsuario;
    TextInputEditText mTextInputNumero;

    AlertDialog.Builder mBuilderSelector;
    CharSequence options[];
    private final int GALLERY_REQUEST_CODE_PROFILE = 1;
    private final int GALLERY_REQUEST_CODE_COVER = 2;
    private final int PHOTO_REQUEST_CODE_PROFILE = 3;
    private final int PHOTO_REQUEST_CODE_COVER = 4;
    //Foto 1
    String mAbsolutePhotoPath;
    String mPhotoPath;
    File mPhotoFile;
    //Foto 2
    String mAbsolutePhotoPath2;
    String mPhotoPath2;
    File mPhotoFile2;

    File mImageFile;
    File mImageFile2;
    String mnombreUsuario ="";
    String mTelefono="";
    String mImageProfile = "";
    String mImageCover = "";
    ImageProvider mImageProvider;

    Button btnEditProfile;
    AuthProvider mAuthProvider;
    FirebaseFirestore mFirestore;
    UsuariosPrivider musuariosProvider;
    AlertDialog mDialog;
    private Spinner mSpinnerComuna;
    private Spinner mSpinnerRegion;
    private String opcionSeleccionadaComuna;
    private String opcionSeleccionadaRegion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        mCircleImageViewback = findViewById(R.id.circleImageBack);
        mCircleImageViewProfile = findViewById(R.id.circleImageProfile);
        mImageViewCover = findViewById(R.id.imageviewCover);
        mTextInputnombreUsuario = findViewById(R.id.textInputnombreUsuario);
        mTextInputNumero = findViewById(R.id.textInputNumero);
        musuariosProvider = new UsuariosPrivider();
        mAuthProvider = new AuthProvider();
        mImageProvider = new ImageProvider();
        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere Un Momento")
                .setCancelable(false).build();
        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Selecciona una Opcion");
        options = new CharSequence[]{"Imagen de galeria","Tomar fotografia"};
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickeditprofile();
            }
        });
        mCircleImageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOptionImage(1);

            }
        });
        mImageViewCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOptionImage(2);
            }
        });
        mCircleImageViewback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });
        obtenerUsuario();
    }

    private void  obtenerUsuario(){
        musuariosProvider.getUsuario(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("nombre")){
                        mnombreUsuario = documentSnapshot.getString("nombre");
                        mTextInputnombreUsuario.setText(mnombreUsuario);
                    }
                    if (documentSnapshot.contains("telefono")){
                        mTelefono = documentSnapshot.getString("telefono");
                        mTextInputNumero.setText(mTelefono);
                    }
                    if (documentSnapshot.contains("imagenperfil")){
                        mImageProfile = documentSnapshot.getString("imagenperfil");
                        if (mImageProfile != null){
                            if(!mImageProfile.isEmpty()) {
                                Picasso.with(EditProfileActivity.this).load(mImageProfile).into(mCircleImageViewProfile);
                            }
                        }

                    }
                   if (documentSnapshot.contains("imagencover")){
                       mImageCover = documentSnapshot.getString("imagencover");
                       if (mImageCover != null){
                           if(!mImageCover.isEmpty()) {
                               Picasso.with(EditProfileActivity.this).load(mImageCover).into(mImageViewCover);
                           }
                       }

                   }








                }
            }
        });

    }

    private void clickeditprofile() {
        mnombreUsuario = mTextInputnombreUsuario.getText().toString();
        mTelefono = mTextInputNumero.getText().toString();
        if(!mnombreUsuario.isEmpty() && !mTelefono.isEmpty()) {
            if (mImageFile != null && mImageFile2 != null ) {
                GuardarImagen(mImageFile, mImageFile2);
            }
            // TOMO LAS DOS FOTOS DE LA CAMARA
            else if (mPhotoFile != null && mPhotoFile2 != null) {
                GuardarImagen(mPhotoFile, mPhotoFile2);
            }
            else if (mImageFile != null && mPhotoFile2 != null) {
                GuardarImagen(mImageFile, mPhotoFile2);
            }
            else if (mPhotoFile != null && mImageFile2 != null) {
                GuardarImagen(mPhotoFile, mImageFile2);
            }
            else if (mPhotoFile != null){
                guardarUnaImagen(mPhotoFile, true);
            }else if (mPhotoFile2 != null){
                guardarUnaImagen(mPhotoFile2, false);
            } else if (mImageFile != null){
                guardarUnaImagen(mImageFile, true);
            }else if (mImageFile2 != null){
                guardarUnaImagen(mImageFile2, false);
            }
            else {
                Usuarios usuarios = new Usuarios();
                usuarios.setNombre(mnombreUsuario);
                usuarios.setTelefono(mTelefono);
                usuarios.setId(mAuthProvider.getUid());
                updateInfo(usuarios);
            }
        }
        else {
            Toast.makeText(this, "Ingresa los datos a modificar", Toast.LENGTH_SHORT).show();
        }
    }

    private void GuardarImagen(File imageFile1, final File imageFile2) {
        mDialog.show();
        mImageProvider.save(EditProfileActivity.this, imageFile1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String urlPerfil = uri.toString();

                            mImageProvider.save(EditProfileActivity.this, imageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(Task<UploadTask.TaskSnapshot> taskImage2) {
                                    if (taskImage2.isSuccessful()) {
                                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                String urlCover = uri2.toString();
                                                Usuarios usuario = new Usuarios();
                                                usuario.setNombre(mnombreUsuario);
                                                usuario.setTelefono(mTelefono);
                                                usuario.setImagenperfil(urlPerfil);
                                                usuario.setImagencover(urlCover);
                                                usuario.setId(mAuthProvider.getUid());
                                                updateInfo(usuario);

                                            }
                                        });
                                    }
                                    else {
                                        mDialog.dismiss();
                                        Toast.makeText(EditProfileActivity.this, "La imagen numero 2 no se pudo guardar", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
                else {
                    mDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Hubo error al almacenar la imagen", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void guardarUnaImagen(File Image, boolean esimagenPerfil){
        mDialog.show();
        mImageProvider.save(EditProfileActivity.this, Image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String url = uri.toString();
                            Usuarios usuario = new Usuarios();
                            usuario.setNombre(mnombreUsuario);
                            usuario.setTelefono(mTelefono);
                            if(esimagenPerfil){
                                usuario.setImagenperfil(url);
                                usuario.setImagencover(mImageCover);
                            }else{
                                usuario.setImagencover(url);
                                usuario.setImagenperfil(mImageProfile);
                            }


                            usuario.setId(mAuthProvider.getUid());
                            updateInfo(usuario);


                        }
                    });
                }
                else {
                    mDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Hubo error al almacenar la imagen", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    private void updateInfo(Usuarios usuario){
        if (mDialog.isShowing()){
            mDialog.show();
        }

        musuariosProvider.completarInfo(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                mDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "La informacion se actualizo correctamente", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(EditProfileActivity.this, "La informacion no se pudo actualizar", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void selectOptionImage(final int numberImage) {

        mBuilderSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    if (numberImage == 1) {
                        abrirGaleria(GALLERY_REQUEST_CODE_PROFILE);
                    }
                    else if (numberImage == 2) {
                        abrirGaleria(GALLERY_REQUEST_CODE_COVER);
                    }
                }
                else if (i == 1){
                    if (numberImage == 1) {
                        tomarFoto(PHOTO_REQUEST_CODE_PROFILE);
                    }
                    else if (numberImage == 2) {
                        tomarFoto(PHOTO_REQUEST_CODE_COVER);
                    }
                }
            }
        });

        mBuilderSelector.show();

    }

    private void tomarFoto(int requestCode) {
        //Toast.makeText(this, "Selecciono tomar foto", Toast.LENGTH_SHORT).show();
        Intent tomarfotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(tomarfotoIntent.resolveActivity(getPackageManager()) != null){
            File PhotoFile = null;
            try {
                PhotoFile = crearPhotoFile(requestCode);

            }catch (Exception e){
                Toast.makeText(this, "Hubo un error con el archivo "+ e.getMessage(), Toast.LENGTH_LONG).show();
            }
            if (PhotoFile != null){
                Uri photoUri = FileProvider.getUriForFile(EditProfileActivity.this,"com.tudu.tu_du",PhotoFile);
                tomarfotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                startActivityForResult(tomarfotoIntent,PHOTO_REQUEST_CODE_COVER);


            }

        }
    }

    private File crearPhotoFile(int requestCode) throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(
                new Date()+"_photo",
                ".jpg",
                storageDir

        );
        if (requestCode == PHOTO_REQUEST_CODE_PROFILE){
            mPhotoPath = "file:"+ photoFile.getAbsolutePath();
            mAbsolutePhotoPath = photoFile.getAbsolutePath();
        }
        else if(requestCode == PHOTO_REQUEST_CODE_COVER){
            mPhotoPath2 = "file:"+ photoFile.getAbsolutePath();
            mAbsolutePhotoPath2 = photoFile.getAbsolutePath();
        }
        return photoFile;
    }
    private void abrirGaleria(int requestCode) {//abre la galeria
        Intent galeriaIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galeriaIntent.setType("image/*");//muestra la galeria
        startActivityForResult(galeriaIntent,requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * Seleccion imagen galeria
         */
        if(requestCode == GALLERY_REQUEST_CODE_PROFILE && resultCode == RESULT_OK){
            try {
                mImageFile = FileUtil.from(this,data.getData());
                mCircleImageViewProfile.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));//muestra la imagen seleccionada

            }catch (Exception e){
                Log.d("error","Se produjo un error" + e.getMessage());
                Toast.makeText(this, "Se produjo un error "+ e.getMessage(), Toast.LENGTH_LONG).show();

            }
        }
        if(requestCode == GALLERY_REQUEST_CODE_COVER && resultCode == RESULT_OK){
            try {
                mImageFile2 = FileUtil.from(this,data.getData());
                mImageViewCover.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));//muestra la imagen seleccionada

            }catch (Exception e){
                Log.d("error","Se produjo un error" + e.getMessage());
                Toast.makeText(this, "Se produjo un error "+ e.getMessage(), Toast.LENGTH_LONG).show();

            }
        }
        /**
         *Seleccion de Foto
         */
        if(requestCode == PHOTO_REQUEST_CODE_PROFILE && resultCode == RESULT_OK){
            mImageFile = null;
            mPhotoFile =  new File(mAbsolutePhotoPath);
            Picasso.with(EditProfileActivity.this).load(mPhotoPath).into(mCircleImageViewProfile);


        }
        /**
         *Seleccion de Foto 2
         */
        if(requestCode == PHOTO_REQUEST_CODE_COVER && resultCode == RESULT_OK){
            mImageFile2 = null;
            mPhotoFile2 =  new File(mAbsolutePhotoPath2);
            Picasso.with(EditProfileActivity.this).load(mPhotoPath2).into(mImageViewCover);


        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        ViewedMensajesHelper.updateOnline(true,EditProfileActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMensajesHelper.updateOnline(false,EditProfileActivity.this);
    }
}
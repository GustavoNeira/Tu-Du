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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;

import com.squareup.picasso.Picasso;

import com.tudu.tu_du.R;
import com.tudu.tu_du.models.Post;
import com.tudu.tu_du.providers.AuthProvider;
import com.tudu.tu_du.providers.ImageProvider;
import com.tudu.tu_du.providers.PostProvider;
import com.tudu.tu_du.utils.FileUtil;
import com.tudu.tu_du.utils.ViewedMensajesHelper;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class PostActivity extends AppCompatActivity {
    ImageView mImagenPost1;
    ImageView mImagenPost2;
    File mImageFile;
    File mImageFile2;
    Button mbuttonPost;
    ImageProvider mImageProvider;
    PostProvider mpostProvider;
    AuthProvider mAuthProvider;
    TextInputEditText mTextInputTitulo;
    TextInputEditText mTextInputprecio;
    TextInputEditText mTextInputdescripcion;
    ImageView mImageViewServicios;
    ImageView mImageViewProductos;
    TextView mTextViewcategoria;
    CircleImageView mcircleImageViewback;
    CharSequence opciones[];

    private final int GALLERY_REQUEST_CODE = 1;
    private final int GALLERY_REQUEST_CODE_2 = 2;
    private final int PHOTO_REQUEST_CODE = 3;
    private final int PHOTO_REQUEST_CODE_2 = 4;
    String mCategory="";
    String mtitulo ="";
    String mdescripcion ="";
    String mprecio ="";
    AlertDialog mDialog;
    AlertDialog.Builder mBuilderSelector;

    String mAbsolutePhotoPath;
    String mPhotoPath;
    File mPhotoFile;

    String mAbsolutePhotoPath2;
    String mPhotoPath2;
    File mPhotoFile2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mImageProvider = new ImageProvider();

        mImagenPost1 = findViewById(R.id.imagenPost1);
        mImagenPost2 = findViewById(R.id.imagenPost2);
        mbuttonPost = findViewById(R.id.btnPost);

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere Un Momento")
                .setCancelable(false).build();
        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Selecciona una Opcion");
        opciones = new CharSequence[]{"Imagen de galeria","Tomar fotografia"};

        mTextInputTitulo = findViewById(R.id.textInputnombreservicio_producto);
        mTextInputprecio = findViewById(R.id.textInputprecio);
        mTextInputdescripcion = findViewById(R.id.textInputdescripcion);
        mImageViewServicios = findViewById(R.id.imageViewservicio);
        mImageViewProductos = findViewById(R.id.imageViewproductos);
        mTextViewcategoria = findViewById(R.id.textViewcategoria);
        mcircleImageViewback = findViewById(R.id.circleImageBack);
        mpostProvider= new PostProvider();
        mAuthProvider = new AuthProvider();


        mcircleImageViewback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mbuttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPost();
            }
        });
        mImagenPost1.setOnClickListener((View)  -> {
            selectOptionImage(1);

        });
        mImagenPost2.setOnClickListener((View)  -> {
            selectOptionImage(2);
        });
        mImageViewServicios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategory = "Servicios";
                mTextViewcategoria.setText(mCategory);
            }
        });
        mImageViewProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategory = "Productos";
                mTextViewcategoria.setText(mCategory);

            }
        });
    }

    private void selectOptionImage(final int numberImage) {

        mBuilderSelector.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    if (numberImage == 1) {
                        abrirGaleria(GALLERY_REQUEST_CODE);
                    }
                    else if (numberImage == 2) {
                        abrirGaleria(GALLERY_REQUEST_CODE_2);
                    }
                }
                else if (i == 1){
                    if (numberImage == 1) {
                        tomarFoto(PHOTO_REQUEST_CODE);
                    }
                    else if (numberImage == 2) {
                        tomarFoto(PHOTO_REQUEST_CODE_2);
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
                Uri photoUri = FileProvider.getUriForFile(PostActivity.this,"com.tudu.tu_du",PhotoFile);
                tomarfotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                startActivityForResult(tomarfotoIntent,PHOTO_REQUEST_CODE);


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
        if (requestCode == PHOTO_REQUEST_CODE) {
            mPhotoPath = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath = photoFile.getAbsolutePath();
        }
        else if (requestCode == PHOTO_REQUEST_CODE_2) {
            mPhotoPath2 = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath2 = photoFile.getAbsolutePath();
        }
        return photoFile;
    }

    private void clickPost() {
        mtitulo = mTextInputTitulo.getText().toString();
        mdescripcion = mTextInputdescripcion.getText().toString();
        mprecio = mTextInputprecio.getText().toString();
       if(!mtitulo.isEmpty() && !mdescripcion.isEmpty() && !mprecio.isEmpty() && !mCategory.isEmpty()){
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
           else {
               Toast.makeText(this, "Debes seleccionar 2 imagenes", Toast.LENGTH_SHORT).show();
           }
       }else{
           Toast.makeText(PostActivity.this, "Completa los campos para publicar", Toast.LENGTH_SHORT).show();
       }
    }

    private void GuardarImagen(File mImageFile, final File mImageFile2) {
        mDialog.show();
        mImageProvider.save(PostActivity.this, mImageFile).addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
                mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        mImageProvider.save(PostActivity.this,mImageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(Task<UploadTask.TaskSnapshot> taskimg2) {
                                if(taskimg2.isSuccessful()){
                                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri2) {
                                            String url2 = uri2.toString();
                                            Post post = new Post();
                                            post.setImg1(url);
                                            post.setImg2(url2);
                                            post.setTitulo(mtitulo.toLowerCase());
                                            post.setDescripcion(mdescripcion);
                                            post.setPrecio(mprecio);
                                            post.setCategoria(mCategory);
                                            post.setIdUser(mAuthProvider.getUid());
                                            post.setTimestamp(new Date().getTime());
                                            mpostProvider.save(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(Task<Void> tasksave) {
                                                    mDialog.dismiss();
                                                    if (tasksave.isSuccessful()) {
                                                        LimpiarFormulario();




                                                                Toast.makeText(PostActivity.this, "La informacion se almaceno correctamente", Toast.LENGTH_SHORT).show();


                                                    } else {

                                                                Toast.makeText(PostActivity.this, "La informacion no se pudo almacenar correctamente", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });


                                        }
                                    });
                                }
                                else {
                                    mDialog.dismiss();
                                    Toast.makeText(PostActivity.this, "la imagen 2 no se pudo Guardar", Toast.LENGTH_SHORT).show();

                                }

                            }
                        });
                    }
                });

            } else {
                mDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PostActivity.this, "Hubo un error al alamcenar la imagen", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void LimpiarFormulario() {
        mTextInputTitulo.setText("");
        mTextInputdescripcion.setText("");
        mTextViewcategoria.setText("");
        mTextInputprecio.setText("");
        mImagenPost1.setImageResource(R.drawable.ic_addfoto);
        mImagenPost2.setImageResource(R.drawable.ic_addfoto);
        mtitulo = "";
        mdescripcion = "";
        mCategory = "Categorias";
        mImageFile = null;
        mImageFile2 = null;

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
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            try {
                mImageFile = FileUtil.from(this,data.getData());
                mImagenPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));//muestra la imagen seleccionada

            }catch (Exception e){
                Log.d("error","Se produjo un error" + e.getMessage());
                Toast.makeText(this, "Se produjo un error "+ e.getMessage(), Toast.LENGTH_LONG).show();

            }
        }
        if(requestCode == GALLERY_REQUEST_CODE_2 && resultCode == RESULT_OK){
            try {
                mImageFile2 = FileUtil.from(this,data.getData());
                mImagenPost2.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));//muestra la imagen seleccionada

            }catch (Exception e){
                Log.d("error","Se produjo un error" + e.getMessage());
                Toast.makeText(this, "Se produjo un error "+ e.getMessage(), Toast.LENGTH_LONG).show();

            }
        }
       /**
        *Seleccion de Foto
       */
       if(requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK){
           mImageFile = null;
           mPhotoFile =  new File(mAbsolutePhotoPath);
           Picasso.with(PostActivity.this).load(mPhotoPath).into(mImagenPost1);


       }
        /**
         *Seleccion de Foto 2
         */
        if(requestCode == PHOTO_REQUEST_CODE_2 && resultCode == RESULT_OK){
            mImageFile2 = null;
            mPhotoFile2 =  new File(mAbsolutePhotoPath2);
            Picasso.with(PostActivity.this).load(mPhotoPath2).into(mImagenPost2);


        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        ViewedMensajesHelper.updateOnline(true,PostActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMensajesHelper.updateOnline(false,PostActivity.this);
    }
}
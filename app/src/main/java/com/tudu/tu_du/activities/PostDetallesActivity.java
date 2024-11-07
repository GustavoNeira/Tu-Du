package com.tudu.tu_du.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;
import com.tudu.tu_du.R;
import com.tudu.tu_du.adapters.ComentariosAdapter;
import com.tudu.tu_du.adapters.PostsAdapter;
import com.tudu.tu_du.adapters.SliderAdapter;
import com.tudu.tu_du.models.Comentarios;
import com.tudu.tu_du.models.FCMBody;
import com.tudu.tu_du.models.FCMResponse;
import com.tudu.tu_du.models.Post;
import com.tudu.tu_du.models.SliderItem;
import com.tudu.tu_du.providers.AuthProvider;
import com.tudu.tu_du.providers.ComentariosProvider;
import com.tudu.tu_du.providers.LikeProvider;
import com.tudu.tu_du.providers.NotificationProvider;
import com.tudu.tu_du.providers.PostProvider;
import com.tudu.tu_du.providers.TokenProvider;
import com.tudu.tu_du.providers.UsuariosPrivider;
import com.tudu.tu_du.utils.RelativeTime;
import com.tudu.tu_du.utils.ViewedMensajesHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetallesActivity extends AppCompatActivity {

    SliderView mSliderView;
    SliderAdapter mSliderAdapter;
    List<SliderItem> mSliderItems = new ArrayList<>();
    PostProvider mPostProvider;
    UsuariosPrivider mUsuariosPrivider;
    ComentariosProvider mComentariosProvider;
    LikeProvider mLikeProvider;
    AuthProvider mAuthProvider;
    ComentariosAdapter mComentariosAdapter;
    NotificationProvider mNotificationProvider;
    TokenProvider mTokenProvider;

    String mExtraPostId;
    String midUser = "";

    Toolbar mToolbar;


    TextView mTextViewTituloPost;
    TextView mTextViewNombreusuario;
    TextView mTextViewDescripcionPost;
    TextView mTextViewTelefono;
    TextView mTextViewNombreCategoria;
    TextView mTextViewprecio;
    TextView mTextViewRelativeTime;
    TextView mTextViewLikes;

    ImageView mImageViewCategoria;
    ImageView mImageViewprecio;
    CircleImageView mCircleImageViewPerfil;
    FloatingActionButton mFabComentarios;
    RecyclerView mRecyclerViewComentarios;


    Button mButtonVerPerfil;
    ListenerRegistration mListenerRegistration;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detalles);
        mSliderView = findViewById(R.id.imageSlider);
        mTextViewTituloPost = findViewById(R.id.textViewTituloPost);
        mTextViewNombreusuario = findViewById(R.id.textViewNombreUsuario);
        mTextViewDescripcionPost = findViewById(R.id.textViewDescripcion);
        mTextViewTelefono = findViewById(R.id.textViewTelefono);
        mTextViewNombreCategoria = findViewById(R.id.textViewnombrecategoria);
        mTextViewprecio = findViewById(R.id.textViewprecio);
        mFabComentarios = findViewById(R.id.fabComentario);
        mTextViewRelativeTime = findViewById(R.id.textViewRelativeTime);
        mRecyclerViewComentarios = findViewById(R.id.recyclerviewComentarios);
        mRecyclerViewComentarios.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PostDetallesActivity.this);
        mRecyclerViewComentarios.setLayoutManager(linearLayoutManager);
        mTextViewLikes=findViewById(R.id.textViewLikes);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        mImageViewCategoria = findViewById(R.id.imageViewcategoria);

        mImageViewprecio = findViewById(R.id.imageViewprecio);
        mCircleImageViewPerfil = findViewById(R.id.circleImageProfile);
        mButtonVerPerfil = findViewById(R.id.btnVerPerfil);
        mPostProvider  = new PostProvider();
        mUsuariosPrivider = new UsuariosPrivider();
        mComentariosProvider = new ComentariosProvider();
        mAuthProvider = new AuthProvider();
        mLikeProvider = new LikeProvider();
        mNotificationProvider = new NotificationProvider();
        mTokenProvider = new TokenProvider();


        mExtraPostId = getIntent().getStringExtra("id");


        mFabComentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              mostrarDialogComentarios();
            }
        });

        mButtonVerPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarPerfil();
            }
        });

        getPost();
        getNumberLikes();
    }

    private void getNumberLikes() {
      mListenerRegistration =  mLikeProvider.getLikebyPost(mExtraPostId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException error) {
                if (queryDocumentSnapshots != null) {
                    int numeroLikes = queryDocumentSnapshots.size();
                    if (numeroLikes == 1){
                        mTextViewLikes.setText(numeroLikes + " Me Gusta");
                    }else{
                        mTextViewLikes.setText(numeroLikes + " Me Gustas");
                    }
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = mComentariosProvider.getComentariobyPost(mExtraPostId);
        FirestoreRecyclerOptions<Comentarios> options =
                new FirestoreRecyclerOptions.Builder<Comentarios>()
                .setQuery(query,Comentarios.class)
                .build();
        mComentariosAdapter = new ComentariosAdapter(options,PostDetallesActivity.this);
        mRecyclerViewComentarios.setAdapter(mComentariosAdapter);
        mComentariosAdapter.startListening();
        ViewedMensajesHelper.updateOnline(true,PostDetallesActivity.this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mComentariosAdapter.stopListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMensajesHelper.updateOnline(false,PostDetallesActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListenerRegistration != null) {
            mListenerRegistration.remove();
        }
    }

    private void mostrarDialogComentarios() {
        AlertDialog.Builder alert =  new AlertDialog.Builder(PostDetallesActivity.this);
        alert.setTitle("¡Comentario!");
        alert.setMessage("Ingresa un comentario");
        EditText editText = new EditText(PostDetallesActivity.this);
        editText.getBackground().setColorFilter(getResources().getColor(R.color.red_700), PorterDuff.Mode.SRC_IN);
        editText.setHint("texto");


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT


        );
        params.setMargins(36,0,36,36);
        editText.setLayoutParams(params);
        RelativeLayout container = new RelativeLayout(PostDetallesActivity.this);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT

        );
        container.setLayoutParams(relativeParams);
        container.addView(editText);
        alert.setView(container);
        alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = editText.getText().toString();
                if (!value.isEmpty()) {
                    crearComentario(value);
                }
                else {
                    Toast.makeText(PostDetallesActivity.this, "Debe ingresar el comentario", Toast.LENGTH_SHORT).show();
                }

            }
        });
        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.show();

    }

    private void crearComentario(String value) {
        Comentarios comentario = new Comentarios();
        comentario.setComentario(value);
        comentario.setIdPost(mExtraPostId);
        comentario.setIdUser(mAuthProvider.getUid());
        comentario.setTimestamp(new Date().getTime());

        mComentariosProvider.create(comentario).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    sendNotification(value);
                    Toast.makeText(PostDetallesActivity.this, "El comentario se registro correctamente", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(PostDetallesActivity.this, "No se pudo crear el Comentario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendNotification(String comentario) {
        if (midUser == null) {
            return;
        }
        mTokenProvider.getToken(midUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("token")) {
                        String token = documentSnapshot.getString("token");
                        Map<String, String> data = new HashMap<>();
                        
                        data.put("title", "Nuevo Comentario");
                        data.put("body", comentario);
                        FCMBody body = new FCMBody(token,"high","4500s",data);
                        mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                            @Override
                            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                               if (response.body() != null) {
                                   if (response.body().getSuccess() == 1){
                                       Toast.makeText(PostDetallesActivity.this, "La Notificacion se envio correctamente", Toast.LENGTH_SHORT).show();
                                   }
                                   else {
                                       Toast.makeText(PostDetallesActivity.this, "La Notificacion no se pudo enviar", Toast.LENGTH_SHORT).show();
                                   }
                               }
                               else {Toast.makeText(PostDetallesActivity.this, "La Notificacion no se pudo enviar", Toast.LENGTH_SHORT).show();}
                            }

                            @Override
                            public void onFailure(Call<FCMResponse> call, Throwable t) {

                            }
                        });
                    }
                }
                else {
                    Toast.makeText(PostDetallesActivity.this, "El token de notificaciones del usuario no existe", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void mostrarPerfil() {
        if (!midUser.equals("")) {
            Intent intent = new Intent(PostDetallesActivity.this,PerfilUsuarioActivity.class);
            intent.putExtra("idUser",midUser );
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "Intentelo nuevamente", Toast.LENGTH_SHORT).show();
        }

    }

    private void instanceSlider(){
        mSliderAdapter = new SliderAdapter(PostDetallesActivity.this, mSliderItems);
        mSliderView.setSliderAdapter(mSliderAdapter);
        mSliderView.setIndicatorAnimation(IndicatorAnimationType.THIN_WORM);
        mSliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        mSliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        mSliderView.setIndicatorSelectedColor(Color.WHITE);
        mSliderView.setIndicatorUnselectedColor(Color.GRAY);
        mSliderView.setScrollTimeInSec(3);
        mSliderView.setAutoCycle(true);
        mSliderView.startAutoCycle();
    }
    private void getPost(){
        mPostProvider.getPostById(mExtraPostId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("img1")){
                        String image1 = documentSnapshot.getString("img1");
                        SliderItem item = new SliderItem();
                        item.setImageUrl(image1);
                        mSliderItems.add(item);
                    }
                    if (documentSnapshot.contains("img2")){
                        String image2 = documentSnapshot.getString("img2");
                        SliderItem item = new SliderItem();
                        item.setImageUrl(image2);
                        mSliderItems.add(item);
                    } if (documentSnapshot.contains("titulo")){
                        String titulo  = documentSnapshot.getString("titulo");
                        mTextViewTituloPost.setText(titulo.toUpperCase());

                    }if (documentSnapshot.contains("precio")){
                        String precio  = documentSnapshot.getString("precio");
                        mTextViewprecio.setText(precio);

                    }if (documentSnapshot.contains("precio")){
                        String precio  = documentSnapshot.getString("precio");
                        mImageViewprecio.setImageResource(R.drawable.ic_preciow);

                    }if (documentSnapshot.contains("descripcion")){
                        String descripcion  = documentSnapshot.getString("descripcion");
                        mTextViewDescripcionPost.setText(descripcion);

                    }if (documentSnapshot.contains("titulo")){
                        String titulo  = documentSnapshot.getString("titulo");
                        mTextViewTituloPost.setText(titulo);

                    }if (documentSnapshot.contains("categoria")){
                        String categoria  = documentSnapshot.getString("categoria");
                        if (categoria.equals("Servicios")){
                            mImageViewCategoria.setImageResource(R.drawable.ic_service);
                        }
                        else if (categoria.equals("Productos")){
                            mImageViewCategoria.setImageResource(R.drawable.ic_product);
                        }
                        mTextViewNombreCategoria.setText(categoria);

                    }if (documentSnapshot.contains("idUser")){
                        midUser  = documentSnapshot.getString("idUser");
                        getUsuarioInfo(midUser);


                    }if (documentSnapshot.contains("timestamp")){
                        long timestamp  = documentSnapshot.getLong("timestamp");
                        String RelativeTime = com.tudu.tu_du.utils.RelativeTime.getTimeAgo(timestamp,PostDetallesActivity.this);
                        mTextViewRelativeTime.setText(RelativeTime);


                    }
                    instanceSlider();

                }
            }
        });
    }

    private void getUsuarioInfo(String idUser) {
        mUsuariosPrivider.getUsuario(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("nombre")){
                        String nombre = documentSnapshot.getString("nombre");
                        mTextViewNombreusuario.setText(nombre);

                    } if (documentSnapshot.contains("telefono")){
                        String telefono = documentSnapshot.getString("telefono");
                        mTextViewTelefono.setText(telefono);

                    }if (documentSnapshot.contains("imagenperfil")){
                        String imagenperfil = documentSnapshot.getString("imagenperfil");
                        Picasso.with(PostDetallesActivity.this).load(imagenperfil).into(mCircleImageViewPerfil);


                    }
                }

            }
        });
    }
}
package com.tudu.tu_du.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.tudu.tu_du.R;
import com.tudu.tu_du.adapters.MyPostAdapter;
import com.tudu.tu_du.models.Post;
import com.tudu.tu_du.providers.AuthProvider;
import com.tudu.tu_du.providers.PostProvider;
import com.tudu.tu_du.providers.UsuariosPrivider;
import com.tudu.tu_du.utils.ViewedMensajesHelper;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilUsuarioActivity extends AppCompatActivity {
    LinearLayout mLinearLayouteditprofile;
    View mView;
    TextView mTextViewNombrePerfil;
    TextView mTextViewTelefono;
    TextView mTextViewEmail;
    TextView mTextViewNPost;
    TextView mTextViewPostExist;
    FloatingActionButton mFabChat;

    ImageView mImageViewCover;
    CircleImageView mCircleImageViewPerfil;
    Toolbar mToolbar;
    ListenerRegistration mListenerRegistration;


    UsuariosPrivider mUsuariosPrivider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;
    MyPostAdapter mMyPostAdapter;

    String mExtrausuario;
    RecyclerView mRecyclerViewMyPost;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        mLinearLayouteditprofile = findViewById(R.id.linearLayoutEditPerfil);
        mTextViewEmail = findViewById(R.id.textViewEmail);
        mTextViewNombrePerfil = findViewById(R.id.textViewNombrePerfil);
        mTextViewTelefono = findViewById(R.id.textViewTelefono);
        mTextViewEmail = findViewById(R.id.textViewEmail);
        mTextViewPostExist = findViewById(R.id.textViewPostExist);
        mTextViewNPost = findViewById(R.id.textViewNPost);
        mCircleImageViewPerfil = findViewById(R.id.circleImageProfile);
        mImageViewCover = findViewById(R.id.imageviewCover);
        mFabChat = findViewById(R.id.fabChat);




        mRecyclerViewMyPost = findViewById(R.id.recyclerviewMyPost);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PerfilUsuarioActivity.this);
        mRecyclerViewMyPost.setLayoutManager(linearLayoutManager);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mUsuariosPrivider = new UsuariosPrivider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();

        mExtrausuario = getIntent().getStringExtra("idUser");


        if (mAuthProvider.getUid().equals(mExtrausuario)){
            mFabChat.setEnabled(false);
        }
        mFabChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotToChatActivity();
            }
        });

        obtenerUsuario();
        getNPost();
        verificarExistePost();
    }

    private void gotToChatActivity() {
        Intent intent = new Intent(PerfilUsuarioActivity.this,ChatActivity.class);
        intent.putExtra("idUser1",mAuthProvider.getUid());
        intent.putExtra("idUser2",mExtrausuario);
        startActivity(intent);
    }

    private void verificarExistePost() {
      mListenerRegistration =  mPostProvider.getPostByIdUsuario(mExtrausuario).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                if (value != null) {
                    int numeroPost = value.size();
                    if (numeroPost > 0) {
                        mTextViewPostExist.setText("Publicaciones");
                        mTextViewPostExist.setTextColor(Color.BLACK);
                    }
                    else {
                        mTextViewPostExist.setText("No hay Publicaciones");
                        mTextViewPostExist.setTextColor(Color.GRAY);
                    }
                }

            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getPostByIdUsuario(mExtrausuario);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query,Post.class)
                .build();
        mMyPostAdapter = new MyPostAdapter(options,PerfilUsuarioActivity.this);
        mRecyclerViewMyPost.setAdapter(mMyPostAdapter);
        mMyPostAdapter.startListening();
        ViewedMensajesHelper.updateOnline(true,PerfilUsuarioActivity.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mMyPostAdapter.stopListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMensajesHelper.updateOnline(false,PerfilUsuarioActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListenerRegistration != null){
            mListenerRegistration.remove();
        }
    }

    private void getNPost() {
        mPostProvider.getPostByIdUsuario(mExtrausuario).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numeroPost = queryDocumentSnapshots.size();
                mTextViewNPost.setText(String.valueOf(numeroPost));

            }
        });
    }
    private void obtenerUsuario() {
        mUsuariosPrivider.getUsuario(mExtrausuario).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("email")) {
                        String email = documentSnapshot.getString("email");
                        mTextViewEmail.setText(email);
                    }if (documentSnapshot.contains("telefono")) {
                        String telefono = documentSnapshot.getString("telefono");
                        mTextViewTelefono.setText(telefono);
                    }if (documentSnapshot.contains("nombre")) {
                        String nombrePerfil = documentSnapshot.getString("nombre");
                        mTextViewNombrePerfil.setText(nombrePerfil);
                    }if (documentSnapshot.contains("nombre")) {
                        String nombre = documentSnapshot.getString("nombre");
                        mTextViewNombrePerfil.setText(nombre);
                    }if (documentSnapshot.contains("imagenperfil")) {
                        String imagenperfil = documentSnapshot.getString("imagenperfil");
                        if (imagenperfil != null){
                            if (!imagenperfil.isEmpty()){
                                Picasso.with(PerfilUsuarioActivity.this).load(imagenperfil).into(mCircleImageViewPerfil);
                            }
                        }
                    }

                    }if (documentSnapshot.contains("imagencover")) {
                        String imagencover = documentSnapshot.getString("imagencover");
                        if (imagencover != null){
                            if (!imagencover.isEmpty()){
                                Picasso.with(PerfilUsuarioActivity.this).load(imagencover).into(mImageViewCover);
                            }
                        }

                    }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }
}
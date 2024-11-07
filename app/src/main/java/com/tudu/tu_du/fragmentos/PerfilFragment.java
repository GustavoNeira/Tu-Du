package com.tudu.tu_du.fragmentos;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.tudu.tu_du.R;
import com.tudu.tu_du.activities.EditProfileActivity;
import com.tudu.tu_du.adapters.MyPostAdapter;
import com.tudu.tu_du.adapters.PostsAdapter;
import com.tudu.tu_du.models.Post;
import com.tudu.tu_du.providers.AuthProvider;
import com.tudu.tu_du.providers.PostProvider;
import com.tudu.tu_du.providers.UsuariosPrivider;

import de.hdodenhof.circleimageview.CircleImageView;


public class PerfilFragment extends Fragment {
    LinearLayout mLinearLayouteditprofile;
    View mView;
    TextView mTextViewNombrePerfil;
    TextView mTextViewTelefono;
    TextView mTextViewEmail;
    TextView mTextViewNPost;
    TextView mTextViewPostExist;

    ImageView mImageViewCover;
    CircleImageView mCircleImageViewPerfil;
    RecyclerView mRecyclerViewMyPost;
    MyPostAdapter mMyPostAdapter;
    ListenerRegistration mListenerRegistration;



    UsuariosPrivider mUsuariosPrivider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;



    public PerfilFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_perfil, container, false);
        mLinearLayouteditprofile = mView.findViewById(R.id.linearLayoutEditPerfil);
        mTextViewEmail = mView.findViewById(R.id.textViewEmail);
        mTextViewNombrePerfil = mView.findViewById(R.id.textViewNombrePerfil);
        mTextViewTelefono = mView.findViewById(R.id.textViewTelefono);
        mTextViewEmail = mView.findViewById(R.id.textViewEmail);
        mTextViewNPost = mView.findViewById(R.id.textViewNPost);
        mCircleImageViewPerfil = mView.findViewById(R.id.circleImageProfile);
        mImageViewCover = mView.findViewById(R.id.imageviewCover);
        mTextViewPostExist = mView.findViewById(R.id.textViewPostExist);
        mRecyclerViewMyPost = mView.findViewById(R.id.recyclerviewMyPost);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerViewMyPost.setLayoutManager(linearLayoutManager);
        mLinearLayouteditprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditarPerfil();
            }
        });
        mUsuariosPrivider = new UsuariosPrivider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();
        obtenerUsuario();
        getNPost();
        verificarExistePost();
        return mView;
    }

    private void verificarExistePost() {
       mListenerRegistration = mPostProvider.getPostByIdUsuario(mAuthProvider.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
        Query query = mPostProvider.getPostByIdUsuario(mAuthProvider.getUid());
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query,Post.class)
                .build();
        mMyPostAdapter = new MyPostAdapter(options,getContext());
        mRecyclerViewMyPost.setAdapter(mMyPostAdapter);
        mMyPostAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMyPostAdapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mListenerRegistration != null){
            mListenerRegistration.remove();
        }
    }

    private void EditarPerfil() {
        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        startActivity(intent);
    }
    private void getNPost() {
        mPostProvider.getPostByIdUsuario(mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numeroPost = queryDocumentSnapshots.size();
                mTextViewNPost.setText(String.valueOf(numeroPost));

            }
        });
    }
    private void obtenerUsuario() {
        mUsuariosPrivider.getUsuario(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                                Picasso.with(getContext()).load(imagenperfil).into(mCircleImageViewPerfil);
                            }
                        }

                    }if (documentSnapshot.contains("imagencover")) {
                        String imagencover = documentSnapshot.getString("imagencover");
                        if (imagencover != null){
                            if (!imagencover.isEmpty()){
                                Picasso.with(getContext()).load(imagencover).into(mImageViewCover);
                            }
                        }

                    }
                }

            }
        });

    }
}
package com.tudu.tu_du.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.tudu.tu_du.R;
import com.tudu.tu_du.activities.PostDetallesActivity;
import com.tudu.tu_du.models.Like;
import com.tudu.tu_du.models.Post;
import com.tudu.tu_du.providers.AuthProvider;
import com.tudu.tu_du.providers.LikeProvider;
import com.tudu.tu_du.providers.PostProvider;
import com.tudu.tu_du.providers.UsuariosPrivider;

import java.util.Date;

public class PostsAdapter extends FirestoreRecyclerAdapter<Post,PostsAdapter.ViewHolder> {
    Context context;
    UsuariosPrivider mUsuariosProvider;
    LikeProvider mLikeProvider;
    AuthProvider mAuthProvider;
    TextView mTextViewNPublicaciones;
    ListenerRegistration mListenerRegistration;
    public PostsAdapter(FirestoreRecyclerOptions<Post> options, Context context){
        super(options);
        this.context = context;
        mUsuariosProvider = new UsuariosPrivider();
        mLikeProvider = new LikeProvider();
        mAuthProvider = new AuthProvider();

    }public PostsAdapter(FirestoreRecyclerOptions<Post> options, Context context,TextView textView){
        super(options);
        this.context = context;
        mUsuariosProvider = new UsuariosPrivider();
        mLikeProvider = new LikeProvider();
        mAuthProvider = new AuthProvider();
        mTextViewNPublicaciones = textView;

    }
    @Override
    protected void onBindViewHolder(ViewHolder holder, int position, Post post) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String postId = document.getId();
        if (mTextViewNPublicaciones != null){
            int numeroPost = getSnapshots().size();
            mTextViewNPublicaciones.setText(String.valueOf(numeroPost));
        }


        holder.textViewtitle.setText(post.getTitulo().toUpperCase());
        holder.textViewdescripcion.setText(post.getDescripcion());
        holder.textViewcomuna.setText("Rancagua");
        holder.textViewprecio.setText("$"+post.getPrecio());


        if (post.getImg1() != null){
            if (!post.getImg1().isEmpty()){
                Picasso.with(context).load(post.getImg1()).into(holder.imageViewPost);
            }

        }
        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetallesActivity.class);
                intent.putExtra("id",postId);
                context.startActivity(intent);

            }
        });
        holder.imageViewLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Like like = new Like();
                like.setIdUser(mAuthProvider.getUid());
                like.setIdPost(postId);
                like.setTimestamp(new Date().getTime());
                like(like,holder);
            }
        });
        getUsuarioInfo(post.getIdUser(),holder);
        getNLikebypost(postId,holder);
        existeLike(postId,mAuthProvider.getUid(),holder);



    }
    private void getNLikebypost(String idPost,final ViewHolder holder){
        mListenerRegistration = mLikeProvider.getLikebyPost(idPost).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException error) {
                if (queryDocumentSnapshots != null){
                    int numeroLike = queryDocumentSnapshots.size();
                    holder.textViewLike.setText(String.valueOf(numeroLike) +" Me Gusta");
                }

            }
        });
    }


    private void like(Like like,ViewHolder holder) {
        mLikeProvider.getLikeByPostandUser(like.getIdPost(), mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numeroDocumento = queryDocumentSnapshots.size();
                if (numeroDocumento > 0) {
                    String idLike = queryDocumentSnapshots.getDocuments().get(0).getId();
                    holder.imageViewLike.setImageResource(R.drawable.icon_like_gray);
                    mLikeProvider.eliminarLike(idLike);

                }else {
                    holder.imageViewLike.setImageResource(R.drawable.icon_like_blue);
                    mLikeProvider.create(like);
                }
            }
        });


    }
    private void existeLike(String idPost,String idUser,ViewHolder holder) {
        mLikeProvider.getLikeByPostandUser(idPost, idUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numeroDocumento = queryDocumentSnapshots.size();
                if (numeroDocumento > 0) {
                    holder.imageViewLike.setImageResource(R.drawable.icon_like_blue);


                }else {

                    holder.imageViewLike.setImageResource(R.drawable.icon_like_gray);
                }
            }
        });


    }

    private void getUsuarioInfo(String idUser,final ViewHolder holder) {
        mUsuariosProvider.getUsuario(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("nombre")) {
                        String nombre = documentSnapshot.getString("nombre");
                        holder.textViewUsuarioPost.setText("By: " + nombre);
                    }
                }
            }
        });


    }
    public ListenerRegistration getListenerRegistration() {
        return mListenerRegistration;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post, parent, false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewtitle;
        TextView textViewdescripcion;
        TextView textViewcomuna;
        TextView textViewprecio;
        TextView textViewUsuarioPost;
        TextView textViewLike;
        ImageView imageViewPost;
        ImageView imageViewLike;
        View viewHolder;

        public ViewHolder(View view){
            super(view);
            textViewtitle = view.findViewById(R.id.textViewTitlePostCard);
            textViewdescripcion = view.findViewById(R.id.textViewDescripcionPostCard);
            textViewcomuna = view.findViewById(R.id.textViewComunaPostCard);
            textViewprecio = view.findViewById(R.id.textViewPrecioPostCard);
            textViewUsuarioPost = view.findViewById(R.id.textViewUsuarioPost);
            textViewLike = view.findViewById(R.id.textViewLike);

            imageViewPost = view.findViewById(R.id.imageViewPostCard);
            imageViewLike = view.findViewById(R.id.imageViewLike);
            viewHolder = view;
        }


    }
}

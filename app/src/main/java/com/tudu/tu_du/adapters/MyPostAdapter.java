package com.tudu.tu_du.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
import com.tudu.tu_du.utils.RelativeTime;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostAdapter extends FirestoreRecyclerAdapter<Post, MyPostAdapter.ViewHolder> {
    Context context;
    UsuariosPrivider mUsuariosProvider;
    LikeProvider mLikeProvider;
    PostProvider mPostProvider;
    AuthProvider mAuthProvider;
    public MyPostAdapter(FirestoreRecyclerOptions<Post> options, Context context){
        super(options);
        this.context = context;
        mUsuariosProvider = new UsuariosPrivider();
        mLikeProvider = new LikeProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();

    }
    @Override
    protected void onBindViewHolder(ViewHolder holder, int position, Post post) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String postId = document.getId();

        String RelativeTime = com.tudu.tu_du.utils.RelativeTime.getTimeAgo(post.getTimestamp(),context);
        holder.textViewRelativeTime.setText(RelativeTime);
        holder.textViewtitulomyPost.setText(post.getTitulo().toUpperCase());
        if (post.getIdUser().equals(mAuthProvider.getUid())){
            holder.imageViewEliminarPost.setVisibility(View.VISIBLE);
        }else {
            holder.imageViewEliminarPost.setVisibility(View.GONE);
        }

        if (post.getImg1() != null){
            if (!post.getImg1().isEmpty()){
                Picasso.with(context).load(post.getImg1()).into(holder.CircleImageMyPost);
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
        holder.imageViewEliminarPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ConfirmarEliminacion(postId);
            }
        });

    }


    private void ConfirmarEliminacion(String postId) {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Elimar Publicacion")
                .setMessage("¿Estas seguro  deseas eliminar la publicacion?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BorrarPost(postId);

                    }
                })
                .setNegativeButton("No" , null)
                .show();
    }

    private void BorrarPost(String postId) {
        mPostProvider.deletePost(postId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "La Publicacion se elimino Correctamente", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "No se pudo eliminar la publicacion ", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_my_post, parent, false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewtitulomyPost;
        TextView textViewRelativeTime;

        CircleImageView CircleImageMyPost;
        ImageView imageViewEliminarPost;

        View viewHolder;

        public ViewHolder(View view){
            super(view);
            textViewtitulomyPost = view.findViewById(R.id.textViewTituloMyPost);
            textViewRelativeTime = view.findViewById(R.id.textViewRelativeTimeMyPost);
            imageViewEliminarPost = view.findViewById(R.id.imageViewEliminarPost);

            CircleImageMyPost = view.findViewById(R.id.circleImageViewMyPost);
            viewHolder = view;
        }


    }
}

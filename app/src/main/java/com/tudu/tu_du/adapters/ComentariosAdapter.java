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
import com.squareup.picasso.Picasso;
import com.tudu.tu_du.R;
import com.tudu.tu_du.activities.PostDetallesActivity;
import com.tudu.tu_du.models.Comentarios;
import com.tudu.tu_du.models.Post;
import com.tudu.tu_du.providers.UsuariosPrivider;

import de.hdodenhof.circleimageview.CircleImageView;

public class ComentariosAdapter extends FirestoreRecyclerAdapter<Comentarios, ComentariosAdapter.ViewHolder> {
    Context context;
    UsuariosPrivider mUsuariosProvider;
    public ComentariosAdapter(FirestoreRecyclerOptions<Comentarios> options, Context context){
        super(options);
        this.context = context;
        mUsuariosProvider = new UsuariosPrivider();
    }
    @Override
    protected void onBindViewHolder(ViewHolder holder, int position, Comentarios comentarios) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String comentarioId = document.getId();
        String idUser = document.getString("idUser");

        holder.textViewComentario.setText(comentarios.getComentario());
        getUsuarioInfo(idUser,holder);



    }
    private void getUsuarioInfo(String idUser, final ViewHolder holder) {
        mUsuariosProvider.getUsuario(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("nombre")) {
                        String nombre = documentSnapshot.getString("nombre");
                        holder.textViewNombreusuario.setText(nombre);
                    }
                    // Mover el cierre de este bloque
                    if (documentSnapshot.contains("imagenperfil")) {
                        String imagenPerfil = documentSnapshot.getString("imagenperfil");
                        if (imagenPerfil != null) {
                            if (!imagenPerfil.isEmpty()) {
                                Picasso.with(context).load(imagenPerfil).into(holder.circleimageViewComentario);
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_comentarios, parent, false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewNombreusuario;
        TextView textViewComentario;
        CircleImageView circleimageViewComentario;
        View viewHolder;

        public ViewHolder(View view){
            super(view);
            textViewNombreusuario = view.findViewById(R.id.textViewNombreUsuario);
            textViewComentario = view.findViewById(R.id.textViewComentario);


            circleimageViewComentario = view.findViewById(R.id.circleImagenComentario);
            viewHolder = view;
        }


    }
}

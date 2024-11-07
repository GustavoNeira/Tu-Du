package com.tudu.tu_du.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;
import com.tudu.tu_du.R;
import com.tudu.tu_du.activities.ChatActivity;
import com.tudu.tu_du.models.Chat;
import com.tudu.tu_du.models.Mensajes;
import com.tudu.tu_du.providers.AuthProvider;
import com.tudu.tu_du.providers.UsuariosPrivider;
import com.tudu.tu_du.utils.RelativeTime;

import de.hdodenhof.circleimageview.CircleImageView;

public class MensajesAdapter extends FirestoreRecyclerAdapter<Mensajes, MensajesAdapter.ViewHolder> {
    Context context;
    UsuariosPrivider mUsuariosProvider;
    AuthProvider mAuthProvider;
    public MensajesAdapter(FirestoreRecyclerOptions<Mensajes> options, Context context){
        super(options);
        this.context = context;
        mUsuariosProvider = new UsuariosPrivider();
        mAuthProvider = new AuthProvider();
    }
    @Override
    protected void onBindViewHolder(ViewHolder holder, int position, Mensajes mensajes) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
       final String mensajeId = document.getId();
        holder.mtextViewMensaje.setText(mensajes.getMensaje());
        String relativetime =  RelativeTime.timeFormatAMPM(mensajes.getTimestamp(),context);
        holder.mtextViewDate.setText(relativetime);

        if (mensajes.getIdEmisor().equals(mAuthProvider.getUid())) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT

            );
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(150,0,0,0);
            holder.linearLayoutmensajes.setLayoutParams(params);
            holder.linearLayoutmensajes.setPadding(30,20,0,20);
            holder.linearLayoutmensajes.setBackground(context.getResources().getDrawable(R.drawable.rounded_linear_layout));
            holder.mimageViewVisto.setVisibility(View.VISIBLE);
            holder.mtextViewMensaje.setTextColor(Color.WHITE);
            holder.mtextViewDate.setTextColor(Color.LTGRAY);
        }
        else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT

            );
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.setMargins(0,0,150,0);
            holder.linearLayoutmensajes.setLayoutParams(params);
            holder.linearLayoutmensajes.setPadding(30,20,30,20);
            holder.linearLayoutmensajes.setBackground(context.getResources().getDrawable(R.drawable.rounded_linear_layout_gray));
            holder.mimageViewVisto.setVisibility(View.GONE);
            holder.mtextViewMensaje.setTextColor(Color.DKGRAY);
            holder.mtextViewDate.setTextColor(Color.LTGRAY);
        }
        if (mensajes.isVisto()) {
            holder.mimageViewVisto.setImageResource(R.drawable.icon_check_blue);
        }else {
            holder.mimageViewVisto.setImageResource(R.drawable.icon_check_grey);
        }


    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_mensajes, parent, false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView mtextViewMensaje;
        TextView mtextViewDate;
        ImageView mimageViewVisto;
        LinearLayout linearLayoutmensajes;
        View viewHolder;

        public ViewHolder(View view){
            super(view);
            mtextViewMensaje = view.findViewById(R.id.textviewmensajes);
            mtextViewDate = view.findViewById(R.id.textviewdatemensaje);
            linearLayoutmensajes = view.findViewById(R.id.linearlayoutmensajes);
            mimageViewVisto = view.findViewById(R.id.imageviewVisto);
            viewHolder = view;
        }


    }
}

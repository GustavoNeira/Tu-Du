package com.tudu.tu_du.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.tudu.tu_du.activities.ChatActivity;
import com.tudu.tu_du.models.Chat;
import com.tudu.tu_du.models.Comentarios;
import com.tudu.tu_du.providers.AuthProvider;
import com.tudu.tu_du.providers.ChatsProvider;
import com.tudu.tu_du.providers.MensajesProvider;
import com.tudu.tu_du.providers.UsuariosPrivider;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends FirestoreRecyclerAdapter<Chat, ChatsAdapter.ViewHolder> {
    Context context;
    UsuariosPrivider mUsuariosProvider;
    AuthProvider mAuthProvider;
    MensajesProvider mMensajesProvider;
    ListenerRegistration mListenerRegistration;
    ListenerRegistration mListenerRegistrationUltimoMensaje;
    public ChatsAdapter(FirestoreRecyclerOptions<Chat> options, Context context){
        super(options);
        this.context = context;
        mUsuariosProvider = new UsuariosPrivider();
        mAuthProvider = new AuthProvider();
        mMensajesProvider = new MensajesProvider();

    }
    @Override
    protected void onBindViewHolder(ViewHolder holder, int position, Chat chat) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String chatId = document.getId();
        if (mAuthProvider.getUid().equals(chat.getIdUser1())){
            getUsuarioInfo(chat.getIdUser2(),holder);
        }
        else {
            getUsuarioInfo(chat.getIdUser1(),holder);
        }

        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoChatActivity(chatId,chat.getIdUser1(),chat.getIdUser2());
            }
        });
        getUltimoMensaje(chatId,holder.textViewUltimoMensaje);
        String idEmisor = "";
        if (mAuthProvider.getUid().equals(chat.getIdUser1())) {
            idEmisor = chat.getIdUser2();
        }else {
            idEmisor = chat.getIdUser1();
        }
        getMensajesNoleidos(chatId,idEmisor,holder.textViewNoleidos,holder.Framelayoutmensajesnoleidos);





    }

    private void getMensajesNoleidos(String chatId, String idEmisor, TextView textViewNoleidos, FrameLayout framelayoutmensajesnoleidos) {
       mListenerRegistration = mMensajesProvider.getMensajesByChatAndEmisor(chatId, idEmisor).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException error) {
                if (queryDocumentSnapshots != null) {
                    int size = queryDocumentSnapshots.size();
                    if (size > 0) {
                        framelayoutmensajesnoleidos.setVisibility(View.VISIBLE);
                        textViewNoleidos.setText(String.valueOf(size));
                    } else {
                        framelayoutmensajesnoleidos.setVisibility(View.GONE);
                    }
                }

            }
        });
    }
    public ListenerRegistration getListenerRegistration() {
        return mListenerRegistration;
    }
    public ListenerRegistration getListenerUltimoMensaje() {
        return mListenerRegistrationUltimoMensaje;
    }

    private void getUltimoMensaje(String chatId, TextView textViewUltimoMensaje) {
      mListenerRegistrationUltimoMensaje =  mMensajesProvider.getUltimoMensaje(chatId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException error) {
                if (queryDocumentSnapshots != null) {
                    int size = queryDocumentSnapshots.size();
                    if (size > 0) {
                        String UltimoMensaje = queryDocumentSnapshots.getDocuments().get(0).getString("mensaje");
                        textViewUltimoMensaje.setText(UltimoMensaje);
                    }
                }
            }
        });
    }

    private void gotoChatActivity(String chatId,String idUser1,String idUser2) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("idChat", chatId);
        intent.putExtra("idUser1", idUser1);
        intent.putExtra("idUser2", idUser2);
        context.startActivity(intent);

    }

    private void getUsuarioInfo(String idUser, final ViewHolder holder) {
        mUsuariosProvider.getUsuario(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("nombre")) {
                        String nombre = documentSnapshot.getString("nombre");
                        holder.textViewNombreusuarioChat.setText(nombre);
                    }
                    // Mover el cierre de este bloque
                    if (documentSnapshot.contains("imagenperfil")) {
                        String imagenPerfil = documentSnapshot.getString("imagenperfil");
                        if (imagenPerfil != null) {
                            if (!imagenPerfil.isEmpty()) {
                                Picasso.with(context).load(imagenPerfil).into(holder.circleimageViewChat);
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chats, parent, false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewNombreusuarioChat;
        TextView textViewUltimoMensaje;
        TextView textViewNoleidos;
        CircleImageView circleimageViewChat;
        FrameLayout Framelayoutmensajesnoleidos;
        View viewHolder;

        public ViewHolder(View view){
            super(view);
            textViewNombreusuarioChat = view.findViewById(R.id.textViewNombreUsuarioChat);
            textViewUltimoMensaje = view.findViewById(R.id.textViewUltimoMensaje);
            textViewNoleidos = view.findViewById(R.id.textViewnoleidos);
            Framelayoutmensajesnoleidos = view.findViewById(R.id.framelayoutmensajesnoleidos);


            circleimageViewChat = view.findViewById(R.id.circleImageChat);
            viewHolder = view;
        }


    }
}

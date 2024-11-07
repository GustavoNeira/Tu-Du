package com.tudu.tu_du.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.tudu.tu_du.R;
import com.tudu.tu_du.adapters.MensajesAdapter;
import com.tudu.tu_du.adapters.MyPostAdapter;
import com.tudu.tu_du.models.Chat;
import com.tudu.tu_du.models.FCMBody;
import com.tudu.tu_du.models.FCMResponse;
import com.tudu.tu_du.models.Mensajes;
import com.tudu.tu_du.models.Post;
import com.tudu.tu_du.providers.AuthProvider;
import com.tudu.tu_du.providers.ChatsProvider;
import com.tudu.tu_du.providers.MensajesProvider;
import com.tudu.tu_du.providers.NotificationProvider;
import com.tudu.tu_du.providers.TokenProvider;
import com.tudu.tu_du.providers.UsuariosPrivider;
import com.tudu.tu_du.utils.RelativeTime;
import com.tudu.tu_du.utils.ViewedMensajesHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    String mExtraIdUser1;
    String mExtraIdUser2;
    String mExtraIdChat;

    long midNotificacionChat;

    ChatsProvider mChatsProvider;
    MensajesProvider mMensajesProvider;
    AuthProvider mAuthProvider;
    UsuariosPrivider mUsuariosProvider;

    EditText mEditTextMensajes;
    ImageView mImageViewMensajes;
    NotificationProvider mNotificationProvider;
    TokenProvider mTokenProvider;

    View mActionBarView;

    CircleImageView mCircleImageViewPerfil;
    TextView mTextviewNombreusuario;
    TextView mTextviewRelativeTime;
    ImageView mImageviewback;
    RecyclerView mRecyclerViewMensajes;
    MensajesAdapter mMensajesAdapter;
    LinearLayoutManager mLinearLayoutManager;
    ListenerRegistration mListenerRegistration;
    String mMyusername;
    String musernameChat;
    String mImageReceptor;
    String mImageEmisor;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mChatsProvider = new ChatsProvider();
        mMensajesProvider = new MensajesProvider();
        mNotificationProvider = new NotificationProvider();
        mAuthProvider = new AuthProvider();
        mTokenProvider = new TokenProvider();
        mUsuariosProvider = new UsuariosPrivider();
        mEditTextMensajes = findViewById(R.id.EditTextMensajes);
        mImageViewMensajes = findViewById(R.id.ImageviewEnviarMensaje);

        mRecyclerViewMensajes = findViewById(R.id.recyclerviewmensajes);
        mLinearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerViewMensajes.setLayoutManager(mLinearLayoutManager);


        mExtraIdUser1 = getIntent().getStringExtra("idUser1");
        mExtraIdUser2 = getIntent().getStringExtra("idUser2");
        mExtraIdChat = getIntent().getStringExtra("idChat");

        showCustomToolbar(R.layout.custom_chat_toolbar);
        getMYInfoUser();



        mImageViewMensajes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              enviarMensaje();
            }
        });

       chatExiste();
    }
    @Override
    public void onStart() {
        super.onStart();
        if (mMensajesAdapter != null) {
            mMensajesAdapter.startListening();
        }
        ViewedMensajesHelper.updateOnline(true,ChatActivity.this);

    }

    @Override
    public void onStop() {
        super.onStop();
        mMensajesAdapter.stopListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMensajesHelper.updateOnline(false,ChatActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListenerRegistration != null) {
            mListenerRegistration.remove();
        }
    }

    private void obtenermensajeschat(){
        Query query = mMensajesProvider.getMensajesByChat(mExtraIdChat);
        FirestoreRecyclerOptions<Mensajes> options = new FirestoreRecyclerOptions.Builder<Mensajes>()
                .setQuery(query,Mensajes.class)
                .build();
        mMensajesAdapter = new MensajesAdapter(options,ChatActivity.this);
        mRecyclerViewMensajes.setAdapter(mMensajesAdapter);
        mMensajesAdapter.startListening();
        mMensajesAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                updateVisto();
                int numeromensajes = mMensajesAdapter.getItemCount();
                int ultimaposiciondemensaje = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (ultimaposiciondemensaje == -1 || (positionStart >= (numeromensajes -1) && ultimaposiciondemensaje == (positionStart -1))){
                    mRecyclerViewMensajes.scrollToPosition(positionStart);
                }
            }
        });
    }

    private void enviarMensaje() {
        String Textmensaje = mEditTextMensajes.getText().toString();
        if (!Textmensaje.isEmpty()) {
            Mensajes mensajes = new Mensajes();
            mensajes.setIdChat(mExtraIdChat);
            if (mAuthProvider.getUid().equals(mExtraIdUser1)){
                mensajes.setIdEmisor(mExtraIdUser1);
                mensajes.setIdReceptor(mExtraIdUser2);
            }else {
                mensajes.setIdEmisor(mExtraIdUser2);
                mensajes.setIdReceptor(mExtraIdUser1);
            }
            mensajes.setTimestamp(new Date().getTime());
            mensajes.setVisto(false);
            mensajes.setIdChat(mExtraIdChat);
            mensajes.setMensaje(Textmensaje);
            mMensajesProvider.create(mensajes).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                   if (task.isSuccessful()){
                       mEditTextMensajes.setText("");
                       mMensajesAdapter.notifyDataSetChanged();
                       getToken(mensajes);

                   }else {
                       Toast.makeText(ChatActivity.this, "El mensaje no se creo ", Toast.LENGTH_SHORT).show();
                   }
                }
            });

        }
    }

    private void showCustomToolbar(int resourse) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar  actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mActionBarView = inflater.inflate(resourse, null);
        actionBar.setCustomView(mActionBarView);
        mCircleImageViewPerfil = mActionBarView.findViewById(R.id.circleImageViewPerfil);
        mTextviewNombreusuario = mActionBarView.findViewById(R.id.textViewNombreUsuario);
        mTextviewRelativeTime = mActionBarView.findViewById(R.id.textViewRelativeTime);
        mImageviewback = mActionBarView.findViewById(R.id.imageViewback);
        mImageviewback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getUsuarioinfo();



    }

    private void getUsuarioinfo() {
        String idUserinfo = "";
        if (mAuthProvider.getUid().equals(mExtraIdUser1)){
            idUserinfo = mExtraIdUser2;
        }
        else {
            idUserinfo = mExtraIdUser1;
        }
        mListenerRegistration = mUsuariosProvider.getUsuarioRealTime(idUserinfo).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException error) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("nombre")){
                       musernameChat = documentSnapshot.getString("nombre");
                        mTextviewNombreusuario.setText(musernameChat);
                    }
                    if (documentSnapshot.contains("online")){
                        boolean online = documentSnapshot.getBoolean("online");
                        if (online){
                            mTextviewRelativeTime.setText("En linea");
                        }
                        else if (documentSnapshot.contains("ultimaconexion")){
                            long ultimaconexion = documentSnapshot.getLong("ultimaconexion");
                            String relativetime = RelativeTime.getTimeAgo(ultimaconexion,ChatActivity.this);
                            mTextviewRelativeTime.setText(relativetime);
                        }
                    }

                }
                if (documentSnapshot.contains("imagenperfil")){
                    mImageReceptor = documentSnapshot.getString("imagenperfil");
                    if (mImageReceptor != null){
                        if (!mImageReceptor.equals("")){
                            Picasso.with(ChatActivity.this).load(mImageReceptor).into(mCircleImageViewPerfil);
                        }
                    }

                }

            }
        });
    }

    private void chatExiste() {
        mChatsProvider.getChatByUser1andUser2(mExtraIdUser1, mExtraIdUser2).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();
                if (size == 0) {
                    createChat();
                } else {
                    try {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        if (documentSnapshot != null && documentSnapshot.contains("idNotificacion")) {
                            Long idNotificacion = documentSnapshot.getLong("idNotificacion");
                            if (idNotificacion != null) {
                                midNotificacionChat = idNotificacion.longValue();
                                Log.d("ChatActivity", "idNotificacion obtenido con éxito: " + midNotificacionChat);
                                obtenermensajeschat();
                                updateVisto();
                            } else {
                                Log.e("ChatActivity", "El valor de idNotificacion en la base de datos es nulo");
                                // Manejar el caso en que "idNotificacion" en la base de datos es nulo
                            }
                        } else {
                            Log.e("ChatActivity", "El documento o la propiedad idNotificacion no existen");
                            // Manejar el caso en que el documento o la propiedad "idNotificacion" no existen
                        }
                    } catch (Exception e) {
                        Log.e("ChatActivity", "Error al procesar la respuesta de la base de datos", e);
                    }
                }
            }
        });
    }

    private void updateVisto() {
        String idEmisor ="";
        if (mAuthProvider.getUid().equals(mExtraIdUser1)){
            idEmisor = mExtraIdUser2;
        }
        else {
            idEmisor = mExtraIdUser1;
        }
        mMensajesProvider.getMensajesByChatAndEmisor(mExtraIdChat,idEmisor).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                    mMensajesProvider.updateVisto(snapshot.getId(),true);

                }
            }
        });
    }

    private void createChat() {
        Chat chat = new Chat();
        chat.setIdUser1(mExtraIdUser1);
        chat.setIdUser2(mExtraIdUser2);
        chat.setEscribiendo(false);
        chat.setTimestamp(new Date().getTime());
        chat.setId(mExtraIdUser1 + mExtraIdUser2);
        Random random = new Random();
        int n = random.nextInt(1000000);
        chat.setIdNotificacion(n);
        midNotificacionChat = n;

        ArrayList<String> ids = new ArrayList<>();
        ids.add(mExtraIdUser1);
        ids.add(mExtraIdUser2);
        chat.setIds(ids);
        mChatsProvider.create(chat);
        mExtraIdChat = chat.getId();
        obtenermensajeschat();
    }
    private void getToken(final Mensajes mensaje) {
        String idUser = "";
        if (mAuthProvider.getUid().equals(mExtraIdUser1)){
            idUser = mExtraIdUser2;
        }else {
            idUser = mExtraIdUser1;
        }

        mTokenProvider.getToken(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("token")) {
                        String token = documentSnapshot.getString("token");
                        getlastTheeMensajes(mensaje,token);

                    }
                }
                else {
                    Toast.makeText(ChatActivity.this, "El token de notificaciones del usuario no existe", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void getlastTheeMensajes(Mensajes mensajes,final String token) {
        mMensajesProvider.getlastThreeMensajesByChatAndEmisor(mExtraIdChat,mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Mensajes> mensajesArrayList = new ArrayList<>();
                for (DocumentSnapshot d: queryDocumentSnapshots.getDocuments()) {
                    if (d.exists()) {
                        Mensajes mesajes = d.toObject(Mensajes.class);
                        mensajesArrayList.add(mesajes);
                    }

                }if (mensajesArrayList.size() == 0){
                    mensajesArrayList.add(mensajes);

                }
                Collections.reverse(mensajesArrayList);


                Gson gson = new Gson();
                String messages = gson.toJson(mensajesArrayList);
                sendNotificacion(token,messages,mensajes);


            }
        });
    }
    private void sendNotificacion(String token,String messages,Mensajes mensajes){
        Map<String, String> data = new HashMap<>();
        data.put("title", "Nuevo Mensaje");
        data.put("body", mensajes.getMensaje());
        data.put("idNotificacion",String.valueOf(midNotificacionChat));
        data.put("messages",messages);
        data.put("usernameEmisor",mMyusername);
        data.put("usernameReceptor",musernameChat);
        data.put("imagenEmisor",mImageEmisor);
        data.put("imagenReceptor",mImageReceptor);

        String idEmisor = "";
        if (mAuthProvider.getUid().equals(mExtraIdUser1)){
            idEmisor = mExtraIdUser2;
        }
        else {
            idEmisor = mExtraIdUser1;
        }

        mMensajesProvider.getUltimoMensajeEmisor(mExtraIdChat,idEmisor).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();
                String ultimoMensaje = "";
                if (size > 0) {
                    ultimoMensaje = queryDocumentSnapshots.getDocuments().get(0).getString("mensaje");
                    data.put("ultimoMensaje",ultimoMensaje);

                }
                FCMBody body = new FCMBody( token,"high","4500s",data);
                mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getSuccess() == 1){
                                // Toast.makeText(ChatActivity.this, "La Notificacion se envio correctamente", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(ChatActivity.this, "La Notificacion no se pudo enviar", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {Toast.makeText(ChatActivity.this, "La Notificacion no se pudo enviar", Toast.LENGTH_SHORT).show();}
                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {

                    }
                });
            }
        });

    }
    private void getMYInfoUser(){
        mUsuariosProvider.getUsuario(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("nombre")) {
                        mMyusername = documentSnapshot.getString("nombre");

                    } if (documentSnapshot.contains("imagenperfil")) {
                        mImageEmisor = documentSnapshot.getString("imagenperfil");

                    }
                }


            }
        });

    }
}
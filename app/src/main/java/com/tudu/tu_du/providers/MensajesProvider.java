package com.tudu.tu_du.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.tudu.tu_du.models.Mensajes;

import java.util.HashMap;
import java.util.Map;

public class MensajesProvider {

    CollectionReference mCollection;

    public MensajesProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Mensajes");
    }
    public Task<Void> create(Mensajes mensajes){
        DocumentReference document = mCollection.document();
        mensajes.setId(document.getId());
        return document.set(mensajes);
    }
    public Query getMensajesByChat(String idChat){
       return mCollection.whereEqualTo("idChat", idChat).orderBy("timestamp",Query.Direction.ASCENDING);
    }
    public Query getMensajesByChatAndEmisor(String idChat,String idEmisor){
       return mCollection.whereEqualTo("idChat", idChat).whereEqualTo("idEmisor",idEmisor).whereEqualTo("visto",false);
    }
    public Query getlastThreeMensajesByChatAndEmisor(String idChat,String idEmisor){
       return mCollection
               .whereEqualTo("idChat", idChat)
               .whereEqualTo("idEmisor",idEmisor)
               .whereEqualTo("visto",false)
               .orderBy("timestamp",Query.Direction.DESCENDING)
               .limit(3);
    }
    public Query getUltimoMensaje(String idChat){
       return mCollection.whereEqualTo("idChat", idChat).orderBy("timestamp", Query.Direction.DESCENDING).limit(1);
    }
    public Query getUltimoMensajeEmisor(String idChat,String idEmisor){
       return mCollection.whereEqualTo("idChat", idChat)
               .whereEqualTo("idEmisor",idEmisor)
               .orderBy("timestamp", Query.Direction.DESCENDING)
               .limit(1);
    }
    public Task<Void> updateVisto(String idDocument,boolean state){
        Map<String, Object>map = new HashMap<>();
        map.put("visto",state);
        return mCollection.document(idDocument).update(map);
    }

}

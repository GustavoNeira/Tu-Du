package com.tudu.tu_du.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.tudu.tu_du.models.Comentarios;

public class ComentariosProvider {
    CollectionReference mCollection;

    public ComentariosProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Comentarios");
    }
    public Task<Void> create(Comentarios comentario){
      return mCollection.document().set(comentario);
    }
    public Query getComentariobyPost(String idPost){
        return mCollection.whereEqualTo("idPost",idPost);
    }
}

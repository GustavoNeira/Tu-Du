package com.tudu.tu_du.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.tudu.tu_du.models.Post;

public class PostProvider {

    CollectionReference mCollection;

    public PostProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Publicaciones");

    }
    public Task<Void> save(Post post){
        return mCollection.document().set(post);

    }
    public Query getAll(){
       return mCollection.orderBy("timestamp",Query.Direction.DESCENDING);

    }
    public Query getPostByTitulo(String titulo){
       return mCollection.orderBy("titulo").startAt(titulo).endAt(titulo+'\uf8ff');
    }
    public Query getPostByCategoriaAndTimestamp(String categoria){
       return mCollection.whereEqualTo("categoria",categoria).orderBy("timestamp",Query.Direction.DESCENDING);
    }

    public Query getPostByIdUsuario(String id){
        return mCollection.whereEqualTo("idUser",id);
    }

    public Task<DocumentSnapshot> getPostById(String id){
        return mCollection.document(id).get();
    }

    public Task<Void> deletePost(String id){
        return mCollection.document(id).delete();
    }

}

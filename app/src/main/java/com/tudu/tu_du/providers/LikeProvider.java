package com.tudu.tu_du.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.tudu.tu_du.models.Like;

public class LikeProvider {
    CollectionReference mCollection;
    public  LikeProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Likes");

    }
    public Task<Void> create(Like like) {
        DocumentReference document = mCollection.document();
        String id = document.getId();
        like.setId(id);
        return document.set(like);
    }
    public Query getLikebyPost(String idPost){
        return mCollection.whereEqualTo("idPost", idPost);
    }
    public Query getLikeByPostandUser(String idPost,String idUser){
        return mCollection.whereEqualTo("idPost", idPost).whereEqualTo("idUser",idUser);
    }
    public Task<Void> eliminarLike(String id){
        return mCollection.document(id).delete();
    }
}

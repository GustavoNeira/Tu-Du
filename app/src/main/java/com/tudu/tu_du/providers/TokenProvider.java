package com.tudu.tu_du.providers;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.messaging.FirebaseMessaging;
import com.tudu.tu_du.models.Token;

public class TokenProvider {
    CollectionReference mCollection;

    public TokenProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Tokens");
    }
    public void create(final String idUser){
        if (idUser == null){
            return;
        }
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                Token token = new Token(s);
                mCollection.document(idUser).set(token);
            }
        });
    }
    public Task<DocumentSnapshot> getToken(String idUser){
        return mCollection.document(idUser).get();
    }
}

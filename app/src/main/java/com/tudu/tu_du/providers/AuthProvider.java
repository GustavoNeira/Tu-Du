package com.tudu.tu_du.providers;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthProvider {
    private FirebaseAuth mAuth;

    public AuthProvider(){
        mAuth = FirebaseAuth.getInstance();

    }
    public Task<AuthResult> registrar(String Email,String Password){
        return mAuth.createUserWithEmailAndPassword(Email,Password);
    }
    public Task<AuthResult> login(String email, String password){
       return mAuth.signInWithEmailAndPassword(email,password);
    }
    public Task<AuthResult> googleLogin(GoogleSignInAccount googleSignInAccount) {
        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        return mAuth.signInWithCredential(credential);
    }
    public String getEmail(){
        if(mAuth.getCurrentUser() != null){
            return mAuth.getCurrentUser().getEmail();
        }else{
            return null;
        }
    }
    public  String getUid(){
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getUid();
        }else {
            return null;
        }
    }  public FirebaseUser getUsersession(){

        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser();
        }else {
            return null;
        }
    }
    public void cerrarsesion(){
        if (mAuth != null) {
            mAuth.signOut();
        }
    }
}

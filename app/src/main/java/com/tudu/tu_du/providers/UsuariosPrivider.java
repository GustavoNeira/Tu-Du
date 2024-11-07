package com.tudu.tu_du.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tudu.tu_du.models.Usuarios;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UsuariosPrivider {
    private CollectionReference mCollection;

    public  UsuariosPrivider(){
           mCollection = FirebaseFirestore.getInstance().collection("Usuarios");
    }
    public Task<DocumentSnapshot> getUsuario(String id){
        return mCollection.document(id).get();

    }
    public DocumentReference getUsuarioRealTime(String id){
        return mCollection.document(id);

    }
    public Task<Void> crearUsuario(Usuarios usuario){
       return mCollection.document(usuario.getId()).set(usuario);
    }
    public Task<Void> completarInfo(Usuarios usuario){
        Map<String,Object> map = new HashMap<>();
        map.put("nombre", usuario.getNombre());
        map.put("telefono",usuario.getTelefono());
        map.put("region", usuario.getRegion());
        map.put("comuna",usuario.getComuna());
        map.put("timestamp",new Date().getTime());
        map.put("imagenperfil",usuario.getImagenperfil());
        map.put("imagencover",usuario.getImagencover());
        return mCollection.document(usuario.getId()).update(map);
    }
    public Task<Void> ActualizarOnline(String idUser,boolean status){
        Map<String,Object> map = new HashMap<>();
        map.put("online", status);
        map.put("ultimaconexion",new Date().getTime());

        return mCollection.document(idUser).update(map);
    }

}

package com.tudu.tu_du.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tudu.tu_du.R;
import com.tudu.tu_du.fragmentos.ChatsFragment;
import com.tudu.tu_du.fragmentos.FiltrosFragment;
import com.tudu.tu_du.fragmentos.HomeFragment;
import com.tudu.tu_du.fragmentos.PerfilFragment;
import com.tudu.tu_du.providers.AuthProvider;
import com.tudu.tu_du.providers.TokenProvider;
import com.tudu.tu_du.providers.UsuariosPrivider;
import com.tudu.tu_du.utils.ViewedMensajesHelper;

import org.checkerframework.checker.nullness.qual.NonNull;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigation;
    TokenProvider mTokenProvider;
    AuthProvider mAuthProvider;
    UsuariosPrivider mUsuariosPrivider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        mTokenProvider = new TokenProvider();
        mAuthProvider = new AuthProvider();
        mUsuariosPrivider = new UsuariosPrivider();
        openFragment(new HomeFragment());
        createToken();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewedMensajesHelper.updateOnline(true,HomeActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMensajesHelper.updateOnline(false,HomeActivity.this);
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId()==R.id.itemHome){
                        //fragmento Home
                        openFragment(new HomeFragment());

                    }
                    else if (item.getItemId()==R.id.itemFiltros){
                        //fragmento Filtros
                        openFragment(new FiltrosFragment());

                    }
                    else if (item.getItemId()==R.id.itemChat){
                        //fragmento chat
                        openFragment(new ChatsFragment());

                    }
                    else if (item.getItemId()==R.id.itemPerfil){
                        //fragmento perfil
                        openFragment(new PerfilFragment());

                    }
                    //si es true muestra el texto de la opcion si es false no lo muestra
                    return true;

                }
            };
 private void createToken(){
     mTokenProvider.create(mAuthProvider.getUid());
 }
}
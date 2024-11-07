package com.tudu.tu_du.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.tudu.tu_du.R;
import com.tudu.tu_du.adapters.PostsAdapter;
import com.tudu.tu_du.models.Post;
import com.tudu.tu_du.providers.AuthProvider;
import com.tudu.tu_du.providers.PostProvider;
import com.tudu.tu_du.utils.ViewedMensajesHelper;

public class FiltrosActivity extends AppCompatActivity {

    String mExtraCategoria;
    AuthProvider mAuthProvider;
    RecyclerView mRecyclerView;
    PostProvider mPostProvider;
    PostsAdapter mPostsAdapter;
    Toolbar mToolbar;
    TextView mNumeroPublicaciones;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtros);
        mRecyclerView = findViewById(R.id.recyclerviewFiltros);

        mRecyclerView.setLayoutManager(new GridLayoutManager(FiltrosActivity.this,2));
        mToolbar = findViewById(R.id.toolbar);
        mNumeroPublicaciones = findViewById(R.id.TextviewNResultados);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setTitle("Publicaciones");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mExtraCategoria = getIntent().getStringExtra("categoria");
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();
        Toast.makeText(this, "La Categoria Seleccionada es "+mExtraCategoria, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getPostByCategoriaAndTimestamp(mExtraCategoria);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query,Post.class)
                .build();
        mPostsAdapter = new PostsAdapter(options,FiltrosActivity.this,mNumeroPublicaciones);
        mRecyclerView.setAdapter(mPostsAdapter);
        mPostsAdapter.startListening();
        ViewedMensajesHelper.updateOnline(true,FiltrosActivity.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mPostsAdapter.stopListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMensajesHelper.updateOnline(false,FiltrosActivity.this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }
}
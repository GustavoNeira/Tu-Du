package com.tudu.tu_du.fragmentos;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.tudu.tu_du.R;
import com.tudu.tu_du.activities.MainActivity;
import com.tudu.tu_du.activities.PostActivity;
import com.tudu.tu_du.adapters.PostsAdapter;
import com.tudu.tu_du.models.Post;
import com.tudu.tu_du.providers.AuthProvider;
import com.tudu.tu_du.providers.PostProvider;


public class HomeFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener {
    View mView;
    FloatingActionButton mFab;

    MaterialSearchBar mSearchBar;
    AuthProvider mAuthProvider;
    RecyclerView mRecyclerView;
    PostProvider mPostProvider;
    PostsAdapter mPostsAdapter;
    PostsAdapter mPostsAdapterSearch;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
      mView =  inflater.inflate(R.layout.fragment_home, container, false);
      mFab = mView.findViewById(R.id.fab);
      mRecyclerView = mView.findViewById(R.id.recyclerviewHome);
      mSearchBar = mView.findViewById(R.id.searchBar);

      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
      mRecyclerView.setLayoutManager(linearLayoutManager);


        setHasOptionsMenu(true);
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();
        mSearchBar.setOnSearchActionListener(this);
        mSearchBar.inflateMenu(R.menu.main_menu);
        mSearchBar.getMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if ((item.getItemId() ==R.id.itemLogout)){
                    cerrarSesion();
                }

                return true;
            }
        });
        
      mFab.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              goToPost();
          }
      });
      return mView;
    }

    private void goToPost() {
        Intent intent = new Intent(getContext(), PostActivity.class);
        startActivity(intent);
    }

    private void SearchByTitulo(String titulo) {
        Query query = mPostProvider.getPostByTitulo(titulo);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query,Post.class)
                .build();
        mPostsAdapterSearch = new PostsAdapter(options,getContext());
        mPostsAdapterSearch.notifyDataSetChanged();
        mRecyclerView.setAdapter(mPostsAdapterSearch);
        mPostsAdapterSearch.startListening();

    }
    private void getallPost(){
        Query query = mPostProvider.getAll();
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query,Post.class)
                .build();
        mPostsAdapter = new PostsAdapter(options,getContext());
        mPostsAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mPostsAdapter);
        mPostsAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        getallPost();

    }

    @Override
    public void onStop() {
        super.onStop();
        mPostsAdapter.stopListening();
        if (mPostsAdapterSearch != null){
            mPostsAdapterSearch.stopListening();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPostsAdapter.getListenerRegistration() != null){
            mPostsAdapter.getListenerRegistration().remove();
        }
    }

    private void cerrarSesion() {
        mAuthProvider.cerrarsesion();
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (!enabled){
            getallPost();

        }

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        SearchByTitulo(text.toString().toLowerCase());

    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}
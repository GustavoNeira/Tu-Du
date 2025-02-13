package com.tudu.tu_du.fragmentos;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.tudu.tu_du.R;
import com.tudu.tu_du.adapters.ChatsAdapter;
import com.tudu.tu_du.adapters.PostsAdapter;
import com.tudu.tu_du.models.Chat;
import com.tudu.tu_du.models.Post;
import com.tudu.tu_du.providers.AuthProvider;
import com.tudu.tu_du.providers.ChatsProvider;


public class ChatsFragment extends Fragment {

ChatsAdapter mAdapter;
RecyclerView mRecyclerView;
View mView;
ChatsProvider mChatsProvider;
AuthProvider mAuthProvider;

Toolbar mToolbar;

    public ChatsFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_chats, container, false);
        mRecyclerView = mView.findViewById(R.id.recyclerviewChats);
        mToolbar = mView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Chats");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);


        mChatsProvider = new ChatsProvider();
        mAuthProvider = new AuthProvider();
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mChatsProvider.getAll(mAuthProvider.getUid());
        FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>()
                .setQuery(query,Chat.class)
                .build();
        mAdapter = new ChatsAdapter(options,getContext());

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter.getListenerRegistration() != null){
            mAdapter.getListenerRegistration().remove();

        }
        if (mAdapter.getListenerUltimoMensaje() != null){
            mAdapter.getListenerUltimoMensaje().remove();
        }
    }
}
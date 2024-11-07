package com.tudu.tu_du.fragmentos;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tudu.tu_du.R;
import com.tudu.tu_du.activities.FiltrosActivity;


public class FiltrosFragment extends Fragment {

    View mView;
    CardView mCardViewServicios;
    CardView mCardViewProducto;




    public FiltrosFragment() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView  = inflater.inflate(R.layout.fragment_filtros, container, false);
        mCardViewServicios = mView.findViewById(R.id.cardviewServicios);
        mCardViewProducto = mView.findViewById(R.id.cardviewProductos);

        mCardViewServicios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilterActivity("Servicios");

            }
        });
        mCardViewProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilterActivity("Productos");

            }
        });
        return mView;
    }
    private void goToFilterActivity(String Categoria) {
        Intent intent = new Intent(getContext(), FiltrosActivity.class);
        intent.putExtra("categoria", Categoria);
        startActivity(intent);

    }
}
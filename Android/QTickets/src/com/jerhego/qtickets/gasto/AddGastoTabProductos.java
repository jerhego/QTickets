package com.jerhego.qtickets.gasto;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jerhego.qtickets.R;

public class AddGastoTabProductos extends Fragment {
	@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	 
		View productos = inflater.inflate(
				R.layout.tab_add_gasto_productos_frag, container, false);
	        ((TextView)productos.findViewById(R.id.textView)).setText("Productos");
	        return productos;
}}

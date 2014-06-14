package com.jerhego.qtickets.gasto;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.jerhego.qtickets.R;

public class AddGastoTabTienda extends Fragment {

	private AutoCompleteTextView autoCompleteShops;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View tiendas = inflater.inflate(R.layout.tab_add_gasto_tienda_frag,
				container, false);
		((TextView) tiendas.findViewById(R.id.textView)).setText("Tienda");

		autoCompleteShops = (AutoCompleteTextView) tiendas
				.findViewById(R.id.autoCompleteShop);

		return tiendas;
	}

}

package com.jerhego.qtickets.gasto;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jerhego.qtickets.R;

public class AddGastoTabCompartir extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View compartir = inflater.inflate(
				R.layout.tab_add_gasto_compartir_frag, container, false);
		((TextView) compartir.findViewById(R.id.textView)).setText("Compartir");
		return compartir;
	}
}

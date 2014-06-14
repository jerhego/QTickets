package com.jerhego.qtickets.gasto;


import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.jerhego.qtickets.R;

public class DetalleGastoActivity extends Activity {
	
	private TextView infoQR;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detalle_gasto);		

		infoQR = (TextView) findViewById(R.id.textInfoQR);			
		
		Bundle bundle = getIntent().getExtras();       
		infoQR.setText( bundle.getString("lectura"));

	}

}

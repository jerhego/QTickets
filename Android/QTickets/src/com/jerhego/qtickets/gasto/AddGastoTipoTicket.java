package com.jerhego.qtickets.gasto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.jerhego.qtickets.R;
import com.jerhego.qtickets.qrcode.IntentIntegrator;
import com.jerhego.qtickets.qrcode.IntentResult;

public class AddGastoTipoTicket extends Activity implements OnClickListener {

	private Button mButtonQRCode;
	private Button mButtonSinQRCode;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_gasto_tipo_ticket);

		mButtonQRCode = (Button) findViewById(R.id.btnAddQRCode);
		mButtonSinQRCode = (Button) findViewById(R.id.btnAddManual);
		mButtonQRCode.setOnClickListener(this);
		mButtonSinQRCode.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {

		case R.id.btnAddQRCode:
			IntentIntegrator integrator = new IntentIntegrator(
					AddGastoTipoTicket.this);
			integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
			break;

		case R.id.btnAddManual:
			Intent iAddGasto = new Intent(this, AddGastoPager.class);
			startActivity(iAddGasto);
			break;

		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		// Tratamos el resultado de nuestro escaneo
		IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
				resultCode, data);
		if (result != null) {
			String contenido = result.getContents();
			if (contenido != null) {
				Intent i = new Intent(this, DetalleGastoActivity.class);
				i.putExtra("lectura", result.toString());
				startActivity(i);
			} else {
				Toast.makeText(getApplicationContext(),
						R.string.escaneo_fallido, Toast.LENGTH_SHORT).show();
			}
		}
	}

}

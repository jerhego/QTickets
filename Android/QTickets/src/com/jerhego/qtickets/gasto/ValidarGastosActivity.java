package com.jerhego.qtickets.gasto;

import com.jerhego.qtickets.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;


public class ValidarGastosActivity extends Activity implements OnClickListener {

	private Button mButtonAdd;
	private Button mButtonDelete;
	private Button mButtonCategorizar;
	private Button mButtonGuardar;
	private ListView mListValidarGastos;
	private String credentials;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_validar_gastos);

		sp = getSharedPreferences("user_data", Activity.MODE_PRIVATE);
		credentials = sp.getString("credentials", "");
		
		mListValidarGastos = (ListView) findViewById(R.id.listViewValidarGastos);

		mButtonAdd = (Button) findViewById(R.id.btnAddGasto);
		mButtonDelete = (Button) findViewById(R.id.btnDeleteGasto);
		mButtonCategorizar = (Button) findViewById(R.id.btnCategorizarGasto);
		mButtonGuardar = (Button) findViewById(R.id.btnGuardarGastos);
		
		mButtonAdd.setOnClickListener(this);
		mButtonDelete.setOnClickListener(this);
		mButtonCategorizar.setOnClickListener(this);
		mButtonGuardar.setOnClickListener(this);

		//ListarGastosWS listarGastosWS = new ListarGastosWS();
		//listarGastosWS.execute();

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {

		case R.id.btnAddGasto:			
			break;
		case R.id.btnDeleteGasto:			
			break;
		case R.id.btnCategorizarGasto:			
			break;
		case R.id.btnGuardarGastos:			
			break;

		}
	}
}

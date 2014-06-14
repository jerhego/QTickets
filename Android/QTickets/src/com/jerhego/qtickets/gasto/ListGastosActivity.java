package com.jerhego.qtickets.gasto;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jerhego.qtickets.R;
import com.jerhego.qtickets.WebServicesRest;
import com.jerhego.qtickets.entity.Expense;
import com.jerhego.qtickets.qrcode.IntentIntegrator;
import com.jerhego.qtickets.qrcode.IntentResult;

public class ListGastosActivity extends Activity implements OnClickListener {

	private Button mButtonBuscarPorQR;
	private ListView mListGastos;
	private String credentials;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_gastos);

		sp = getSharedPreferences("user_data", Activity.MODE_PRIVATE);
		credentials = sp.getString("credentials", "");

		mButtonBuscarPorQR = (Button) findViewById(R.id.btnBuscarPorQR);
		mButtonBuscarPorQR.setOnClickListener(this);

		ListarGastosWS listarGastosWS = new ListarGastosWS();
		listarGastosWS.execute();

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {

		case R.id.btnBuscarPorQR:
			IntentIntegrator integrator = new IntentIntegrator(
					ListGastosActivity.this);
			integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
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

	private class ListarGastosWS extends AsyncTask<String, Integer, Boolean> {

		private String[] gastos;

		protected Boolean doInBackground(String... params) {

			boolean resul = true;

			HttpClient httpClient = new DefaultHttpClient();

			HttpGet get = new HttpGet(WebServicesRest.EXPENSE_URL);

			get.setHeader("content-type", "application/json");

			get.addHeader("Authorization", "Basic " + credentials);

			try {
				HttpResponse resp = httpClient.execute(get);
				String respStr = EntityUtils.toString(resp.getEntity());

				Type listExpenseType = new TypeToken<ArrayList<Expense>>() {
				}.getType();
				List<Expense> expenses = new Gson().fromJson(respStr,
						listExpenseType);

				gastos = new String[expenses.size()];

				for (int i = 0; i < expenses.size(); i++) {
					gastos[i] = expenses.get(i).getName();
				}

			} catch (Exception ex) {
				Log.e("ServicioRest", "Error!", ex);
				resul = false;
			}

			return resul;
		}

		protected void onPostExecute(Boolean result) {

			if (result) {
				if (gastos.length != 0) {
					// Rellenamos la lista con los gastos
					// Rellenamos la lista con los resultados
					ArrayAdapter<String> adaptador = new ArrayAdapter<String>(
							ListGastosActivity.this,
							android.R.layout.simple_list_item_1, gastos);

					mListGastos.setAdapter(adaptador);
				}
			} else {
				Toast.makeText(getApplicationContext(),
						R.string.error_conexion_rest, Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

}

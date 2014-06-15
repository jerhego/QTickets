package com.jerhego.qtickets;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.PlusClient;
import com.google.gson.GsonBuilder;
import com.jerhego.qtickets.entity.User;

public class LoginActivity extends Activity implements OnClickListener,
		PlusClient.ConnectionCallbacks, PlusClient.OnConnectionFailedListener {

	private static final int DIALOG_GET_GOOGLE_PLAY_SERVICES = 1;
	private static final int REQUEST_CODE_SIGN_IN = 1;
	private static final int REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES = 2;

	private PlusClient mPlusClient;
	private String idGoogle;
	private SignInButton mSignInButton;
	private ConnectionResult mConnectionResult;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mPlusClient = new PlusClient.Builder(this, this, this).build();

		mSignInButton = (SignInButton) findViewById(R.id.b_sign_in);
		mSignInButton.setOnClickListener(this);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Comprueba si estamos conectados a Google
		if (requestCode == REQUEST_CODE_SIGN_IN
				|| requestCode == REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES) {
			if (resultCode == RESULT_OK && !mPlusClient.isConnected()
					&& !mPlusClient.isConnecting()) {
				// This time, connect should succeed.
				mPlusClient.connect();
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		mPlusClient.connect();
	}

	@Override
	public void onStop() {
		mPlusClient.disconnect();
		super.onStop();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.b_sign_in:
			int available = GooglePlayServicesUtil
					.isGooglePlayServicesAvailable(this);
			if (available != ConnectionResult.SUCCESS) {
				showDialog(DIALOG_GET_GOOGLE_PLAY_SERVICES);
				return;
			}

			try {
				mConnectionResult.startResolutionForResult(this,
						REQUEST_CODE_SIGN_IN);
			} catch (IntentSender.SendIntentException e) {
				// Fetch a new result to start.
				mPlusClient.connect();
			}
			break;

		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		mConnectionResult = result;
		if (mConnectionResult == null) {
			// Disable the sign-in button until onConnectionFailed is called
			// with result.
			mSignInButton.setVisibility(View.INVISIBLE);
		} else {
			// Enable the sign-in button since a connection result is
			// available.
			mSignInButton.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {

		idGoogle = mPlusClient.getCurrentPerson().getId();
		new Autenticar().execute();
	}

	@Override
	public void onDisconnected() {
		mPlusClient.connect();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id != DIALOG_GET_GOOGLE_PLAY_SERVICES) {
			return super.onCreateDialog(id);
		}

		int available = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (available == ConnectionResult.SUCCESS) {
			return null;
		}
		if (GooglePlayServicesUtil.isUserRecoverableError(available)) {
			return GooglePlayServicesUtil.getErrorDialog(available, this,
					REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES);
		}
		return new AlertDialog.Builder(this)
				.setMessage(R.string.plus_generic_error).setCancelable(true)
				.create();
	}

	private class Autenticar extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			String token = null;

			try {
				token = GoogleAuthUtil.getToken(getApplicationContext(),
						mPlusClient.getAccountName(), "oauth2:"
								+ Scopes.PLUS_LOGIN);
			} catch (Exception e) {
				Log.e("getTokenError", e.getMessage());
			}

			return token;
		}

		@Override
		protected void onPostExecute(String token) {
			Log.i("Token recibido:", token);

			sp = getSharedPreferences("user_data", Activity.MODE_PRIVATE);

			// Nos autenticamos con el servidor
			// Comprobamos si tenemos token en el dispositivo
			String tokenDispositivo = sp.getString("token", "");
			if (!tokenDispositivo.equals("")) {
				// Tenemos un token en el dispositivo
				// Si el token recibido es distinto al del dispositivo,
				// guardamos el nuevo
				// y enviamos el token y el id al servidor (PUT)
				if (!token.equals(tokenDispositivo)) {
					new IniciarAutenticacionWS().execute(mPlusClient
							.getCurrentPerson().getId(), token);

				} else {

					// Si nos coincide, estamos autenticados y podemos pasar al
					// menu.
					irAMenuPrincipal(token);
				}

			} else {
				// No tenemos token, consultamos si estamos registrados (GET)
				new IsRegisteredWS().execute(mPlusClient.getAccountName(),
						token);

			}
		}
	}

	// Metodo para llamar al WS que consulta si el usuario esta registrado

	private class IsRegisteredWS extends AsyncTask<String, Void, String> {

		private String token;

		protected String doInBackground(String... params) {

			String resul;
			token = params[1];

			HttpClient httpClient = new DefaultHttpClient();

			HttpGet get = new HttpGet(WebServicesRest.USER_URL + "/"
					+ params[0].substring(0, params[0].indexOf("@")));

			get.setHeader("content-type", "application/json");

			try {
				HttpResponse resp = httpClient.execute(get);
				String respStr = EntityUtils.toString(resp.getEntity());
				resul = respStr;
				

			} catch (Exception ex) {
				Log.e("ServicioRest", "Error!", ex);
				resul = null;
			}

			return resul;

		}

		protected void onPostExecute(String resul) {
			if (resul != null) {
				if (resul.equals("0")) {
					// Esta registrado, mandamos token e id (PUT)
					new IniciarAutenticacionWS().execute(mPlusClient
							.getCurrentPerson().getId(), token);
				} else {
					// No esta registrado mandamos correo, id y token (POST)
					new RegistrarWS().execute(mPlusClient.getAccountName(),
							mPlusClient.getCurrentPerson().getDisplayName(),
							mPlusClient.getCurrentPerson().getId(), token);
				}
			} else {

				Toast.makeText(getApplicationContext(),
						"El servidor no responde...", Toast.LENGTH_SHORT)
						.show();
				reiniciarInicioSesion();

			}
		}
	}

	// Metodo para llamar al WS que inicia el proceso de autenticacion

	private class IniciarAutenticacionWS extends
			AsyncTask<String, Integer, String> {

		private String idGoogle;
		private String token;

		protected String doInBackground(String... params) {

			String resul;

			idGoogle = params[0];
			token = params[1];
			String email = mPlusClient.getAccountName().substring(0,
					mPlusClient.getAccountName().indexOf("@"));

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpPut put = new HttpPut(WebServicesRest.USER_URL + "/" + email);

			put.setHeader("content-type", "application/json");

			try {
				// Construimos el objeto cliente en formato JSON
				User user = new User();

				user.setEmail(mPlusClient.getAccountName());
				user.setIdGoogle(idGoogle);
				user.setToken(token);

				String jsonUser = new GsonBuilder().create().toJson(user);

				ByteArrayEntity entity = new ByteArrayEntity(
						jsonUser.getBytes());
				put.setEntity(entity);

				HttpResponse resp = httpClient.execute(put);
				String respStr = EntityUtils.toString(resp.getEntity());

				resul = respStr;

			} catch (Exception ex) {
				Log.e("ServicioRest", "Error!", ex);
				resul = null;
			}

			return resul;
		}

		protected void onPostExecute(String resul) {

			if (resul != null) {
				if (resul.equals("0")) {
					// Guardar y pasar de activity
					saveUserData(token);
					irAMenuPrincipal(token);
				} else {
					Toast.makeText(getApplicationContext(),
							"Fallo en la autenticación ("+resul+")", Toast.LENGTH_SHORT)
							.show();
					reiniciarInicioSesion();
				}
			} else {

				Toast.makeText(getApplicationContext(),
						"El servidor no responde...", Toast.LENGTH_SHORT)
						.show();
				reiniciarInicioSesion();
			}
		}
	}

	// Metodo para llamar al WS de registro de usuario

	private class RegistrarWS extends AsyncTask<String, Integer, String> {

		private String email;
		private String name;
		private String idGoogle;
		private String token;

		protected String doInBackground(String... params) {

			String resul;
			email = params[0];
			name = params[1];
			idGoogle = params[2];
			token = params[3];

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpPost post = new HttpPost(WebServicesRest.USER_URL);
			post.setHeader("Content-type", "application/json");

			try {
				// Construimos el objeto cliente en formato JSON
				User user = new User();

				user.setName(name);
				user.setEmail(email);
				user.setIdGoogle(idGoogle);
				user.setToken(token);

				String jsonUser = new GsonBuilder().create().toJson(user);
				
				ByteArrayEntity entity = new ByteArrayEntity(
						jsonUser.getBytes());
				post.setEntity(entity);
				
				
				/*JSONObject dato = new JSONObject();
				 
				dato.put("Name", name);
				dato.put("Email", email);
				dato.put("IdGoogle", idGoogle);
				dato.put("Token", token);					

				ByteArrayEntity entity = new ByteArrayEntity(
						dato.toString().getBytes());
				post.setEntity(entity);*/

				HttpResponse resp = httpClient.execute(post);
				String respStr = EntityUtils.toString(resp.getEntity());
				resul = respStr;
				
			} catch (Exception ex) {
				Log.e("ServicioRest", "Error!", ex);
				resul = null;
			}

			return resul;
		}

		protected void onPostExecute(String resul) {

			if (resul != null) {
				if (resul.equals("0")) {
					// Guardar y pasar de activity
					saveUserData(token);
					irAMenuPrincipal(token);
				} else {
					Toast.makeText(getApplicationContext(),
							"Fallo en el registro ("+resul+")", Toast.LENGTH_SHORT).show();
					reiniciarInicioSesion();
				}
			} else {

				Toast.makeText(getApplicationContext(),
						"El servidor no responde...", Toast.LENGTH_SHORT)
						.show();
				reiniciarInicioSesion();

			}
		}
	}

	public void saveUserData(String token) {
		Editor editor = sp.edit();
		editor.putString("token", token);

		String credentials = Seguridad.md5(idGoogle) + ":"
				+ Seguridad.md5(token);
		String base64EncodedCredentials = Base64.encodeToString(
				credentials.getBytes(), Base64.NO_WRAP);

		editor.putString("credentials", base64EncodedCredentials);
		editor.commit();
	}

	// Vamos al menu principal
	public void irAMenuPrincipal(String token) {
		Intent i = new Intent(getApplicationContext(),
				MenuPrincipalActivity.class);
		startActivity(i);
		finish();
	}

	public void reiniciarInicioSesion() {
		if (mPlusClient.isConnected()) {
			mPlusClient.clearDefaultAccount();
			mPlusClient.disconnect();
			mPlusClient.connect();
		}
	}
}

package com.jerhego.qtickets;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.plus.PlusClient;
import com.jerhego.qtickets.gasto.AddGastoTipoTicket;
import com.jerhego.qtickets.gasto.ListGastosActivity;


public class MenuPrincipalActivity extends Activity implements OnClickListener,
		PlusClient.ConnectionCallbacks, PlusClient.OnConnectionFailedListener,
		PlusClient.OnAccessRevokedListener {

	private static final int DIALOG_GET_GOOGLE_PLAY_SERVICES = 1;
	private static final int REQUEST_CODE_SIGN_IN = 1;
	private static final int REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES = 2;

	private TextView mSignInStatus;
	private PlusClient mPlusClient;
	private Button mButtonFindProducto;
	private Button mButtonAddCompra;
	private boolean isSignedIn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_principal);

		mPlusClient = new PlusClient.Builder(this, this, this).build();

		mSignInStatus = (TextView) findViewById(R.id.txtNombreUsuario);
		mButtonAddCompra = (Button) findViewById(R.id.btnNuevaCompra);
		mButtonFindProducto = (Button) findViewById(R.id.btnBuscarProducto);
		mButtonFindProducto.setOnClickListener(this);
		mButtonAddCompra.setOnClickListener(this);

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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_opc_cerrar_sesion:
			if (mPlusClient.isConnected()) {
				mPlusClient.clearDefaultAccount();
				mPlusClient.disconnect();
				mPlusClient.connect();

				// Volvemos a la pantalla de login
				toSreenLogin();
			}
			return true;
		case R.id.menu_opc_revocar_permisos:
			if (mPlusClient.isConnected()) {
				mPlusClient.revokeAccessAndDisconnect(this);
			}
			return true;
		case R.id.menu_opc_acerca_de:
			// Acerca de Pocket Shopper
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (!isSignedIn) {
			menu.setGroupEnabled(R.id.menu_grupo_google, false);
		} else {
			menu.setGroupEnabled(R.id.menu_grupo_google, true);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {

		case R.id.btnNuevaCompra:
			Intent iAddGatoTipoTicket = new Intent(this,
					AddGastoTipoTicket.class);
			startActivity(iAddGatoTipoTicket);
			break;
		case R.id.btnBuscarProducto:
			Intent iListGastos = new Intent(this, ListGastosActivity.class);
			startActivity(iListGastos);
			break;

		}
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
	public void onAccessRevoked(ConnectionResult status) {
		if (status.isSuccess()) {
			// Volvemos a la pantalla de login
			toSreenLogin();
		} else {
			Toast.makeText(getApplicationContext(),
					R.string.error_acceso_revocado_status, Toast.LENGTH_SHORT)
					.show();
			if (mPlusClient.isConnected()) {
				mPlusClient.clearDefaultAccount();
				mPlusClient.disconnect();
				mPlusClient.connect();
			}
		}
	}

	public void toSreenLogin() {
		Intent i = new Intent();
		i.setClass(this, LoginActivity.class);
		startActivity(i);
		finish();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (result == null) {
			isSignedIn = false;
		} else {
			mPlusClient.connect();
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		isSignedIn = true;
		String currentPersonName = mPlusClient.getCurrentPerson() != null ? mPlusClient
				.getCurrentPerson().getDisplayName()
				: getString(R.string.persona_desconocida_status);
		mSignInStatus.setText(currentPersonName);
	}

	@Override
	public void onDisconnected() {
		isSignedIn = false;
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

}

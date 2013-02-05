package com.example.androidhive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainScreenActivity extends Activity{
	
	Button btnIniciarSesion;
	
	TextView txtUsuario;
	TextView txtContrasena;
	TextView txtRespuesta;
	
	// Progress Dialog
		private ProgressDialog pDialog;
		JSONParser jsonParser = new JSONParser();
		// Creating JSON Parser object
		JSONParser jParser = new JSONParser();

		ArrayList<HashMap<String, String>> usuariosList;

		// url to get all products list
		private static String url_verificar_usuario = "http://www.ecuaconnect.com/ihm_android/crud/get_user.php";

		// JSON Node names
		private static final String TAG_SUCCESS = "success";
		private static final String TAG_TIPO_USUARIO = "usu_tipo_usuario";
		private static final String TAG_USUARIOS = "usuarios";
		
		/*private static final String TAG_NOMBRES_APELLIDOS = "usu_nombres_apellidos";
		private static final String TAG_TIPO_USUARIO = "usu_tipo_usuario";*/
		
		// products JSONArray
		JSONArray usuarios = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);
		
		// Buttons
		btnIniciarSesion = (Button) findViewById(R.id.btnIniciarSesion);
		
		// view products click event
		btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				new verificarUsuario().execute();
			}
			
		});

	}
	
	class verificarUsuario extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainScreenActivity.this);
			pDialog.setMessage("Verificando usuario... Por favor espere...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * Getting product details in background thread
		 * */
		protected String doInBackground(String... params) {

			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					// Check for success tag
					int success;
					String tipo_usuario;
					try {
						
						txtUsuario = (TextView) findViewById(R.id.usu_usuario);
						txtContrasena = (TextView) findViewById(R.id.usu_contrasena);
						
						// Building Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("usu_usuario", (String) txtUsuario.getText().toString()));
						params.add(new BasicNameValuePair("usu_contrasena", (String) txtContrasena.getText().toString()));

						// getting product details by making HTTP request
						// Note that product details url will use GET request
						JSONObject json = jsonParser.makeHttpRequest(url_verificar_usuario, "GET", params);

						// check your log for json response
						Log.d("Single Product Details", json.toString());
						
						// json success tag
						success = json.getInt(TAG_SUCCESS);
						tipo_usuario = json.getString(TAG_TIPO_USUARIO);
						if (success == 1) {
							if (tipo_usuario.equalsIgnoreCase("OD")) {
								Intent i = new Intent(getApplicationContext(), CitasDoctor.class);
								startActivity(i);
							}
						}else{
							// product with pid not found
							txtRespuesta = (TextView) findViewById(R.id.respuesta);
							txtRespuesta.setText("Usuario o contraseña incorrecta");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

			return null;
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once got all details
			pDialog.dismiss();
		}
	

	}
}

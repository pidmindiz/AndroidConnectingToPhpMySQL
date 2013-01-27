package com.example.androidhive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ListView;

public class MostrarDetallesPaciente extends ListActivity {

	TextView txtCedula;
	TextView txtNombresApellidos;
	TextView txtTipoSangre;
	TextView txtTelefono;
	TextView txtEdad;
	
	ListView lvDiagnosticos;

	/*Button btnSave;
	Button btnDelete;*/

	String pac_cedula;
	
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
		
	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// single product url
	private static final String url_paciente_detalles = "http://www.ecuaconnect.com/ihm_android/crud/get_detalles_paciente.php";

	// url to update product
	private static final String url_update_product = "http://www.ecuaconnect.com/ihm_android/crud/get_detalles_paciente.php";
	
	private static final String url_paciente_diagnosticos = "http://www.ecuaconnect.com/ihm_android/crud/get_diagnosticos_paciente.php";
	
	// url to delete product
	private static final String url_delete_product = "http://www.ecuaconnect.com/ihm_android/crud/get_detalles_paciente.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PACIENTE = "pacientes";
	private static final String TAG_DIAGNOSTICOS = "diagnosticos";
	
	//private static final String TAG_CODIGO = "cit_codigo";
	private static final String TAG_CEDULA = "pac_cedula";
	private static final String TAG_NOMBRES_APELLIDOS = "pac_nombres_apellidos";
	private static final String TAG_TIPO_SANGRE = "pac_tipo_sangre";
	private static final String TAG_TELEFONO = "pac_telefono";
	private static final String TAG_EDAD = "pac_edad";
	
	//private static final String TAG_CODIGO = "cit_codigo";
	private static final String TAG_DIAGNOSTICO = "dia_diagnostico";
	private static final String TAG_FECHA = "dia_fecha";

	JSONArray diagnosticos = null;
	ArrayList<HashMap<String, String>> diagnosticosList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mostrar_detalles_paciente);

		// save button
		/*btnSave = (Button) findViewById(R.id.btnSave);
		btnDelete = (Button) findViewById(R.id.btnDelete);*/
		
		diagnosticosList = new ArrayList<HashMap<String, String>>();
		
		// getting product details from intent
		Intent i = getIntent();
		
		// getting product id (pid) from intent
		pac_cedula = i.getStringExtra(TAG_CEDULA);

		// Getting complete product details in background thread
		new GetPacienteDetalles().execute();
		new CargarDiagnosticoPaciente().execute();

		// save button click event
		/*btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// starting background task to update product
				new SaveProductDetails().execute();
			}
		});*/

		// Delete button click event
		/*btnDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// deleting product in background thread
				new DeleteProduct().execute();
			}
		});*/

	}

	/**
	 * Background Async Task to Get complete product details
	 * */
	class GetPacienteDetalles extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MostrarDetallesPaciente.this);
			pDialog.setMessage("Cargando detalles de paciente... Por favor espere...");
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
					try {
						// Building Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("pac_cedula", pac_cedula));

						// getting product details by making HTTP request
						// Note that product details url will use GET request
						JSONObject json = jsonParser.makeHttpRequest(
								url_paciente_detalles, "GET", params);

						// check your log for json response
						Log.d("Single Product Details", json.toString());
						
						// json success tag
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							// successfully received product details
							JSONArray pacienteObj = json
									.getJSONArray(TAG_PACIENTE); // JSON Array
							
							// get first product object from JSON Array
							JSONObject paciente = pacienteObj.getJSONObject(0);

							// product with this cit_codigo found
							// Edit Text
							txtNombresApellidos = (TextView) findViewById(R.id.pac_nombres_apellidos);
							txtTipoSangre = (TextView) findViewById(R.id.pac_tipo_sangre);
							txtEdad = (TextView) findViewById(R.id.pac_edad);
							txtCedula = (TextView) findViewById(R.id.pac_cedula);
							txtTelefono = (TextView) findViewById(R.id.pac_telefono);
							

							// display product data in EditText
							txtNombresApellidos.setText(paciente.getString(TAG_NOMBRES_APELLIDOS));
							txtTipoSangre.setText("Tipo de Sangre: "+ paciente.getString(TAG_TIPO_SANGRE));
							txtTipoSangre.setText("Tipo de Sangre: "+paciente.getString(TAG_TIPO_SANGRE));
							txtEdad.setText("Edad: "+ paciente.getString(TAG_EDAD)+ " años");
							txtCedula.setText("CI: "+ paciente.getString(TAG_CEDULA));
							txtTelefono.setText("Teléfono: "+paciente.getString(TAG_TELEFONO));
							
						}else{
							// product with pid not found
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

	/**
	 * Background Async Task to  Save product Details
	 * */
	class SaveProductDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MostrarDetallesPaciente.this);
			pDialog.setMessage("Saving product ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Saving product
		 * */
		protected String doInBackground(String... args) {

			// getting updated data from EditTexts
			//String name = txtName.getText().toString();
			//String price = txtPrice.getText().toString();
			//String description = txtDesc.getText().toString();

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_CEDULA, pac_cedula));
			params.add(new BasicNameValuePair(TAG_NOMBRES_APELLIDOS, ""));
			params.add(new BasicNameValuePair(TAG_TELEFONO, ""));
			params.add(new BasicNameValuePair(TAG_TIPO_SANGRE, ""));
			params.add(new BasicNameValuePair(TAG_EDAD, ""));
			// sending modified data through http request
			// Notice that update product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_update_product,
					"POST", params);

			// check json success tag
			try {
				int success = json.getInt(TAG_SUCCESS);
				
				if (success == 1) {
					// successfully updated
					Intent i = getIntent();
					// send result code 100 to notify about product update
					setResult(100, i);
					finish();
				} else {
					// failed to update product
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return null;
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product uupdated
			pDialog.dismiss();
		}
	}

	/*****************************************************************
	 * Background Async Task to Delete Product
	 * */
	class DeleteProduct extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MostrarDetallesPaciente.this);
			pDialog.setMessage("Deleting Product...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Deleting product
		 * */
		protected String doInBackground(String... args) {

			// Check for success tag
			int success;
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("pac_cedula", pac_cedula));

				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(
						url_delete_product, "POST", params);

				// check your log for json response
				Log.d("Delete Product", json.toString());
				
				// json success tag
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					// product successfully deleted
					// notify previous activity by sending code 100
					Intent i = getIntent();
					// send result code 100 to notify about product deletion
					setResult(100, i);
					finish();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

	}
	
	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class CargarDiagnosticoPaciente extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			/*pDialog = new ProgressDialog(MostrarDetallesPaciente.this);
			pDialog.setMessage("Cargando Diagnosticos. Por favor espere...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();*/
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("pac_cedula", pac_cedula));
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_paciente_diagnosticos, "GET", params);
			
			// Check your log cat for JSON reponse
			Log.d("Todas los diagnosticos: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					// Getting Array of Products
					diagnosticos = json.getJSONArray(TAG_DIAGNOSTICOS);

					// looping through All Products
					for (int i = 0; i < diagnosticos.length(); i++) {
						JSONObject d = diagnosticos.getJSONObject(i);

						// Storing each json item in variable
						String dia_diagnostico = d.getString(TAG_DIAGNOSTICO);
						String dia_fecha = d.getString(TAG_FECHA);

						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put(TAG_DIAGNOSTICO, dia_diagnostico);
						map.put(TAG_FECHA, dia_fecha);

						// adding HashList to ArrayList
						diagnosticosList.add(map);
					}
				} else {
					// no products found
					// Launch Add New product Activity
					Intent i = new Intent(getApplicationContext(),
							NewProductActivity.class);
					// Closing all previous activities
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 **/
					ListAdapter adapter = new SimpleAdapter(
							MostrarDetallesPaciente.this, diagnosticosList,
							R.layout.list_item_diagnostico, new String[] { TAG_DIAGNOSTICO, TAG_FECHA},
							new int[] { R.id.dia_diagnostico, R.id.dia_fecha });
					// updating listview
					setListAdapter(adapter);
				}
			});

		}

	}
}

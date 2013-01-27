package com.example.androidhive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class CitasDoctor extends ListActivity {

	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	ArrayList<HashMap<String, String>> citasList;

	// url to get all products list
	private static String url_all_citas = "http://www.ecuaconnect.com/ihm_android/crud/get_all_citas.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_CITAS = "citas";
	
	private static final String TAG_CODIGO = "cit_codigo";
	private static final String TAG_CEDULA = "pac_cedula";
	private static final String TAG_HORA = "cit_hora";
	private static final String TAG_FECHA = "cit_fecha";
	private static final String TAG_USUARIO = "cit_usuario";
	private static final String TAG_PACIENTE = "cit_paciente";

	// products JSONArray
	JSONArray citas = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_products);

		// Hashmap for ListView
		citasList = new ArrayList<HashMap<String, String>>();

		// Loading products in Background Thread
		new CagarCitas().execute();

		// Get listview
		ListView lv = getListView();

		// on seleting single product
		// launching Edit Product Screen
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// getting values from selected ListItem
				String pac_cedula = ((TextView) view.findViewById(R.id.pac_cedula)).getText()
						.toString();

				// Starting new intent
				Intent in = new Intent(getApplicationContext(),
						EditProductActivity.class);
				// sending pid to next activity
				in.putExtra(TAG_CEDULA, pac_cedula);
				
				// starting new activity and expecting some response back
				startActivityForResult(in, 100);
			}
		});

	}

	// Response from Edit Product Activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if result code 100
		if (resultCode == 100) {
			// if result code 100 is received 
			// means user edited/deleted product
			// reload this screen again
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}

	}

	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class CagarCitas extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(CitasDoctor.this);
			pDialog.setMessage("Loading products. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_all_citas, "GET", params);
			
			// Check your log cat for JSON reponse
			Log.d("Todas las citas: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					// Getting Array of Products
					citas = json.getJSONArray(TAG_CITAS);

					// looping through All Products
					for (int i = 0; i < citas.length(); i++) {
						JSONObject c = citas.getJSONObject(i);

						// Storing each json item in variable
						String cit_codigo = c.getString(TAG_CODIGO);
						String pac_cedula = c.getString(TAG_CEDULA);
						String hora = c.getString(TAG_HORA);
						String fecha = c.getString(TAG_FECHA);
						String usuario = c.getString(TAG_USUARIO);
						String paciente = c.getString(TAG_PACIENTE);
						

						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put(TAG_CODIGO, cit_codigo);
						map.put(TAG_CEDULA, pac_cedula);
						map.put(TAG_PACIENTE, paciente);
						map.put(TAG_USUARIO, usuario);
						map.put(TAG_FECHA, hora);
						map.put(TAG_HORA, fecha);

						// adding HashList to ArrayList
						citasList.add(map);
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
					 * */
					ListAdapter adapter = new SimpleAdapter(
							CitasDoctor.this, citasList,
							R.layout.list_item, new String[] { TAG_CODIGO,
									TAG_PACIENTE, TAG_USUARIO, TAG_HORA, TAG_FECHA},
							new int[] { R.id.cit_codigo, R.id.pac_cedula, R.id.paciente, R.id.usuario, R.id.hora, R.id.fecha });
					// updating listview
					setListAdapter(adapter);
				}
			});

		}

	}
}
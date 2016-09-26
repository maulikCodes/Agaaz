package edisonchallenge.agaaz.fragments;


import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edisonchallenge.agaaz.AppConfig;
import edisonchallenge.agaaz.AppController;
import edisonchallenge.agaaz.MainActivity;
import edisonchallenge.agaaz.R;
import edisonchallenge.agaaz.SQLiteHandler;
import edisonchallenge.agaaz.SessionManager;

public class Nharvfragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button dos;
    private Spinner stype;
    private Spinner crop;
    private EditText size;
    private Button update;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nharvest, container, false);

        dos = (Button)rootView.findViewById(R.id.dos);
        stype = (Spinner)rootView.findViewById(R.id.stype);
        crop = (Spinner)rootView.findViewById(R.id.crop);
        size = (EditText)rootView.findViewById(R.id.fsize);
        update = (Button)rootView.findViewById(R.id.update);

        //Spinner items for soil type
        /*List<String> categories = new ArrayList<String>();
        categories.add("Sandy");
        categories.add("Silty");
        categories.add("Clay");
        categories.add("Peaty");
        categories.add("Saline");*/

        String[] items1 = getResources().getStringArray(R.array.croptype);

        ArrayAdapter<String>dataAdapter = new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_spinner_dropdown_item, items1);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stype.setAdapter(dataAdapter);
        stype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item
                String item = parent.getItemAtPosition(position).toString();

                // Showing selected spinner item
                Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Spinner items for crop
        /*List<String> categories1 = new ArrayList<String>();
        categories1.add("Rice");
        categories1.add("Jowar");
        categories1.add("Maize");
        categories1.add("Ragi");
        categories1.add("Cotton");
        categories1.add("Bajra");
        categories1.add("Sugarcane");
        categories1.add("Jute");
        categories1.add("Wheat");
        categories1.add("Peas");
        categories1.add("Pulses");
        categories1.add("Mustard");
        categories1.add("Rapeseed");*/
        String[] items = getResources().getStringArray(R.array.soiltype);
        ArrayAdapter<String>dataAdapter1 = new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_spinner_dropdown_item,items);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        crop.setAdapter(dataAdapter1);
        crop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item
                String item = parent.getItemAtPosition(position).toString();

                // Showing selected spinner item
                Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getActivity());

        // Session manager
        session = new SessionManager(getActivity());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
        }

        final Calendar myCalender = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalender.set(Calendar.YEAR, year);
                myCalender.set(Calendar.MONTH, monthOfYear);
                myCalender.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                dos.setText(sdf.format(myCalender.getTime()));
            }
        };
        dos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date , myCalender.get(Calendar.YEAR), myCalender.get(Calendar.MONTH),
                        myCalender.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nsoiltype = stype.getSelectedItem().toString().trim();
                String cp = crop.getSelectedItem().toString().trim();
                String fs = size.getText().toString().trim();
                String date = dos.getText().toString().trim();

                if(!nsoiltype.isEmpty() && !cp.isEmpty() && !fs.isEmpty() && !date.isEmpty()){
                    updateUserData(nsoiltype, cp, fs, date);
                }else{
                    Toast.makeText(getActivity(), "Please enter the details!", Toast.LENGTH_LONG).show();
                }
            }
        });

        return rootView;
    }

    private void updateUserData(final String nsoiltype, final String cp, final String fs, final String date){
        // Tag used to cancel the request
        String tag_string_req = "req_update";

        pDialog.setMessage("Updating ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update Response: " + response.toString());
                hideDialog();

                try {
                    int jsonStart = response.indexOf('{');
                    int jsonEnd = response.lastIndexOf('}');
                    if(jsonStart>=0 && jsonEnd>=0 && jsonEnd>jsonStart) response = response.substring(jsonStart,jsonEnd+1);
                    else response = "";
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully updated in MySQL
                        // Now update the user in sqlite

                        JSONObject user = jObj.getJSONObject("user");
                        String soil = user.getString("nsoiltype");
                        String crop = user.getString("crop");
                        String fsize = user.getString("fieldsize");
                        String dateofs = user.getString("dateofsowing");



                        // Updating row in users table
                        db.updateDetails(soil,crop,fsize,dateofs);


                        Toast.makeText(getActivity(), "User successfully updated", Toast.LENGTH_LONG).show();

                    } else {

                        // Error occurred in update. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getActivity(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Update Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("nsoiltype", nsoiltype);
                params.put("crop", cp);
                params.put("fieldsize", fs);
                params.put("dateofsowing", date);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}

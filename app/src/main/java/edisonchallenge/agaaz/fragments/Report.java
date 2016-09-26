package edisonchallenge.agaaz.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

import edisonchallenge.agaaz.LoginActivity;
import edisonchallenge.agaaz.R;
import edisonchallenge.agaaz.SQLiteHandler;
import edisonchallenge.agaaz.SessionManager;


public class Report extends Fragment {
    private TextView txtTemp;
    private TextView txtMoist;
    private TextView txtHumid;
    private TextView txtSoil;
    private TextView txtFert;
    private TextView txtWater;

    private SQLiteHandler db;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report,container,false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtTemp = (TextView)getView().findViewById(R.id.temp);
        txtMoist = (TextView)getView().findViewById(R.id.moist);
        txtHumid = (TextView)getView().findViewById(R.id.humid);
        txtSoil = (TextView)getView().findViewById(R.id.stype);
        txtFert = (TextView)getView().findViewById(R.id.fert);
        txtWater = (TextView)getView().findViewById(R.id.wr);

        // SqLite database handler
        db = new SQLiteHandler(getActivity().getApplicationContext());

        // session manager
        session = new SessionManager(getActivity().getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        String temp = user.get("temp");
        String moisture = user.get("moist");
        String humidity = user.get("humid");
        String soil = user.get("soiltype");
        String fertilizer = user.get("fertilizer");
        String water = user.get("water");

        //Displaying details
        txtTemp.setText(temp + (char)0x00B0 +"C");
        txtMoist.setText(moisture + "%");
        txtHumid.setText(humidity + "%");
        txtSoil.setText(soil);
        txtFert.setText(fertilizer);
        txtWater.setText(water + " litres/m2");

    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}

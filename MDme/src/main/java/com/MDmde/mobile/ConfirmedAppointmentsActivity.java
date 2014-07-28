package com.MDmde.mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.savagelook.android.UrlJsonAsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ConfirmedAppointmentsActivity extends ActionBarActivity {

    private final String CONFIRMED_APPOINTMENTS_URL = WebserverUrl.ROOT_URL + "/api/v1/patients/appointments/confirmed.json";

    private SharedPreferences mPreferences;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmed_appointments);
        setTitle("Confirmed Appointments");

        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.confirmed_appointments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //only load if still signed in
        if(mPreferences.contains("ApiToken"))
        {
            loadAppointmentsFromAPI(CONFIRMED_APPOINTMENTS_URL);
        }
        else
        {
            Intent intent = new Intent(ConfirmedAppointmentsActivity.this, WelcomeActivity.class);
            startActivityForResult(intent, 0);
        }
    }

    private void loadAppointmentsFromAPI(String url)
    {
        GetAppointmentsTask getAppointmentsTask = new GetAppointmentsTask(ConfirmedAppointmentsActivity.this);
        getAppointmentsTask.setMessageLoading("Loading Appointments...");
        getAppointmentsTask.execute(url + "?api_token=" + mPreferences.getString("ApiToken", ""));
    }

    private class GetAppointmentsTask extends UrlJsonAsyncTask
    {
        public GetAppointmentsTask(Context context) { super(context); }

        @Override
        protected void onPostExecute(JSONObject json)
        {
            try
            {
                //add appointment times to list
                JSONArray jsonAppointments = json.getJSONObject("data").getJSONArray("appointments");
                int length = jsonAppointments.length();
                final List<Appointment> appointments = new ArrayList<Appointment>(length);
                for (int i = 0; i < length; i++)
                {
                    int id = jsonAppointments.getJSONObject(i).getInt("id");
                    String time = jsonAppointments.getJSONObject(i).getString("delayed_date_time_ampm");
                    String description = jsonAppointments.getJSONObject(i).getString("description");
                    String doctorName  = jsonAppointments.getJSONObject(i).getString("doctor_full_name");
                    int docId = jsonAppointments.getJSONObject(i).getInt("doctor_id");
                    Appointment apt = new Appointment(id, time, description, doctorName, docId);
                    appointments.add(apt);
                }

                //TODO may need to make appointment class to store id and time for easier api retrieval of show
                //add to listview
                final ListView appointmentsListView = (ListView)findViewById(R.id.confirmed_appointments_list_view);
                if (appointmentsListView != null)
                {
                    appointmentsListView.setAdapter(new ArrayAdapter<Appointment>(ConfirmedAppointmentsActivity.this,
                            android.R.layout.simple_list_item_1, appointments));
                }
            }
            catch (Exception e)
            {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ConfirmedAppointmentsIndex", "Error loading appointments", e);
            }
            finally
            {
                super.onPostExecute(json);
            }

        }
    }


}
package com.tsmsogn.pulllocationwitheslibrary;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ubhave.dataformatter.DataFormatter;
import com.ubhave.dataformatter.json.JSONFormatter;
import com.ubhave.datahandler.except.DataHandlerException;
import com.ubhave.sensormanager.ESException;
import com.ubhave.sensormanager.ESSensorManager;
import com.ubhave.sensormanager.config.GlobalConfig;
import com.ubhave.sensormanager.data.pull.LocationData;
import com.ubhave.sensormanager.sensors.SensorUtils;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private EditText editText;
    private JSONFormatter formatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button1);
        editText = (EditText) findViewById(R.id.editText1);

        formatter = DataFormatter.getJSONFormatter(this, SensorUtils.SENSOR_TYPE_LOCATION);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pullLocation();
            }
        });
    }

    private void updateUI(LocationData locationData) {
        try {
            editText.setText(formatter.toString(locationData));
        } catch (DataHandlerException e) {
            e.printStackTrace();
        }
    }

    private void pullLocation() {

        new PullLocationAsyncTask(this) {

            @Override
            protected void onPostExecute(LocationData locationData) {
                updateUI(locationData);
            }
        }.execute();
    }

    private class PullLocationAsyncTask extends AsyncTask<Void, Void, LocationData> {

        private static final String TAG = "PullLocationAsyncTask";
        private ESSensorManager sensorManager;

        PullLocationAsyncTask(Context context) {
            try {
                sensorManager = ESSensorManager.getSensorManager(context);
                sensorManager.setGlobalConfig(GlobalConfig.PRINT_LOG_D_MESSAGES, false);
            } catch (ESException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected LocationData doInBackground(Void... params) {
            LocationData data = null;
            try {
                Log.d(TAG, "Start sensing");
                data = (LocationData) sensorManager.getDataFromSensor(SensorUtils.SENSOR_TYPE_LOCATION);
                Log.d(TAG, "Sensed");
            } catch (ESException e) {
                e.printStackTrace();
            }

            return data;
        }
    }
}

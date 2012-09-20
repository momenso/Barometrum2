package momenso.barometrum2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;
import java.util.Observable;
import java.util.Observer;
import momenso.barometrum2.PressureDataPoint.PressureMode;
import momenso.barometrum2.PressureDataPoint.PressureUnit;
import momenso.barometrum2.gui.BorderedTextView;
import momenso.barometrum2.gui.ChartView;


public class Barometrum2Activity extends Activity implements Observer {

    private Barometer barometer;
    private Altimeter altimeter;
    private ReadingsData pressureData;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Context context = getApplicationContext();

        barometer = new Barometer(context);
        barometer.addObserver(this);

        altimeter = new Altimeter(context);
        altimeter.addObserver(this);

        pressureData = ReadingsData.getInstance(context);
        
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        pressureData.setMode(PressureMode.valueOf(preferences.getString("BarometerMode", "Absolute")),
                Integer.valueOf(preferences.getString("KnownAltitude", "0")));
        pressureData.setUnit(PressureUnit.valueOf(preferences.getString("BarometerUnit", "Bar")));

        // initialize pressure reading display
        final TextView currentReading = (TextView) findViewById(R.id.currentReading);
        final Typeface digitalFont = Typeface.createFromAsset(getAssets(), "font/digital.ttf");
        currentReading.setTypeface(digitalFont);
        currentReading.setTextColor(Color.WHITE);

        // initialize minimum pressure display
        final BorderedTextView minPressureReading = (BorderedTextView) findViewById(R.id.minimumReading);
        final Typeface normalFont = Typeface.createFromAsset(getAssets(), "font/normal.ttf");
        minPressureReading.setTypeface(normalFont);
        minPressureReading.setText(String.format("%.2f", pressureData.getMinimum()));

        // initialize maximum pressure display
        final BorderedTextView maxPressureReading = (BorderedTextView) findViewById(R.id.maximumReading);
        maxPressureReading.setTypeface(normalFont);
        maxPressureReading.setText(String.format("%.2f", pressureData.getMaximum()));

        // initialize altimeter display
        final BorderedTextView altitudeReading = (BorderedTextView) findViewById(R.id.altitudeReading);
        altitudeReading.setTypeface(normalFont);
        altitudeReading.setText(String.format("%.0fm", altimeter.getAltitude()));

    }
    
    public void clearReadings(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to clear all readings?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                pressureData.clear();
            }
        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void adjustSettings(View view) {
        Intent settingsIntent = new Intent(view.getContext(), SettingsActivity.class);
        startActivity(settingsIntent);
    }

    @Override
    protected void onStop() {
        pressureData.saveReadings();

//		altimeter.disable();
//		barometer.terminate();			

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (isFinishing()) {
            altimeter.disable();
            barometer.terminate();
        }

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu);

        return true;
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable.getClass() == Altimeter.class) {

            float altitude = (Float) data;
            pressureData.setCurrentElevation(altitude);

            BorderedTextView altitudeText = (BorderedTextView) findViewById(R.id.altitudeReading);
            altitudeText.setText(String.format("%.0fm", altitude));

        } else if (observable.getClass() == Barometer.class) {

            float pressure = (Float) data;
            pressureData.add(pressure);

            // update minimum pressure display
            final BorderedTextView minPressureReading = (BorderedTextView) findViewById(R.id.minimumReading);
            minPressureReading.setText(String.format("%.2f",
                    Math.round(pressureData.getMinimum() * 100.0) / 100.0));

            // update maximum pressure display
            final BorderedTextView maxPressureReading = (BorderedTextView) findViewById(R.id.maximumReading);
            maxPressureReading.setText(String.format("%.2f",
                    Math.round(pressureData.getMaximum() * 100.0) / 100.0));

            // update current pressure value
            final TextView currentValueText = (TextView) findViewById(R.id.currentReading);
            currentValueText.setText(String.format("%.2f",
                    Math.round(pressureData.getAverage() * 100.0) / 100.0));

            updateHistoryChart();
        }

    }

    private void updateHistoryChart() {
        ChartView historyChart = (ChartView) this
                .findViewById(R.id.historyChart);
        if (historyChart != null) {
            historyChart.updateData(pressureData);
        }
    }

}
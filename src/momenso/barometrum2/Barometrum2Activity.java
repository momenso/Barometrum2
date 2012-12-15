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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import java.util.Observable;
import java.util.Observer;

import momenso.barometrum2.PressureDataPoint.PressureMode;
import momenso.barometrum2.PressureDataPoint.PressureUnit;
import momenso.barometrum2.gui.BorderedTextView;
import momenso.barometrum2.gui.ChangeView;
import momenso.barometrum2.gui.ChartView;
import momenso.barometrum2.gui.Gauge;


public class Barometrum2Activity extends Activity implements Observer {

    private Barometer barometer;
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

        pressureData = ReadingsData.getInstance(context);

        // Handles failure when loading preferences
        try {
        	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	        pressureData.setMode(PressureMode.valueOf(preferences.getString("BarometerMode", "Absolute")),
	                Integer.valueOf(preferences.getString("KnownAltitude", "0")));
	        pressureData.setUnit(PressureUnit.valueOf(preferences.getString("BarometerUnit", "Bar")));
	        pressureData.setCorrection(Float.valueOf(preferences.getString("SensorCorrection", "0")));
	        pressureData.setPrecision(Integer.valueOf(preferences.getString("SensorPrecision", "1")));
	        int interval = Integer.valueOf(preferences.getString("GraphTimeScale", "1")) * 60000;
	        pressureData.setHistoryInterval(interval);
        } catch (Exception e) {
        	// load default settings
        	e.printStackTrace();
        	pressureData.setMode(PressureMode.Absolute);
        	pressureData.setUnit(PressureUnit.mBar);
        	pressureData.setHistoryInterval(60000);
        	pressureData.setCorrection(0);
        }

        // initialize pressure reading display
        final TextView currentReading = (TextView) findViewById(R.id.currentReading);
        if (currentReading != null) {
        	currentReading.setTextColor(Color.BLACK);
	        //final Typeface digitalFont = Typeface.createFromAsset(getAssets(), "font/digital.ttf");
        	final Typeface digitalFont = Typeface.createFromAsset(getAssets(), "font/lcdphone.ttf");
	        currentReading.setTypeface(digitalFont);
	        currentReading.setTextColor(Color.WHITE);
        }

        // initialize altimeter display
//        final BorderedTextView altitudeReading = (BorderedTextView) findViewById(R.id.altitudeReading);
//        if (altitudeReading != null) {
//	        final Typeface normalFont = Typeface.createFromAsset(getAssets(), "font/normal.ttf");
//	        altitudeReading.setTypeface(normalFont);
//	        altitudeReading.setText(String.format("%sm", preferences.getString("KnownAltitude", "0")));
//        }
    }

    public void clearReadings(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to clear all readings?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                pressureData.clear();
                pressureData.saveReadings();
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
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	return false;
    }

    public void adjustSettings(View view) {
        Intent settingsIntent = new Intent(view.getContext(), SettingsActivity.class);
        startActivity(settingsIntent);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	
    	pressureData.saveReadings();
    }

    @Override
    protected void onDestroy() {
        if (isFinishing()) {
            barometer.terminate();
        }

        super.onDestroy();
    }

    public void update(Observable observable, Object data) {
        if (observable.getClass() == Barometer.class) {

            float pressure = (Float) data;
            pressureData.add(pressure);

            // update current pressure value
            final BorderedTextView currentValueText = (BorderedTextView) findViewById(R.id.currentReading);
            if (currentValueText != null) {
            	currentValueText.setText(pressureData.getAverageDisplayValue());
            }
            
            Gauge gauge = (Gauge) this.findViewById(R.id.pressureGauge);
            if (gauge != null) {
            	gauge.updatePressure(pressureData);
            }
            
            ChangeView changer = (ChangeView) this.findViewById(R.id.tendencyArrow);
            if (changer != null) {
            	changer.update(pressureData);
            }

            updateHistoryChart();
        }
    }

    private void updateHistoryChart() {
        ChartView historyChart = (ChartView) this.findViewById(R.id.historyChart);
        if (historyChart != null) {
            historyChart.updateData(pressureData);
        }
    }
}
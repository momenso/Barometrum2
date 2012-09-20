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
    //private Preferences preferences;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Context context = getApplicationContext();
        //preferences = new Preferences(context);

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

        // button event handlers
//		final Button clearButton = (Button)findViewById(R.id.clearButton);
//		clearButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//            }
//        });
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

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//
//            case R.id.itemBarometerMode:
//                selectBarometerMode();
//                return true;
//
//            case R.id.itemPressureUnit:
//                selectPressureUnit();
//                return true;
//
//            case R.id.itemLogInterval:
//                selectLoggingInterval();
//                return true;
//
//            case R.id.itemAltimeter:
//                if (altimeter.switchSensor()) {
//                    item.setTitle(R.string.altimeterDisable);
//                } else {
//                    item.setTitle(R.string.altimeterEnable);
//                }
//                return true;
//
//            case R.id.itemMenuClear:
//                pressureData.clear();
//                return true;
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable.getClass() == Altimeter.class) {

            float altitude = (Float) data;
            pressureData.setCurrentElevation(altitude);

            // BlockView altitudeText =
            // (BlockView)findViewById(R.id.altitudeReading);
            // altitudeText.setText(String.format("%.0fm", altitude));
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

            /*
             * float trend = pressureData.getTrend(); float degrees =
             * (float)Math.toDegrees(Math.atan(trend)); ImageView arrow =
             * (ImageView) findViewById(R.id.arrowImage);
             * arrow.setRotation(degrees);
             */

            // updateGraph();
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

//    private void selectPressureUnit() {
//        final CharSequence[] items = {"Bar", "Inches of Mercury", "Torr", "Pascal"};
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Pressure Unit");
//        builder.setItems(items, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int item) {
//                //Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
//
//                if (item == 0) {
//                    pressureData.setUnit(PressureUnit.Bar);
//                    preferences.setPressureUnit(PressureUnit.Bar);
//                } else if (item == 1) {
//                    pressureData.setUnit(PressureUnit.InHg);
//                    preferences.setPressureUnit(PressureUnit.InHg);
//                } else if (item == 2) {
//                    pressureData.setUnit(PressureUnit.Torr);
//                    preferences.setPressureUnit(PressureUnit.Torr);
//                } else if (item == 3) {
//                    pressureData.setUnit(PressureUnit.Pascal);
//                    preferences.setPressureUnit(PressureUnit.Pascal);
//                }
//                /*
//                 BlockView maxReading = (BlockView) findViewById(R.id.maximumReading);
//                 maxReading.setUnit(ReadingsData.getUnitName());
//                 BlockView minReading = (BlockView) findViewById(R.id.minimumReading);
//                 minReading.setUnit(ReadingsData.getUnitName());
//                 */
//            }
//        });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }

//    private void selectBarometerMode() {
//        final CharSequence[] items = {"Absolute", "Weather (MSLP)"};
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Reading Mode");
//        builder.setItems(items, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int item) {
//                //Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
//                if (item == 0) {
//                    pressureData.setMode(PressureMode.ABSOLUTE);
//                    preferences.setPressureMode(PressureMode.ABSOLUTE);
//                } else if (item == 1) {
//                    pressureData.setMode(PressureMode.MSLP);
//                    preferences.setPressureMode(PressureMode.MSLP);
//                }
//            }
//        });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }
//
//    private void selectLoggingInterval() {
//        final CharSequence[] items = {"1 min", "5 min", "30 min"};
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Logging Interval");
//        builder.setItems(items, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int item) {
//                int interval = 60000;
//                if (item == 1) {
//                    interval *= 5;
//                } else if (item == 2) {
//                    interval *= 30;
//                }
//
//                pressureData.setHistoryInterval(interval);
//                preferences.setLoggingInterval(interval);
//            }
//        });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }
}
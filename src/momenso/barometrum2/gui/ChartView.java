package momenso.barometrum2.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import momenso.barometrum2.PressureDataPoint;
import momenso.barometrum2.ReadingsData;

public class ChartView extends TextView {

	private ReadingsData data;
	private PointerCoords selectedSpot = new PointerCoords();
	private int selectedBar = -1;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM");
	private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

	public ChartView(Context context) {
		super(context);
	}

	public ChartView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// initializeParams(attrs);
		//fake();
	}

	public ChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// initializeParams(attrs);
	}

	public void fake() {
		data = ReadingsData.getInstance(getContext());
		List<PressureDataPoint> dummy = new ArrayList<PressureDataPoint>();
		int p = 0;
		for (int i = 2000; i > 0; i -= 100) {
			dummy.add(new PressureDataPoint(p++, i));
		}
		for (int i = 0; i < 2000; i += 100) {
			dummy.add(new PressureDataPoint(p++, i));
		}

		data.setHistory(dummy);
	}

	/*
	 * protected void initializeParams(AttributeSet attrs) {
	 * super.initializeParams(attrs);
	 * 
	 * String nameSpace =
	 * "http://schemas.android.com/apk/res/momenso.barometrum";
	 * 
	 * this.top = attrs.getAttributeBooleanValue(nameSpace, "border_top", true);
	 * this.left = attrs.getAttributeBooleanValue(nameSpace, "border_left",
	 * true); this.right = attrs.getAttributeBooleanValue(nameSpace,
	 * "border_right", true); this.bottom =
	 * attrs.getAttributeBooleanValue(nameSpace, "border_bottom", true); }
	 */
	public void updateData(ReadingsData data) {
		this.data = data;
		this.invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Rect rect = new Rect();
		getDrawingRect(rect);

		Paint paint = new Paint();
		paint.setTypeface(getTypeface());
		paint.setAntiAlias(true);

		// Generate border
		int borderWidth = 3;
		RectF borderRect = new RectF(rect.left + borderWidth / 2, rect.top
				+ borderWidth / 2, rect.right - borderWidth / 2, rect.bottom
				- borderWidth / 2);
		paint.setStyle(Style.FILL);
		int backGroundColor = Color.rgb(30, 30, 30);
		paint.setColor(backGroundColor);
		canvas.drawRoundRect(borderRect, 15, 15, paint);

		int borderColor = Color.rgb(150, 150, 150);
		paint.setColor(borderColor);
		paint.setStrokeWidth(borderWidth);
		paint.setStyle(Style.STROKE);
		canvas.drawRoundRect(borderRect, 15, 15, paint);

		// draw bottom axis
		paint.setAntiAlias(false);
		paint.setStrokeWidth(2);
		paint.setStyle(Style.FILL);
		paint.setColor(Color.WHITE);
		float textMargin = paint.descent() + 2;
		float axisMargin = getTextSize() + 2 * textMargin;
		
		canvas.drawLine(rect.left + borderWidth, rect.bottom - axisMargin,
				rect.right - borderWidth, rect.bottom - axisMargin, paint);
		
		// draw top axis
		canvas.drawLine(rect.left + borderWidth, rect.top + axisMargin,
				rect.right - borderWidth, rect.top + axisMargin, paint);

		if (this.data == null) {
			return;
		}

		// draw y axis
		getDrawingRect(rect);
		paint.setColor(Color.WHITE);
		paint.setTextAlign(Align.LEFT);
		paint.setTypeface(Typeface.MONOSPACE);
		paint.setTextSize(13 + rect.width() / 50);

		canvas.drawText(String.format("%.2f", data.getMinimum()), 6,
				rect.bottom - axisMargin - paint.descent(), paint);
		String maxValue = String.format("%.2f", data.getMaximum());
		canvas.drawText(maxValue, 6, paint.getTextSize() + axisMargin, paint);
		int yMargin = Math.round(paint.measureText(maxValue));
		//paint.setTextSize(originalSize);
		//paint.setTypeface(getTypeface().DEFAULT);
		// get range for Y axis
		float maximum = data.getMaximumValue().getRawValue();
		float minimum = data.getMinimumValue().getRawValue();
		
		List<PressureDataPoint> values = data.getHistory();
		if (values.size() <= 0) {
			return;
		}

		// draw data columns
		paint.setStrokeWidth(1);
		int barColor = Color.rgb(0x33, 0xb5, 0xe5);
		paint.setAntiAlias(true);
		float lateralMargin = getTextSize() / 2;
		int columnWidth = ((rect.width() - yMargin - (4 * borderWidth)) / values.size());
		float barPos = rect.left + lateralMargin + columnWidth / 2;
		//int yValue = values.size();
		float maximumColumnHeight = rect.height() - 2 * axisMargin - borderWidth;
		int barIndex = 0;
		selectedBar = -1;
		for (PressureDataPoint bar : values) {
			
			RectF barRect = new RectF(
					yMargin + borderWidth + barPos + 3 - columnWidth / 3, 
					convertY(bar.getRawValue(), minimum, maximum, maximumColumnHeight) + borderWidth + axisMargin, 
					yMargin + borderWidth + barPos + 3 + columnWidth / 3, 
					rect.height() - axisMargin);

			if (barRect.contains(selectedSpot.x, selectedSpot.y)) {
				paint.setColor(Color.WHITE);
				//selectedSpot.clear();
				selectedBar = barIndex;
			}
			else {
				paint.setColor(barColor);
			}
			
			// draw bar
			canvas.drawRect(barRect, paint);

			// draw index number
			// paint.setColor(Color.WHITE);
			// paint.setTextAlign(Align.CENTER);
			// paint.setTextSize(getTextSize());
			// canvas.drawText(String.valueOf(yValue),
			// barRect.centerX(), rect.bottom - borderWidth - textMargin,
			// paint);

			// update for next bar
			barIndex++;
			barPos += columnWidth;
			//yValue--;
		}
		
		if (selectedBar > -1 && selectedBar < values.size()) {
			//paint.setTextSize(13 + rect.width() / 80);
			PressureDataPoint selected = values.get(selectedBar);
			
			String pressure = String.format("%.2f",
					selected.getValue(data.getMode(), data.getUnit(), data.getAltitude(), data.getCorrection()));
			
			paint.setColor(Color.WHITE);
			canvas.drawText(pressure, 8,
					rect.centerY() + paint.getTextSize(), paint);
			
			float width = paint.measureText(pressure);
			paint.setTextAlign(Align.CENTER);
			Date timestamp = new Date(selected.getTime());
			canvas.drawText(dateFormat.format(timestamp), 
					8 + width / 2, rect.centerY() - paint.getTextSize(), paint);
			canvas.drawText(timeFormat.format(timestamp), 
					8 + width / 2 + 1, rect.centerY(), paint);
			
			float barX = rect.left + lateralMargin + columnWidth / 2 + selectedBar*columnWidth + yMargin + borderWidth;
			canvas.drawLine(8 + width, 
					rect.centerY(), 
					barX, 
					rect.centerY(), 
					paint);
			
			float barHeight = convertY(selected.getRawValue(), minimum, maximum, maximumColumnHeight) + borderWidth + axisMargin;
			if (barHeight > rect.centerY()) {
				canvas.drawLine(barX, rect.centerY(), barX, barHeight, paint);
			}
			
			paint.setStyle(Style.STROKE);
			canvas.drawRect(6, rect.centerY() - paint.getTextSize() + paint.ascent(), 
					8 + width, rect.centerY() + paint.getTextSize() + paint.descent(), paint);
		}
	}

	private float convertY(float value, float min, float max, float height) {
		float factor = (float) (1 - (value - min) / (max - min));
		// int y = Math.round(factor * (float)height);
		// int y = (int)(factor * (float)height);

		return factor * height;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		event.getPointerCoords(0, selectedSpot);
		invalidate();
		return super.onTouchEvent(event);
	}
	
	/*
	 * private PressureDataPoint[] sample = { new PressureDataPoint(3600000L*0,
	 * 1010.0F), new PressureDataPoint(3600000L*1, 1014.0F), new
	 * PressureDataPoint(3600000L*2, 1016.0F), new PressureDataPoint(3600000L*3,
	 * 1010.0F), new PressureDataPoint(3600000L*4, 1008.0F), new
	 * PressureDataPoint(3600000L*5, 1010.0F), new PressureDataPoint(3600000L*6,
	 * 1010.0F), new PressureDataPoint(3600000L*7, 1010.0F), new
	 * PressureDataPoint(3600000L*8, 1010.0F), new PressureDataPoint(3600000L*9,
	 * 1010.0F), new PressureDataPoint(3600000L*10, 1020.0F), new
	 * PressureDataPoint(3600000L*11, 1025.0F), new
	 * PressureDataPoint(3600000L*12, 1010.0F), new
	 * PressureDataPoint(3600000L*13, 1010.0F), new
	 * PressureDataPoint(3600000L*14, 1010.0F), new
	 * PressureDataPoint(3600000L*15, 1010.0F), new
	 * PressureDataPoint(3600000L*16, 1010.0F), new
	 * PressureDataPoint(3600000L*17, 1010.0F), new
	 * PressureDataPoint(3600000L*18, 1010.0F), new
	 * PressureDataPoint(3600000L*19, 1010.0F), new
	 * PressureDataPoint(3600000L*20, 1010.0F), new
	 * PressureDataPoint(3600000L*21, 1010.0F), new
	 * PressureDataPoint(3600000L*22, 1010.0F), new
	 * PressureDataPoint(3600000L*23, 1012.0F) };
	 */
}

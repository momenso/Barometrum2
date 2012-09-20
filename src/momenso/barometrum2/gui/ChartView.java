package momenso.barometrum2.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.TextView;
import java.util.List;
import momenso.barometrum2.PressureDataPoint;
import momenso.barometrum2.ReadingsData;

public class ChartView extends TextView {

    private ReadingsData data;

    public ChartView(Context context) {
        super(context);
    }

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //initializeParams(attrs);
    }

    public ChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        //initializeParams(attrs);
    }

    /*protected void initializeParams(AttributeSet attrs) {
     super.initializeParams(attrs);
		
     String nameSpace = "http://schemas.android.com/apk/res/momenso.barometrum";
		
     this.top = attrs.getAttributeBooleanValue(nameSpace, "border_top", true);
     this.left = attrs.getAttributeBooleanValue(nameSpace, "border_left", true);
     this.right = attrs.getAttributeBooleanValue(nameSpace, "border_right", true);
     this.bottom = attrs.getAttributeBooleanValue(nameSpace, "border_bottom", true);
     }*/
    public void updateData(ReadingsData data) {
        this.data = data;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Rect rect = new Rect();
        getLocalVisibleRect(rect);

        Paint paint = new Paint();
        paint.setTypeface(getTypeface());
        paint.setAntiAlias(true);

        // Generate border
        int borderWidth = 4;
        RectF borderRect = new RectF(
                rect.left + borderWidth / 2,
                rect.top + borderWidth / 2,
                rect.right - borderWidth / 2,
                rect.bottom - borderWidth / 2);
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
        paint.setColor(borderColor);
        float textMargin = paint.descent() + 2;
        float axisTop = getTextSize() + 2 * textMargin;
        canvas.drawLine(
                rect.left + borderWidth,
                rect.bottom - axisTop,
                rect.right - borderWidth,
                rect.bottom - axisTop, paint);

        if (this.data == null) {
            return;
        }

        // get range for Y axis 
        float maximum = data.getMaximumValue().getRawValue();
        float minimum = data.getMinimumValue().getRawValue();

        //Log.v("ChartView", String.format("min=%.2f max=%.2f", minimum, maximum));

        // draw data columns
        paint.setStrokeWidth(1);
        int barColor = Color.rgb(0x33, 0xb5, 0xe5);
        List<PressureDataPoint> values = data.getHistory();
        paint.setAntiAlias(true);
        float lateralMargin = getTextSize() / 2;
        int columnWidth = ((rect.width() - (10 * borderWidth)) / values.size());
        float barPos = rect.left + lateralMargin + columnWidth / 2;
        int yValue = values.size();
        float maximumColumnHeight = rect.height() - axisTop - borderWidth;
        for (PressureDataPoint bar : values) {
            RectF barRect = new RectF(
                    barPos - columnWidth / 3,
                    convertY(bar.getRawValue(), minimum, maximum, maximumColumnHeight) + borderWidth,
                    barPos + columnWidth / 3,
                    rect.height() - axisTop);

            // draw bar
            paint.setColor(barColor);
            canvas.drawRect(barRect, paint);

            // draw index number
            paint.setColor(Color.WHITE);
            paint.setTextAlign(Align.CENTER);
            paint.setTextSize(getTextSize());
            canvas.drawText(String.valueOf(yValue),
                    barRect.centerX(), rect.bottom - borderWidth - textMargin, paint);

            // update for next bar
            barPos += columnWidth;
            yValue--;
        }
    }

    private float convertY(float value, float min, float max, float height) {
        float factor = 1 - (value - min) / (max - min);
//		int y = Math.round(factor * (float)height);
//		int y = (int)(factor * (float)height);

        return factor * height;
    }
    /*
     @Override
     public boolean onTouchEvent(MotionEvent event) {

     PointerCoords coords = new PointerCoords();
     event.getPointerCoords(0, coords);
		
     selectedSpot.x = (int) coords.x;
     selectedSpot.y = (int) coords.y;
		
     return super.onTouchEvent(event);
     }*/
    /*
     private PressureDataPoint[] sample = {
     new PressureDataPoint(3600000L*0, 1010.0F),
     new PressureDataPoint(3600000L*1, 1014.0F),
     new PressureDataPoint(3600000L*2, 1016.0F),
     new PressureDataPoint(3600000L*3, 1010.0F),
     new PressureDataPoint(3600000L*4, 1008.0F),
     new PressureDataPoint(3600000L*5, 1010.0F),
     new PressureDataPoint(3600000L*6, 1010.0F),
     new PressureDataPoint(3600000L*7, 1010.0F),
     new PressureDataPoint(3600000L*8, 1010.0F),
     new PressureDataPoint(3600000L*9, 1010.0F),
     new PressureDataPoint(3600000L*10, 1020.0F),
     new PressureDataPoint(3600000L*11, 1025.0F),
     new PressureDataPoint(3600000L*12, 1010.0F),
     new PressureDataPoint(3600000L*13, 1010.0F),
     new PressureDataPoint(3600000L*14, 1010.0F),
     new PressureDataPoint(3600000L*15, 1010.0F),
     new PressureDataPoint(3600000L*16, 1010.0F),
     new PressureDataPoint(3600000L*17, 1010.0F),
     new PressureDataPoint(3600000L*18, 1010.0F),
     new PressureDataPoint(3600000L*19, 1010.0F),
     new PressureDataPoint(3600000L*20, 1010.0F),
     new PressureDataPoint(3600000L*21, 1010.0F),
     new PressureDataPoint(3600000L*22, 1010.0F),
     new PressureDataPoint(3600000L*23, 1012.0F)
     };
     */
}

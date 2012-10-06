package momenso.barometrum2.gui;

import momenso.barometrum2.ReadingsData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.View;


public class Gauge extends View {

    private Rect screen = new Rect();
    private Paint paint = new Paint();
    private Path labelPath = new Path();
    private float maximum = 1070;
    private float minimum = 930;
    private float current = 930;
    
    public float getMaximum() {
		return maximum;
	}

	public void setMaximum(float maximum) {
		this.maximum = maximum;
		adjustCurrent();
	}

	public float getMinimum() {
		return minimum;
	}

	public void setMinimum(float minimum) {
		this.minimum = minimum;
		adjustCurrent();
	}
	
	private void adjustCurrent() {
		if (current > maximum) {
			current = maximum;
		}
		
		if (current < minimum) {
			current = minimum;
		}
	}

    public Gauge(Context context) {
        super(context);
        initialize();
    }

    public Gauge(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public Gauge(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        screen = new Rect();
        paint = new Paint();
        labelPath = new Path();
    }

    public void updatePressure(ReadingsData pressure) {
    	this.current = pressure.getAverage();
    	
    	switch (pressure.getUnit()) {
    		case Bar:
    			maximum = 1070;
    			minimum = 930;
    			break;
    			
    		case InHg:
    			maximum = 31;
    			minimum = 28;
    			break;
    			
    		case Pascal:
    			maximum = 110;
    			minimum = 95;
    			break;
    			
    		case Torr:
    			maximum = 800;
    			minimum = 700;
    			break;
    	}
    	
    	invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
    	
        getDrawingRect(screen);

        paint.setAntiAlias(true);
        paint.setStrokeWidth(4);

        int radius = (8 * Math.min(screen.width(), screen.height()) / 9) / 2;
        paint.setTextSize(radius / 9);
        
        // background
        paint.setColor(Color.LTGRAY);
        paint.setStyle(Style.FILL);
        canvas.drawCircle(
                screen.centerX(),
                screen.centerY(),
                radius,
                paint);

        // border
        paint.setColor(Color.GRAY);
        paint.setStyle(Style.STROKE);
        canvas.drawCircle(
                screen.centerX(),
                screen.centerY(),
                radius,
                paint);
        
        labelPath.reset();
        labelPath.addCircle(screen.centerX(), screen.centerY(), radius, Direction.CW);
        
        //labels 930-1070
		float pointerSize = (8 * radius / 9);
		float interval = Math.min(10, gcd((int) maximum, (int) minimum));
		float labels = ((maximum - minimum) / interval);
        float offset = -65;
        float angularStep = 310 / labels;

        paint.setTextAlign(Align.CENTER);
        paint.setStyle(Style.FILL_AND_STROKE);
		paint.setColor(Color.DKGRAY);
		paint.setStrokeWidth(2);
		for (int value = (int) minimum; value <= maximum; value += interval, offset += angularStep) {
			canvas.save();
			canvas.rotate(offset, screen.centerX(), screen.centerY());
			canvas.drawTextOnPath(String.valueOf(value), labelPath, 0, radius / 10, paint);
			canvas.restore();
		}
		
		// draw ticks
		float minorStep = interval / 10;
		paint.setColor(Color.BLACK);
		for (float mark = minimum; mark <= maximum; mark += minorStep) {
			mark = (mark * 100) / 100; // fix decimal precision
			float angl = (float) (((maximum - mark) * 310.0f) / (maximum - minimum));
			
			paint.setStrokeWidth(mark % interval == 0 ? 5 : 2);
			float rad = (float) Math.toRadians(angl - 65);
			canvas.drawLine(screen.exactCenterX(), screen.exactCenterY(),
					screen.exactCenterX() + FloatMath.cos(rad) * pointerSize,
					screen.exactCenterY() - FloatMath.sin(rad) * pointerSize,
					paint);
		}
		
		paint.setColor(Color.LTGRAY);
        paint.setStyle(Style.FILL_AND_STROKE);
        canvas.drawCircle(
                screen.centerX(),
                screen.centerY(),
                7 * pointerSize / 8,
                paint);


        // draw pointer
        paint.setColor(Color.RED);
		if (current >= minimum && current <= maximum) {
			float angl = (float) (((maximum - current) * 310.0f) / (maximum - minimum));
			float rad = (float) Math.toRadians(angl - 65);
			paint.setStrokeWidth(2);
			canvas.drawLine(screen.exactCenterX(), screen.exactCenterY(),
					screen.exactCenterX() + FloatMath.cos(rad) * pointerSize,
					screen.exactCenterY() - FloatMath.sin(rad) * pointerSize,
					paint);
			paint.setStrokeWidth(5);
			canvas.drawLine(screen.exactCenterX(), screen.exactCenterY(),
					screen.exactCenterX() + FloatMath.cos(rad) * (6 * pointerSize / 8),
					screen.exactCenterY() - FloatMath.sin(rad) * (6 * pointerSize / 8),
					paint);
		}
		else {
			Log.w("Draw", "Invalid pressure value: " + current);
		}
		
		paint.setColor(Color.rgb(28, 15, 14));
        paint.setStyle(Style.FILL_AND_STROKE);
        canvas.drawCircle(
                screen.centerX(),
                screen.centerY(),
                radius / 50,
                paint);
		
        super.onDraw(canvas);
    }
    
	public static int gcd(int p, int q) {
		if (q == 0) {
			return p;
		}
		return gcd(q, p % q);
	}
}

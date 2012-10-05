package momenso.barometrum2.gui;

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
        current = 930;
        maximum = 1070;
        minimum = 930;
    }

    public void updatePressure(float pressure) {
    	this.current = pressure;
    	
//    	float offset = (current - pressure) / 2;
//    	if (Math.abs(current - pressure) > 0.001) {
//			Log.i("Smoother", String.format("Current = %.3f (%f) -> %.2f", current, offset, pressure));
//			current -= offset;
//			postInvalidate();
//		}
//    	else {
//    		Log.i("Smoother", "Complete");
//    	}
    	
    	invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
    	
        getDrawingRect(screen);

        paint.setAntiAlias(true);
        paint.setStrokeWidth(4);
        paint.setTextSize(32);

        int radius = (8 * Math.min(screen.width(), screen.height()) / 9) / 2;

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
		float interval = gcd((int) maximum, (int) minimum);
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
			canvas.drawTextOnPath(String.valueOf(value), labelPath, 0, paint.getTextSize(), paint);
			canvas.restore();
		}
		
		for (int mark = (int) minimum; mark <= maximum; mark++) {
			float angl = (float) (((maximum - mark) * 310.0f) / (maximum - minimum));

			paint.setColor(Color.BLACK);
			paint.setStrokeWidth(mark % 10 == 0 ? 5 : 2);
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

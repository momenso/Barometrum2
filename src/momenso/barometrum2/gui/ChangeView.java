package momenso.barometrum2.gui;

import momenso.barometrum2.ReadingsData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class ChangeView extends View {

    private Rect screen;
    private RectF roundRect;
    private Paint paint = new Paint();
    private int cornerXRadius = 15;
	private int cornerYRadius = 15;
	private int lineWidth = 2;
    private float change = 0;
	
    public int arrowColor = Color.rgb(0x33, 0xb5, 0xe5);
	public int foregroundColor = Color.rgb(30, 30, 30);
	public int borderColor = Color.rgb(150, 150, 150);
    
	public ChangeView(Context context) {
		super(context);
		initialize();
	}

	public ChangeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public ChangeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}
	
	protected void initialize() {
		screen = new Rect();
		paint = new Paint();
		roundRect = new RectF();
	}
	
	public void update(ReadingsData data) {
		this.change = data.getChange();
		
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		
		getDrawingRect(screen);
		
		// define border limits
		screen.set(screen.left + lineWidth / 2, screen.top + lineWidth / 2, 
				screen.right - lineWidth / 2 - 1, screen.bottom - lineWidth / 2 - 1);
		roundRect.set(screen);
		
		// fill inside the round border
		paint.setStyle(Style.FILL);
		paint.setColor(foregroundColor);
		paint.setStrokeWidth(lineWidth);
		canvas.drawRoundRect(roundRect, cornerXRadius, cornerYRadius, paint);
		
		// draw round border
		paint.setStyle(Style.STROKE);
		paint.setColor(borderColor);
		canvas.drawRoundRect(roundRect, cornerXRadius, cornerYRadius, paint);
		
		//int radius = (8 * Math.min(screen.width(), screen.height()) / 9) / 2;
		int radius = screen.height();
		
        paint.setStrokeWidth(6);
        paint.setStrokeCap(Cap.ROUND);
        paint.setStrokeJoin(Join.ROUND);
        paint.setColor(arrowColor);
        
		if (change > 0) {
	    	// low - up
	    	if (change > 0.0005) {
	    		paint.setColor(arrowColor);
	    	} else {
	    		paint.setColor(Color.BLACK);
	    	}
	        canvas.drawLine(screen.exactCenterX(), 7*radius/8, 
	    		  screen.exactCenterX()-radius/6, 5*radius/8, paint);
	        canvas.drawLine(screen.exactCenterX(), 7*radius/8, 
	    		  screen.exactCenterX()+radius/6, 5*radius/8, paint);

	        // medium - up
	    	if (change > 0.03) {
	    		paint.setColor(arrowColor);
	    	} else {
	    		paint.setColor(Color.BLACK);
	    	}
	        canvas.drawLine(screen.exactCenterX(), 5*radius/8, 
	        		screen.exactCenterX()-radius/6, 3*radius/8, paint);
	        canvas.drawLine(screen.exactCenterX(), 5*radius/8, 
	        		screen.exactCenterX()+radius/6, 3*radius/8, paint);

	        // high - up
	    	if (change > 0.09) {
	    		paint.setColor(arrowColor);
	    	} else {
	    		paint.setColor(Color.BLACK);
	    	}
	    	canvas.drawLine(screen.exactCenterX(), 3*radius/8, 
	        		screen.exactCenterX()-radius/6, 1*radius/8, paint);
	        canvas.drawLine(screen.exactCenterX(), 3*radius/8, 
	        		screen.exactCenterX()+radius/6, 1*radius/8, paint);

		} else {
	    	// low - down
	    	if (change < -0.0005) {
	    		paint.setColor(arrowColor);
	    	} else {
	    		paint.setColor(Color.BLACK);
	    	}
	        canvas.drawLine(screen.exactCenterX(), 5*radius/8, 
	    		  screen.exactCenterX()-radius/6, 7*radius/8, paint);
	        canvas.drawLine(screen.exactCenterX(), 5*radius/8, 
	    		  screen.exactCenterX()+radius/6, 7*radius/8, paint);

	        // medium - down
	    	if (change < -0.03) {
	    		paint.setColor(arrowColor);
	    	} else {
	    		paint.setColor(Color.BLACK);
	    	}
	        canvas.drawLine(screen.exactCenterX(), 3*radius/8, 
	        		screen.exactCenterX()-radius/6, 5*radius/8, paint);
	        canvas.drawLine(screen.exactCenterX(), 3*radius/8, 
	        		screen.exactCenterX()+radius/6, 5*radius/8, paint);

	        // high - down
	    	if (change < -0.09) {
	    		paint.setColor(arrowColor);
	    	} else {
	    		paint.setColor(Color.BLACK);
	    	}
	    	canvas.drawLine(screen.exactCenterX(), 1*radius/8, 
	        		screen.exactCenterX()-radius/6, 3*radius/8, paint);
	        canvas.drawLine(screen.exactCenterX(), 1*radius/8, 
	        		screen.exactCenterX()+radius/6, 3*radius/8, paint);

		}
		
    //}
		super.onDraw(canvas);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	
		int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
	    int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
	    
	    this.setMeasuredDimension(parentWidth, parentHeight);
	}
}

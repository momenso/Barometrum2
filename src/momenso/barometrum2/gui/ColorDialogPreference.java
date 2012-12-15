package momenso.barometrum2.gui;

import android.app.AlertDialog.Builder;

import android.content.Context;
import android.content.SharedPreferences;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SweepGradient;

import android.preference.DialogPreference;
import android.preference.PreferenceManager;

import android.util.AttributeSet;

import android.view.MotionEvent;
import android.view.View;

public class ColorDialogPreference extends DialogPreference {
	private int color = 0;
	private OnColorChangedListener dialogColorChangedListener = null;

	public interface OnColorChangedListener {
		void colorChanged(int color);
	}

	private class ColorPickerView extends View {
		private Rect screen = new Rect();
		private Paint paint = null;
		private Paint centerPaint = null;
		private boolean trackingCenter = false;
		private final int[] colors = new int[] {
			0xff000000, 0xff0000ff, 0xff00ff00, 0xff00ffff, 0xffff0000,
			0xffff00ff, 0xffffff00, 0xffffffff, 0xff000000 				
		};
//			0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFFFFFFFF, 0xFF000000,
//			0xFF00FFFF, 0xFF00FF00,	0xFFFFFF00, 0xFFFF0000 };

		ColorPickerView(Context context, OnColorChangedListener listener,
				int color) {
			super(context);

			paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setShader(new SweepGradient(0, 0, colors, null));
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(80); // 32

			centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			centerPaint.setColor(color);
			centerPaint.setStrokeWidth(5);
		}
		/**
		 * {@inheritDoc}
		 */
		protected void onDraw(Canvas canvas) {
			getDrawingRect(screen);

			canvas.translate(screen.exactCenterX(), screen.exactCenterY());
			float radius = ((8 * Math.min(screen.width(), screen.height()) / 9) / 2) - paint.getStrokeWidth();

			canvas.drawCircle(0, 0, radius, paint);
			canvas.drawCircle(0, 0, radius-paint.getStrokeWidth()/2, centerPaint);

			if (trackingCenter) {
				int c = centerPaint.getColor();
				centerPaint.setStyle(Paint.Style.STROKE);
				centerPaint.setAlpha(0x80);

				canvas.drawCircle(0, 0, radius - centerPaint.getStrokeWidth(), centerPaint);

				centerPaint.setStyle(Paint.Style.FILL);
				centerPaint.setColor(c);
			}
		}

		private int ave(int s, int d, float p) {
			return s + Math.round(p * (d - s));
		}

		private int interpColor(int colors[], float unit) {
			if (unit <= 0) {
				return colors[0];
			}

			if (unit >= 1) {
				return colors[colors.length - 1];
			}

			float p = unit * (colors.length - 1);
			int i = (int) p;
			p -= i;

			// now p is just the fractional part [0...1) and i is the index
			int c0 = colors[i];
			int c1 = colors[i + 1];
			int a = ave(Color.alpha(c0), Color.alpha(c1), p);
			int r = ave(Color.red(c0), Color.red(c1), p);
			int g = ave(Color.green(c0), Color.green(c1), p);
			int b = ave(Color.blue(c0), Color.blue(c1), p);

			return Color.argb(a, r, g, b);
		}

		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX() - screen.exactCenterX();
			float y = event.getY() - screen.exactCenterY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
				float angle = (float) java.lang.Math.atan2(y, x);
				// need to turn angle [-PI ... PI] into unit [0....1]
				float unit = (float)(angle / (2 * Math.PI));

				if (unit < 0) {
					unit += 1;
				}

				centerPaint.setColor(interpColor(colors, unit));
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				ColorDialogPreference.this.color = centerPaint.getColor();
				break;
			}

			return true;
		}
	}

	public ColorDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		String namespace = "http://schemas.android.com/apk/res/android";
		String colorId = attrs.getAttributeValue(namespace, "key");
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		color = prefs.getInt(colorId, Color.BLACK);
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void onDialogClosed(boolean positiveResult) {
		// Persist the color after the ok button is clicked.
		if (positiveResult && color != 0) {
			persistInt(color);
		}

		super.onDialogClosed(positiveResult);
	}

	protected void onPrepareDialogBuilder(Builder builder) {
		dialogColorChangedListener = new OnColorChangedListener() {
			public void colorChanged(int c) {
				ColorDialogPreference.this.color = c;
			}
		};

		//int current_color = getSharedPreferences().getInt(name, color);

		builder.setView(new ColorPickerView(getContext(),
			dialogColorChangedListener, color));

		super.onPrepareDialogBuilder(builder);
	}
}
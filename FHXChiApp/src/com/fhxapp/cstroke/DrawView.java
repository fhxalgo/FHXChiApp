package com.fhxapp.cstroke;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

public class DrawView extends View {
	private static final String TAG = "DrawView";
	
	private boolean drawing;
	private boolean stopThread = false;
	private int scanInterval = 5;
	private int strokeInterval = 200;
	private Paint mPaint;
	private List<CStroke> cstrokes;
	public DrawView(Context context) {
		super(context);
		
		// create a paint for global use
		mPaint = new Paint();
		//mPaint.setDither(true);
		mPaint.setColor(Color.RED);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(20);
		
		cstrokes = new ArrayList<CStroke>();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int x = 0;
		int y = 0;
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		
		paint.setColor(Color.WHITE);
		canvas.drawPaint(paint);
		
		paint.setColor(Color.BLUE);
		canvas.drawCircle(20, 20, 15, paint);
		
		int i = 0;
		for (CStroke currstroke : cstrokes) {
			//paint.setColor(Color.BLACK);
			paint.setStrokeWidth(5);
			//canvas.drawPath(currstroke.polygon, paint);
			canvas.drawPoints((float[])currstroke.polygon.getPolygonPointsFloat(), paint);			
			Log.i(TAG, "drawing stroke #: " + ++i);
		}
		
		drawStrokes(canvas);
//		for (LineData ld: ln) {
//			paint.setColor(Color.RED);
//			paint.setStrokeWidth(5);
//			//canvas.drawLine(ld.x1, ld.y1, ld.x2, ld.y2, paint);
//			canvas.drawCircle(ld.x1, ld.y1, 5, paint);
//		}
//		Log.i(TAG, "drawing LineData: " + ln.size());
	}
	
	public void setStrokeData(String strokes) {
		cstrokes.clear();

		int i, start;
		StringBuffer tmpstroke = new StringBuffer();
		for (i = 0; i < strokes.length() && strokes.charAt(i) == ' '; i++) {	}
		start = i;
		for ( ; i < strokes.length(); i++) {
			if (strokes.charAt(i) == '#' && i != start) {
				cstrokes.add(new CStroke(tmpstroke.toString()));
				tmpstroke.setLength(0);
			}
			tmpstroke.append(strokes.charAt(i));
		}
		cstrokes.add(new CStroke(tmpstroke.toString()));
		
		Log.i(TAG, "# of cstrokes added: "+cstrokes.size());
		
		//drawStrokesOnCanvas(0);
		
		invalidate();  // update view 
	}
	
	private void drawStrokes(Canvas canvas) {
		Log.i(TAG, "drawStrokes: "+cstrokes.size());
		
		//drawing = true;
		for (int i = 0; i < cstrokes.size(); i++) {
			drawCurrentStroke(canvas, i);
		}
		//drawing = false;		
	}
	
	private void drawStrokesOnCanvas(int id) {
		Log.i(TAG, "drawStrokesOnCanvas: "+cstrokes.size());
		ln.clear();

		Bitmap b = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		
		for (int i = 0; i < cstrokes.size(); i++) {
			drawCurrentStroke(c, i);
		}
		
		Log.i(TAG, "drawStrokesOnCanvas: ln.size= "+ln.size());
	}

	private void drawCurrentStroke(Canvas gc, int i) {
		
		CStroke currstroke = (CStroke) cstrokes.get(i);
		Rect bounds = currstroke.polygon.getBounds();
		
		//setCurrentStrokeColor(currstroke, gc);
		Log.i(TAG, "drawCurrentStroke: "+i + ", direction=" + currstroke.direction + ", draw="+currstroke.draw);
		
		if (currstroke.direction == 1) 
		{ 
			leftToRightStroke(currstroke, bounds, gc);
		} 
		else if (currstroke.direction == 2) 
		{ 
			// \ (down)
			downRightStroke(currstroke, bounds, gc);
		} 
		else if (currstroke.direction == 3) 
		{ 	
			// | (down)
			downStroke(currstroke, bounds, gc);
		} 
		else if (currstroke.direction == 4) 
		{ 
			// 4 / (down)
			downLeftStroke(currstroke, bounds, gc);
		} 
		else if (currstroke.direction == 5) 
		{ 
			// 5 -- (right to left)
			rightToLeftStroke(currstroke, bounds, gc);

		} else if (currstroke.direction == 6) 
		{ 
			// 6 \ (up)
			upLeftStroke(currstroke, bounds, gc);
		}
		else if (currstroke.direction == 7) 
		{ 
			// 7 | (up)
			upStroke(currstroke, bounds, gc);
		}
		else if (currstroke.direction == 8) { 
			// 8 / (up)
			upRightStroke(currstroke, bounds, gc);
		}
		
		if (currstroke.pause) {
			pause(strokeInterval);
		}

		currstroke.draw = true;
	}

	/**
	 * @param currstroke
	 * @param bounding
	 * @param gc
	 */
	private void leftToRightStroke(CStroke currstroke, Rect bounding, Canvas gc) {
		int bounding_x = bounding.left;
		int bounding_y = bounding.top;
		
		Log.i(TAG, " leftToRightStroke(): " + bounding);
		Log.i("TAG", String.format("left=%d,tom=%d,right=%d,bottom=%d,width=%d,height=%d", bounding.left, bounding.top, bounding.right, bounding.bottom, bounding.width(), bounding.height()));

		for (int x = bounding_x; x < bounding_x + bounding.width(); x++) 
		{
			for (int y = bounding_y; y < bounding_y + bounding.height(); y++) 
			{
				if (currstroke.polygon.contains(x, y)) 
				{
					drawLineInUIThread(gc, x, y, x, y);
				}
			}
			pause(scanInterval);
		}
	}
	
	/**
	 * @param currstroke
	 * @param bounding
	 * @param gc
	 */
	private void upRightStroke(CStroke currstroke, Rect bounding, Canvas gc) {
		int bounding_height = bounding.height();
		int bounding_width = bounding.width();
		int bounding_x = bounding.left;
		int bounding_y = bounding.top;
		
		double slope = (double) bounding_height / (double) bounding_width;
		for (int j = bounding_height; j > -bounding_height; j--) 
		{
			for (int k = bounding_width; (k * slope) + j > 0; k--) 
			{
				int l = (int) ((k * slope) + j);
				if (currstroke.polygon.contains(k + bounding_x, l+ bounding_y)) 
				{
					drawLineInUIThread(gc, k + bounding_x, l + bounding_y, k
							+ bounding_x, l + bounding_y);
				}
			}
			pause(scanInterval);
		}
	}

	/**
	 * @param currstroke
	 * @param bounds
	 * @param gc
	 */
	private void upStroke(CStroke currstroke, Rect bounds, Canvas gc) {
//		System.out.println("up stroke: " + bounds.x + ", " + bounds.y+
//				" width: " + bounds.width + " height: " + bounds.height);
		
		int bounds_x = bounds.left;
		int bounds_y = bounds.top;
		
		for (int y = bounds_y + bounds.height(); y > bounds_y; y--) 
		{
			for (int x = bounds_x; x < bounds_x + bounds.width(); x++) 
			{
				if (currstroke.polygon.contains(x, y)) 
				{
//					System.out.println("drawing up: (" + x + ", " + y + ")");
					drawLineInUIThread(gc, x, y, x, y);
				}
			}
			pause(scanInterval);

		}
	}

	/**
	 * @param currstroke
	 * @param bounds
	 * @param gc
	 */
	private void upLeftStroke(CStroke currstroke, Rect bounds, Canvas gc) {
		double slope = -((double) bounds.height() / (double) bounds.width());
		
		int bounds_x = bounds.left;
		int bounds_y = bounds.right;
		
		for (int y = (int)bounds.height() * 2; y > 0; y--) 
		{
			for (int x = 0; (x * slope) + y > 0; x++) 
			{
				int l = (int) ((x * slope) + y);
				if (currstroke.polygon.contains(x + bounds_x, l+ bounds_y)) 
				{
					drawLineInUIThread(gc, x + bounds_x, l + bounds_y, x
							+ bounds_x, l + bounds_y);
				}
			}
			pause(scanInterval);
		}
	}

	/**
	 * @param currstroke
	 * @param bounds
	 * @param gc
	 */
	private void rightToLeftStroke(CStroke currstroke, Rect bounds, Canvas gc) {
		int bounds_x = bounds.left;
		int bounds_y = bounds.top;

		for (int x = bounds_x + (int)bounds.width(); x > bounds_x; x--) 
		{
			for (int y = bounds_y; y < bounds_y + bounds.height(); y++) 
			{
				if (currstroke.polygon.contains(x, y)) 
				{
					drawLineInUIThread(gc, x, y, x, y);
				}
			}
			pause(scanInterval);
		}
	}

	/**
	 * @param currstroke
	 * @param bounds
	 * @param gc
	 */
	private void downLeftStroke(CStroke currstroke, Rect bounds, Canvas gc) {
		double slope = (double) bounds.height() / (double) bounds.width();
		
		int bounds_height = bounds.height();
		int bounds_width = bounds.width();
		int bounds_x = bounds.left;
		int bounds_y = bounds.top;
		
		for (int y = -bounds_height; y < bounds_height; y++) 
		{
			for (int x = bounds_width; (x * slope) + y > 0; x--) 
			{
				int l = (int) ((x * slope) + y);
				if (currstroke.polygon.contains(x + bounds_x, l+ bounds_y)) 
				{
					drawLineInUIThread(gc, x + bounds_x, l + bounds_y, x
							+ bounds_x, l + bounds_y);
				}
			}
			pause(scanInterval);
		}
	}

	/**
	 * @param currstroke
	 * @param bounding
	 * @param gc
	 */
	private void downStroke(CStroke currstroke, Rect bounding, Canvas gc) {
		int bounding_x = (int) bounding.left;
		int bounding_y = (int) bounding.top;

		for (int y = bounding_y; y < bounding_y + bounding.height(); y++) 
		{
			for (int x = bounding_x; x < bounding_x + bounding.width(); x++) 
			{
				if (currstroke.polygon.contains(x, y)) {
					drawLineInUIThread(gc, x, y, x, y);
				}
			}
			pause(scanInterval);
		}
	}

	/**
	 * @param currstroke
	 * @param bounds
	 * @param gc
	 */
	private void downRightStroke(CStroke currstroke, Rect bounds, Canvas gc) {
		int bounds_x = bounds.left;
		int bounds_y = bounds.top;
		
		Log.i(TAG, " downRightStroke(): " + bounds);
		Log.i("TAG", String.format("left=%d,top=%d,right=%d,bottom=%d,width=%d,height=%d", bounds.left, bounds.top, bounds.right, bounds.bottom, bounds.width(), bounds.height()));
		
		double slope = -((double) bounds.height() / (double) bounds.width());
		for (int y = 0; y < bounds.height() * 2; y++) 
		{
			for (int x = 0; (x * slope) + y > 0; x++) 
			{
				int l = (int) ((x * slope) + y);
				if (currstroke.polygon.contains(x + bounds_x, l+ bounds_y)) 
				{
					drawLineInUIThread(gc, x + bounds_x, l + bounds_y, x
							+ bounds_x, l + bounds_y);
				}
			}
			pause(scanInterval);
		}
	}


	private void pause(int i)
    {
        try
        {
            Thread.sleep(i);
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

	private void drawLineInUIThread(final Canvas gc, final int x1, 
			final int y1, final int x2, final int y2) 
	{
			new Thread(new Runnable() {
				public void run()
				{
					//Log.i(TAG, String.format("drawLineInUIThread: (%d, %d) -> (%d, %d)", x1, y1,x2,y2));
					gc.drawCircle(x1, y1, 5, mPaint);
					// drawPath
					// add all the points to cache instead
					//ln.add(new LineData(x1,y1,x2,y2));
					postInvalidate();
				}
			}).start();
		
	}
	
	private List<LineData> ln = new ArrayList<LineData>();
	
	class LineData {
		public int x1, y1, x2, y2;
		public LineData(int x1, int y1, int x2, int y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}
	}
}

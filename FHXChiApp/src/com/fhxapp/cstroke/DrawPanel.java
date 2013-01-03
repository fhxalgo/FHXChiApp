package com.fhxapp.cstroke;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DrawPanel extends SurfaceView implements Runnable {
	private Paint mPaint;
	private List<Path> pointsToDraw;
	Thread t = null;
	SurfaceHolder holder;
	boolean isItOk = false;

	public DrawPanel(Context context, List<Path> drawPoints, Paint paint) {
		super(context);
		pointsToDraw = drawPoints;
		mPaint = paint;

		holder = getHolder();
	}

	public void run() {
		// TODO Auto-generated method stub
		while (isItOk == true) {

			if (!holder.getSurface().isValid()) {
				continue;
			}

			Canvas c = holder.lockCanvas();
			c.drawARGB(255, 0, 0, 0);
			onDraw(c);
			holder.unlockCanvasAndPost(c);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		synchronized (pointsToDraw) {
			for (Path path : pointsToDraw) {
				canvas.drawPath(path, mPaint);
				Log.i("onDraw: ", "path:"+path);
			}
		}
	}

	public void pause() {
		isItOk = false;
		while (true) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		}
		t = null;
	}

	public void resume() {
		isItOk = true;
		t = new Thread(this);
		t.start();

	}
	
	public void addPoints(int x, int y) {
		Path path = new Path();
		path.moveTo(x, y);

		synchronized(this.pointsToDraw) {
			pointsToDraw.add(path);
		}
		
	}
}
package com.fhxapp.cstroke;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class CanvasDrawActivity extends Activity implements OnTouchListener {

	DrawPanel dp;
	private ArrayList<Path> pointsToDraw = new ArrayList<Path>();
	private Paint mPaint;
	Path path;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		dp = new DrawPanel(this);
		dp.setOnTouchListener(this);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mPaint = new Paint();
		mPaint.setDither(true);
		mPaint.setColor(Color.WHITE);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(14);
		FrameLayout fl = new FrameLayout(this);
		fl.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		fl.addView(dp);
		setContentView(fl);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		dp.pause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		dp.resume();
	}

	public boolean onTouch(View v, MotionEvent me) {
		// TODO Auto-generated method stub
		synchronized (pointsToDraw) {
			if (me.getAction() == MotionEvent.ACTION_DOWN) {
				path = new Path();
				path.moveTo(me.getX(), me.getY());
				// path.lineTo(me.getX(), me.getY());
				pointsToDraw.add(path);
			} else if (me.getAction() == MotionEvent.ACTION_MOVE) {
				path.lineTo(me.getX(), me.getY());
			} else if (me.getAction() == MotionEvent.ACTION_UP) {
				// path.lineTo(me.getX(), me.getY());
			}
		}
		return true;
	}

	public class DrawPanel extends SurfaceView implements Runnable {

		Thread t = null;
		SurfaceHolder holder;
		boolean isItOk = false;

		public DrawPanel(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
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
	}
}

package com.fhxapp.cstroke;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.SampleCanvas.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class FHXMainActivity extends Activity implements OnTouchListener {
	private static final String TAG = "CanvasActivity";

	DrawPanel dp;
	private List<Path> pointsToDraw = new ArrayList<Path>();
	private Paint mPaint;
	Path path;
	// DrawView dv;
	CStrokeView dv;

	private Map<String, String> strokeDataMap = new HashMap<String, String>();
	private Spinner spinner1, spinner2;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		// read in stroke data file
		loadStrokeDataFile();

		spinner1 = (Spinner) findViewById(R.id.spinner1);
		List<String> list = new ArrayList<String>();
		list.addAll(strokeDataMap.keySet());
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner1.setAdapter(dataAdapter);
		addListenerOnSpinnerItemSelection();

		// polulate words
		Button bt = (Button) findViewById(R.id.button1);
		CharSequence text = Arrays.toString(strokeDataMap.keySet().toArray());
		bt.setText("Demo");

		// set up paint
		mPaint = new Paint();
		mPaint.setDither(true);
		mPaint.setColor(Color.WHITE);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(20);

		// init some points
		// path = new Path();
		// path.moveTo(100, 100);
		// pointsToDraw.add(path);
		// path.moveTo(100, 200);
		// pointsToDraw.add(path);
		// path.moveTo(0, 200);
		// // path.lineTo(me.getX(), me.getY());
		// pointsToDraw.add(path);

		dp = new DrawPanel(this, pointsToDraw, mPaint);
		dp.setOnTouchListener(this);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// FrameLayout fl = new FrameLayout(this);
		// fl.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
		// LayoutParams.FILL_PARENT));

		// FrameLayout fl = (FrameLayout) findViewById(R.id.view_panel);
		// dv = new DrawView(this);
		// fl.addView(dv);

		LinearLayout ll = (LinearLayout) findViewById(R.id.linearView1);
		dv = new CStrokeView(this);
		ll.addView(dv);
		// setContentView(dv);

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

	int x, y;

	public void runMe(View v) {
		x += 10;
		y += 20;
		dp.addPoints(x, y);
		// dp.resume();

		// two
		String strokes = "#2PR:118,74;131,81;141,88;151,94;157,101;160,109;160,119;158,125;153,130;149,131;144,129;142,127;140,119;139,111;138,106;135,100;130,92;123,86;116,78	#1PR:31,151;217,151;231,136;252,157;48,157;38,159;";
		// shi1, teacher
		// strokes =
		// "#4PO: 84,19;83,28;81,36;78,45;74,53;          70,60;64,67;71,63;79,58;87,53;          95,46;104,39;111,32;112,31;104,25;          95,21;90,20;      #3PO: 33,76;42,77;48,79;55,81;55,121;          55,129;55,155;55,164;57,191;57,202;          56,210;55,216;55,222;50,231;45,229;          45,221;45,217;45,211;44,202;44,192;          44,183;44,173;43,164;43,154;41,141;          40,128;39,114;37,102;35,89;      #8NO: 64,82;66,86;69,88;78,85;88,82;          96,79;108,68;100,64;72,78;      #3PO: 96,79;108,68;116,75;124,81;123,81;          115,88;109,95;105,102;103,109;91,112;          92,107;94,102;95,95;95,87;      #1PO: 55,121;61,120;67,119;75,117;83,115;          91,112;103,109;104,111;105,115;96,119;          90,120;80,123;71,126;62,128;55,129;      #8NO: 55,155;64,153;74,150;83,146;92,142;          100,136;104,138;95,153;87,155;82,156;          72,159;63,162;55,164;      #3PO: 104,138;95,153;94,161;94,167;94,173;          94,180;95,179;103,177;104,168;110,158;          117,152;111,145;      #8PO: 57,191;63,190;69,189;77,187;86,183;          95,179;103,177;106,179;108,184;99,187;          92,189;83,193;74,196;65,199;57,202;      #1PO: 137,80;143,76;152,75;160,74;172,72;          185,70;197,67;209,64;221,61;234,57;          238,57;244,58;249,59;254,65;251,69;          242,71;235,72;229,72;219,74;210,75;          201,76;192,78;183,79;170,81;146,84;          142,83;      #3PR: 129,127;143,125;146,133;146,176;145,179;          141,182;136,177;135,169;135,162;133,154;          132,145;130,136;      #1NR: 143,125;146,133;176,126;189,126;226,117;          238,105;232,103;189,117;176,119;167,121;          158,123;150,124;      #3NR: 226,117;228,124;230,130;230,136;231,142;          230,152;228,161;226,169;242,191;247,183;          248,176;249,166;249,157;249,149;249,141;          250,133;252,127;256,121;257,121;250,113;          242,107;238,105;232,103;      #6PR: 242,191;236,196;227,187;221,180;215,173;          208,164;226,169;      #3PR: 170,81;183,79;185,81;189,84;189,117;          189,126;189,226;189,230;188,240;188,247;          186,255;184,263;181,272;178,270;176,262;          176,126;176,119;176,112;175,105;174,97;          172,89;";
		// you3, friend
		strokes = "     #1PO: 61,82;69,79;78,79;86,78;95,77;          104,76;113,74;122,72;139,69;148,68;          156,66;163,65;173,63;183,61;192,59;          200,57;208,55;214,58;218,60;224,66;          216,71;207,72;201,73;187,76;175,78;          163,79;153,81;143,82;134,83;119,84;          111,87;105,88;96,90;88,91;81,91;          74,91;70,89;      #4PO: 130,12;126,16;127,45;122,72;119,84;          113,99;106,113;99,127;91,141;82,154;          73,167;64,178;55,188;46,198;36,207;          26,216;15,226;8,232;5,233;14,229;          24,224;33,218;42,212;51,205;61,197;          69,189;78,180;86,171;95,160;101,152;          107,143;110,136;134,83;139,69;141,60;          144,50;148,40;150,35;153,28;151,23;          145,18;136,14;      #1NR: 110,136;107,143;112,144;117,144;125,144;          135,141;147,138;157,136;163,135;172,136;          187,116;183,114;174,119;165,122;156,126;          146,129;136,132;125,134;118,135;      #4PR: 172,136;160,191;155,200;147,210;138,218;          128,226;118,233;109,238;101,243;92,246;          83,250;72,253;67,255;75,254;84,253;          93,252;102,249;110,246;119,243;127,239;          135,233;143,227;151,220;156,214;162,208;          169,198;191,147;203,133;187,116;      #2PR: 105,157;114,157;119,159;124,160;130,165;          136,169;145,177;153,185;160,191;169,198;          176,203;184,208;192,214;200,218;208,223;          217,227;226,230;236,235;246,238;257,241;          267,244;278,246;289,248;289,249;280,249;          273,251;266,252;256,253;247,254;238,255;          229,256;220,256;212,257;203,249;193,239;          182,228;173,219;166,213;162,208;155,200;          146,192;139,185;129,178;121,170;112,164;";

		dv.setStrokeData(strokes);
	}

	public void loadStrokeDataFile() {
		try {
			AssetManager am = this.getAssets();
			InputStream is = am.open("zdtStrokeData.txt");
			InputStreamReader inputStreamReader = new InputStreamReader(is,
					"UTF-8");
			BufferedReader reader = new BufferedReader(inputStreamReader);

			String line;
			while ((line = reader.readLine()) != null) {
				Pattern pat = Pattern.compile("([^\\s])\\t(.+)"); //$NON-NLS-1$
				Matcher m = pat.matcher(line);
				if (m.find()) {
					String character = m.group(1);
					String strokeData = m.group(2);
					strokeDataMap.put(character, strokeData);
				}
			}

			Log.i(TAG, "loaded stroke characters: "
					+ strokeDataMap.keySet().size());
			Log.i(TAG,
					"loaded chinese characters: "
							+ Arrays.toString(strokeDataMap.keySet().toArray()));
		} catch (Exception io) {

		}
	}

	public List<String> getWords() {

		List<String> contents = new ArrayList<String>();
		try {
			InputStream inputStream = getResources().openRawResource(
					R.id.button1);
			BufferedReader input = new BufferedReader(new InputStreamReader(
					inputStream, Charset.forName("UTF-8")));
			try {
				String line = null;
				while ((line = input.readLine()) != null) {
					contents.add(line);
				}
			} finally {
				input.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return contents;

	}

	public void onClickWordList(View v) {

	}

	public void addListenerOnSpinnerItemSelection() {
		spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
	}

	/*
	 * // add items into spinner dynamically public void addItemsOnSpinner2() {
	 * 
	 * spinner2 = (Spinner) findViewById(R.id.spinner2); List<String> list = new
	 * ArrayList<String>(); list.add("list 1"); list.add("list 2");
	 * list.add("list 3"); ArrayAdapter<String> dataAdapter = new
	 * ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
	 * dataAdapter
	 * .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	 * spinner2.setAdapter(dataAdapter); }
	 * 
	 * public void addListenerOnSpinnerItemSelection() { spinner1 = (Spinner)
	 * findViewById(R.id.spinner1); spinner1.setOnItemSelectedListener(new
	 * CustomOnItemSelectedListener()); }
	 */
	public class CustomOnItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			Toast.makeText(
					parent.getContext(),
					"OnItemSelectedListener : "
							+ parent.getItemAtPosition(pos).toString(),
					Toast.LENGTH_SHORT).show();

			if (pos > 0) {
				String strokes = strokeDataMap.get(parent.getItemAtPosition(pos).toString());
				dv.setStrokeData(strokes);
			}
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}

	}
}

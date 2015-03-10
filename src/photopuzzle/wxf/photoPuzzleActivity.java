package photopuzzle.wxf;

import java.io.FileNotFoundException;
import java.util.Vector;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class photoPuzzleActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	private Button startBtn, settingBtn, scoreBtn, aboutBtn, exitBtn;
	public static photoPuzzleActivity instance;

	private SharedPreferences sharedData;
	private Editor editor;

	public static final int PROGRAM = 1, CAMERAL = 2, FILESYSTEM = 3;// 
	public static int puzzleType, typeNow;
	public int numbersInScoreView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 全屏
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

		instance = this;

		sharedData = this.getSharedPreferences("PHOTOPUZZLE",
				Context.MODE_PRIVATE);
		editor = sharedData.edit();
		
		typeNow = PROGRAM;
		puzzleType = CAMERAL;
		numbersInScoreView = 0;
		int tmp;
		tmp = sharedData.getInt("type&num", -1);
		;
		if (tmp != -1) {
			puzzleType = tmp & 0xf;
			numbersInScoreView = (tmp >> 4) & 0xf;
		}
		int i, timeused;
		String filename;
		vec = new Vector<ListContent>();
		for (i = 0; i < numbersInScoreView; i++) {
			// 将数据取出
			filename = sharedData.getString(fn_str[i], "");
			timeused = sharedData.getInt(time_int[i], Integer.MAX_VALUE);
			// 如果数据没有错误，加入到vec中
			if ((filename != "") && (timeused != Integer.MAX_VALUE)) {
				ListContent content = new ListContent(filename, timeused);
				vec.add(content);
			}
		}
		// 重设numbersInScoreView的大小
		numbersInScoreView = vec.size();

		showMenuView();

		// setScore("showMenuView/aaddvv.png", 4);
		// setScore("showMenuView/bbgghh.png", 5);
		// setScore("showMenuView/ddssrr.png", 7);
		// setScore("showMenuView/cczzcc.png", 1);
		// setScore("showMenuView/xxhhll.png", 3);
		// setScore("showMenuView/qqwwee.png", 8);
		// parsePath("showMenuView/qqwwee.png");
	}
	
	// 界面宏
	public static final int MENUVIEW = 1, SETTINGVIEW = 3, SCOREVIEW = 4,
			ABOUTVIEW = 5, GAMEVIEW = 2, CLIPBMPVIEW = 6;
	public int currentView;

	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v instanceof Button) {
			int id = ((Button) v).getId();
			switch (id) {
			case R.id.startbtn:
				showGameView(true);// showClipBmpView();
				break;
			case R.id.setbtn:
				showSettingView();
				break;
			case R.id.scorebtn:
				showScoreView();
				break;
			case R.id.aboutbtn:
				showAboutView();
				break;
			case R.id.exitbtn:
				onDestroy();
				finish();
				System.exit(0);
				break;
			}
		}
	}

	public void showMenuView() {
		currentView = MENUVIEW;
		setContentView(R.layout.menu);
		startBtn = (Button) findViewById(R.id.startbtn);
		settingBtn = (Button) findViewById(R.id.setbtn);
		scoreBtn = (Button) findViewById(R.id.scorebtn);
		aboutBtn = (Button) findViewById(R.id.aboutbtn);
		exitBtn = (Button) findViewById(R.id.exitbtn);
		startBtn.setOnClickListener(this);
		settingBtn.setOnClickListener(this);
		scoreBtn.setOnClickListener(this);
		aboutBtn.setOnClickListener(this);
		exitBtn.setOnClickListener(this);
	}

	private GameView gameView;
	public static Rect boardPos;

	public void showGameView(boolean reset) {
		currentView = GAMEVIEW;
		if (gameView == null) {
			gameView = new GameView(this);
		}
		if(reset){
			gameView.resetParam();
		}
		setContentView(gameView);
	}

	public void showSettingView() {
		currentView = SETTINGVIEW;
		setContentView(R.layout.setting);
//		final RadioButton program_rb = (RadioButton) findViewById(R.id.program_rb);
		final RadioButton filesystem_rb = (RadioButton) findViewById(R.id.filesystem_rb);
		final RadioButton cameral_rb = (RadioButton) findViewById(R.id.cameral_rb);
		Button ok_btn = (Button) findViewById(R.id.ok_btn);
		switch (puzzleType) {
//		case PROGRAM:
//			program_rb.setChecked(true);
//			break;
		case CAMERAL:
			cameral_rb.setChecked(true);
			break;
		case FILESYSTEM:
			filesystem_rb.setChecked(true);
			break;
		}

		ok_btn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				if (program_rb.isChecked()) {
//					puzzleType = PROGRAM;
//				} else 
				if (filesystem_rb.isChecked()) {
					puzzleType = FILESYSTEM;
				} else if (cameral_rb.isChecked()) {
					puzzleType = CAMERAL;
				}
				showMenuView();
			}
		});

	}

	private String[] fn_str = { "fn1", "fn2", "fn3", "fn4", "fn5", "fn6",
			"fn7", "fn8", "fn9", "fn10" };
	private String[] time_int = { "t1", "t2", "t3", "t4", "t5", "t6", "t7",
			"t8", "t9", "t10" };
	private Vector<ListContent> vec = null;

	public void setScore(String fn, int time) {
		ListContent newcontent = new ListContent(fn, time);
		int i, pos = -1, timeused;
		if (numbersInScoreView == 0) {// 如果没有存储数据，直接加入vec
			vec.add(newcontent);
		} else {// 已有历史数据
				// 将新的成绩加入到适当的位置
			for (i = 0; i < numbersInScoreView; i++) {
				timeused = vec.get(i).timeused;
				if (timeused > time) {
					pos = i;
					break;
				}
			}
			if (pos == -1) {
				vec.add(newcontent);
			} else {
				vec.add(pos, newcontent);
			}
		}
		numbersInScoreView++;
		if (numbersInScoreView > 10) {
			numbersInScoreView = 10;
		}
	}

	private TextView tv;
	private ListView lv;

	public void showScoreView() {
		currentView = SCOREVIEW;
		setContentView(R.layout.score);

		tv = (TextView) findViewById(R.id.textview);
		lv = (ListView) findViewById(R.id.list);
		lv.setOnItemClickListener(listener);
		lv.setCacheColorHint(0);
		if (vec.size() == 0) {
			tv.setText("您还没有成功过哦");
		} else {
			lv.setAdapter(new ContentAdapter(this, 0, vec));
		}
	}

	OnItemClickListener listener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long id) {
			// Toast.makeText(photoPuzzleActivity.instance, position + "",
			// Toast.LENGTH_SHORT).show();
		}
	};
	private TextView about_content1, about_content2;
	public void showAboutView() {
		currentView = ABOUTVIEW;
		setContentView(R.layout.about);
		about_content1 = (TextView) findViewById(R.id.about_content1);
		about_content2 = (TextView) findViewById(R.id.about_content2);
		about_content1.setText("    可以在设置界面选择图片来源：相机或文件系统。" +
				"一个图片要经过容易（3乘3）、中等（4乘4）、困难（5乘5）三种模式后才能" +
				"获得成功，最好成绩保存在成绩界面。拼图游戏不仅需要一定的技巧，还需要有" +
				"超强的记忆力，尽量选择纹理比较丰富的图片，现在就开始你的拼图之旅吧。");
		about_content2.setText("    在选择图片之前，程序有自带的图片，使用自带图片结果不会记入成绩。");
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (currentView != MENUVIEW) {
				if (currentView == CLIPBMPVIEW) {
					showGameView(false);
				} else {
					showMenuView();
				}
				return true;
			}

		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		int tmp;
		// 最后还是要同步vec中的数目
		numbersInScoreView = vec.size();
		if (numbersInScoreView > 10) {
			numbersInScoreView = 10;
		}

		tmp = (numbersInScoreView << 4) | puzzleType;
		editor.putInt("type&num", tmp);
		for (int i = 0; i < numbersInScoreView; i++) {
			editor.putString(fn_str[i], vec.get(i).filename);
			editor.putInt(time_int[i], vec.get(i).timeused);
		}
		editor.commit();
		super.onDestroy();
	}
	
	
	private Bitmap requestedBmp;
	public void requestImgFromFileSystem() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, 1);		
	}	
	public void requestImgFromCamera(){
	      Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
	      startActivityForResult(intent, 0);		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == RESULT_OK) {
			Uri uri = data.getData();
			ContentResolver cr = this.getContentResolver();
			requestedBmp = null;
			try {
				requestedBmp = BitmapFactory.decodeStream(cr
						.openInputStream(uri));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (requestedBmp != null) {
				showClipBmpView();
			} else {
				Toast.makeText(this, "您没有选择图片", Toast.LENGTH_LONG).show();
				showGameView(false);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	// 只有一个入口，就是GameView中的新图片按钮。放在Activity中是因为需要重要Activity的方法，View类中没有。
	public void showClipBmpView() {
		currentView = CLIPBMPVIEW;
		ClipBmpView clipBmpView = new ClipBmpView(this, requestedBmp);
		setContentView(clipBmpView);
	}
	public void fromClipView2GameView(Bitmap bmp) {
		gameView.setPuzzleBmp(bmp);
		showGameView(true);
	}
}

// Calendar calendar = Calendar.getInstance();
// String[] tz = TimeZone.getAvailableIDs();
// for(int i = 0; i < tz.length; i++){
// Log.v("timezone", tz[i]);
// }
// calendar.setTimeZone(TimeZone.)
// int year = calendar.get(Calendar.YEAR);
// int month = calendar.get(Calendar.MONTH);
// int day = calendar.get(Calendar.DAY_OF_MONTH);
// int hour = calendar.get(Calendar.HOUR_OF_DAY);
// int minutes = calendar.get(Calendar.MINUTE);
// int seconds = calendar.get(Calendar.SECOND);
// Log.v("year:" + year + "month:" + month + "day:" + day + "||",
// "hour:" + hour + "minutes:" + minutes + "seconds:" + seconds);
// Toast.makeText(this, "year:" + year + "month:" + month + "day:" + day + "||"
// +
// "hour:" + hour + "minutes:" + minutes + "seconds:" + seconds,
// Toast.LENGTH_LONG).show();

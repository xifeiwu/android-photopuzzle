package photopuzzle.wxf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;

public class GameView extends SurfaceView implements Callback, Runnable {

	private SurfaceHolder sfh;
	private Paint paint;
	private Canvas canvas;
	private photoPuzzleActivity photoPuzzle;
	private Bitmap puzzleBmp;

	private Bitmap newbmp, breakup, newbmp_pressed, breakup_pressed, tip, tip_pressed, gamebg;
	private int btnW, btnH;
	private Bitmap[] btnBmps;
	private int[][] btnPos;

	private final int ONPREVIEW = 1, ONPLAYING = 2, SHOWBMP = 3, GAMEOVER = 4;
	private int gameState, preGameState;

	public GameView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		sfh = this.getHolder();
		sfh.addCallback(this);
		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.STROKE);
		paint.setTextSize(20);

		photoPuzzle = (photoPuzzleActivity) context;

		Resources res = this.getResources();
		breakup = BitmapFactory.decodeResource(res, R.drawable.breakup);
		breakup_pressed = BitmapFactory.decodeResource(res,
				R.drawable.breakup_pressed);
		newbmp = BitmapFactory.decodeResource(res, R.drawable.newbmp);
		newbmp_pressed = BitmapFactory.decodeResource(res,
				R.drawable.newbmp_pressed);
		tip = BitmapFactory.decodeResource(res,
				R.drawable.tip);
		tip_pressed = BitmapFactory.decodeResource(res,
				R.drawable.tip_pressed);
		
		btnW = breakup.getWidth();
		btnH = breakup.getHeight();
		btnPos = new int[3][2];
		btnBmps = new Bitmap[btnPos.length * 2];
		btnBmps[0] = breakup;
		btnBmps[1] = newbmp;
		btnBmps[2] = tip;
		btnBmps[3] = breakup_pressed;
		btnBmps[4] = newbmp_pressed;
		btnBmps[5] = tip_pressed;

		gamebg = BitmapFactory.decodeResource(res, R.drawable.gamebg);
		
		resetParam();

	}

	public void resetParam(){	
		rows = 3;
		cols = 3;
		level = 0;
		hour = min = sec = 0;
		gameState = ONPREVIEW;
	}
	public void setPuzzleBmp(Bitmap bmp) {	
//		resetParam();
		puzzleBmp = bmp;
	}

	private final int NONE = -1, BREAKUP_BTN = 0, NEWBMP_BTN = 1, TIP_BTN = 2;
	private int selectedBtn = NONE, ti;
	private float eventX, eventY, preEventX, preEventY;
	private float distanceX, distanceY;
	private int blockX, blockY, dir;
	private boolean isMoveEnough;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		eventX = event.getX();
		eventY = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			dir = GameLogic.NONE;
			if ((eventX > boardX) && (eventX < (boardX + boardW))
					&& (eventY > boardY)
					&& (eventY < (boardY + boardH + blockH))
					&& (gameState == ONPLAYING)) {
				blockX = (int) ((eventX - boardX) / blockW);
				blockY = (int) ((eventY - boardY) / blockH);
				if (blockX == logic.hiddenX) {
					if (blockY == logic.hiddenY + 1) {
						// ���·�
						dir = GameLogic.UP;
					} else if (blockY == logic.hiddenY - 1) {
						// ���Ϸ�
						dir = GameLogic.DOWN;
					}
				} else if (blockY == logic.hiddenY) {
					if (blockX == logic.hiddenX + 1) {
						// ���ұ�
						dir = GameLogic.LEFT;
					} else if (blockX == logic.hiddenX - 1) {
						// �����
						dir = GameLogic.RIGHT;
					}
				}
				if (dir != GameLogic.NONE) {
					preEventX = eventX;
					preEventY = eventY;
				}
			}else if ((eventY > btnPos[0][1])
					&& (eventY < (btnPos[0][1] + btnH))) {
				selectedBtn = NONE;
				for (ti = 0; ti < btnPos.length; ti++) {
					if ((eventX > btnPos[ti][0])
							&& (eventX < (btnPos[ti][0] + btnW))) {
						selectedBtn = ti;
						if(selectedBtn == TIP_BTN){
							preGameState = gameState;
							gameState = SHOWBMP;
							for(int i = 0; i < 5; i++){
								logic();
							}
						}
					}
				}
			}

		case MotionEvent.ACTION_MOVE:
			if ((eventX > boardX) && (eventX < (boardX + boardW))
					&& (eventY > boardY)
					&& (eventY < (boardY + boardH + blockH))
					&& (gameState == ONPLAYING)) {
				switch (dir) {
				case GameLogic.UP:
					if ((eventY - preEventY) < 0) {
						distanceY = (eventY - preEventY) * 2;
						if(Math.abs(distanceY) > blockH){
							distanceY = -1 * blockH;
						}
						distanceX = 0;
					}
					break;
				case GameLogic.DOWN:
					if ((eventY - preEventY) > 0) {
						distanceY = (eventY - preEventY) * 2;
						if(Math.abs(distanceY) > blockH){
							distanceY = blockH;
						}
						distanceX = 0;
					}
					break;
				case GameLogic.LEFT:
					if ((eventX - preEventX) < 0) {
						distanceX = (eventX - preEventX) * 2;
						if(Math.abs(distanceX) > blockW){
							distanceX = -1 * blockW;
						}
						distanceY = 0;
					}
					break;
				case GameLogic.RIGHT:
					if ((eventX - preEventX) > 0) {
						distanceX = (eventX - preEventX) * 2;
						if(Math.abs(distanceX) > blockW){
							distanceX = blockW;
						}
						distanceY = 0;
					}
					break;
				}

				isMoveEnough = false;
				if ((Math.abs(distanceY) > blockH / 3)
						|| (Math.abs(distanceX) > blockW / 3)) {
					isMoveEnough = true;
				}
				if ((dir != GameLogic.NONE) && isMoveEnough) {
					logic.moveTo(dir);
					if (logic.isFinish()) {//ƴͼ�ɹ�
						rows++;
						cols++;
						level++;
						newStage();
					}
					dir = GameLogic.NONE;
				}
			} else if ((eventY > btnPos[0][1])
					&& (eventY < (btnPos[0][1] + btnH))) {
				selectedBtn = NONE;
				for (ti = 0; ti < btnPos.length; ti++) {
					if ((eventX > btnPos[ti][0])
							&& (eventX < (btnPos[ti][0] + btnW))) {
						selectedBtn = ti;
					}
				}
			}
			break;
		case MotionEvent.ACTION_UP:
//			if ((eventX > boardX) && (eventX < (boardX + boardW))
//					&& (eventY > boardY)
//					&& (eventY < (boardY + boardH + blockH))
//					&& (gameState == ONPLAYING)) {
//				blockX = (int) ((eventX - boardX) / blockW);
//				blockY = (int) ((eventY - boardY) / blockH);
//				if ((blockX == logic.hiddenX) && (blockY == logic.hiddenY)) {
//					isMoveEnough = false;
//					if ((Math.abs(distanceY) > blockH / 3)
//							|| (Math.abs(distanceX) > blockW / 3)) {
//						isMoveEnough = true;
//					}
//					if ((dir != GameLogic.NONE) && isMoveEnough) {
//						logic.moveTo(dir);
//						if (logic.isFinish()) {//ƴͼ�ɹ�
//							rows++;
//							cols++;
//							level++;
//							newStage();
//						}
//						dir = GameLogic.NONE;
//					}
//				}
//			} else 
				if ((eventY > btnPos[0][1])
					&& (eventY < (btnPos[0][1] + btnH))) {
				for (ti = 0; ti < btnPos.length; ti++) {
					if ((eventX > btnPos[ti][0])
							&& (eventX < (btnPos[ti][0] + btnW))) {
						if (selectedBtn == ti) {
							switch (ti) {
							case BREAKUP_BTN:
								// Toast.makeText(photoPuzzle, "���´���",
								// Toast.LENGTH_LONG).show();
								logic.breakUp();
								gameState = ONPLAYING;
								break;
							case NEWBMP_BTN:
								// Toast.makeText(photoPuzzle, "��ͼƬ",
								// Toast.LENGTH_LONG).show();
								switch (photoPuzzleActivity.puzzleType) {
								case photoPuzzleActivity.FILESYSTEM:
									photoPuzzle.requestImgFromFileSystem();									
									break;
								case photoPuzzleActivity.CAMERAL:
									photoPuzzle.requestImgFromCamera();
									break;
								}
								break;
							case TIP_BTN:
								gameState = preGameState;
							}
						}
					}
				}
			}
			distanceX = 0;
			distanceY = 0;
			blockX = -5;
			blockY = -5;
			selectedBtn = NONE;
			break;
		}
		myDraw();
		return true;// return super.onTouchEvent(event);
	}

	private int dx, dy, px, py, di;

	public void myDraw() {
		canvas = sfh.lockCanvas();
		// canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(gamebg, 0, 0, paint);
		switch (gameState) {
		case ONPREVIEW:
		case SHOWBMP:
			if (puzzleBmp != null) {
				canvas.drawBitmap(puzzleBmp, boardX, boardY, paint);
			}
			canvas.drawRect(boardX, boardY, boardX + boardW, boardY + boardH,
					paint);
			break;
		case ONPLAYING:
			// ���Ʒ���
			for (dy = 0; dy < logic.rows; dy++) {
				for (dx = 0; dx < logic.cols; dx++) {
					if (logic.blocks[dy][dx].key != -1) {
						py = logic.blocks[dy][dx].y;
						px = logic.blocks[dy][dx].x;
						if ((dy == this.blockY) && (dx == this.blockX)) {
							canvas.drawBitmap(logic.bmps[py][px], boardX
									+ blockW * dx + distanceX, boardY + blockH
									* dy + distanceY, paint);
						} else {
							canvas.drawBitmap(logic.bmps[py][px], boardX
									+ blockW * dx, boardY + blockH * dy, paint);
						}
					}
				}
			}
			// ���ƾ���
			for (dy = 0; dy < logic.rows; dy++) {
				for (dx = 0; dx < logic.cols; dx++) {
					if (isHiddenDown && (dy == (logic.rows - 1))
							&& (dx < logic.cols - 1)) {
						continue;
					}
					if (!isHiddenDown && (dx == (logic.cols - 1))
							&& (dy < logic.rows - 1)) {
						continue;
					}
					canvas.drawRect(boardX + blockW * dx, boardY + blockH * dy,
							boardX + blockW * (dx + 1), boardY + blockH
									* (dy + 1), paint);
					// canvas.drawText(logic.blocks[dy][dx].key + "", boardX +
					// blockW * dx, boardY + blockH * dy + paint.getTextSize(),
					// paint);
				}
			}
			// ����ʱ��
			canvas.drawText(timeStr, timeStrX, timeStrY, paint);
			break;
		case GAMEOVER:
			break;
		}
		// ���ư�ť
		for (di = 0; di < btnPos.length; di++) {
			if (selectedBtn == di) {
				canvas.drawBitmap(btnBmps[di + btnPos.length], btnPos[di][0],
						btnPos[di][1], paint);
			} else {
				canvas.drawBitmap(btnBmps[di], btnPos[di][0], btnPos[di][1],
						paint);
			}
		}
		Log.v("Ontouch", gameState + "");
		sfh.unlockCanvasAndPost(canvas);
	}

	private File path, file;

	private boolean saveBmp() {
		if (puzzleBmp == null) {
			return false;
		}
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		// int seconds = calendar.get(Calendar.SECOND);
		// Log.v("year:" + year + "month:" + month + "day:" + day + "||",
		// "hour:" + hour + "minutes:" + minutes + "seconds:" + seconds);
		String fileName = year + "��" + (month + 1) + "��" + day + "��" + hour
				+ "��" + minutes + "��";// + "-" year + "-"
		//-��ʱ + (hour * 3600 + min * 60 + sec) + "��"
		FileOutputStream fos = null;
		try {
			if (Environment.getExternalStorageState() != null
					&& !Environment.getExternalStorageState().equals("removed")) {
				path = new File("/sdcard/photopuzzle");
				file = new File("/sdcard/photopuzzle/" + fileName + ".png");
				if (!path.exists()) {
					path.mkdir();
				}
				if (!file.exists()) {
					file.createNewFile();
				}

				fos = new FileOutputStream(file);
				// DataInputStream dis;
			} else {
				path = new File("/photopuzzle");
				file = new File("/photopuzzle/" + fileName + ".png");
				if (!path.exists()) {
					path.mkdir();
				}
				if (!file.exists()) {
					file.createNewFile();
				}

				fos = new FileOutputStream(file);
				// DataInputStream dis;
			}
			puzzleBmp.compress(Bitmap.CompressFormat.PNG, 80, fos);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// �������أ�3*3, 4*4, 5*5
	private int level;
	private int[] bmpid = new int[] { R.drawable.from1to9,
			R.drawable.from1to16, R.drawable.from1to25 };

	private void newStage() {
		if ((rows > 5) || (level > 2)) {//�����Ϸ����5
			gameState = GAMEOVER;
			String dialogMsg = "";
			if (photoPuzzleActivity.typeNow == photoPuzzleActivity.PROGRAM) {
				dialogMsg = "��һ������" + (hour * 3600 + min * 60 + sec)
						+ "�룬��ʾ��ͼƬʱ�䲻����Ϊ�ɼ��洢��ȥ�����ɼ������ɡ�";
				//ͼƬ�����ڣ�" + file.getPath() + "�С�
			}else{
				saveBmp();
				dialogMsg = "��һ������" + (hour * 3600 + min * 60 + sec) + "�롣ȥ�����ɼ������ɡ�";				
				photoPuzzle.setScore(file.getPath(), (hour * 3600 + min * 60 + sec));
			}

			resetParam();
			
			Builder builder = new Builder(photoPuzzle);
			builder.setIcon(android.R.drawable.ic_dialog_info);
			builder.setTitle("��ϲ����Ӯ�ˣ�");
			builder.setMessage(dialogMsg);
			builder.setPositiveButton("���ȥ",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
//							resetParam();
							photoPuzzle.showScoreView();
						}

					});
			builder.setNegativeButton("������Ϸ",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
//							resetParam();
							newStage();
						}

					});
			builder.show();
			return;
		}
		//��ʾ
		String levelStr = "";
		switch (rows) {
		case 3:
			levelStr = "�Ѷȣ�����";
			break;
		case 4:
			levelStr = "�Ѷȣ��е�";
			break;
		case 5:
			levelStr = "�Ѷȣ�����";
			break;

		}
		if(rows > 3){
		Toast.makeText(photoPuzzle,
				"����" + rows + "*" + cols + "ģʽ��" + levelStr + "��",
				Toast.LENGTH_SHORT).show();
		}
		
		
		isHiddenDown = true;
//		int halfblank = 0;
		if (isHiddenDown) {
			boardX = 10;
			boardW = screenW - boardX * 2;
			boardH = boardW;
			blockW = boardW / cols;
			blockH = boardH / rows;
			boardY = (screenH - boardH - blockH) / 2;//(screenH - boardH - blockH - btnH - 10) / 2;
//			boardY = halfblank + btnH + 10;
			while(boardY < (btnH + 20)){//boardY
				boardX += 4;
				boardW = screenW - boardX * 2;
				boardH = boardW;
				blockW = boardW / cols;
				blockH = boardH / rows;
				boardY = (screenH - boardH - blockH) / 2;
//				boardY = halfblank + btnH + 10;		
			}
		}
		
		btnPos[0][1] = btnPos[1][1] = btnPos[2][1] = (boardY - btnH) / 2;//screenH - btnH - 10;
		btnPos[0][0] = 10;
		btnPos[1][0] = (screenW - btnW) / 2;
		btnPos[2][0] = screenW - btnW - 10;
		
		timeStrX = boardX;
		timeStrY = (int) (boardY + boardH + blockH / 2 + paint
				.getTextSize());
		
		Rect rect = new Rect();
		rect.left = boardX;
		rect.top = boardY;
		rect.right = boardX + boardW;
		rect.bottom = boardY + boardH;
		photoPuzzleActivity.boardPos = rect;
		
//		Toast.makeText(photoPuzzle, level + "", Toast.LENGTH_LONG).show();
		switch (photoPuzzleActivity.typeNow) {
		case photoPuzzleActivity.PROGRAM:
			puzzleBmp = BitmapFactory.decodeResource(this.getResources(),
					bmpid[level]);
			puzzleBmp = Bitmap.createScaledBitmap(puzzleBmp, boardW, boardH,
					false);
			break;
		case photoPuzzleActivity.FILESYSTEM:
		case photoPuzzleActivity.CAMERAL:
			if(puzzleBmp != null){
				if (level == 0) {
					puzzleBmp = Bitmap.createScaledBitmap(puzzleBmp, boardW,
							boardH, false);
				}
			}else{
				Toast.makeText(photoPuzzle, "����ѡ��ͼƬ��", Toast.LENGTH_LONG);
			}
			break;
		}
//		if (saveBmp()) {
//			Toast.makeText(photoPuzzle, file.getPath() + "����ɹ�",
//					Toast.LENGTH_LONG).show();
//		}
		logic = new GameLogic(puzzleBmp, rows, cols, blockW, blockH,
				isHiddenDown);
		logic.breakUp();
		selectedBtn = NONE;
	}

	private int screenW, screenH, boardX, boardY, boardW, boardH;
	private int rows, cols, blockW, blockH;
	private GameLogic logic;
	private boolean isHiddenDown;
	private int timeStrX, timeStrY;

	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		screenW = getWidth();
		screenH = getHeight();

		newStage();

//		 Toast.makeText(photoPuzzle, "surfaceCreated:" + screenW + "*" + screenH,
//		 Toast.LENGTH_LONG).show();
		flag = true;
		th = new Thread(this);
		th.start();
	}

	private int sec, min, hour;
	private String timeStr = "";
	private void logic() {
		if ((gameState == ONPLAYING) || (gameState == SHOWBMP)) {
			timeStr = "";
			sec++;
			if (sec > 59) {
				min++;
				sec %= 60;
			}
			if (min > 59) {
				hour++;
				min %= 60;
			}
			if (hour > 0) {
				timeStr = hour + "Сʱ";
			}
			if (min > 0) {
				timeStr = timeStr + min + "��";
			}
			timeStr = timeStr + sec + "��";
		}
	}

	private Thread th;
	private boolean flag;
	private long start, during;
	private final long MILLISPERSECOND = 1000;

	public void run() {
		// TODO Auto-generated method stub
		while (flag) {
			start = System.currentTimeMillis();
			logic();
			myDraw();
			during = System.currentTimeMillis() - start;
			if (during < MILLISPERSECOND) {
				try {
					Thread.sleep(MILLISPERSECOND - during);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		// Toast.makeText(photoPuzzle, "surfaceDestroyed",
		// Toast.LENGTH_LONG).show();
		flag = false;
	}
}

package photopuzzle.wxf;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.widget.Toast;

public class ClipBmpView  extends SurfaceView implements Callback, Runnable{
	private SurfaceHolder sfh;
	private Paint paint;
	private Canvas canvas;
	
	private photoPuzzleActivity photoPuzzle;
	private Bitmap bitmap, gamebg;
	private int bmpW, bmpH;
	private Bitmap ok, ok_pressed, rotate, rotate_pressed, cancel, cancel_pressed;
	private int btnW, btnH;
	private Bitmap[] btnBmps;
	private int[][] btnPos;
	
//	private Rect
	public ClipBmpView(Context context, Bitmap bmp) {
		super(context);
		photoPuzzle = (photoPuzzleActivity) context;
		sfh = this.getHolder();
		sfh.addCallback(this);
		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.STROKE);
		paint.setTextSize(15);
		
		photoPuzzle = (photoPuzzleActivity) context;
		
		bitmap = bmp;
		boardX = photoPuzzleActivity.boardPos.left;
		boardW = photoPuzzleActivity.boardPos.right - photoPuzzleActivity.boardPos.left;
		boardH = boardW;
		boardY = photoPuzzleActivity.boardPos.top;
//		bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.menubg);
		
	}
	public void setBmp(){
		
	}
	private int startX, startY, di;
	private Matrix matrix;
	private Bitmap scaledBmp, clipedBmp;
	public void myDraw(){
		canvas = sfh.lockCanvas();
		canvas.drawBitmap(gamebg, 0, 0, paint);
		if(bitmap != null){
			canvas.drawBitmap(bitmap, matrix, paint);
//			canvas.save();			
			//缩放画布(以图片中心点进行缩放，XY轴缩放比例相同)
//			canvas.scale(rate, rate, screenW / 2, screenH / 2);
//			canvas.drawBitmap(bitmap, startX, startY, paint);
//			canvas.drawBitmap(bitmap, screenW / 2 - bmpIcon.getWidth() / 2, screenH / 2 - bmpIcon.getHeight() / 2, paint);
//			canvas.restore();
		}
//		scaledBmp = Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth() * rate), (int)(bitmap.getHeight() * rate), false);	
//		canvas.drawText(startX + "*" + startY + " | " + scaledBmp.getWidth() + "*" + scaledBmp.getHeight()
//				+ " | " + bitmap.getScaledWidth(canvas) + "*" + bitmap.getScaledHeight(canvas), 
//				5, screenH - 50, paint);	
		canvas.drawRect(boardX, boardY, boardX + boardW, boardY + boardH, paint);
		for(di = 0; di < btnPos.length; di++){
			if(selectedBtn == di){
				canvas.drawBitmap(btnBmps[di + btnPos.length], btnPos[di][0], btnPos[di][1], paint);
			}else{
				canvas.drawBitmap(btnBmps[di], btnPos[di][0], btnPos[di][1], paint);				
			}
		}
		sfh.unlockCanvasAndPost(canvas);
	}
	
	private boolean isFirst = true, isKeyUp = true;
	private float oldLineDistance, newLineDistance, oldX, oldY, rate, oldRate, preRate;//, angle = 0;	
	private int eventAction, ti;
	private final int NONE = -1, OK_BTN = 0, CANCEL_BTN = 1;//ROTATE_BTN = 1;
	private int selectedBtn;
	private float eventX, eventY;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		eventAction = event.getAction();
		if (eventAction == MotionEvent.ACTION_UP || (eventAction == MotionEvent.ACTION_POINTER_1_UP)
				|| (eventAction == MotionEvent.ACTION_POINTER_2_UP)) {
			if(!isFirst && (eventAction == MotionEvent.ACTION_UP)){
				isFirst = true;
//				matrix = new Matrix();
//				matrix.postScale(rate, rate);
//				startX += bmpW * (oldRate - rate) / 2;
//				startY += bmpH * (oldRate - rate) / 2;
//				matrix.postTranslate(startX, startY);
				oldRate = rate;
			}
			if(!isKeyUp){
				isKeyUp = true;
			}
			if(eventAction == MotionEvent.ACTION_UP){
				eventX = event.getX();
				eventY = event.getY();
				if((eventY > btnPos[0][1]) && (eventY < (btnPos[0][1] + btnH)) && isKeyUp){
					for(ti = 0; ti < btnPos.length; ti++){
						if((eventX > btnPos[ti][0]) && (eventX < (btnPos[ti][0] + btnW))){
							if(selectedBtn == ti){
								switch(ti){
								case OK_BTN:
//									Toast.makeText(photoPuzzle, "确认键按下", Toast.LENGTH_LONG).show();
									scaledBmp = Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth() * rate), (int)(bitmap.getHeight() * rate), false);	
									if((startX > boardX) || ((startX + scaledBmp.getWidth()) < (boardX + boardW))
											|| (startY > boardY) || ((startY + scaledBmp.getHeight()) < (boardY + boardH))){
										Toast.makeText(photoPuzzle, "请用图片将红色矩阵填满", Toast.LENGTH_LONG).show();
									}else{
										clipedBmp = Bitmap.createBitmap(scaledBmp, boardX - startX, boardY - startY, boardW, boardH);
										photoPuzzle.fromClipView2GameView(clipedBmp);
									}
									photoPuzzleActivity.typeNow = photoPuzzleActivity.puzzleType;
									break;
								case CANCEL_BTN://
//									Toast.makeText(photoPuzzle, "取消键按下", Toast.LENGTH_LONG).show();
									photoPuzzle.showGameView(false);
									break;
//								case ROTATE_BTN:
//									angle += 90;
//									if(angle > 360){
//										angle %= 360;
//									}
//									matrix = new Matrix();
//									matrix.postScale(rate, rate);
//									matrix.postTranslate(startX, startY);
//									matrix.postRotate(angle);
//									break;
								}
							}
						}
					}
				}
			}
			selectedBtn = NONE;
		} else {
			if (event.getPointerCount() == 2) {
				if (isFirst) {
					//得到第一次触屏时线段的长度
					oldLineDistance = (float) Math.sqrt(Math.pow(event.getX(1) - event.getX(0), 2) + Math.pow(event.getY(1) - event.getY(0), 2));
					isFirst = false;
				} else {
					//得到非第一次触屏时线段的长度
					newLineDistance = (float) Math.sqrt(Math.pow(event.getX(1) - event.getX(0), 2) + Math.pow(event.getY(1) - event.getY(0), 2));
					//获取本次的缩放比例
					rate = oldRate * newLineDistance / oldLineDistance;

					matrix = new Matrix();
					matrix.postScale(rate, rate);
					startX += bmpW * (preRate - rate) / 2;
					startY += bmpH * (preRate - rate) / 2;
					matrix.postTranslate(startX, startY);
//					matrix.postRotate(angle);
					preRate = rate;
				}
			}
			else{
				eventX = event.getX();
				eventY = event.getY();
				if((eventY > btnPos[0][1]) && (eventY < (btnPos[0][1] + btnH))){
					selectedBtn = NONE;
					for(ti = 0; ti < btnPos.length; ti++){
						if((eventX > btnPos[ti][0]) && (eventX < (btnPos[ti][0] + btnW))){
							selectedBtn = ti;
						}
					}
				}else{
					selectedBtn = NONE;
					if(isKeyUp){
						oldX = eventX;
						oldY = eventY;
						isKeyUp = false;
					}else{
						startX += (eventX - oldX) / 2;
						startY += (eventY - oldY) / 2;
						matrix = new Matrix();
						matrix.postScale(rate, rate);
						matrix.postTranslate(startX, startY);
//						matrix.postRotate(angle);
						oldX = event.getX();
						oldY = event.getY();
					}
				}
			}
		}
		return true;
	}
	
	private int screenW, screenH, boardX, boardY, boardW, boardH;
	private Thread th;
	private boolean flag;
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		screenW = getWidth();
		screenH = getHeight();
		
//		boardX = 10;
//		boardW = screenW - boardX * 2;
//		boardH = boardW;
//		boardY = (screenH - boardH) / 2;
		
//		startX = boardX;
//		startY = boardY;

		//int calrate;
		bmpW = bitmap.getWidth();
		bmpH = bitmap.getHeight();
		startX = (screenW - bmpW) / 2;
		startY = (screenH - bmpH) / 2;
		float rateW = (bmpW < boardW)? (boardW / bmpW) : 1;
		float rateH = (bmpH < boardH)? (boardH / bmpH) : 1;
		rate = (rateW > rateH) ? rateW : rateH;
		oldRate = rate;
		preRate = rate;
		
		matrix = new Matrix();
		matrix.postScale(rate, rate);
		matrix.postTranslate(startX, startY);
		
		Resources res = this.getResources();
		ok = BitmapFactory.decodeResource(res, R.drawable.ok);
		ok_pressed = BitmapFactory.decodeResource(res, R.drawable.ok_pressed);
		cancel = BitmapFactory.decodeResource(res, R.drawable.cancel);
		cancel_pressed = BitmapFactory.decodeResource(res, R.drawable.cancel_pressed);
		rotate = BitmapFactory.decodeResource(res, R.drawable.rotate);
		rotate_pressed = BitmapFactory.decodeResource(res, R.drawable.rotate_pressed);
		gamebg = BitmapFactory.decodeResource(res, R.drawable.gamebg);
		btnW = ok.getWidth();
		btnH = ok.getHeight();
		btnPos = new int[2][2];
		btnBmps = new Bitmap[btnPos.length * 2];
		btnBmps[0] = ok;
		btnBmps[1] = cancel;//rotate;
		btnBmps[2] = ok_pressed;
		btnBmps[3] = cancel_pressed;//rotate_pressed;
		btnPos[0][1] = btnPos[1][1] = screenH - btnH - 10;
		btnPos[0][0] = 10;
		btnPos[1][0] = screenW - btnW - 10;
		
		selectedBtn = NONE;
		
		th = new Thread(this);
		flag = true;
		th.start();
		myDraw();
	}

	private long start, during;
	private final long MILLISPERSECOND = 100;
	public void run() {
		// TODO Auto-generated method stub
		while(flag){
			start = System.currentTimeMillis();
			myDraw();
			during = System.currentTimeMillis() - start;
			if(during < MILLISPERSECOND){
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
		flag = false;
	}

}

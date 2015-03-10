package photopuzzle.wxf;

import java.util.Random;

import android.graphics.Bitmap;
import android.util.Log;

public class GameLogic {
	
	public int rows, cols, width, height;	
	private int blockW, blockH;
	public static final int UP = 0, DOWN = 1,LEFT = 2, RIGHT = 3, NONE = -1;

	public Block[][] blocks;
	public boolean[][] tableMap ;
	public Bitmap[][] bmps;
	public int hiddenX, hiddenY;
	private boolean isHiddenDown;
	public GameLogic(Bitmap bmp, int r, int c, int bW, int bH, boolean isHiddenDown) {		

		rows = r;
		cols = c;
		blockW = bW;//width / cols;
		blockH = bH;//height / rows;
		
		int x, y;
		bmps = new Bitmap[rows][cols];
		for(y = 0; y < rows; y++){
			for(x = 0; x < cols; x++){
				bmps[y][x] = Bitmap.createBitmap(bmp, x * blockW, y * blockH, blockW, blockH);
			}
		}
		this.isHiddenDown = isHiddenDown;
		if(isHiddenDown){
			rows++;
		}else{
			cols++;
		}
		
		tableMap = new boolean[rows][cols];
		blocks = new Block[rows][cols];
		for (y = 0; y < rows; y++) {
			blocks[y] = new Block[cols];
			for (x = 0; x < cols; x++) {
				blocks[y][x] = new Block(x, y, y * cols + x);
				tableMap[y][x] = true;
			}
		}
		
		if(isHiddenDown){
			for(x = 0; x < cols - 1; x++){
				tableMap[rows - 1][x] = false;
				blocks[rows - 1][x].key = -1;
			}
			blocks[rows - 1][cols - 1].key = -1;
		}else{
			for(y = 0; y < rows - 1; y++){
				tableMap[y][cols - 1] = false;
				blocks[y][cols - 1].key = -1;
			}
			blocks[rows - 1][cols - 1].key = -1;
		}
		
		hiddenY = rows - 1;
		hiddenX = cols - 1;
	}
//	private int bx, by;
	public void breakUp() {
		Random rand = new Random();
		for (int i = 0; i < 255; i++) {//3
//			moveTo((rand.nextInt() >>> 1) % 4);
			moveTo(rand.nextInt(4));
		}

//		Log.v("photoPuzzle", "has passed first phase");
		for (int i = 0; i < cols; i++) {
			moveTo(LEFT);
			if (i == 0) {
				for (int j = 0; j < rows; j++) {
					moveTo(UP);
				}
			}
		}

		if (isFinish()) {
			breakUp();
		}
	}
	int key;
	private Block tmp;
	public void moveTo(int direction) {
//		Log.v("photoPuzzle moveTo", hiddenY + "*" + hiddenX + "-" + direction);
		switch (direction) {
		case UP:
//			if ((getHiddenJ() < getN() - 1) && tableMap[hiddenI][hiddenJ + 1]){
//				//设置移动后空格的y坐标
//				blocks[getHiddenI()][getHiddenJ()].setStartY(blocks[getHiddenI()][getHiddenJ()].getStartY() + getBlockHeight());
//				//设置被移动方格移动后的y坐标
//				blocks[getHiddenI()][getHiddenJ() + 1].setStartY(blocks[getHiddenI()][getHiddenJ() + 1].getStartY() - getBlockHeight());
//
//				//修改Block
//				Block temp = blocks[getHiddenI()][getHiddenJ()];
//				blocks[hiddenI][hiddenJ] = blocks[getHiddenI()][getHiddenJ() + 1];
//				blocks[hiddenI][hiddenJ + 1] = temp;
//
//				//空格的y坐标加1
//				hiddenJ++;
//				
//			}
			//hidden block至少要在倒数第二行之前，而且它的下一个快是可用的。
			if((hiddenY < (rows - 1)) && (tableMap[hiddenY + 1][hiddenX])){
//				blocks[hiddenY][hiddenX].y++;
//				blocks[hiddenY + 1][hiddenX].y--;
//				key = blocks[hiddenY][hiddenX].key;
//				blocks[hiddenY][hiddenX].key = blocks[hiddenY + 1][hiddenX].key;
//				blocks[hiddenY + 1][hiddenX].key = key;
				tmp = blocks[hiddenY][hiddenX];
				blocks[hiddenY][hiddenX] = blocks[hiddenY + 1][hiddenX];
				blocks[hiddenY + 1][hiddenX] = tmp;
				hiddenY++;
			}
			break;
		case DOWN:
			if((hiddenY > 0) && (tableMap[hiddenY - 1][hiddenX])){
				tmp = blocks[hiddenY][hiddenX];
				blocks[hiddenY][hiddenX] = blocks[hiddenY - 1][hiddenX];
				blocks[hiddenY - 1][hiddenX] = tmp;
				hiddenY--;
			}
			break;
		case LEFT:
			if((hiddenX < (cols - 1)) && (tableMap[hiddenY][hiddenX + 1])){
				tmp = blocks[hiddenY][hiddenX];
				blocks[hiddenY][hiddenX] = blocks[hiddenY][hiddenX + 1];
				blocks[hiddenY][hiddenX + 1] = tmp;
				hiddenX++;
			}
			break;
		case RIGHT:
			if((hiddenX > 0) && (tableMap[hiddenY][hiddenX - 1])){
				tmp = blocks[hiddenY][hiddenX];
				blocks[hiddenY][hiddenX] = blocks[hiddenY][hiddenX - 1];
				blocks[hiddenY][hiddenX - 1] = tmp;
				hiddenX--;
			}
			break;
		}
	}
	private int fx, fy;
	public boolean isFinish() {
		int r = rows, c = cols;
		if(isHiddenDown){
			r = rows - 1;
		}else{
			c = cols - 1;
		}
//		Log.v("r and c", r + "*" + c);
		for (fy = 0; fy < r; fy++) {
			for (fx = 0; fx < c; fx++) {
				if ((blocks[fy][fx].key != (fy * cols + fx))){// && tableMap[fy][fx]
//					Log.v(blocks[fy][fx].key + "",  (fy * cols + fx) + "");
					return false;
				}
			}
		}
//		Log.v("is finish", "return true");
		return true;
	}
}

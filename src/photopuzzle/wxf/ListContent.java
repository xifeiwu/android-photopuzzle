package photopuzzle.wxf;

import android.graphics.Bitmap;

public class ListContent {
	public Bitmap bmp;
	public String filename;
	public int timeused;
	public ListContent(String fn, int time){
		this.filename = fn;
		this.timeused = time;
	}
}

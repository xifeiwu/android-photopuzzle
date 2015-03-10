package photopuzzle.wxf;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


// 重写Adapter事件,自定义列表内容
public class ContentAdapter extends ArrayAdapter<ListContent> {
	private List<ListContent> items;
	public ContentAdapter(Context context, int textViewResourceId,
			List<ListContent> items) {
		super(context, textViewResourceId, items);
		this.items = items;
	}
	private Bitmap scaledBmp(String path){
		File file = new File(path);
		Bitmap bmp;
		Bitmap scaledBmp;
		if(file.exists()){
			bmp = BitmapFactory.decodeFile(path);
			scaledBmp = Bitmap.createScaledBitmap(bmp, 150, 150, false);
		}else{
			bmp = BitmapFactory.decodeResource(photoPuzzleActivity.instance.getResources(),
					R.drawable.nopic);
			scaledBmp = Bitmap.createScaledBitmap(bmp, 150, 150, false);
		}
		return scaledBmp;
	}
	private String parsePath(String path){
		int start = path.lastIndexOf("/") + 1;
		int end = path.lastIndexOf(".");
		if((start > 0) && (start < path.length()) && (end > 0) && (end < path.length())){
			return path.substring(start, end);
		}else{
			return path;
		}
	}
	private Bitmap tmpBmp;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) photoPuzzleActivity.instance.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.content, null);
		}
		ListContent content = items.get(position);
		Log.v("listcontent", content.filename + content.timeused);
		if (content != null) {
			ImageView imageView = (ImageView) view.findViewById(R.id.image);
			TextView filename = (TextView) view
					.findViewById(R.id.filename);
			TextView timeused = (TextView) view
					.findViewById(R.id.timeused);

//			imageView.setImageDrawable(contentList.this.getResources()
//					.getDrawable(Integer.parseInt(content.getImageUrl())));

			imageView.setImageBitmap(scaledBmp(content.filename));
			timeused.setText("耗时" + content.timeused + "秒");
			filename.setText(parsePath(content.filename));
		}
		return view;
	}
}

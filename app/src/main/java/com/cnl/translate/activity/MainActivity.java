package com.cnl.translate.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import com.cnl.translate.R;
import com.cnl.translate.fragments.MainPreFragment;
import com.cnl.translate.service.MainService;

import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.IOException;
import android.view.View;
import com.cnl.translate.utils.MainModul;
import java.io.InputStream;

import android.view.View.OnClickListener;
import com.cnl.translate.core.CaptureCore;
import android.widget.Toast;
import com.cnl.translate.core.OCRCore;

import android.widget.TextView;

public class MainActivity extends BaseActivity{
	
	private TextView tv;
	
	@Override
	protected void onCreate(){
		setContentView(R.layout.main);
//		tv = (TextView) findViewById(R.id.text);
		init();
		splash();
		startService(new Intent(this, MainService.class));
	}

	private void splash(){
		try{
			ImageView img = (ImageView) findViewById(R.id.back);
			img.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View img){
//						setTheme(R.style.AppThemeP);
//						img.setRotation(180);
					}
				});
			InputStream am = getAssets().open("splash.webp");
			Bitmap bmp = BitmapFactory.decodeStream(am);
			bmp = MainModul.ReduceLight(bmp, 0x88);
			bmp = MainModul.RoughGlass(this, bmp, 12);
			img.setImageBitmap(bmp);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	private void init(){
		View pat = findViewById(R.id.frame);
		pat.setPadding(0, MainModul.getHeight1(), 0, MainModul.getHeight2());
		FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        MainPreFragment prefFragment = new MainPreFragment();
		prefFragment.setDefault(this, false);
        transaction.add(R.id.frame, prefFragment);
        transaction.commit();
	}

	@Override
	protected void afterActivityResult(){
//		finish();
//		test();
		super.afterActivityResult();
	}

	private void test(){
		CaptureCore cap = CaptureCore.newInstance();
		
		final long t = System.currentTimeMillis();
		cap.capture(new CaptureCore.CaptureCallBack(){
				@Override
				public void onCapture(Bitmap bmp){
					long tx = System.currentTimeMillis() - t;
					Toast.makeText(MainActivity.this, "" + tx, 0).show();
				}

				@Override
				public boolean onDefeated(Exception e){
					// TODO: Implement this method
					return false;
				}
			});
//		Bitmap bmp = BitmapFactory.decodeFile("/storage/emulated/0/fooViewSave/Screenshot_20190227184507.jpg");
//		HashMap<String, String> options = new HashMap<String, String>();
//		options.put("language_type", "CHN_ENG");
//		OCRCore ocr = OCRCore.getInstance();
//		ocr.ocrAsync(bmp, options, new OCRCore.OnOCRListener(){
//				@Override
//				public void onResult(String text){
//					tv.append(text);
//				}
//
//				@Override
//				public void onError(String msg){
//					Toast.makeText(MainActivity.this, "err!" + msg, 0).show();
//					tv.append(msg);
//					tv.setTextColor(Color.RED);
//				}
//		});
	}

}

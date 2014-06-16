/* 
 * Copyright 2001-2013 Aspose Pty Ltd. All Rights Reserved.
 *
 * This file is part of Aspose.Words. The source code in this file
 * is only intended as a supplement to the documentation, and is provided
 * "as is", without warranty of any kind, either expressed or implied.
 */

package com.aspose.words.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.android.filechooser.FileChooserActivity;

public class AsposeWordsViewerActivity extends Activity implements
		OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mBtnOpen = (Button) findViewById(R.id.btnCreateFromDoc);
		mLogsTextView = (TextView) findViewById(R.id.textView);
		mDocumentImageView = (ImageView) findViewById(R.id.imageView1);
		mBtnOpen.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnCreateFromDoc:
			Intent intent = new Intent(this, FileChooserActivity.class);
			startActivityForResult(intent, FILE_SELECTED);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case FILE_SELECTED:
				Uri uriFile = data.getData();				
				log("File selected: " + uriFile.getPath());
				convertDocument(uriFile);
			}
		}
	}

	private void convertDocument(final Uri uriFile) {
		log("Loading document: " + uriFile.getPath());

		mProgressDialog = ProgressDialog.show(AsposeWordsViewerActivity.this,
				gProgressDialogTitle, gProgressDialogBody, true, false, null);

		Thread t = new Thread() {
			
			public void run() {			
				setLicense();
				try {
					Document doc = new Document(uriFile.getPath());
					final String pngFileName = getConvertedFileName(uriFile);
					doc.save(pngFileName);
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							log("Saved " + pngFileName);
							mProgressDialog.dismiss();
							loadToImageView(pngFileName);
						}

					});

				} catch (Exception e) {
					e.printStackTrace();
					mHandler.post(updateUILog("Error: " + e.getMessage()));
				}
			}
			
		};
		t.start();
	}

	private String getConvertedFileName(Uri uriFile) {
		String orig = uriFile.getPath();		
		int slashIdx = orig.lastIndexOf(File.separator);
		int dotIdx = orig.lastIndexOf(gDot);
		String newName = orig.substring(slashIdx, dotIdx) + gFileExtension;
		
		return new File(gPathToSave, newName).getAbsolutePath();
	}

	private void log(final String text) {
	    mHandler.post(new Runnable() {
	
	        @Override
	            public void run() {
	                StringBuilder msg = new StringBuilder(mLogsTextView.getText());
	                msg.append("\n");
	                msg.append(text);
	                mLogsTextView.setText(msg.toString());
	            }
	        });
	    }

	private void setLicense() {
		log("Trying to find license at " + gLicenseFile.getAbsolutePath());

		if (gLicenseFile == null || !gLicenseFile.exists()) {
			{
				log("License not found.");
				mLicensed = false;
				return;
			}
		}

		if (mLicensed)
			return;

		try {
			License lic = new License();
			InputStream stream = new FileInputStream(gLicenseFile.getAbsolutePath());
			lic.setLicense(stream);
			mLicensed = true;
			log("License set successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			log("License is not valid: " + e.getMessage());
		}

	}

	private Runnable updateUILog(final String msg) {
		return new Runnable() {

			@Override
			public void run() {
				log(msg);
			}
		};
	}

	private void loadToImageView(String pngFileName) {
		Bitmap bm = BitmapFactory.decodeFile(pngFileName);
		mDocumentImageView.setImageBitmap(bm);
	}
	
	private static final String gProgressDialogBody = "Please wait. The time depends on the document size.";
	private static final String gProgressDialogTitle = "Conversion is in progress";
	private static final String gDot = ".";
	private static final String gFileExtension = ".png";
	private static File gPathToSave = Environment.getExternalStorageDirectory();
	private static final int FILE_SELECTED = 1000;
	private static File gLicenseFile = new File(
			Environment.getExternalStorageDirectory(), "Aspose.Words.Android.lic");

	private ProgressDialog mProgressDialog;
	private Button mBtnOpen;
	private TextView mLogsTextView;
	private Handler mHandler = new Handler();
	private boolean mLicensed;
	private ImageView mDocumentImageView;
}

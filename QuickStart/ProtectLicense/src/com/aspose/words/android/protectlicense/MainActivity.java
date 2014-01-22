/*
 * Copyright 2001-2013 Aspose Pty Ltd. All Rights Reserved.
 *
 * This file is part of Aspose.Words. The source code in this file
 * is only intended as a supplement to the documentation, and is provided
 * "as is", without warranty of any kind, either expressed or implied.
 */

package com.aspose.words.android.protectlicense;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

import com.aspose.words.License;

public class MainActivity extends Activity {

	//ExStart
	//ExFor:License.SetLicense(Stream)
	//ExSummary:Demonstrates how to encrypt and decrypt a license file in order to keep it safe.
	public void encryptDecryptLicense() throws Exception
	{
		String dataDir = Environment.getExternalStorageDirectory().getPath() + File.separator;

		String encryptedFilePath = dataDir + "EncryptedLicense.txt";

		// Make sure your license file can be found at this path on the SD card.
		byte[] licBytes = readAllBytesFromFile(dataDir + "Aspose.Words.lic");

		// Use this key only once for this license file. To protect another file first generate a new key.
		byte[] key = generateKey(licBytes.length);

		// Write the encrypted license to disk.
		BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(encryptedFilePath));
		output.write(encryptDecryptLicense(licBytes, key));
		output.close();

		// Load the encrypted license and decrypt it using the key.
		byte[] decryptedLicense = encryptDecryptLicense(readAllBytesFromFile(encryptedFilePath), key);

		// Load the decrypted license into a stream and set the license.
		ByteArrayInputStream licenseStream = new ByteArrayInputStream(decryptedLicense);

		License license = new License();
		license.setLicense(licenseStream);
	}

	/**
	 * A method used for encrypting and decrypting data using XOR.
	 */
	public byte[] encryptDecryptLicense(byte[] licBytes, byte[] key)
	{
		byte[] output = new byte[licBytes.length];

		for (int i = 0; i < licBytes.length; i++)
			output[i] = (byte)(licBytes[i] ^ key[i]);

		return output;
	}

	/**
	 * Generates a random key the same length as the license (a one time pad).
	 */
	public byte[] generateKey(int size)
	{
		SecureRandom rng = new SecureRandom();

		byte[] strongBytes = new byte[size];
		rng.nextBytes(strongBytes);

		return strongBytes;
	}

	public byte[] readAllBytesFromFile(String filePath) throws IOException
	{
		int pos;

		InputStream inputStream = new FileInputStream(filePath);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((pos = inputStream.read()) != -1)
			bos.write(pos);
		
		inputStream.close();

		return bos.toByteArray();
	}
	//ExEnd

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final TextView tx = (TextView) findViewById(R.id.textBox);

		try {
			encryptDecryptLicense();
			tx.setText("License sucessfully encrypted then decrypted and set.");
		} catch (Exception e) {
			tx.setText("Error during encryption or decryption of license: " + e.getMessage());
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

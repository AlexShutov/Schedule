package com.SFEDU.schedule_1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;


public class MemoryCardFileManager {

	private File targetFile;
	private File rootDir;

	
	// busy flag
	private boolean isRWSessionInProgress;
	
	// read/write streams
	BufferedWriter writeStream;
	BufferedReader readStream;


	/**
	 * Checks whether external storage is ready to be read from. Reading is possible
	 * if media is mounted, or is read only. Context-agnostic.
	 * @return
	 */
	public static boolean isExternalReadable() {
		boolean isExternalAvailible = true;
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED) ||
				state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
			isExternalAvailible = true;
		} else {
			isExternalAvailible = false;
		}
		return  isExternalAvailible;
	}

	/**
	 * Verifies storage writeability. Writeable only when stat is
	 * Environment.MEDIA_MOUNTED
	 */
	public static boolean isExternalWriteable() {
		boolean isExternalWriteable = false;
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			isExternalWriteable = true;
		}
		return  isExternalWriteable;
	}

	
	public void eraseFile()
	{
		// remove file and create new one
		targetFile.delete();
		try {
			targetFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			// создастся, если удалили
		}
	}


	/**
	 * resets session flag
	 */
	MemoryCardFileManager() {
		isRWSessionInProgress = false;
	}
	
	
	// true- если файл на флешке без папок

	/**
	 * Checks storage writeability, appents root path, if neccessary and creates a new file
	 *
	 * @param fileName name of the file
	 * @param inRootDir true, if you want to appent root dir path
	 * @throws IllegalStateException thrown when storage isn't writeable
	 * @throws IllegalArgumentException thrown when path points to directory,
	 * not a file
	 */
	public void setFileName(String fileName,
							boolean inRootDir)
			throws IllegalStateException, IllegalArgumentException
	{
		if (!isExternalWriteable()) {
			throw new IllegalStateException("Memory card isn't writeable");
		}
		
		if (inRootDir) {
			File r = Environment.getExternalStorageDirectory();
			targetFile = new File(r, fileName);
		} else {
			targetFile = new File(fileName);
		}
		
		if (targetFile.isDirectory()) {
			throw new IllegalArgumentException("file is required, not a directory");
		}

		if (!targetFile.exists()) {
			try {
				targetFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * closes read/write streams and clears busy flag
	 * @return true if ok
	 */
	public boolean closeFileSession()
	{
		if (!isRWSessionInProgress) {
			return false;
		}
		if (writeStream != null) {
			try {
				writeStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			writeStream = null;
		}
		if (readStream != null) {
			try {
				readStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			readStream = null;
		}
		// clear busy flag
		isRWSessionInProgress = false;
		return true;
	}

	/**
	 *
	 * @return opens stream to the specified file and returns buffered write stream reference
	 * @throws IllegalAccessException report about access error
	 */
	public BufferedWriter openWriteSession() throws IllegalAccessException
	{
		// report if file is in use
		if (isRWSessionInProgress || !isExternalWriteable()) {
			throw new IllegalAccessException("can't write cause session is already in progress");
		}
		
		try {
			FileWriter fWriter = new FileWriter(targetFile, true);
			writeStream = new BufferedWriter(fWriter); 
		} catch (IOException e) { // file is in use
			throw new IllegalAccessException("Can't open write session: " +
						e.getMessage() );
		}	
		// set busy flag
		isRWSessionInProgress = true;
		return writeStream;
	}

	/**
	 *
	 * @return opens stream to the specified file and returns read stream
	 * @throws IllegalAccessException report about access error
	 */
	public BufferedReader openReadSession() throws IllegalAccessException
	{
		// report, if file is busy
		if (isRWSessionInProgress || !isExternalReadable()) {
			throw new IllegalAccessException("can't read, because session is already in progress " +
					"or storage isn't readable");
		}
	
		try {
			FileReader fReader = new FileReader(targetFile);
			readStream = new BufferedReader(fReader);
			//can't gain acceess to file because it's busy
		} catch (IOException e) {
			throw new IllegalAccessException("Can't open write session: " +
						e.getMessage() );
		}	
		// set busy flag
		isRWSessionInProgress = true;
		return readStream;
	}
	

	public BufferedWriter getWriteStream() { return writeStream; }
	public BufferedReader getReadStream() { return readStream; }
	
	public boolean isWriting() { return isRWSessionInProgress && (getReadStream() != null); }
	public boolean isReading() { return isRWSessionInProgress && (getWriteStream() != null); }
	
	
}

package io.github.zeroaicy.util;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IOUtils {

	private static final String TAG = "IOUtils";


	public static void streamTransfer(InputStream bufferedInputStream, OutputStream packagingZipOutput) throws IOException {
		byte[] data = new byte[4096];
		int read;
		while ((read = bufferedInputStream.read(data)) > 0) {
			packagingZipOutput.write(data, 0, read);
		}
	}

	public static byte[] readAllBytes(InputStream inputStream) throws IOException {
		return readAllBytes(inputStream, true);
	}

	public static byte[] readAllBytes(InputStream inputStream, boolean autoClose) throws IOException {
		try {
			byte[] data = new byte[4096];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int count;
			while ((count = inputStream.read(data)) > 0) {
				baos.write(data, 0, count);
			}
			byte[] readAllBytes = baos.toByteArray();
			baos.close();
			return readAllBytes;
		}
		finally {
			if (autoClose) {
				inputStream.close();
			}
		}
	}

	public static char[] readAllChars(Reader reader, int initialCapacity) throws IOException {
		return readAllChars(reader, false, initialCapacity);
	}

	public static char[] readAllChars(Reader reader, boolean autoClose) throws IOException {
		// 8k
		return readAllChars(reader, autoClose, 8192);
	}
	
	public static char[] readAllChars(Reader reader, boolean autoClose, int initialCapacity) throws IOException {
		try {

			// 初始缓冲区大小
			int bufferSize = Math.max(initialCapacity, 8192);

			char[] buffer = new char[bufferSize];

			// 已读取
			int totalRead = 0;

			int charsReadCount;
			// 循环读取数据
			while ((charsReadCount = reader.read(buffer, totalRead, bufferSize - totalRead)) >= 0) {

				totalRead += charsReadCount;
				// 如果缓冲区已满，扩展缓冲区
				if (totalRead == bufferSize) {
					char[] newBuffer = new char[bufferSize * 2];
					System.arraycopy(buffer, 0, newBuffer, 0, totalRead);
					buffer = newBuffer;
					bufferSize *= 2;
				}
			}

			// 创建最终的char[]数组
			char[] result = new char[totalRead];
			System.arraycopy(buffer, 0, result, 0, totalRead);
			return result;
		}
		finally {
			if (autoClose) {
				reader.close();
			}
		}
	}


	public static void close(AutoCloseable autoCloseable) {
		try {
			if (autoCloseable != null) autoCloseable.close();
		}
		catch (Throwable e) {}
	}

	public static List<String> readLines(InputStream input) {
		ArrayList<String> lines = new ArrayList<>();
		readLines(new InputStreamReader(input), lines, true);
		return lines;
	}
	public static void readLines(InputStream input, Collection<String> lines) {
		readLines(new InputStreamReader(input), lines, true);
	}

	public static void readLines(InputStream input, Collection<String> lines, boolean autoClose) {
		readLines(new InputStreamReader(input), lines, autoClose);
	}

	public static void readLines(Reader reader, Collection<String> lines) {
		readLines(new BufferedReader(reader), lines, true);
	}

	public static void readLines(Reader reader, Collection<String> lines, boolean autoClose) {
		readLines(new BufferedReader(reader), lines, autoClose);
	}

	public static void readLines(BufferedReader bufferedReader, Collection<String> lines, boolean autoClose) {
		try {
			String readLine;
			while ((readLine = bufferedReader.readLine()) != null) {
				lines.add(readLine);
			}
		}
		catch (Exception e) {}

		finally {
			if (autoClose) {
				IOUtils.close(bufferedReader);
			}
		}
	}

	public static void writeLines(Collection<String> lines, String outputPath) {
		try {
			writeLines(lines, new FileOutputStream(outputPath), true);
		}
		catch (FileNotFoundException e) {
			Log.w(TAG, String.format("outputPath: %s 没有发现", outputPath));
		}
	}
	public static void writeLines(Collection<String> lines, File outputFile) {
		try {
			writeLines(lines, new FileOutputStream(outputFile), true);
		}
		catch (FileNotFoundException e) {
			Log.w(TAG, String.format("outputFile: %s 没有发现", outputFile.getAbsolutePath()));
		}
	}
	public static void writeLines(Collection<String> lines, OutputStream output) {
		writeLines(lines, output, true);
	}

	public static void writeLines(Collection<String> lines, OutputStream output, boolean autoClose) {
		if (output == null) {
			Log.w(TAG, "output is null");
		}
		try {
			for (String line : lines) {
				output.write(line.getBytes());
				output.write('\n');
			}
		}
		catch (Throwable e) {}
		finally {
			IOUtils.close(output);
		}
	}
}

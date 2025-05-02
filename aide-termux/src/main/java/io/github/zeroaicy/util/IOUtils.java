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
import java.util.Arrays;

public class IOUtils {

	private static final String TAG = "IOUtils";

	/**
	 * 循环读取，使用场景为 Process getInputStream 读取
	 */
	public static void streamTransfer(InputStream bufferedInputStream) throws IOException {
		byte[] data = new byte[4096];
		int read;
		while ((read = bufferedInputStream.read(data)) > 0);
	}

	public static void streamTransfer(InputStream bufferedInputStream, OutputStream packagingZipOutput)
			throws IOException {
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
		} finally {
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
		} finally {
			if (autoClose) {
				reader.close();
			}
		}
	}

	public static void close(AutoCloseable autoCloseable) {
		try {
			if (autoCloseable != null)
				autoCloseable.close();
		} catch (Throwable e) {
		}
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
		} catch (Exception e) {
		}

		finally {
			if (autoClose) {
				IOUtils.close(bufferedReader);
			}
		}
	}

	public static void writeLines(Collection<String> lines, String outputPath) {
		try {
			writeLines(lines, new FileOutputStream(outputPath), true);
		} catch (FileNotFoundException e) {
			Log.w(TAG, String.format("outputPath: %s 没有发现", outputPath));
		}
	}
	public static void writeLines(Collection<String> lines, File outputFile) {
		try {
			writeLines(lines, new FileOutputStream(outputFile), true);
		} catch (FileNotFoundException e) {
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
		} catch (Throwable e) {
		} finally {
			IOUtils.close(output);
		}
	}

	private static final int DEFAULT_BUFFER_SIZE = 8192;
	private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;
	/**
	 * Reads up to a specified number of bytes from the input stream. This
	 * method blocks until the requested number of bytes has been read, end
	 * of stream is detected, or an exception is thrown. This method does not
	 * close the input stream.
	 *
	 * <p> The length of the returned array equals the number of bytes read
	 * from the stream. If {@code len} is zero, then no bytes are read and
	 * an empty byte array is returned. Otherwise, up to {@code len} bytes
	 * are read from the stream. Fewer than {@code len} bytes may be read if
	 * end of stream is encountered.
	 *
	 * <p> When this stream reaches end of stream, further invocations of this
	 * method will return an empty byte array.
	 *
	 * <p> Note that this method is intended for simple cases where it is
	 * convenient to read the specified number of bytes into a byte array. The
	 * total amount of memory allocated by this method is proportional to the
	 * number of bytes read from the stream which is bounded by {@code len}.
	 * Therefore, the method may be safely called with very large values of
	 * {@code len} provided sufficient memory is available.
	 *
	 * <p> The behavior for the case where the input stream is <i>asynchronously
	 * closed</i>, or the thread interrupted during the read, is highly input
	 * stream specific, and therefore not specified.
	 *
	 * <p> If an I/O error occurs reading from the input stream, then it may do
	 * so after some, but not all, bytes have been read. Consequently the input
	 * stream may not be at end of stream and may be in an inconsistent state.
	 * It is strongly recommended that the stream be promptly closed if an I/O
	 * error occurs.
	 *
	 * @implNote
	 * The number of bytes allocated to read data from this stream and return
	 * the result is bounded by {@code 2*(long)len}, inclusive.
	 *
	 * @param len the maximum number of bytes to read
	 * @return a byte array containing the bytes read from this input stream
	 * @throws IllegalArgumentException if {@code length} is negative
	 * @throws IOException if an I/O error occurs
	 * @throws OutOfMemoryError if an array of the required size cannot be
	 *         allocated.
	 *
	 * @since 11
	 */
	public static byte[] readNBytes(InputStream input, int len) throws IOException {
		if (len < 0) {
			throw new IllegalArgumentException("len < 0");
		}

		List<byte[]> bufs = null;
		byte[] result = null;
		int total = 0;
		int remaining = len;
		int n;
		do {
			byte[] buf = new byte[Math.min(remaining, DEFAULT_BUFFER_SIZE)];
			int nread = 0;

			// read to EOF which may read more or less than buffer size
			while ((n = input.read(buf, nread, Math.min(buf.length - nread, remaining))) > 0) {
				nread += n;
				remaining -= n;
			}

			if (nread > 0) {
				if (MAX_BUFFER_SIZE - total < nread) {
					throw new OutOfMemoryError("Required array size too large");
				}
				if (nread < buf.length) {
					buf = Arrays.copyOfRange(buf, 0, nread);
				}
				total += nread;
				if (result == null) {
					result = buf;
				} else {
					if (bufs == null) {
						bufs = new ArrayList<>();
						bufs.add(result);
					}
					bufs.add(buf);
				}
			}
			// if the last call to read returned -1 or the number of bytes
			// requested have been read then break
		} while (n >= 0 && remaining > 0);

		if (bufs == null) {
			if (result == null) {
				return new byte[0];
			}
			return result.length == total ? result : Arrays.copyOf(result, total);
		}

		result = new byte[total];
		int offset = 0;
		remaining = total;
		for (byte[] b : bufs) {
			int count = Math.min(b.length, remaining);
			System.arraycopy(b, 0, result, offset, count);
			offset += count;
			remaining -= count;
		}

		return result;
	}

	public static int readNBytes(InputStream input, byte[] b, int off, int len) throws IOException {

		checkFromIndexSize(off, len, b.length);

		int n = 0;
		while (n < len) {
			int count = input.read(b, off + n, len - n);
			if (count < 0)
				break;
			n += count;
		}
		return n;
	}

	public static int checkFromIndexSize(int fromIndex, int size, int length) {
		if (fromIndex < 0 || size < 0 || length < 0 || fromIndex > length - size) {
			throw new IndexOutOfBoundsException(
					"Range [" + fromIndex + ", " + fromIndex + " + " + size + ") out of bounds for length " + length);
		}
		return fromIndex;
	}
}


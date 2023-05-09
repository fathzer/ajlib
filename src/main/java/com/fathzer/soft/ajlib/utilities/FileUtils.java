package com.fathzer.soft.ajlib.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.FileLock;
import java.text.MessageFormat;

import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

import sun.awt.shell.ShellFolder;

/** Utility to perform some operations on files.
 */
@IgnoreJRERequirement
public class FileUtils {
	private static final String ACCESS_DENIED_MESSAGE = "What's the right message ?";

	private FileUtils() {
	}
	
	/** Gets the canonical file of a file even on windows where links are ignored by File.getCanonicalPath().
	 * <br>Even if the link is broken, the method returns the linked file. You should use File.exists() to test if the returned file is available.
	 * <br>If the link is a link to a link to a file, this method returns the final file. 
	 * @param file the file to test
	 * @return a File
	 * @throws IOException If something goes wrong
	 */
	@SuppressWarnings("unchecked")
	public static File getCanonical(File file) throws IOException {
		if (!file.exists()) {
			return file;
		}
		try {
			// The following lines are equivalent to sf = new sun.awt.shell.Win32ShellFolderManager2().createShellFolder(file);
			// We use reflection in order the code to compile on non Windows platform (where new sun.awt.shell.Win32ShellFolderManager2
			// is a unknown class.
			ShellFolder sf;
			@SuppressWarnings("rawtypes")
			Class cl = Class.forName("sun.awt.shell.Win32ShellFolderManager2");
			Object windowsFolderManager = cl.newInstance();
			sf = (ShellFolder) cl.getMethod("createShellFolder", File.class).invoke(windowsFolderManager, file);
			if (sf.isLink()) {
				return sf.getLinkLocation();
			}
		} catch (ClassNotFoundException e) {
			// We're not on a windows platform
			// We also ignore other errors that may not happen.
			// Ok, errors always happens ... In such a case, we can do we have already done our best effort and
			// we will let file.CanonicalFile do better.
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		} catch (NoSuchMethodException e) {
		} catch (InstantiationException e) {
		}
		return file.getCanonicalFile();
	}
	
	/** Move a file from one path to another.
	 * <br>Unlike java.io.File.renameTo, this method always moves the file (if it doesn't fail).
	 * If the source and the destination paths are not on the same file system, the file is copied to the new file system
	 * and then erased from the old one. 
	 * @param src The src path
	 * @param dest The dest path
	 * @throws IOException If the move fails
	 */
	public static void move(File src, File dest) throws IOException {
		// Check whether the destination directory exists or not.
		// If not, create it.
		File parent = dest.getAbsoluteFile().getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		// Try to simply rename the file
		if (!src.renameTo(dest)) {
			// renameTo may fail if src and dest files are not on the same file system.
			// Then we have to copy the src file.
			// Before, we will ensure we will be able to erase the src file after the copy
			if (src.canWrite()) {
				copy(src, dest, true);
				// Now, deletes the src file
				if (!src.delete()) {
					// Oh ... we were thinking we had the right to delete the file ... but we can't
					// delete the dest file
					dest.delete();
					throw new SecurityException(ACCESS_DENIED_MESSAGE);
				}
			} else {
				throw new SecurityException(ACCESS_DENIED_MESSAGE);
			}
		}
	}

	/** Copy a file to another.
	 * @param src The src path
	 * @param dest The dest path
	 * @param overrideExisting true if copying to an existing file is ok.
	 * @throws IOException If the copy fails
	 */
	public static void copy(File src, File dest, boolean overrideExisting) throws IOException {
		if (dest.exists() && !overrideExisting) {
			throw new IOException(MessageFormat.format("File {0} already exists", dest));
		}
		InputStream in = new BufferedInputStream(new FileInputStream(src));
		try {
			OutputStream out = new BufferedOutputStream(new FileOutputStream(dest));
			try {
				int c;
				while ((c = in.read()) != -1) {
					out.write(c);
				}
			} finally {
				out.close();
			}
		} finally {
			in.close();
		}
		dest.setLastModified(src.lastModified());
	}
	
	/** Deletes recursively a directory.
	 * <br>This means that the directory and all of its files or subfolders are deleted.
	 * @param file the directory to be deleted (if is is a file, the file will be deleted).
	 * @return true if the directory has been successfully deleted.
	 */
	public static boolean deleteDirectory(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteDirectory(files[i]);
			}
		}
		return file.delete();
	}

	/** Tests whether a file is contained in a directory.
	 * <BR>The directory and the file may not exists, the search is done on the absolute paths.
	 * @param file The file to be tested
	 * @param directory The tested directory
	 * @return true if the file is contained in the directory.
	 */
	public static boolean isIncluded(File file, File directory) {
		for (File parent = file.getParentFile(); parent!=null; parent = parent.getParentFile()) {
			if (parent.equals(directory)) {
				return true;
			}
		}
		return false;
	}
	
	/** Gets a FileOutputStream even on a windows hidden file.
	 * <br>Under windows, it is impossible to write directly in a hidden file with Java.
	 * You have to make the file visible first. That's what this method try to do.
	 * <br>When the file was initially hidden, the close method of the returned stream hide it again.
	 * @param file The file to be opened for writing
	 * @return a new stream
	 * @throws IOException If something goes wrong
	 */
	public static FileOutputStream getHiddenCompliantStream(File file) throws IOException {
		if (file.isHidden() && System.getProperty("os.name", "?").startsWith("Windows")) {
			try {
				Process process = Runtime.getRuntime().exec("attrib -H \""+file.getAbsolutePath()+"\"");
				try {
					int result = process.waitFor();
					if (result==0) {
						return new HiddenFileOutputStream(file);
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			} catch (IOException e) {
				// This try catch block is empty because this exception, in this context, means that the attrib command is not available.
				// In such a case, we just have to do ... nothing: If the OutputStream creation fails, an IOException will be thrown
			}
		}
		// If the file was not hidden, or if making the file visible failed, try to open a classic stream.
		return new FileOutputStream(file);
	}
	
	/** The FileOutputStream returned by getHiddenCompliantStream method when it is called on a Windows hidden file.
	 * @see FileUtils#getHiddenCompliantStream(File)
	 */
	private static class HiddenFileOutputStream extends FileOutputStream {
		private File file;

		HiddenFileOutputStream(File file) throws FileNotFoundException {
			super(file);
			this.file = file;
		}
		
		@Override
		public void close() throws IOException {
			super.close();
			Process process = Runtime.getRuntime().exec("attrib +H \""+file.getAbsolutePath()+"\"");
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
	
	/** Tests whether the application can write to a file (or a folder).
	 * <br>It differs from File.canWrite because File.canWrite ignore the security policies of the platform.
	 * This method returns true only if the calling thread have all the rights necessary to write to the file, and the file
	 * is not already locked.
	 * @param file The file or folder to test
	 * @return true if the file or folder exists and the calling thread can write into it.
	 * <br>If the file doesn't exist, it returns true if the parent folder is writable.
	 */
	public static boolean isWritable(File file) {
		if (!file.exists()) {
			File parentFile = file.getAbsoluteFile().getParentFile();
			return parentFile==null?false:isWritable(parentFile);
		}
		if (!file.canWrite()) {
			return false;
		}
		if (file.isDirectory()) {
			// If the file is a folder, the easiest way is to create a temporary file.
			try {
				File f = File.createTempFile("ajlib", null, file);
				f.delete();
				return true;
			} catch (IOException e) {
				return false;
			} catch (SecurityException e) {
				return false;
			}
		} else {
			// If the argument is a file, we will simply to open it for writing 
			try {
				FileOutputStream x = new FileOutputStream(file,true);
				try {
					FileLock lock = null;
					lock = x.getChannel().tryLock();
					if (lock==null) {
						return false;
					} else {
						lock.release();
						return true;
					}
				} finally {
					x.close();
				}
			} catch (FileNotFoundException e) {
				// File is locked by another application
				return false;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/** Tests whether the application can read from a file (or a folder).
	 * <br>It differs from File.canRead because File.canRead ignore the security policies of the platform.
	 * This method returns true only if the calling thread have all the rights necessary to read from the file.
	 * @param file The file or folder to test
	 * @return true if the file or folder exists and the calling thread can write into it
	 */
	public static boolean isReadable(File file) {
		if (!file.canRead()) {
			return false;
		}
		if (file.isDirectory()) {
			return true;
		}
		// If the argument is a file, we will simply to open it for reading 
		try {
			FileInputStream x = new FileInputStream(file);
			x.close();
			return true;
		} catch (FileNotFoundException e) {
			// The application have the right to read the file
			return false;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/** Gets the extension of a file.
	 * <br>For example, the extension of file "file.xml" is ".xml".
	 * @param file The file.
	 * @return The extension including the point, or null if fileName does not have an extension.
	 */
	public static String getExtension(File file) {
		String name = file.getName();
		int index = name.lastIndexOf('.');
		if (index<0) {
			return null;
		} else {
			return name.substring(index);
		}
	}

	/** Gets the root name of a file.
	 * <br>For example, the root name of file "file.xml" is "file".
	 * @param file The file.
	 * @return The extension including the point, or null if fileName does not have an extension.
	 */
	public static String getRootName(File file) {
		String name = file.getName();
		int index = name.lastIndexOf('.');
		if (index<0) {
			return name;
		} else {
			return name.substring(0,index);
		}
	}
}

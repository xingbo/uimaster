package org.shaolin.bmdp.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloseUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(CloseUtil.class);

	private CloseUtil() {
	}
	
	public static void close(Reader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				if (logger.isDebugEnabled()) {
					logger.debug("Close reader:" + reader + " error", e);
				}
			}
		}
	}

	public static void close(Writer writer) {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				if (logger.isDebugEnabled()) {
					logger.debug("Close writer:" + writer + " error", e);
				}
			}
		}
	}

	public static void close(InputStream ins) {
		if (ins != null) {
			try {
				ins.close();
			} catch (IOException e) {
				if (logger.isDebugEnabled()) {
					logger.debug("Close input stream:" + ins + " error", e);
				}
			}
		}
	}

	public static void close(OutputStream outs) {
		if (outs != null) {
			try {
				outs.close();
			} catch (IOException e) {
				if (logger.isDebugEnabled()) {
					logger.debug("Close output stream:" + outs + " error", e);
				}
			}
		}
	}

	public static void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				if (logger.isDebugEnabled()) {
					logger.debug("Close result set:" + rs + " error", e);
				}
			}
		}
	}

	public static void close(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				if (logger.isDebugEnabled()) {
					logger.debug("Close statement:" + stmt + " error", e);
				}
			}
		}
	}

	public static void close(java.sql.Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				if (logger.isDebugEnabled()) {
					logger.debug("Close connection:" + con + " error", e);
				}
			}
		}
	}

	public static void close(ZipFile zipFile) {
		if (zipFile != null) {
			try {
				zipFile.close();
			} catch (IOException e) {
				if (logger.isDebugEnabled()) {
					logger.debug("Close zip file:" + zipFile + " error", e);
				}
			}
		}
	}

}

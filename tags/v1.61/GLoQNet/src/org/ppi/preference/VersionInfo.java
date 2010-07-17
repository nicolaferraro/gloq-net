package org.ppi.preference;

import java.io.InputStream;
import java.util.Properties;

public class VersionInfo {

	private static final VersionInfo instance = new VersionInfo();

	Properties prop;

	public VersionInfo() {
		InputStream versionStream = null;
		try {
			versionStream = getClass().getClassLoader().getResourceAsStream(Constants.APPLICATION_VERSION_FILE_NAME);

			prop = new Properties();
			prop.load(versionStream);

		} catch (Exception ex) {
			throw new RuntimeException("Unable to load preferences", ex);
		} finally {
			try {
				versionStream.close();
			} catch (Exception silex) {
			}
		}

	}
	
	public static VersionInfo getInstance() {
		return instance;
	}

	public String getApplicationVersion() {
		String main = prop.getProperty("app.main.version");
		String num = prop.getProperty("build.number");
		return main + "." + num;
	}

}

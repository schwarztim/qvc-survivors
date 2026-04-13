package com.qvc.survivors.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionUtil {
   @Generated
   private static final Logger log = LoggerFactory.getLogger(VersionUtil.class);
   private static final String VERSION_FILE = "version.properties";
   private static final String VERSION_KEY = "version";
   private static final String DEFAULT_VERSION = "1.0-SNAPSHOT";
   private static String version;

   public static String getVersion() {
      if (version == null) {
         loadVersion();
      }

      return version;
   }

   private static void loadVersion() {
      Properties properties = new Properties();

      try (InputStream input = VersionUtil.class.getClassLoader().getResourceAsStream("version.properties")) {
         if (input != null) {
            properties.load(input);
            version = properties.getProperty("version", "1.0-SNAPSHOT");
         } else {
            log.warn("Version properties file not found, using default version");
            version = "1.0-SNAPSHOT";
         }
      } catch (IOException var6) {
         log.error("Error loading version properties", var6);
         version = "1.0-SNAPSHOT";
      }
   }
}

package com.qvc.survivors.service;

import com.qvc.survivors.model.meta.MetaProgression;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetaProgressionManager {
   @Generated
   private static final Logger log = LoggerFactory.getLogger(MetaProgressionManager.class);
   private static final String SAVE_FILE_DAT = "meta_progression.dat";
   private static final String SAVE_FILE_JSON = "meta_progression.json";
   private static final String SAVE_DIRECTORY = System.getProperty("user.home") + File.separator + ".qvcsurvivors";
   private MetaProgression metaProgression;

   public MetaProgressionManager() {
      this.load();
   }

   public MetaProgression getMetaProgression() {
      if (this.metaProgression == null) {
         this.metaProgression = new MetaProgression();
      }

      return this.metaProgression;
   }

   public void save() {
      try {
         File saveDir = new File(SAVE_DIRECTORY);
         if (!saveDir.exists()) {
            saveDir.mkdirs();
         }

         Path jsonPath = Path.of(SAVE_DIRECTORY, SAVE_FILE_JSON);
         Files.writeString(jsonPath, this.metaProgression.toJson(), StandardCharsets.UTF_8);
         log.info("Meta progression saved as JSON");
      } catch (Exception e) {
         log.error("Failed to save meta progression", e);
      }
   }

   public void load() {
      // Try JSON first
      try {
         Path jsonPath = Path.of(SAVE_DIRECTORY, SAVE_FILE_JSON);
         if (Files.exists(jsonPath)) {
            String json = Files.readString(jsonPath, StandardCharsets.UTF_8);
            this.metaProgression = MetaProgression.fromJson(json);
            log.info("Meta progression loaded from JSON");
            return;
         }
      } catch (Exception e) {
         log.error("Failed to load meta progression JSON", e);
      }

      // Fallback: try old binary format
      try {
         File datFile = new File(SAVE_DIRECTORY, SAVE_FILE_DAT);
         if (datFile.exists()) {
            try (
               FileInputStream fileIn = new FileInputStream(datFile);
               ObjectInputStream in = new ObjectInputStream(fileIn);
            ) {
               this.metaProgression = (MetaProgression)in.readObject();
               log.info("Meta progression loaded from legacy .dat format");
            }
            // Migrate: save as JSON immediately
            this.save();
            log.info("Migrated meta progression to JSON format");
            return;
         }
      } catch (Exception e) {
         log.error("Failed to load meta progression .dat", e);
      }

      this.metaProgression = new MetaProgression();
      log.info("Created new meta progression");
   }

   public void reset() {
      this.metaProgression = new MetaProgression();
      this.save();
      log.info("Meta progression reset");
   }
}

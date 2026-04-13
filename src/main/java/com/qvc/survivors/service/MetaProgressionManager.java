package com.qvc.survivors.service;

import com.qvc.survivors.model.meta.MetaProgression;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetaProgressionManager {
   @Generated
   private static final Logger log = LoggerFactory.getLogger(MetaProgressionManager.class);
   private static final String SAVE_FILE_NAME = "meta_progression.dat";
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

         File saveFile = new File(saveDir, "meta_progression.dat");

         try (
            FileOutputStream fileOut = new FileOutputStream(saveFile);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
         ) {
            out.writeObject(this.metaProgression);
            log.info("Meta progression saved successfully");
         }
      } catch (Exception var11) {
         log.error("Failed to save meta progression", var11);
      }
   }

   public void load() {
      try {
         File saveFile = new File(SAVE_DIRECTORY, "meta_progression.dat");
         if (saveFile.exists()) {
            try (
               FileInputStream fileIn = new FileInputStream(saveFile);
               ObjectInputStream in = new ObjectInputStream(fileIn);
            ) {
               this.metaProgression = (MetaProgression)in.readObject();
               log.info("Meta progression loaded successfully");
            }

            return;
         }
      } catch (Exception var10) {
         log.error("Failed to load meta progression", var10);
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

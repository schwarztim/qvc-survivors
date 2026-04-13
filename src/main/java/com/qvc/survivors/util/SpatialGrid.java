package com.qvc.survivors.util;

import com.qvc.survivors.model.entity.Entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpatialGrid {
   private final int cellSize;
   private final int gridWidth;
   private final int gridHeight;
   private final Map<Long, List<Entity>> grid;

   public SpatialGrid(int worldWidth, int worldHeight, int cellSize) {
      this.cellSize = cellSize;
      this.gridWidth = worldWidth / cellSize + 1;
      this.gridHeight = worldHeight / cellSize + 1;
      this.grid = new HashMap<>();
   }

   public void clear() {
      this.grid.clear();
   }

   public void insert(Entity entity) {
      int minCellX = this.getCellX(entity.getX());
      int maxCellX = this.getCellX(entity.getX() + entity.getWidth());
      int minCellY = this.getCellY(entity.getY());
      int maxCellY = this.getCellY(entity.getY() + entity.getHeight());

      for (int x = minCellX; x <= maxCellX; x++) {
         for (int y = minCellY; y <= maxCellY; y++) {
            long key = this.getKey(x, y);
            this.grid.computeIfAbsent(key, k -> new ArrayList<>()).add(entity);
         }
      }
   }

   public List<Entity> query(Entity entity) {
      List<Entity> results = new ArrayList<>();
      int minCellX = this.getCellX(entity.getX());
      int maxCellX = this.getCellX(entity.getX() + entity.getWidth());
      int minCellY = this.getCellY(entity.getY());
      int maxCellY = this.getCellY(entity.getY() + entity.getHeight());

      for (int x = minCellX; x <= maxCellX; x++) {
         for (int y = minCellY; y <= maxCellY; y++) {
            long key = this.getKey(x, y);
            List<Entity> cellEntities = this.grid.get(key);
            if (cellEntities != null) {
               for (Entity e : cellEntities) {
                  if (!results.contains(e) && e != entity) {
                     results.add(e);
                  }
               }
            }
         }
      }

      return results;
   }

   private int getCellX(double worldX) {
      return Math.max(0, Math.min(this.gridWidth - 1, (int)(worldX / this.cellSize)));
   }

   private int getCellY(double worldY) {
      return Math.max(0, Math.min(this.gridHeight - 1, (int)(worldY / this.cellSize)));
   }

   private long getKey(int cellX, int cellY) {
      return (long)cellX << 32 | cellY & 4294967295L;
   }
}

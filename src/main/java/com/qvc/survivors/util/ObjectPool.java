package com.qvc.survivors.util;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Supplier;

public class ObjectPool<T> {
   private final Queue<T> pool;
   private final Supplier<T> factory;
   private final int maxSize;

   public ObjectPool(Supplier<T> factory, int maxSize) {
      this.factory = factory;
      this.maxSize = maxSize;
      this.pool = new ArrayDeque<>(maxSize);
   }

   public T obtain() {
      T object = this.pool.poll();
      if (object == null) {
         object = this.factory.get();
      }

      return object;
   }

   public void free(T object) {
      if (this.pool.size() < this.maxSize) {
         this.pool.offer(object);
      }
   }

   public void clear() {
      this.pool.clear();
   }

   public int size() {
      return this.pool.size();
   }
}

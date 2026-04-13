package com.qvc.survivors.service;

import com.qvc.survivors.model.entity.Collectible;
import com.qvc.survivors.model.entity.PackageEntity;
import com.qvc.survivors.model.entity.RegularCustomer;
import com.qvc.survivors.model.entity.VIPCustomer;
import com.qvc.survivors.util.ObjectPool;

public class EntityPoolManager {
   private final ObjectPool<PackageEntity> packagePool = new ObjectPool<>(() -> new PackageEntity(0.0, 0.0, 0.0, 0.0, 0.0), 200);
   private final ObjectPool<RegularCustomer> regularCustomerPool = new ObjectPool<>(() -> new RegularCustomer(0.0, 0.0), 100);
   private final ObjectPool<VIPCustomer> vipCustomerPool = new ObjectPool<>(() -> new VIPCustomer(0.0, 0.0), 50);
   private final ObjectPool<Collectible> collectiblePool = new ObjectPool<>(() -> new Collectible(0.0, 0.0, 0), 150);

   public PackageEntity obtainPackage(double x, double y, double velocityX, double velocityY, double damage) {
      PackageEntity packageEntity = this.packagePool.obtain();
      packageEntity.reset(x, y, velocityX, velocityY, damage);
      return packageEntity;
   }

   public void freePackage(PackageEntity packageEntity) {
      this.packagePool.free(packageEntity);
   }

   public RegularCustomer obtainRegularCustomer(double x, double y) {
      RegularCustomer customer = this.regularCustomerPool.obtain();
      customer.reset(x, y);
      return customer;
   }

   public void freeRegularCustomer(RegularCustomer customer) {
      this.regularCustomerPool.free(customer);
   }

   public VIPCustomer obtainVIPCustomer(double x, double y) {
      VIPCustomer customer = this.vipCustomerPool.obtain();
      customer.reset(x, y);
      return customer;
   }

   public void freeVIPCustomer(VIPCustomer customer) {
      this.vipCustomerPool.free(customer);
   }

   public Collectible obtainCollectible(double x, double y, int value, boolean isHealthPack) {
      Collectible collectible = this.collectiblePool.obtain();
      collectible.reset(x, y, value, isHealthPack);
      return collectible;
   }

   public void freeCollectible(Collectible collectible) {
      this.collectiblePool.free(collectible);
   }

   public void clear() {
      this.packagePool.clear();
      this.regularCustomerPool.clear();
      this.vipCustomerPool.clear();
      this.collectiblePool.clear();
   }
}

package com.qvc.survivors.service;

import com.qvc.survivors.model.upgrade.Upgrade;
import com.qvc.survivors.model.upgrade.UpgradeRegistry;
import java.util.List;

public class UpgradeManager {
   private static final int UPGRADE_OPTIONS_COUNT = 3;
   private final UpgradeRegistry upgradeRegistry = new UpgradeRegistry();

   public List<Upgrade> generateUpgradeOptions() {
      return this.upgradeRegistry.getRandomUpgrades(3);
   }
}

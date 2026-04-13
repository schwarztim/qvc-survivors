package com.qvc.survivors.controller;

import java.util.HashSet;
import java.util.Set;
import javafx.scene.input.KeyCode;
import lombok.Generated;

public class InputHandler {
   private final Set<KeyCode> pressedKeys = new HashSet<>();
   private KeyCode lastPressedKey;

   public void handleKeyPressed(KeyCode keyCode) {
      this.pressedKeys.add(keyCode);
      this.lastPressedKey = keyCode;
   }

   public void handleKeyReleased(KeyCode keyCode) {
      this.pressedKeys.remove(keyCode);
   }

   public boolean isKeyPressed(KeyCode keyCode) {
      return this.pressedKeys.contains(keyCode);
   }

   public KeyCode consumeLastPressedKey() {
      KeyCode key = this.lastPressedKey;
      this.lastPressedKey = null;
      return key;
   }

   public boolean hasLastPressedKey() {
      return this.lastPressedKey != null;
   }

   public void reset() {
      this.pressedKeys.clear();
      this.lastPressedKey = null;
   }

   @Generated
   public Set<KeyCode> getPressedKeys() {
      return this.pressedKeys;
   }

   @Generated
   public KeyCode getLastPressedKey() {
      return this.lastPressedKey;
   }
}

package com.qvc.survivors;

import com.qvc.survivors.controller.GameController;
import com.qvc.survivors.service.SoundEffectGenerator;
import javafx.stage.Stage;

public class Application extends javafx.application.Application {
   public void start(Stage primaryStage) {
      SoundEffectGenerator soundGenerator = new SoundEffectGenerator();
      new GameController(primaryStage, soundGenerator);
   }

   public static void main(String[] args) {
      launch(args);
   }
}

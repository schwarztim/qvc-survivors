package com.qvc.survivors.view;

import com.qvc.survivors.util.VersionUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PreloaderView {
   private static final Random RANDOM = new Random();
   private static final Color BACKGROUND_COLOR = Color.rgb(20, 20, 25);
   private static final String[] JOKE_TEXTS = new String[]{
      "SO AWESOME!",
      "Just-in-time delivery!",
      "0 defects... mostly",
      "Agile? More like fragile!",
      "Works on my machine",
      "It's not a bug, it's a feature",
      "Optimizing delivery routes...",
      "Calculating logistics...",
      "Package velocity: OVER 9000!",
      "Six Sigma? How about Six Speed!",
      "Lean manufacturing, fat tips!",
      "Supply chain? More like supply GAIN!",
      "Continuous improvement!",
      "Kaizen your way to victory!",
      "Error 404: Customer not satisfied"
   };
   private final GameView gameView;
   private final ArrayList<String> jokeDeck;
   private final List<PreloaderView.Particle> particles;
   private final List<PreloaderView.Explosion> explosions;
   private final List<PreloaderView.Star> stars;
   private final List<PreloaderView.FloatingJoke> jokes;
   private boolean titleExplosionTriggered;
   private double nextJokeTime;

   public PreloaderView(GameView gameView) {
      this.gameView = gameView;
      this.particles = new ArrayList<>();
      this.explosions = new ArrayList<>();
      this.stars = new ArrayList<>();
      this.jokes = new ArrayList<>();
      this.jokeDeck = new ArrayList<>();
      this.titleExplosionTriggered = false;
      this.nextJokeTime = 2.0;
      this.initializeParticles();
      this.initializeStars();
      this.shuffleJokeDeck();
   }

   public void render(double animationTime) {
      this.gameView.getGraphicsContext().save();
      this.renderBackground();
      this.renderAnimatedBackground(animationTime);
      this.renderStars(animationTime);
      this.renderFloatingJokes(animationTime);
      this.renderExplosions();
      this.renderParticles(animationTime);
      this.renderScanlines(animationTime);
      this.renderAnimatedLogo(animationTime);
      this.renderAnimatedInstructions(animationTime);
      this.updateExplosions();
      this.updateJokes(animationTime);
      if (animationTime > 2.4 && !this.titleExplosionTriggered) {
         this.createTitleExplosion();
         this.titleExplosionTriggered = true;
      }

      this.gameView.getGraphicsContext().restore();
   }

   private void initializeParticles() {
      int width = (int)this.gameView.getWidth();
      int height = (int)this.gameView.getHeight();

      for (int i = 0; i < 100; i++) {
         this.particles
            .add(
               new PreloaderView.Particle(
                  RANDOM.nextDouble() * width,
                  RANDOM.nextDouble() * height,
                  RANDOM.nextDouble() * 2.0 - 1.0,
                  RANDOM.nextDouble() * 2.0 - 1.0,
                  RANDOM.nextDouble() * 360.0
               )
            );
      }
   }

   private void initializeStars() {
      int width = (int)this.gameView.getWidth();
      int height = (int)this.gameView.getHeight();

      for (int i = 0; i < 150; i++) {
         this.stars.add(new PreloaderView.Star(RANDOM.nextDouble() * width, RANDOM.nextDouble() * height, 1 + RANDOM.nextInt(3), RANDOM.nextDouble()));
      }
   }

   private void renderBackground() {
      this.gameView.drawBox(0.0, 0.0, this.gameView.getWidth(), this.gameView.getHeight(), Color.TRANSPARENT, BACKGROUND_COLOR);
   }

   private void renderAnimatedBackground(double animationTime) {
      double width = this.gameView.getWidth();
      double height = this.gameView.getHeight();
      this.gameView.getGraphicsContext().save();
      this.gameView.getGraphicsContext().setGlobalBlendMode(BlendMode.ADD);

      for (int i = 0; i < 5; i++) {
         double x = (i * 200 + animationTime * 30.0) % width;
         double y = height * (0.2 + i * 0.15);
         double size = 120.0 + Math.sin(animationTime * 1.5 + i) * 30.0;
         double alpha = 0.08 + 0.04 * Math.sin(animationTime * 2.0 + i);
         Color[] nebulaColors = new Color[]{
            Color.rgb(100, 150, 255, alpha),
            Color.rgb(150, 100, 255, alpha),
            Color.rgb(100, 255, 200, alpha),
            Color.rgb(255, 150, 100, alpha),
            Color.rgb(255, 100, 150, alpha)
         };
         RadialGradient gradient = new RadialGradient(
            0.0, 0.0, x, y, size, false, CycleMethod.NO_CYCLE, new Stop[]{new Stop(0.0, nebulaColors[i]), new Stop(1.0, Color.TRANSPARENT)}
         );
         this.gameView.getGraphicsContext().setFill(gradient);
         this.gameView.getGraphicsContext().fillOval(x - size, y - size, size * 2.0, size * 2.0);
      }

      this.gameView.getGraphicsContext().restore();
   }

   private void renderStars(double animationTime) {
      this.gameView.getGraphicsContext().save();
      this.gameView.getGraphicsContext().setGlobalBlendMode(BlendMode.ADD);

      for (PreloaderView.Star star : this.stars) {
         star.update();
         double brightness = star.baseBrightness + 0.3 * Math.sin(animationTime * 3.0 + star.x * 0.1);
         brightness = Math.max(0.0, Math.min(1.0, brightness));
         this.gameView.getGraphicsContext().setFill(Color.rgb(200, 220, 255, brightness));
         this.gameView.getGraphicsContext().fillOval(star.x, star.y, star.size, star.size);
         if (star.size > 1) {
            double coreBrightness = Math.max(0.0, Math.min(1.0, brightness * 0.5));
            this.gameView.getGraphicsContext().setFill(Color.rgb(255, 255, 255, coreBrightness));
            this.gameView.getGraphicsContext().fillOval(star.x + 0.5, star.y + 0.5, star.size - 1, star.size - 1);
         }
      }

      this.gameView.getGraphicsContext().restore();
   }

   private void renderExplosions() {
      this.gameView.getGraphicsContext().save();
      this.gameView.getGraphicsContext().setGlobalBlendMode(BlendMode.ADD);

      for (PreloaderView.Explosion explosion : this.explosions) {
         explosion.update();
         explosion.render(this.gameView.getGraphicsContext());
      }

      this.gameView.getGraphicsContext().restore();
   }

   private void createTitleExplosion() {
      double centerX = this.gameView.getWidth() / 2.0;
      double centerY = 200.0;
      this.explosions.add(new PreloaderView.Explosion(centerX, centerY, 50, Color.rgb(100, 200, 255)));
      this.explosions.add(new PreloaderView.Explosion(centerX, centerY, 30, Color.rgb(255, 200, 100)));

      for (int i = 0; i < 30; i++) {
         double angle = (Math.PI * 2) * i / 30.0;
         double speed = 100.0 + RANDOM.nextDouble() * 100.0;
         double vx = Math.cos(angle) * speed;
         double vy = Math.sin(angle) * speed;
         this.particles.add(new PreloaderView.Particle(centerX, centerY, vx / 20.0, vy / 20.0, RANDOM.nextDouble() * 360.0));
      }
   }

   private void updateExplosions() {
      this.explosions.removeIf(explosion -> !explosion.isActive());
   }

   private void renderParticles(double animationTime) {
      double width = this.gameView.getWidth();
      double height = this.gameView.getHeight();
      this.gameView.getGraphicsContext().save();
      this.gameView.getGraphicsContext().setGlobalBlendMode(BlendMode.ADD);
      this.gameView.getGraphicsContext().setFont(Font.font("Courier New", FontWeight.NORMAL, 12.0));

      for (PreloaderView.Particle particle : this.particles) {
         particle.update();
         if (particle.x < 0.0 || particle.x > width) {
            particle.vx *= -1.0;
         }

         if (particle.y < 0.0 || particle.y > height) {
            particle.vy *= -1.0;
         }

         double opacity = 0.4 + Math.sin(animationTime * 2.0 + particle.hue * 0.01) * 0.3;
         double hue = (particle.hue + animationTime * 50.0) % 360.0;
         Color particleColor = Color.hsb(hue, 0.8, 1.0, opacity);
         this.gameView.getGraphicsContext().setFill(particleColor);
         char[] symbols = new char[]{'*', '$', 'c', 'C', '.', '+', '◆', '★'};
         char symbol = symbols[(int)(particle.x + particle.y) % symbols.length];
         this.gameView.getGraphicsContext().fillText(String.valueOf(symbol), particle.x, particle.y);
         this.gameView.getGraphicsContext().setFill(Color.rgb(255, 255, 255, opacity * 0.3));
         this.gameView.getGraphicsContext().fillOval(particle.x - 2.0, particle.y - 2.0, 4.0, 4.0);
      }

      this.gameView.getGraphicsContext().restore();
   }

   private void renderFloatingJokes(double animationTime) {
      this.gameView.getGraphicsContext().save();
      this.gameView.getGraphicsContext().setFont(Font.font("Courier New", FontWeight.BOLD, 16.0));

      for (PreloaderView.FloatingJoke joke : this.jokes) {
         double fadeInDuration = 0.8;
         double fadeOutDuration = 0.8;
         double alpha = 0.0;
         if (joke.age < fadeInDuration) {
            alpha = joke.age / fadeInDuration;
         } else if (joke.age > joke.lifetime - fadeOutDuration) {
            alpha = (joke.lifetime - joke.age) / fadeOutDuration;
         } else {
            alpha = 1.0;
         }

         alpha *= 0.25;
         double hue = (animationTime * 30.0 + joke.x * 0.1) % 360.0;
         Color jokeColor = Color.hsb(hue, 0.6, 0.9, alpha);
         this.gameView.getGraphicsContext().setFill(jokeColor);
         this.gameView.getGraphicsContext().fillText(joke.text, joke.x, joke.y);
         Color glowColor = Color.hsb(hue, 0.8, 1.0, alpha * 0.3);
         this.gameView.getGraphicsContext().setFill(glowColor);
         this.gameView.getGraphicsContext().fillText(joke.text, joke.x + 1.0, joke.y + 1.0);
      }

      this.gameView.getGraphicsContext().restore();
   }

   private void updateJokes(double animationTime) {
      if (animationTime > this.nextJokeTime && this.jokes.size() < 3) {
         if (this.jokeDeck.isEmpty()) {
            this.shuffleJokeDeck();
         }

         double x = 50.0 + RANDOM.nextDouble() * (this.gameView.getWidth() - 300.0);
         double y = 100.0 + RANDOM.nextDouble() * (this.gameView.getHeight() - 200.0);
         String text = this.jokeDeck.remove(0);
         double lifetime = 4.0 + RANDOM.nextDouble() * 3.0;
         this.jokes.add(new PreloaderView.FloatingJoke(x, y, text, lifetime));
         this.nextJokeTime = animationTime + 2.0 + RANDOM.nextDouble() * 3.0;
      }

      for (PreloaderView.FloatingJoke joke : this.jokes) {
         joke.update();
      }

      this.jokes.removeIf(jokex -> !jokex.isActive());
   }

   private void shuffleJokeDeck() {
      this.jokeDeck.clear();

      for (String joke : JOKE_TEXTS) {
         this.jokeDeck.add(joke);
      }

      for (int i = this.jokeDeck.size() - 1; i > 0; i--) {
         int j = RANDOM.nextInt(i + 1);
         String temp = this.jokeDeck.get(i);
         this.jokeDeck.set(i, this.jokeDeck.get(j));
         this.jokeDeck.set(j, temp);
      }
   }

   private void renderScanlines(double animationTime) {
      double width = this.gameView.getWidth();
      double height = this.gameView.getHeight();
      this.gameView.getGraphicsContext().setStroke(Color.rgb(255, 255, 255, 0.02));
      this.gameView.getGraphicsContext().setLineWidth(1.0);
      double offset = animationTime * 50.0 % 4.0;

      for (int i = 0; i < height; i += 4) {
         this.gameView.getGraphicsContext().strokeLine(0.0, i + offset, width, i + offset);
      }
   }

   private void renderAnimatedLogo(double animationTime) {
      double pulse = Math.sin(animationTime * 2.0) * 0.5 + 0.5;
      double colorCycle = animationTime * 0.5;
      int r = clampColor((int)(100.0 + Math.sin(colorCycle) * 155.0));
      int g = clampColor((int)(200.0 + Math.sin(colorCycle + 2.0) * 55.0));
      int b = 255;
      double centerX = this.gameView.getWidth() / 2.0;
      double startY = 200.0;
      double qvcFadeIn = Math.min(1.0, Math.max(0.0, animationTime));
      if (qvcFadeIn > 0.0) {
         this.draw3DText(centerX, startY, r, g, b, qvcFadeIn, pulse, animationTime);
      }

      double survivorsFadeIn = Math.min(1.0, Math.max(0.0, animationTime - 1.0));
      if (survivorsFadeIn > 0.0) {
         this.gameView.getGraphicsContext().setFont(Font.font("Courier New", FontWeight.BOLD, 40.0));
         String survivorsText = "SURVIVORS";
         double survivorsWidth = this.gameView.getGraphicsContext().getFont().getSize() * survivorsText.length() * 0.6;
         double survivorsX = centerX - survivorsWidth / 2.0;
         double survivorsY = startY + 100.0;
         this.gameView.getGraphicsContext().setFill(Color.rgb(r, g, b, pulse * 0.3 * survivorsFadeIn));
         this.gameView.getGraphicsContext().fillText(survivorsText, survivorsX + 2.0, survivorsY + 2.0);
         this.gameView.getGraphicsContext().setFill(Color.rgb(255, 255, 255, 0.9 * survivorsFadeIn));
         this.gameView.getGraphicsContext().fillText(survivorsText, survivorsX, survivorsY);
      }
   }

   private void draw3DText(double centerX, double centerY, int r, int g, int b, double fadeIn, double pulse, double animationTime) {
      this.gameView.getGraphicsContext().save();
      String text = "QVC";
      double fontSize = 120.0;
      this.gameView.getGraphicsContext().setFont(Font.font("Courier New", FontWeight.BOLD, fontSize));
      double rotationY = Math.sin(animationTime * 0.8) * 15.0;
      double rotationX = Math.cos(animationTime * 0.6) * 10.0;
      double textWidth = fontSize * text.length() * 0.6;
      int layers = 25;
      double depthScale = 2.0;
      this.gameView.getGraphicsContext().translate(centerX, centerY);

      for (int layer = layers; layer > 0; layer--) {
         double depth = (layers - layer) * depthScale;
         double perspectiveScale = 1.0 - depth * 0.008;
         double offsetX = depth * Math.sin(Math.toRadians(rotationY)) * 0.5;
         double offsetY = depth * Math.sin(Math.toRadians(rotationX)) * 0.3;
         double layerBrightness = (double)layer / layers;
         double darkness = 0.2 + layerBrightness * 0.8;
         this.gameView.getGraphicsContext().save();
         this.gameView.getGraphicsContext().scale(perspectiveScale, perspectiveScale);
         int layerR = (int)(r * darkness);
         int layerG = (int)(g * darkness);
         int layerB = (int)(b * darkness);
         double layerOpacity = fadeIn * 0.8 * layerBrightness;
         if (layer == layers) {
            layerOpacity *= 0.3;
         }

         this.gameView.getGraphicsContext().setFill(Color.rgb(layerR, layerG, layerB, layerOpacity));
         this.gameView.getGraphicsContext().fillText(text, -textWidth / 2.0 + offsetX, offsetY);
         this.gameView.getGraphicsContext().restore();
      }

      this.gameView.getGraphicsContext().save();
      this.gameView.getGraphicsContext().scale(1.0, 1.0);

      for (int glowLayer = 3; glowLayer > 0; glowLayer--) {
         double glowSize = glowLayer * 1.5;
         double glowOpacity = pulse * 0.2 * fadeIn / glowLayer;
         this.gameView.getGraphicsContext().setFill(Color.rgb(r, g, b, glowOpacity));
         this.gameView.getGraphicsContext().fillText(text, -textWidth / 2.0 + glowSize, glowSize);
         this.gameView.getGraphicsContext().fillText(text, -textWidth / 2.0 - glowSize, -glowSize);
      }

      this.gameView.getGraphicsContext().setFill(Color.rgb(255, 255, 255, 0.9 * fadeIn * pulse));
      this.gameView.getGraphicsContext().fillText(text, -textWidth / 2.0, 0.0);
      this.gameView.getGraphicsContext().setFill(Color.rgb(r, g, b, 0.95 * fadeIn));
      this.gameView.getGraphicsContext().fillText(text, -textWidth / 2.0, 0.0);
      this.gameView.getGraphicsContext().restore();
      this.gameView.getGraphicsContext().restore();
   }

   private void renderAnimatedInstructions(double animationTime) {
      double fadeDelay = 4.0;
      double fadeIn = Math.min(1.0, Math.max(0.0, (animationTime - fadeDelay) * 0.4));
      if (fadeIn > 0.0) {
         double centerX = this.gameView.getWidth() / 2.0;
         double y = 550.0;
         this.gameView.getGraphicsContext().setFont(Font.font("Courier New", FontWeight.NORMAL, 16.0));
         this.gameView.getGraphicsContext().setFill(Color.rgb(255, 255, 255, fadeIn));
         String line1 = "Black Friday is coming...";
         double line1Width = this.gameView.getGraphicsContext().getFont().getSize() * line1.length() * 0.6;
         this.gameView.getGraphicsContext().fillText(line1, centerX - line1Width / 2.0, y);
         y += 30.0;
         String line2 = "Can you survive the rush?";
         double line2Width = this.gameView.getGraphicsContext().getFont().getSize() * line2.length() * 0.6;
         this.gameView.getGraphicsContext().fillText(line2, centerX - line2Width / 2.0, y);
         y += 60.0;
         double blinkSpeed = Math.sin(animationTime * 3.0) * 0.5 + 0.5;
         this.gameView.getGraphicsContext().setFill(Color.rgb(100, 200, 255, fadeIn * (0.5 + blinkSpeed * 0.5)));
         this.gameView.getGraphicsContext().setFont(Font.font("Courier New", FontWeight.BOLD, 18.0));
         String instruction = "Press SPACE to begin";
         double instructionWidth = this.gameView.getGraphicsContext().getFont().getSize() * instruction.length() * 0.6;
         this.gameView.getGraphicsContext().fillText(instruction, centerX - instructionWidth / 2.0, y);
         y += 30.0;
         this.gameView.getGraphicsContext().setFill(Color.rgb(150, 200, 255, fadeIn * (0.4 + blinkSpeed * 0.3)));
         this.gameView.getGraphicsContext().setFont(Font.font("Courier New", FontWeight.NORMAL, 14.0));
         String settingsHint = "Press S for Settings";
         double settingsWidth = this.gameView.getGraphicsContext().getFont().getSize() * settingsHint.length() * 0.6;
         this.gameView.getGraphicsContext().fillText(settingsHint, centerX - settingsWidth / 2.0, y);
         y += 70.0;
         this.gameView.getGraphicsContext().setFont(Font.font("Courier New", FontWeight.NORMAL, 14.0));
         this.gameView.getGraphicsContext().setFill(Color.rgb(150, 150, 150, fadeIn * 0.7));
         String versionText = "Version " + VersionUtil.getVersion();
         double versionWidth = this.gameView.getGraphicsContext().getFont().getSize() * versionText.length() * 0.6;
         this.gameView.getGraphicsContext().fillText(versionText, centerX - versionWidth / 2.0, y);
      }
   }

   private static int clampColor(int value) {
      return Math.max(0, Math.min(255, value));
   }

   private static class Explosion {
      double x;
      double y;
      int particleCount;
      Color color;
      List<PreloaderView.ExplosionParticle> particles;
      double lifetime;
      double maxLifetime;

      Explosion(double x, double y, int particleCount, Color color) {
         this.x = x;
         this.y = y;
         this.particleCount = particleCount;
         this.color = color;
         this.particles = new ArrayList<>();
         this.lifetime = 1.0;
         this.maxLifetime = 1.0;

         for (int i = 0; i < particleCount; i++) {
            double angle = (Math.PI * 2) * i / particleCount;
            double speed = 50.0 + Math.random() * 100.0;
            this.particles.add(new PreloaderView.ExplosionParticle(x, y, Math.cos(angle) * speed, Math.sin(angle) * speed));
         }
      }

      void update() {
         this.lifetime -= 0.016;

         for (PreloaderView.ExplosionParticle p : this.particles) {
            p.update();
         }
      }

      void render(GraphicsContext gc) {
         double alpha = Math.max(0.0, this.lifetime / this.maxLifetime);

         for (PreloaderView.ExplosionParticle p : this.particles) {
            gc.setFill(Color.rgb((int)(this.color.getRed() * 255.0), (int)(this.color.getGreen() * 255.0), (int)(this.color.getBlue() * 255.0), alpha));
            gc.fillOval(p.x - 2.0, p.y - 2.0, 4.0, 4.0);
            gc.setFill(Color.rgb(255, 255, 255, alpha * 0.5));
            gc.fillOval(p.x - 1.0, p.y - 1.0, 2.0, 2.0);
         }
      }

      boolean isActive() {
         return this.lifetime > 0.0;
      }
   }

   private static class ExplosionParticle {
      double x;
      double y;
      double vx;
      double vy;

      ExplosionParticle(double x, double y, double vx, double vy) {
         this.x = x;
         this.y = y;
         this.vx = vx;
         this.vy = vy;
      }

      void update() {
         this.x = this.x + this.vx * 0.016;
         this.y = this.y + this.vy * 0.016;
         this.vx *= 0.98;
         this.vy *= 0.98;
         this.vy += 3.2;
      }
   }

   private static class FloatingJoke {
      double x;
      double y;
      String text;
      double lifetime;
      double age;

      FloatingJoke(double x, double y, String text, double lifetime) {
         this.x = x;
         this.y = y;
         this.text = text;
         this.lifetime = lifetime;
         this.age = 0.0;
      }

      void update() {
         this.age += 0.016;
         this.y -= 0.16;
      }

      boolean isActive() {
         return this.age < this.lifetime;
      }
   }

   private static class Particle {
      double x;
      double y;
      double vx;
      double vy;
      double hue;

      Particle(double x, double y, double vx, double vy, double hue) {
         this.x = x;
         this.y = y;
         this.vx = vx * 20.0;
         this.vy = vy * 20.0;
         this.hue = hue;
      }

      void update() {
         this.x = this.x + this.vx * 0.016;
         this.y = this.y + this.vy * 0.016;
      }
   }

   private static class Star {
      double x;
      double y;
      int size;
      double baseBrightness;

      Star(double x, double y, int size, double baseBrightness) {
         this.x = x;
         this.y = y;
         this.size = size;
         this.baseBrightness = baseBrightness;
      }

      void update() {
      }
   }
}

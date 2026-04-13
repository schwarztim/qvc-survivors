package com.qvc.survivors.view;

import com.qvc.survivors.engine.Camera;
import com.qvc.survivors.util.ObjectPool;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;

public class ParticleSystem {
   private final List<Particle> particles = new ArrayList<>();
   private final int maxParticles;
   private final ObjectPool<Particle> particlePool;

   public ParticleSystem(int maxParticles) {
      this.maxParticles = maxParticles;
      this.particlePool = new ObjectPool<>(() -> new Particle(0.0, 0.0, 0.0, 0.0, 0.0, Color.WHITE, 1.0, Particle.ParticleType.CIRCLE), maxParticles);
   }

   public void update(double deltaTime) {
      for (int i = this.particles.size() - 1; i >= 0; i--) {
         Particle particle = this.particles.get(i);
         particle.update(deltaTime);
         if (!particle.isActive()) {
            this.particlePool.free(particle);
            this.particles.remove(i);
         }
      }
   }

   public void createExplosion(double x, double y, Color color, int particleCount) {
      for (int i = 0; i < particleCount; i++) {
         double angle = (Math.PI * 2) * i / particleCount;
         double speed = 30.0 + Math.random() * 40.0;
         double velocityX = Math.cos(angle) * speed;
         double velocityY = Math.sin(angle) * speed;
         double lifetime = 0.5 + Math.random() * 0.5;
         double size = 2.0 + Math.random() * 3.0;
         Particle particle = this.particlePool.obtain();
         particle.reset(x, y, velocityX, velocityY, lifetime, color, size, Particle.ParticleType.CIRCLE);
         this.addParticle(particle);
      }

      for (int i = 0; i < particleCount / 2; i++) {
         double angle = Math.random() * Math.PI * 2.0;
         double speed = 10.0 + Math.random() * 20.0;
         double velocityX = Math.cos(angle) * speed;
         double velocityY = Math.sin(angle) * speed;
         double lifetime = 0.3 + Math.random() * 0.4;
         double size = 3.0 + Math.random() * 5.0;
         Particle particle = this.particlePool.obtain();
         particle.reset(x, y, velocityX, velocityY, lifetime, color.brighter(), size, Particle.ParticleType.STAR);
         this.addParticle(particle);
      }
   }

   public void createImpact(double x, double y, Color color) {
      for (int i = 0; i < 8; i++) {
         double angle = (Math.PI * 2) * i / 8.0;
         double speed = 20.0 + Math.random() * 20.0;
         double velocityX = Math.cos(angle) * speed;
         double velocityY = Math.sin(angle) * speed;
         double lifetime = 0.2 + Math.random() * 0.2;
         double size = 1.5 + Math.random() * 2.0;
         Particle particle = this.particlePool.obtain();
         particle.reset(x, y, velocityX, velocityY, lifetime, color, size, Particle.ParticleType.SPARK);
         this.addParticle(particle);
      }
   }

   public void createTrail(double x, double y, Color color) {
      double velocityX = (Math.random() - 0.5) * 5.0;
      double velocityY = (Math.random() - 0.5) * 5.0;
      double lifetime = 0.3 + Math.random() * 0.2;
      double size = 1.0 + Math.random() * 2.0;
      Particle particle = this.particlePool.obtain();
      particle.reset(x, y, velocityX, velocityY, lifetime, color, size, Particle.ParticleType.CIRCLE);
      this.addParticle(particle);
   }

   public void createCollectionEffect(double x, double y, Color color) {
      for (int i = 0; i < 12; i++) {
         double angle = (Math.PI * 2) * i / 12.0;
         double speed = 15.0 + Math.random() * 15.0;
         double velocityX = Math.cos(angle) * speed;
         double velocityY = Math.sin(angle) * speed;
         double lifetime = 0.4 + Math.random() * 0.3;
         double size = 2.0 + Math.random() * 2.0;
         Particle particle = this.particlePool.obtain();
         particle.reset(x, y, velocityX, velocityY, lifetime, color, size, Particle.ParticleType.STAR);
         this.addParticle(particle);
      }
   }

   public void createLevelUpEffect(double x, double y) {
      for (int i = 0; i < 30; i++) {
         double angle = (Math.PI * 2) * i / 30.0;
         double speed = 40.0 + Math.random() * 30.0;
         double velocityX = Math.cos(angle) * speed;
         double velocityY = Math.sin(angle) * speed;
         double lifetime = 0.8 + Math.random() * 0.5;
         double size = 3.0 + Math.random() * 4.0;
         Color particleColor = Color.hsb(i * 12.0 % 360.0, 0.8, 1.0);
         Particle particle = this.particlePool.obtain();
         particle.reset(x, y, velocityX, velocityY, lifetime, particleColor, size, Particle.ParticleType.STAR);
         particle.setGravity(-20.0);
         this.addParticle(particle);
      }

      for (int i = 0; i < 3; i++) {
         double lifetime = 1.0 + i * 0.2;
         double size = 20 + i * 10;
         Color ringColor = Color.rgb(100, 255, 200, 0.5);
         Particle particle = this.particlePool.obtain();
         particle.reset(x, y, 0.0, 0.0, lifetime, ringColor, size, Particle.ParticleType.RING);
         this.addParticle(particle);
      }
   }

   public void createDroneTrail(double x, double y) {
      if (Math.random() < 0.3) {
         double velocityX = (Math.random() - 0.5) * 3.0;
         double velocityY = (Math.random() - 0.5) * 3.0;
         double lifetime = 0.4;
         double size = 1.5;
         Color color = Color.LIGHTGREEN;
         Particle particle = this.particlePool.obtain();
         particle.reset(x, y, velocityX, velocityY, lifetime, color, size, Particle.ParticleType.CIRCLE);
         this.addParticle(particle);
      }
   }

   private void addParticle(Particle particle) {
      if (this.particles.size() < this.maxParticles) {
         this.particles.add(particle);
      } else {
         this.particlePool.free(particle);
      }
   }

   public void render(GraphicsContext gc, Camera camera) {
      gc.save();
      gc.setGlobalBlendMode(BlendMode.ADD);

      for (Particle particle : this.particles) {
         if (particle.isActive()) {
            double alpha = particle.getAlpha();
            Color renderColor = Color.rgb(
               (int)(particle.getColor().getRed() * 255.0), (int)(particle.getColor().getGreen() * 255.0), (int)(particle.getColor().getBlue() * 255.0), alpha
            );
            double screenX = camera.worldToScreenX(particle.getX());
            double screenY = camera.worldToScreenY(particle.getY());
            double size = particle.getSize();
            gc.setFill(renderColor);
            switch (particle.getType()) {
               case CIRCLE:
                  gc.fillOval(screenX - size / 2.0, screenY - size / 2.0, size, size);
                  break;
               case SQUARE:
                  gc.save();
                  gc.translate(screenX, screenY);
                  gc.rotate(Math.toDegrees(particle.getRotation()));
                  gc.fillRect(-size / 2.0, -size / 2.0, size, size);
                  gc.restore();
                  break;
               case STAR:
                  this.drawStar(gc, screenX, screenY, size, particle.getRotation());
                  break;
               case SPARK:
                  gc.fillRect(screenX - size / 2.0, screenY - size / 2.0, size, size * 3.0);
                  gc.fillRect(screenX - size * 1.5, screenY - size / 2.0, size * 3.0, size);
                  break;
               case RING:
                  gc.setStroke(renderColor);
                  gc.setLineWidth(2.0);
                  gc.strokeOval(screenX - size / 2.0, screenY - size / 2.0, size, size);
                  break;
               case SMOKE:
                  gc.fillOval(screenX - size / 2.0, screenY - size / 2.0, size, size);
            }
         }
      }

      gc.restore();
   }

   private void drawStar(GraphicsContext gc, double centerX, double centerY, double size, double rotation) {
      int points = 5;
      double[] xPoints = new double[points * 2];
      double[] yPoints = new double[points * 2];
      double innerRadius = size * 0.4;

      for (int i = 0; i < points * 2; i++) {
         double angle = rotation + Math.PI * i / points;
         double radius = i % 2 == 0 ? size : innerRadius;
         xPoints[i] = centerX + Math.cos(angle) * radius;
         yPoints[i] = centerY + Math.sin(angle) * radius;
      }

      gc.fillPolygon(xPoints, yPoints, points * 2);
   }

   public void clear() {
      this.particles.clear();
   }
}

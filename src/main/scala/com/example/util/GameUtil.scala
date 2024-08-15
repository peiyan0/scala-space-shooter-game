package com.example.util

import com.example.model.{LaserModel, LeaderboardEntry, RandomEnemy, VerticalEnemy}
import scalafx.Includes.handle
import scalafx.animation.{KeyFrame, Timeline}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.Pane
import scalafx.scene.media.AudioClip
import scalafx.util.Duration

object GameUtil {
  private val laserSound = new AudioClip(getClass.getResource("/sounds/laser.mp3").toString) {
    volume = 0.2
  }

  def formExplosion(obj: ImageView): ImageView = {
    val explosionImage = new ImageView(new Image(getClass.getResource("/images/effect/explosion.png").toString))
    explosionImage.scaleX = 0.15
    explosionImage.scaleY = 0.15
    explosionImage.setLayoutX(obj.getLayoutX + obj.getBoundsInParent.getWidth / 2 - explosionImage.getImage.getWidth / 2)
    explosionImage.setLayoutY(obj.getLayoutY + obj.getBoundsInParent.getHeight / 2 - explosionImage.getImage.getHeight / 2)
    explosionImage
  }

  def fireLaser(spaceship: ImageView): Seq[LaserModel] = {
    val laser1 = new LaserModel("/images/effect/laser.png")
    val laser2 = new LaserModel("/images/effect/laser.png")

    laserSound.play()
    laser1.initialize(20, 60, spaceship.layoutX.value + 10, spaceship.layoutY.value)
    laser2.initialize(20, 60, spaceship.layoutX.value + spaceship.boundsInParent.value.getWidth - 15, spaceship.layoutY.value)

    Seq(laser1, laser2)
  }
  // create and initialize enemy
  def spawnNormalEnemy(): VerticalEnemy = {
    val enemy = new VerticalEnemy("/images/enemy/enemy.png")
    enemy.initialize(100)
    enemy
  }

  def spawnRandomEnemy(): RandomEnemy = {
    val randomEnemy = new RandomEnemy("/images/enemy/random_enemy.png")
    randomEnemy.initialize(80)
    randomEnemy
  }

  def createExplosionTimeline(gamePane: Pane, imageToRemove: Seq[ImageView]): Timeline = {
     new Timeline {
      cycleCount = 1
      keyFrames = Seq(
        KeyFrame(Duration(100), onFinished = handle {
          imageToRemove.foreach(imageView => gamePane.children.remove(imageView))
        })
      )
    }
  }

  // add score to leaderboard
  def addScoreToLeaderboard(username: String, difficulty: String, score: Int): Unit = {
    val leaderboard = LeaderboardUtil.loadLeaderboard("leaderboard.txt")
    val entry = LeaderboardEntry(username, difficulty, score)
    leaderboard.addEntry(entry)
    LeaderboardUtil.saveLeaderboard(leaderboard, "leaderboard.txt")
  }

}

package com.example.util

import com.example.model.{EnemyModel, LaserModel, LeaderboardEntry, RandomEnemy, VerticalEnemy}
import scalafx.Includes.handle
import scalafx.animation.{KeyFrame, Timeline}
import scalafx.beans.property.StringProperty
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.Pane
import scalafx.scene.media.AudioClip
import scalafx.stage.Stage
import scalafx.util.Duration
import scala.collection.mutable.ListBuffer

object GameUtil {
  private val laserSound = new AudioClip(getClass.getResource("/sounds/laser.mp3").toString) {volume = 0.2}
  // User data
  var username: String = _
  var difficulty: String = _
  val selectedSpaceship = new StringProperty(this, "selectedSpaceship", "")
  // others
  var stage: Stage = _

  private def formExplosion(obj: ImageView): ImageView = {
    val explosionImage = new ImageView(new Image(getClass.getResource("/images/effect/explosion.png").toString))
    explosionImage.scaleX = 0.15
    explosionImage.scaleY = 0.15
    explosionImage.setLayoutX(obj.getLayoutX + obj.getBoundsInParent.getWidth / 2 - explosionImage.getImage.getWidth / 2)
    explosionImage.setLayoutY(obj.getLayoutY + obj.getBoundsInParent.getHeight / 2 - explosionImage.getImage.getHeight / 2)
    explosionImage
  }

  // Create and initialize enemy based on type
  private def spawnEnemy(enemyType: String): EnemyModel = {
    val (imagePath, initValue) = enemyType match {
      case "Normal" => ("/images/enemy/enemy.png", 100)
      case "Random" => ("/images/enemy/random_enemy.png", 80)
    }
    val enemy = enemyType match {
      case "Normal" => new VerticalEnemy(imagePath)
      case "Random" => new RandomEnemy(imagePath)
    }
    enemy.initialize(initValue)
    enemy
  }

  private def createExplosionTimeline(gamePane: Pane, imageToRemove: Seq[ImageView]): Timeline = {
    new Timeline {
      cycleCount = 1
      keyFrames = Seq(
        KeyFrame(Duration(100), onFinished = handle {
          imageToRemove.foreach(imageView => gamePane.children.remove(imageView))
        })
      )
    }
  }

  // Handle enemy spawning logic based on difficulty
  def spawnEnemies(difficulty: String, gamePane: Pane, enemies: ListBuffer[EnemyModel], totalSpawned: Int): (ListBuffer[EnemyModel], Int) = {
    val enemyTypes = if (difficulty == "HARD") Seq("Normal", "Random") else Seq("Normal")
    enemyTypes.foreach { enemyType =>
      val enemy = spawnEnemy(enemyType)
      enemies += enemy
      gamePane.children.add(enemy.imageView)
    }
    (enemies, totalSpawned + enemyTypes.size)
  }

  // add score to leaderboard
  def addScoreToLeaderboard(username: String, difficulty: String, score: Int): Unit = {
    val leaderboard = LeaderboardUtil.loadLeaderboard("leaderboard.txt")
    val entry = LeaderboardEntry(username, difficulty, score)
    leaderboard.addEntry(entry)
    LeaderboardUtil.saveLeaderboard(leaderboard, "leaderboard.txt")
  }

  // Handle laser firing logic
  def fireLaser(spaceship: ImageView, gamePane: Pane, lasers: ListBuffer[LaserModel]): ListBuffer[LaserModel] = {
    val laser1 = new LaserModel("/images/effect/laser.png")
    val laser2 = new LaserModel("/images/effect/laser.png")
    laserSound.play()
    laser1.initialize(20, 60, spaceship.layoutX.value + 10, spaceship.layoutY.value)
    laser2.initialize(20, 60, spaceship.layoutX.value + spaceship.boundsInParent.value.getWidth - 15, spaceship.layoutY.value)
    lasers += laser1
    lasers += laser2
    gamePane.children.addAll(laser1.imageView, laser2.imageView)
    lasers
  }

  // Update enemy positions and remove off-screen enemies
  def updateEnemies(gamePane: Pane, enemies: ListBuffer[EnemyModel]): ListBuffer[EnemyModel] = {
    enemies.foreach(_.move())
    val remainingEnemies = enemies.filter(_.imageView.layoutY.value < gamePane.height.value)
    gamePane.children.removeIf(node =>
      enemies.exists(enemy => node == enemy.imageView && enemy.imageView.layoutY.value >= gamePane.height.value)
    )
    remainingEnemies
  }

  // Check for collisions between lasers, enemies, and the spaceship
  def checkCollisions(lasers: ListBuffer[LaserModel], enemies: ListBuffer[EnemyModel], spaceshipImageView: ImageView, gamePane: Pane, score: Int, explosionSound: AudioClip)
  : (ListBuffer[LaserModel], ListBuffer[EnemyModel], Int) = {
    var newScore = score
    // Handle laser-enemy collisions
    lasers.foreach { laser =>
      enemies.foreach { enemy =>
        if (enemy.imageView.boundsInParent.value.intersects(laser.imageView.boundsInParent.value)) {
          newScore += 10
          val explosionImage = formExplosion(laser.imageView)
          gamePane.children.add(explosionImage)
          explosionSound.play()

          enemies -= enemy
          lasers -= laser
          val timer = createExplosionTimeline(gamePane, Seq(explosionImage, laser.imageView, enemy.imageView))
          timer.play()
        }
      }
    }
    // Handle spaceship-enemy collisions
    enemies.foreach { enemy =>
      if (spaceshipImageView.boundsInParent.value.intersects(enemy.imageView.boundsInParent.value)) {
        newScore -= 10
        val explosionImage = formExplosion(spaceshipImageView)
        gamePane.children.add(explosionImage)

        explosionSound.play()
        enemies -= enemy
        val timer = createExplosionTimeline(gamePane, Seq(explosionImage, enemy.imageView))
        timer.play()
      }
    }
    (lasers, enemies, newScore)
  }
}

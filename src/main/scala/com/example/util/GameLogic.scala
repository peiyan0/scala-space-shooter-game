package com.example.util

import com.example.model.{EnemyModel, LaserModel}
import scalafx.Includes.jfxDoubleProperty2sfx
import scalafx.animation.{AnimationTimer, KeyFrame, Timeline}
import scalafx.beans.property.StringProperty
import scalafx.scene.Node
import scalafx.scene.image.ImageView
import scalafx.scene.layout.Pane
import scalafx.scene.media.AudioClip
import scalafx.stage.Stage
import scalafx.util.Duration

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.DurationDouble

object GameLogic {
  // Timer
  private var gameRunning = false
  private var animationTimer: AnimationTimer = _
  private var gameTimeline: Timeline = _
  private var countdownTimer: AnimationTimer = _
  // Object intervals
  private var laserInterval = 0.25.second
  private var enemySpawnInterval = 0.1.second
  private var lastLaserTime = 0L
  private var lastEnemySpawnTime = 0L
  private var remainingTime = 30
  private var lastUpdateTime = 0L
  // Game objects
  private var lasers: ListBuffer[LaserModel] = ListBuffer()
  private var enemies: ListBuffer[EnemyModel] = ListBuffer()
  private var totalSpawned: Int = 0
  // Objects sounds
  private val explosionSound = new AudioClip(getClass.getResource("/sounds/explosion.mp3").toString) {
    volume = 0.2
  }
  // User data
  private var score = 0
  var username: String = _
  var difficulty: String = _
  val selectedSpaceship = new StringProperty(this, "selectedSpaceship", "")
  // others
  var stage: Stage = _


  // ----------GAME LOGIC----------

  // Handle laser firing logic
  def fireLaser(spaceshipImageView: ImageView, gamePane: Pane, lasers: ListBuffer[LaserModel]): ListBuffer[LaserModel] = {
    val newLasers = GameUtil.fireLaser(spaceshipImageView)
    newLasers.foreach { laser =>
      gamePane.children.add(laser.imageView)
    }
    lasers ++= newLasers
    lasers
  }

  // Handle enemy spawning logic
  def spawnEnemies(difficulty: String, gamePane: Pane, enemies: ListBuffer[EnemyModel], totalSpawned: Int): (ListBuffer[EnemyModel], Int) = {
    val enemyTypes = if (difficulty == "HARD") Seq("Normal", "Random") else Seq("Normal")
    enemyTypes.foreach { enemyType =>
      val enemy = GameUtil.spawnEnemy(enemyType)
      enemies += enemy
      gamePane.children.add(enemy.imageView)
    }
    (enemies, totalSpawned + enemyTypes.size)
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
          val explosionImage = GameUtil.formExplosion(laser.imageView)
          gamePane.children.add(explosionImage)
          explosionSound.play()

          enemies -= enemy
          lasers -= laser
          val timer = GameUtil.createExplosionTimeline(gamePane, Seq(explosionImage, laser.imageView, enemy.imageView))
          timer.play()
        }
      }
    }
    // Handle spaceship-enemy collisions
    enemies.foreach { enemy =>
      if (spaceshipImageView.boundsInParent.value.intersects(enemy.imageView.boundsInParent.value)) {
        newScore -= 10
        val explosionImage = GameUtil.formExplosion(spaceshipImageView)
        gamePane.children.add(explosionImage)

        explosionSound.play()
        enemies -= enemy
        val timer = GameUtil.createExplosionTimeline(gamePane, Seq(explosionImage, enemy.imageView))
        timer.play()
      }
    }
    (lasers, enemies, newScore)
  }

}

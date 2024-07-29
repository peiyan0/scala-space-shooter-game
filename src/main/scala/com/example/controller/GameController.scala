package com.example.controller

import scalafx.beans.property.StringProperty
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.MouseEvent
import scalafx.scene.control.Label
import scalafx.scene.layout.Pane
import scalafx.stage.Stage
import scalafx.animation.{AnimationTimer, KeyFrame, Timeline}
import scalafx.util.Duration
import scalafxml.core.macros.sfxml
import scalafx.Includes._
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.DurationDouble

import com.example.model.{EnemyModel, LaserModel}

@sfxml
class GameController(private val gamePane: Pane,
                     private val spaceshipImageView: ImageView,
                     private val countdownLabel: Label,
                     private val scoreLabel: Label) {

  private var score = 0
  private var gameRunning = false
  private var lasers: ListBuffer[LaserModel] = ListBuffer()
  private var enemies: ListBuffer[EnemyModel] = ListBuffer()
  private val laserInterval = 0.5.second
  private val enemySpawnInterval = 0.5.second
  private var lastLaserTime = 0L
  private var lastEnemySpawnTime = 0L
  private var remainingTime = 30
  private var lastUpdateTime = 0L
  var stage: Stage = _
  val selectedSpaceship = new StringProperty(this, "selectedSpaceship", "")

  def initialize(): Unit = {
    selectedSpaceship.onChange((_, _, newValue) => updateSpaceshipImage(newValue))
    gamePane.onMouseMoved = handleSpaceshipMovement _
    startGame()
  }

  private def updateSpaceshipImage(spaceship: String): Unit = {
    val image = new Image(getClass.getResourceAsStream(spaceship))
    spaceshipImageView.image = image
  }

  private def handleSpaceshipMovement(event: MouseEvent): Unit = {
    spaceshipImageView.layoutX = event.sceneX - spaceshipImageView.boundsInParent.value.getWidth / 2
  }

  private def startGame(): Unit = {
    gameRunning = true
    countdownLabel.text = s"Time: $remainingTime s"
    val gameDuration = Duration(30000)

    val gameTimeline = new Timeline {
      keyFrames = Seq(
        KeyFrame(gameDuration, onFinished = _ => endGame())
      )
    }

    val animationTimer = AnimationTimer { now =>
      if (gameRunning) {
        if (now - lastLaserTime > laserInterval.toNanos) {
          fireLaser()
          lastLaserTime = now
        }
        if (now - lastEnemySpawnTime > enemySpawnInterval.toNanos) {
          spawnEnemy()
          lastEnemySpawnTime = now
        }
        updateLasers()
        updateEnemies()
        checkCollisions()
      }
    }

    gameTimeline.play()
    animationTimer.start()

    val countdownTimer = AnimationTimer { now =>
      if (gameRunning && now - lastUpdateTime > 1.second.toNanos) {
        remainingTime -= 1
        countdownLabel.text = s"Time: $remainingTime s"
        lastUpdateTime = now
        if (remainingTime <= 0) {
          endGame()
        }
      }
    }
    countdownTimer.start()
  }

  private def fireLaser(): Unit = {
    val laser1 = new LaserModel("/images/effect/laser.png")
    val laser2 = new LaserModel("/images/effect/laser.png")

    laser1.initialize(20, 60, spaceshipImageView.layoutX.value + 10, spaceshipImageView.layoutY.value)
    laser2.initialize(20, 60, spaceshipImageView.layoutX.value + spaceshipImageView.boundsInParent.value.getWidth - 15, spaceshipImageView.layoutY.value)

    lasers += laser1
    lasers += laser2
    gamePane.children.addAll(laser1.imageView, laser2.imageView)
  }

  private def spawnEnemy(): Unit = {
    val enemy = new EnemyModel("/images/enemy/enemy.png")
    val maxX = gamePane.width.value - 100
    val randomX = math.random() * maxX
    enemy.initialize(100, randomX, 100)

    enemies += enemy
    gamePane.children.add(enemy.imageView)
  }

  private def updateLasers(): Unit = {
    lasers.foreach(_.move())
    lasers = lasers.filter(_.imageView.layoutY.value > 0)
    gamePane.children.removeIf(laser => laser.layoutY.value <= 0)
  }

  private def updateEnemies(): Unit = {
    enemies.foreach(_.move())
    enemies = enemies.filter(_.imageView.layoutY.value < gamePane.height.value)
    gamePane.children.removeIf(node =>
      enemies.exists(enemy => node == enemy.imageView && enemy.imageView.layoutY.value >= gamePane.height.value)
    )
  }

  private def checkCollisions(): Unit = {
    lasers.foreach { laser =>
      enemies.foreach { enemy =>
        if (laser.imageView.boundsInParent.value.intersects(enemy.imageView.boundsInParent.value)) {
          score += 10
          scoreLabel.text = s"Score: $score"
          lasers -= laser
          enemies -= enemy
          gamePane.children.removeAll(laser.imageView, enemy.imageView)
        }
      }
    }
  }

  private def endGame(): Unit = {
    gameRunning = false
    println(s"Game over! Your score: $score")
    countdownLabel.text = s"Game over! Your score: $score"
  }

  initialize()
}

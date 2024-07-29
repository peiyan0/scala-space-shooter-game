package com.example.controller

import scalafx.beans.property.StringProperty
import scalafx.scene.image.Image
import scalafx.scene.image.ImageView
import scalafx.scene.input.MouseEvent
import scalafx.scene.control.Label
import scalafx.scene.layout.Pane
import scalafx.stage.Stage
import scalafx.animation.{AnimationTimer, KeyFrame, Timeline}
import scalafx.util.Duration
import scalafxml.core.macros.sfxml
import scalafx.Includes._
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.{DurationDouble, DurationInt}

@sfxml
class GameController(private val gamePane: Pane,
                     private val spaceshipImageView: ImageView,
                     private val countdownLabel: Label,
                     private val scoreLabel: Label) {

  private var score = 0
  private var gameRunning = false
  private var lasers: ListBuffer[ImageView] = ListBuffer()
  private var enemies: ListBuffer[ImageView] = ListBuffer()
  private val laserInterval = 0.5.second
  private val enemySpawnInterval = 1.second
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
    val laser1 = new ImageView(new Image(getClass.getResourceAsStream("/images/effect/laser.png")))
    val laser2 = new ImageView(new Image(getClass.getResourceAsStream("/images/effect/laser.png")))

    // Adjusting laser size
    laser1.fitWidth = 20
    laser1.fitHeight = 60
    laser2.fitWidth = 20
    laser2.fitHeight = 60

    // Positioning the lasers
    laser1.layoutX = spaceshipImageView.layoutX.value + 10
    laser2.layoutX = spaceshipImageView.layoutX.value + spaceshipImageView.boundsInParent.value.getWidth - 15
    laser1.layoutY = spaceshipImageView.layoutY.value
    laser2.layoutY = spaceshipImageView.layoutY.value

    lasers += laser1
    lasers += laser2
    gamePane.children.addAll(laser1, laser2)
  }

  private def spawnEnemy(): Unit = {
    val enemy = new ImageView(new Image(getClass.getResourceAsStream("/images/enemy/enemy.png")))

    enemy.fitWidth = 100
    enemy.preserveRatio = true

    val maxX = gamePane.width.value - enemy.fitWidth.value
    val randomX = math.random() * maxX
    enemy.layoutX = randomX
    enemy.layoutY = 100

    enemies += enemy
    gamePane.children.add(enemy)
  }


  private def updateLasers(): Unit = {
    lasers.foreach { laser =>
      laser.layoutY.value -= 5
    }
    lasers = lasers.filter(_.layoutY.value > 0)
    gamePane.children.removeIf(laser => laser.layoutY.value <= 0)
  }

  private def updateEnemies(): Unit = {
    enemies.foreach { enemy =>
      enemy.layoutY.value += 2
    }
    enemies = enemies.filter(_.layoutY.value < gamePane.height.value)
    gamePane.children.removeIf(enemy => enemy.layoutY.value >= gamePane.height.value)
  }

  private def checkCollisions(): Unit = {
    lasers.foreach { laser =>
      enemies.foreach { enemy =>
        if (laser.boundsInParent.value.intersects(enemy.boundsInParent.value)) {
          score += 10
          scoreLabel.text = s"Score: $score"
          lasers -= laser
          enemies -= enemy
          gamePane.children.removeAll(laser, enemy)
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

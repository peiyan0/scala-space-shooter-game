package com.example.controller

import scalafx.beans.property.StringProperty
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.MouseEvent
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.Pane
import scalafx.stage.{Modality, Stage}
import scalafx.animation.{AnimationTimer, KeyFrame, Timeline}
import scalafx.util.Duration
import scalafxml.core.macros.sfxml
import scalafx.Includes._

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.DurationDouble
import scalafx.scene.{Parent, Scene}
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import javafx.{scene => jfxs}
import com.example.model.{EnemyModel, LaserModel, LeaderboardEntry}
import com.example.util.LeaderboardUtil
import scalafx.event.ActionEvent

@sfxml
class GameController(private val gamePane: Pane,
                     private val spaceshipImageView: ImageView,
                     private val countdownLabel: Label,
                     private val scoreLabel: Label,
                     private val difficultyLabel: Label,
                     private val pauseBtn: Button ) {

  // labels
  private var score = 0
  private var gameRunning = false
  private var animationTimer: AnimationTimer = _
  private var gameTimeline: Timeline = _
  private var countdownTimer: AnimationTimer = _
  // objects
  private var lasers: ListBuffer[LaserModel] = ListBuffer()
  private var enemies: ListBuffer[EnemyModel] = ListBuffer()
  // objects interval
  private var laserInterval = 0.25.second
  private var enemySpawnInterval = 0.1.second
  private var lastLaserTime = 0L
  private var lastEnemySpawnTime = 0L
  private var remainingTime = 30
  private var lastUpdateTime = 0L

  var stage: Stage = _
  val selectedSpaceship = new StringProperty(this, "selectedSpaceship", "")
  // user data
  var username: String = _
  var difficulty: String = _

  def initialize(): Unit = {
    selectedSpaceship.onChange((_, _, newValue) => updateSpaceshipImage(newValue))
    gamePane.onMouseMoved = handleSpaceshipMovement _
    pauseBtn.onAction = showPauseMenu _
  }

  def setDifficulty(difficulty: String): Unit = {
    difficulty match {
      case "EASY" =>
        laserInterval = 0.5.second
        enemySpawnInterval = 0.5.second
      case "MEDIUM" =>
        laserInterval = 0.5.second
        enemySpawnInterval = 0.25.second
      case "HARD" =>
        laserInterval = 0.25.second
        enemySpawnInterval = 0.1.second
    }
    difficultyLabel.text = s"Difficulty: $difficulty"
    showDifficultyLabel()
  }

  private def showDifficultyLabel(): Unit = {
    difficultyLabel.visible = true
    val showDuration = Duration(2000)

    val showTimeline = new Timeline {
      keyFrames = Seq(
        KeyFrame(showDuration, onFinished = _ => {
          difficultyLabel.visible = false
          startGame()
        })
      )
    }
    showTimeline.play()
  }

  private def updateSpaceshipImage(spaceship: String): Unit = {
    val image = new Image(getClass.getResourceAsStream(spaceship))
    spaceshipImageView.image = image
  }

  private def handleSpaceshipMovement(event: MouseEvent): Unit = {
    spaceshipImageView.layoutX = event.sceneX - spaceshipImageView.boundsInParent.value.getWidth / 2
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

  private def startGame(): Unit = {
    gameRunning = true
    countdownLabel.text = s"Time: $remainingTime s"
    val gameDuration = Duration(30000)

    gameTimeline = new Timeline {
      keyFrames = Seq(
        KeyFrame(gameDuration, onFinished = _ => endGame())
      )
    }
    gameTimeline.play()


    animationTimer = AnimationTimer { now =>
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
    animationTimer.start()

    countdownTimer = AnimationTimer { now =>
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

  private def endGame(): Unit = {
    gameRunning = false
    println(s"Game over! Your score: $score")
    countdownLabel.text = s"Game over! Your score: $score"
    // Save the score to leaderboard
    val leaderboard = LeaderboardUtil.loadLeaderboard("leaderboard.txt")
    val entry = LeaderboardEntry(username, difficulty, score)
    leaderboard.addEntry(entry)
    LeaderboardUtil.saveLeaderboard(leaderboard, "leaderboard.txt")
  }

  def pauseGame(): Unit = {
    gameRunning = false
    if (animationTimer != null) animationTimer.stop()
    if (gameTimeline != null) gameTimeline.pause()
    if (countdownTimer != null) countdownTimer.stop()
  }

  def resumeGame(): Unit = {
    gameRunning = true
    if (animationTimer != null) animationTimer.start()
    if (gameTimeline != null) gameTimeline.play()
    if (countdownTimer != null) countdownTimer.start()
  }

  def restartGame(): Unit = {
    gameRunning = false
    score = 0
    remainingTime = 30
    lasers.clear()
    enemies.clear()
    gamePane.children.clear()
    gamePane.children.addAll(countdownLabel, scoreLabel, difficultyLabel, spaceshipImageView)
    initialize()
  }

  def exitGame(): Unit = {
    val resource = getClass.getResource("/com/example/view/MainMenuLayout.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root = loader.getRoot[jfxs.Parent]
    stage.scene = new Scene(root.asInstanceOf[scalafx.scene.Parent])
  }

  def showPauseMenu(event: ActionEvent): Unit = {
    pauseGame()

    val resource = getClass.getResource("/com/example/view/GamePauseLayout.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root: Parent = loader.getRoot[jfxs.Parent]

    val pauseStage = new Stage() {
      scene = new Scene(root)
      title = "Game Paused"
      initOwner(stage)
      initModality(Modality.ApplicationModal)
    }

    // do not explicitly call controller.gameController = this
    // use GamePauseController#Controller instead
    val controller = loader.getController[GamePauseController#Controller]
    controller.stage = pauseStage

    pauseStage.showAndWait()

    if (gameRunning) {
      resumeGame()
    }
  }

  initialize()
}

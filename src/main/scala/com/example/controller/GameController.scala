package com.example.controller

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.DurationDouble
import scalafx.animation.{AnimationTimer, KeyFrame, Timeline}
import scalafx.beans.property.StringProperty
import scalafx.event.ActionEvent
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.MouseEvent
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.{AnchorPane, Pane}
import scalafx.scene.media.AudioClip
import scalafx.scene.Scene
import scalafx.stage.Stage
import scalafx.util.Duration
import scalafx.Includes._
import scalafxml.core.macros.sfxml
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import javafx.{scene => jfxs}
import com.example.model.{EnemyModel, LaserModel}
import com.example.util.{AudioUtil, GameLogic, GameUtil, StatusUtil}

@sfxml
class GameController(private val gamePane: Pane,
                     private val spaceshipImageView: ImageView,
                     private val countdownLabel: Label,
                     private val scoreLabel: Label,
                     private val statusLabel: Label,
                     private val exitBtn: Button,
                     private val pauseBtn: Button,
                     private val muteBtn: Button,
                     private val resumeBtn: Button,
                     private val restartBtn: Button,
                     private val unmuteBtn: Button
                    ) {

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

  def initialize(): Unit = {
    selectedSpaceship.onChange((_, _, newValue) => updateSpaceshipImage(newValue))
    gamePane.onMouseMoved = handleSpaceshipMovement _
  }

  // adjust intervals based on difficulty
  def setDifficulty(difficulty: String): Unit = {
    this.difficulty = difficulty
    difficulty match {
      case "EASY" =>
        laserInterval = 0.5.second
        enemySpawnInterval = 0.5.second
      case "MEDIUM" =>
        laserInterval = 0.5.second
        enemySpawnInterval = 0.25.second
      case "HARD" =>
        laserInterval = 0.25.second
        enemySpawnInterval = 0.2.second
    }
    StatusUtil.showMessage(statusLabel, s"Difficulty: $difficulty")
    hideLabelAndStartGame()
  }

  // set username
  def setUsername(username: String): Unit = {
    this.username = username
  }

  // ----------GAME LOGIC----------
  // update spaceship image
  private def updateSpaceshipImage(spaceship: String): Unit = {
    val image = new Image(getClass.getResourceAsStream(spaceship))
    spaceshipImageView.image = image
  }

  // handle spaceship movement
  private def handleSpaceshipMovement(event: MouseEvent): Unit = {
    spaceshipImageView.layoutX = event.sceneX - spaceshipImageView.boundsInParent.value.getWidth / 2
  }
  // update laser positions and remove off-screen lasers
  private def updateLasers(): Unit = {
    lasers.foreach(_.move())
    lasers = lasers.filter(_.imageView.layoutY.value > 0)
    gamePane.children.removeIf(laser => laser.layoutY.value <= 0)
  }


  // ----------GENERAL LOGIC----------
  // start game logic
  private def startGame(): Unit = {
    gameRunning = true
    countdownLabel.text = s"Time: $remainingTime s"
    val gameDuration = Duration(30000)

    gameTimeline = new Timeline {
      keyFrames = Seq(
        KeyFrame(gameDuration, onFinished = _ => if (gameRunning) endGame())
      )
    }
    gameTimeline.play()

    animationTimer = AnimationTimer { now =>
      if (gameRunning) {
        if (now - lastLaserTime > laserInterval.toNanos) {
          lasers = GameLogic.fireLaser(spaceshipImageView, gamePane, lasers)
          lastLaserTime = now
        }
        if (now - lastEnemySpawnTime > enemySpawnInterval.toNanos) {
          val (newEnemies, newTotalSpawned) = GameLogic.spawnEnemies(difficulty, gamePane, enemies, totalSpawned)
          enemies = newEnemies
          totalSpawned = newTotalSpawned
          lastEnemySpawnTime = now
        }
        updateLasers()
        enemies = GameLogic.updateEnemies(gamePane, enemies)
        val (newLasers, newEnemies, newScore) = GameLogic.checkCollisions(lasers, enemies, spaceshipImageView, gamePane, score, explosionSound)
        lasers = newLasers
        enemies = newEnemies
        score = newScore
        scoreLabel.text = s"Score: $score"
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

  // hide status label and start game
  def hideLabelAndStartGame(): Unit = {
    val showTimeline = new Timeline {
      keyFrames = Seq(
        KeyFrame(Duration(4000), onFinished = _ => {
          statusLabel.visible = false
          startGame()
        })
      )
    }
    showTimeline.play()
  }

  // game reset
  def reset(): Unit = {
    lasers.clear()
    enemies.clear()
    gamePane.children.clear()
    gamePane.children.addAll(countdownLabel, scoreLabel,
      statusLabel, spaceshipImageView, pauseBtn,
      resumeBtn, restartBtn, exitBtn, muteBtn, unmuteBtn)
    pauseBtn.visible = false
    resumeBtn.visible = true

    // reset timers and state
    if (animationTimer != null) animationTimer.stop()
    if (gameTimeline != null) gameTimeline.stop()
    if (countdownTimer != null) countdownTimer.stop()
    lastLaserTime = 0L
    lastEnemySpawnTime = 0L
    lastUpdateTime = 0L
  }

  // end game logic
  def endGame(): Unit = {
    gameRunning = false
    println(s"Game over! Your score: $score")
    StatusUtil.showMessage(statusLabel, s"Finished! Your score: $score", fade = false)
    GameUtil.addScoreToLeaderboard(this.username, this.difficulty, score)

    val waitTimeline = new Timeline {
      keyFrames = Seq(
        KeyFrame(Duration(3000), onFinished = _ => {
          openGameEnd()
        })
      )
    }
    waitTimeline.play()
  }

  // open new stage
  def openGameEnd(): Unit = {
    val resource = getClass.getResource("/com/example/view/GameEndLayout.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root = loader.getRoot[jfxs.layout.AnchorPane]

    stage.scene = new Scene(new AnchorPane(root))

    var enemiesHit = score / 10
    var enemiesMissed = totalSpawned - enemiesHit

    val gameEndController = loader.getController[GameEndController#Controller]()
    gameEndController.stage = stage
    gameEndController.setResults(
    username = username,
    totalScore = score,
    enemiesHit = enemiesHit,
    enemiesMissed = enemiesMissed,
    difficultyLevel = difficulty
    )
  }

  // handle pause action
  def handlePauseAction(event: ActionEvent): Unit = {
    gameRunning = false
    if (animationTimer != null) animationTimer.stop()
    if (gameTimeline != null) gameTimeline.pause()
    if (countdownTimer != null) countdownTimer.stop()
    pauseBtn.visible = false
    resumeBtn.visible = true
    StatusUtil.showMessage(statusLabel, "Paused", fade = false)
  }

  // resume game logic
  def handleResumeAction(event: ActionEvent): Unit = {
    pauseBtn.visible = true
    resumeBtn.visible = false
    statusLabel.visible = false
    gameRunning = true
    if (animationTimer != null) animationTimer.start()
    if (gameTimeline != null) gameTimeline.play()
    if (countdownTimer != null) countdownTimer.start()
  }

  // reset game state and components
  def handleRestartAction(event: ActionEvent): Unit = {
    gameRunning = false
    score = 0
    remainingTime = 30
    reset()
    initialize()
  }

  // handle exit action
  def handleExitAction(event: ActionEvent): Unit = {
    AudioUtil.pressedSoundAction()
    val resource = getClass.getResource("/com/example/view/MainMenuLayout.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root = loader.getRoot[jfxs.layout.AnchorPane]
    stage.scene = new Scene(new AnchorPane(root))

    val mainMenuController = loader.getController[MainMenuController#Controller]()
    mainMenuController.stage = stage
  }

  def handleMuteAction(event: ActionEvent): Unit = {
    AudioUtil.handleMuteAction(muteBtn, unmuteBtn)
  }

  def handleUnmuteAction(event: ActionEvent): Unit = {
    AudioUtil.handleUnmuteAction(muteBtn, unmuteBtn)
  }
  initialize()
}
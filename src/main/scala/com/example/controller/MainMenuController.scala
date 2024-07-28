package com.example.controller

import com.example.MainApp
import scalafx.event.ActionEvent
import scalafx.scene.control.Button
import scalafx.scene.layout.AnchorPane
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml
import javafx.{scene => jfxs}
import scalafx.scene.Scene
import scalafx.Includes._
import scalafx.scene.image.{Image, ImageView}
import scalafxml.core.{FXMLLoader, NoDependencyResolver}

@sfxml
class MainMenuController(
    var stage: Stage,
    private val unmuteBtn: Button,
    private val muteBtn: Button,
    private val unmuteIcon: ImageView,
    private val muteIcon: ImageView
  ) {

  private var isMuted = false
  unmuteBtn.visible = true
  muteBtn.visible = false

  def quitGame(event: ActionEvent): Unit = {
    stage.close() // Close the main stage
  }

  def showInstruction(event: ActionEvent): Unit = {
    val resource = getClass.getResource("/com/example/view/instruction.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    val root = loader.load[jfxs.layout.AnchorPane] // or appropriate root type

    val controller = loader.getController[InstructionController#Controller]()

    val instructionsStage = new Stage {
      title.value = "Instructions"
      icons += new Image(getClass.getResourceAsStream("/images/icons/instruction.png"))
      scene = new Scene(root)
    }

    controller.stage = instructionsStage
    instructionsStage.initOwner(stage)
    instructionsStage.show()
  }

  def showLeaderboard(event: ActionEvent): Unit = {
    val resource = getClass.getResource("/com/example/view/leaderboard.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    val root = loader.load[jfxs.layout.AnchorPane] // or appropriate root type

    val controller = loader.getController[LeaderboardController#Controller]()

    val leaderboardStage = new Stage {
      title.value = "Leaderboard"
      scene = new Scene(root)
    }

    controller.stage = leaderboardStage
    leaderboardStage.initOwner(stage)
    leaderboardStage.show()
  }


  def handleMuteAction(event: ActionEvent): Unit = {
    isMuted = true
    MainApp.mediaPlayer.setMute(isMuted)
    updateMuteButtons()
  }

  def handleUnmuteAction(event: ActionEvent): Unit = {
    isMuted = false
    MainApp.mediaPlayer.setMute(isMuted)
    updateMuteButtons()
  }

  private def updateMuteButtons(): Unit = {
    unmuteBtn.visible = !isMuted
    muteBtn.visible = isMuted
  }
}

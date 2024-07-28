package com.example.controller

import com.example.MainApp
import scalafx.event.ActionEvent
import scalafx.scene.control.Button
import scalafx.stage.{Modality, Stage}
import scalafxml.core.macros.sfxml
import javafx.{scene => jfxs}
import scalafx.scene.{Parent, Scene}
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
    val resource = getClass.getResource("/com/example/view/InstructionLayout.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    val root = loader.load[jfxs.layout.AnchorPane]

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
    val resource = getClass.getResource("/com/example/view/LeaderboardLayout.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    val root = loader.load[jfxs.layout.AnchorPane]

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

  def handleStartAction(event: ActionEvent): Unit = {
    val resource = getClass.getResource("/com/example/view/UserInputLayout.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root: Parent = loader.getRoot[jfxs.Parent]

    val dialogStage = new Stage() {
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      scene = new Scene(root)
    }

    val controller = loader.getController[UserInputController#Controller]()
    controller.stage = stage
    controller.dialogStage = dialogStage
    dialogStage.showAndWait()
  }
}

package com.example.controller

import com.example.util.AudioUtil
import scalafx.event.ActionEvent
import scalafx.scene.control.Button
import scalafx.stage.{Modality, Stage}
import scalafxml.core.macros.sfxml
import javafx.{scene => jfxs}
import scalafx.scene.{Parent, Scene}
import scalafx.Includes._
import scalafx.scene.image.Image
import scalafxml.core.{FXMLLoader, NoDependencyResolver}

@sfxml
class MainMenuController(
                          var stage: Stage,
                          private val unmuteBtn: Button,
                          private val muteBtn: Button
                        ) {

  def quitGame(event: ActionEvent): Unit = {
    stage.close()
  }

  def showInstruction(event: ActionEvent): Unit = {
    AudioUtil.pressedSoundAction()
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
    AudioUtil.pressedSoundAction()
    val resource = getClass.getResource("/com/example/view/LeaderboardLayout.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    val root = loader.load[jfxs.layout.AnchorPane]

    val controller = loader.getController[LeaderboardController#Controller]()

    val leaderboardStage = new Stage {
      title.value = "Leaderboard"
      icons += new Image(getClass.getResourceAsStream("/images/icons/leaderboard.png"))
      scene = new Scene(root)
    }

    controller.stage = leaderboardStage
    controller.initialize()
    leaderboardStage.initOwner(stage)
    leaderboardStage.show()
  }

  def handleStartAction(event: ActionEvent): Unit = {
    AudioUtil.pressedSoundAction()
    val resource = getClass.getResource("/com/example/view/UserInputLayout.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root: Parent = loader.getRoot[jfxs.Parent]

    val dialogStage = new Stage() {
      title = "Spaceship Game"
      icons += new Image(getClass.getResourceAsStream("/images/spaceship/ship1.png"))
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      scene = new Scene(root)
    }

    val controller = loader.getController[UserInputController#Controller]()
    controller.stage = stage
    controller.dialogStage = dialogStage
    dialogStage.showAndWait()
  }

  def handleMuteAction(event: ActionEvent): Unit = {
    AudioUtil.handleMuteAction(muteBtn, unmuteBtn)
  }

  def handleUnmuteAction(event: ActionEvent): Unit = {
    AudioUtil.handleUnmuteAction(muteBtn, unmuteBtn)
  }
}

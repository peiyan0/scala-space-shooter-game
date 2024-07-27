package com.example.controller

import scalafx.event.ActionEvent
import scalafx.scene.control.Button
import scalafx.scene.layout.AnchorPane
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml
import javafx.{scene => jfxs}
import scalafx.scene.Scene
import scalafx.Includes._
import scalafx.scene.image.Image
import scalafxml.core.{FXMLLoader, NoDependencyResolver}

@sfxml
class MainMenuController(var stage: Stage) {

  def quitGame(event: ActionEvent): Unit = {
    stage.close() // Close the main stage
  }

  def showInstruction(event: ActionEvent): Unit = {
    val resource = getClass.getResource("/com/example/view/instruction.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    val root = loader.load[jfxs.layout.AnchorPane] // or appropriate root type

    // Access the controller
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

    // Access the controller
    val controller = loader.getController[LeaderboardController#Controller]()

    val leaderboardStage = new Stage {
      title.value = "Leaderboard"
      scene = new Scene(root)
    }

    // Set the stage property in the controller
    controller.stage = leaderboardStage

    leaderboardStage.initOwner(stage)
    leaderboardStage.show()
  }
}

package com.example.controller

import scalafx.Includes._
import scalafx.stage.{Modality, Stage}
import scalafx.scene.image.Image
import scalafx.scene.Scene
import javafx.{scene => jfxs}
import scalafx.event.ActionEvent
import scalafxml.core.macros.sfxml
import javafx.scene.{Parent, image => jfxi, layout => jfxl}
import scalafxml.core.{FXMLLoader, NoDependencyResolver}

@sfxml
class GameEndController {
  var stage: Stage = _

  // Show the leaderboard screen
  def showLeaderboard(event: ActionEvent): Unit = {
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
    leaderboardStage.initOwner(stage)
    leaderboardStage.initModality(Modality.ApplicationModal)
    leaderboardStage.show()
  }

  // Restart the game by showing the user input screen
  def playAgain(event: ActionEvent): Unit = {
    val resource = getClass.getResource("/com/example/view/UserInputLayout.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root: Parent = loader.getRoot[jfxs.layout.AnchorPane]

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

  // Return to the main menu
  def quitToMainMenu(event: ActionEvent): Unit = {
    val resource = getClass.getResource("/com/example/view/MainMenuLayout.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root: Parent = loader.getRoot[jfxs.layout.AnchorPane]

    stage.scene = new Scene(root)
    val mainMenuController = loader.getController[MainMenuController#Controller]()
    mainMenuController.stage = stage
  }
}

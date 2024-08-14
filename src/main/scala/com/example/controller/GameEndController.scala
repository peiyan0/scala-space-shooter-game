package com.example.controller

import scalafx.Includes._
import scalafx.stage.{Modality, Stage}
import scalafx.scene.image.Image
import scalafx.scene.Scene
import javafx.{scene => jfxs}
import scalafx.event.ActionEvent
import scalafxml.core.macros.sfxml
import javafx.scene.Parent
import scalafx.scene.control.Label
import scalafxml.core.{FXMLLoader, NoDependencyResolver}

@sfxml
class GameEndController(private var difficultyLabel: Label,
                        private var enemiesHitLabel: Label,
                        private var enemiesMissedLabel: Label,
                        private var totalScoreLabel: Label,
                        private var totalScoreLabel1: Label,
                        private var usernameLabel: Label
                       ) {
  var stage: Stage = _

  def setResults(username: String, totalScore: Int, enemiesHit: Int, enemiesMissed: Int, difficultyLevel: String): Unit = {
    usernameLabel.text = username
    totalScoreLabel.text = totalScore.toString
    totalScoreLabel1.text = totalScore.toString
    enemiesHitLabel.text = enemiesHit.toString
    enemiesMissedLabel.text = enemiesMissed.toString
    difficultyLabel.text = difficultyLevel
  }

  // show the leaderboard screen
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

  // restart the game by showing the user input screen
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

  // return to the main menu
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

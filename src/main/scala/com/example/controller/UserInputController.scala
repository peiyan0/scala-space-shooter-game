package com.example.controller

import com.example.util.AlertUtil
import javafx.event.ActionEvent
import javafx.scene.control.{Button, TextField}
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import javafx.{scene => jfxs}
import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.scene.layout.{AnchorPane, StackPane}

@sfxml
class UserInputController(
   private val usernameField: TextField,
   private val easyBtn: Button,
   private val mediumBtn: Button,
   private val hardBtn: Button
                         ) {

  var stage: Stage = _
  var dialogStage: Stage = _
  private var difficulty: String = _

  // Initialize the difficulty buttons
  easyBtn.setOnAction((e: ActionEvent) => handleDifficultyAction("EASY"))
  mediumBtn.setOnAction((e: ActionEvent) => handleDifficultyAction("MEDIUM"))
  hardBtn.setOnAction((e: ActionEvent) => handleDifficultyAction("HARD"))

  def handleBackAction(event: ActionEvent): Unit = {
    dialogStage.close()
  }

  def handleNextAction(event: ActionEvent): Unit = {
    val username = usernameField.text.value
    println(s"Username entered: $username")
    println(s"Difficulty selected: $difficulty")

    AlertUtil.showUsernameCreated(dialogStage, username)
    dialogStage.close()

    val resource = getClass.getResource("/com/example/view/SpaceshipSelectionLayout.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root: jfxs.Parent = loader.getRoot[jfxs.Parent]
    val scalafxRoot = new StackPane()
    scalafxRoot.getChildren.add(root)
    stage.scene = new Scene(scalafxRoot)

    val controller = loader.getController[SpaceshipSelectionController#Controller]()
    controller.stage = stage
  }

   private def handleDifficultyAction(selectedDifficulty: String): Unit = {
    difficulty = selectedDifficulty
  }
}

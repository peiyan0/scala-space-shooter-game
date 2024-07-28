package com.example.controller

import com.example.util.AlertUtil
import scalafx.event.ActionEvent
import scalafx.scene.control.TextField
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import javafx.{scene => jfxs}
import scalafx.scene.Scene
import scalafx.scene.layout.{AnchorPane, StackPane}

@sfxml
class UserInputController(private val usernameField: TextField) {
  var stage: Stage = _
  var dialogStage: Stage = _

  def handleBackAction(event: ActionEvent): Unit = {
    dialogStage.close()
  }

  def handleNextAction(event: ActionEvent): Unit = {
    val username = usernameField.text.value
    println(s"Username entered: $username")

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
}

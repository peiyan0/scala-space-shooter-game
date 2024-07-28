package com.example.controller

import scalafx.event.ActionEvent
import scalafx.scene.image.{Image, ImageView}
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import javafx.{scene => jfxs}
import scalafx.scene.Scene

@sfxml
class SpaceshipSelectionController(private val spaceshipImageView: ImageView) {
  var stage: Stage = _
  private val spaceshipImages = List(
    "/images/spaceship/ship1.png",
    "/images/spaceship/ship2.png",
    "/images/spaceship/ship3.png",
    "/images/spaceship/ship4.png",
    "/images/spaceship/ship5.png",
    "/images/spaceship/ship6.png"
  )
  private var currentIndex = 0
  updateSpaceshipImage()


  def handleLeftAction(event: ActionEvent): Unit = {
    currentIndex = (currentIndex - 1 + spaceshipImages.length) % spaceshipImages.length
    updateSpaceshipImage()
  }

  def handleRightAction(event: ActionEvent): Unit = {
    currentIndex = (currentIndex + 1) % spaceshipImages.length
    updateSpaceshipImage()
  }

  def handleBackAction(event: ActionEvent): Unit = {
    val resource = getClass.getResource("/com/example/view/UserInputLayout.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root = loader.getRoot[jfxs.Parent]
    stage.scene = new Scene(root.asInstanceOf[scalafx.scene.Parent])

    val controller = loader.getController[UserInputController#Controller]()
    controller.stage = stage
  }

  def handleDoneAction(event: ActionEvent): Unit = {
    val selectedSpaceship = spaceshipImages(currentIndex)
    println(s"Selected spaceship: $selectedSpaceship")
    stage.close()
  }

  private def updateSpaceshipImage(): Unit = {
    val image = new Image(getClass.getResourceAsStream(spaceshipImages(currentIndex)))
    spaceshipImageView.image = image
  }
}

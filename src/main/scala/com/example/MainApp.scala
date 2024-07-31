package com.example

import com.example.controller.MainMenuController
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes._
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import javafx.{scene => jfxs}
import scalafx.scene.image.Image
import scalafx.scene.media.MediaPlayer.Status
import scalafx.scene.media.{Media, MediaPlayer}

object MainApp extends JFXApp {
  val rootResource = getClass.getResourceAsStream("/com/example/view/RootLayout.fxml")
  val loader = new FXMLLoader(null, NoDependencyResolver)
  loader.load(rootResource)
  val roots = loader.getRoot[jfxs.layout.BorderPane]
  val cssResource = getClass.getResource("/style/Main.css")
  roots.stylesheets = List(cssResource.toExternalForm)

  val backgroundMusic = new Media(getClass.getResource("/sounds/MainBg.mp3").toString)
  val mediaPlayer = new MediaPlayer(backgroundMusic)
  mediaPlayer.setCycleCount(MediaPlayer.Indefinite)

  stage = new PrimaryStage {
    title = "Spaceship Game"
    icons += new Image(getClass.getResourceAsStream("/images/spaceship/ship1.png"))
    scene = new Scene {
      root = roots
      stylesheets = Seq(cssResource.toExternalForm)
    }
  }

  def showMainMenu(): Unit = {
    val resource = getClass.getResourceAsStream("/com/example/view/MainMenuLayout.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource)
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    roots.stylesheets = List(cssResource.toExternalForm)
    this.roots.setCenter(roots)

    val mainMenuController = loader.getController[MainMenuController#Controller]()
    mainMenuController.stage = stage

    if (mediaPlayer.getStatus != Status.PLAYING) {
      mediaPlayer.play()
    }
  }

  showMainMenu()
}

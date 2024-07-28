package com.example.util

import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.Stage

object AlertUtil {
  def showUsernameCreated(dialogStage: Stage, username: String): Unit = {
    val alert = new Alert(AlertType.Information) {
      initOwner(dialogStage)
      title = "Success"
      headerText = "Username Created!"
      contentText = s"Username '$username' created successfully."
      graphic = null
    }
    alert.getDialogPane.getStylesheets.add(getClass.getResource("/style/Main.css").toExternalForm)
    alert.showAndWait()
  }
}

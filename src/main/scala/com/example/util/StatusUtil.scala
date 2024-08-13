package com.example.util

import scalafx.animation.FadeTransition
import scalafx.scene.control.Label
import scalafx.util.Duration

object StatusUtil {
  def showMessage(label: Label, message: String, fade: Boolean = true): Unit = {
    label.visible = true
    label.text = message

    if (fade) {
      val fadeIn = new FadeTransition(Duration(500), label) {
        fromValue = 0.0
        toValue = 1.0
      }
      fadeIn.play()
    }
  }
}

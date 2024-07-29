package com.example.model

import scalafx.scene.image.{Image, ImageView}

class LaserModel(val imagePath: String) {
  val imageView: ImageView = new ImageView(new Image(getClass.getResourceAsStream(imagePath)))

  def move(): Unit = {
    imageView.layoutY.value -= 5
  }

  def isOutOfBounds(gamePaneHeight: Double): Boolean = {
    imageView.layoutY.value <= 0
  }
}

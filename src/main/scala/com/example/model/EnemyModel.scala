package com.example.model

import scalafx.scene.image.Image
import scalafx.scene.image.ImageView

class Enemy(val imagePath: String) {
  val imageView: ImageView = new ImageView(new Image(getClass.getResourceAsStream(imagePath)))

  def setPosition(x: Double, y: Double): Unit = {
    imageView.layoutX = x
    imageView.layoutY = y
  }

  def getPosition: (Double, Double) = (imageView.layoutX.value, imageView.layoutY.value)
}

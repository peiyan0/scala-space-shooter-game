package com.example.model

import scalafx.scene.image.{Image, ImageView}

class EnemyModel(val imagePath: String) {
  val imageView: ImageView = new ImageView(new Image(getClass.getResourceAsStream(imagePath)))

  def initialize(width: Double, x: Double, y: Double): Unit = {
    imageView.fitWidth = width
    imageView.preserveRatio = true
    imageView.layoutX = x
    imageView.layoutY = y
  }

  def move(): Unit = {
    imageView.layoutY.value += 2
  }
}

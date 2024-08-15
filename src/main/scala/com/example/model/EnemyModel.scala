package com.example.model

import scalafx.scene.effect.DropShadow
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.paint.Color

class EnemyModel(val imagePath: String) {
  val imageView: ImageView = new ImageView(new Image(getClass.getResourceAsStream(imagePath)))

  def initialize(width: Double, x: Double, y: Double): Unit = {
    imageView.fitWidth = width
    imageView.preserveRatio = true
    imageView.layoutX = x
    imageView.layoutY = y
    imageView.effect = new DropShadow(
      radius = 5, offsetX = 0, offsetY = 0, color = Color.Red
    )
  }

  def move(): Unit = {
    imageView.layoutY.value += 2
  }
}

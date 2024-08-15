package com.example.model

import scalafx.scene.effect.{DropShadow, SepiaTone}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.paint.Color

abstract class EnemyModel(val imagePath: String) {
  val imageView: ImageView = new ImageView(new Image(getClass.getResourceAsStream(imagePath)))

  def initialize(width: Double): Unit = {
    imageView.fitWidth = width
    imageView.preserveRatio = true
    imageView.layoutX = math.random() * 1180 // 1280 - 100 = 1180 pane width
    imageView.layoutY = 100
  }
  def move(): Unit
}


class VerticalEnemy(imagePath: String) extends EnemyModel(imagePath) {
  override def initialize(width: Double): Unit = {
    super.initialize(width)
    imageView.effect = new DropShadow(
      radius = 4, offsetX = 0, offsetY = 0, color = Color.Red
    )
  }
  override def move(): Unit = {
    imageView.layoutY.value += 2
  }
}


class RandomEnemy(imagePath: String) extends EnemyModel(imagePath) {
  private val random = new scala.util.Random()

  override def initialize(width: Double): Unit = {
    super.initialize(width)
    imageView.effect = new SepiaTone(0.5)
  }

  override def move(): Unit = {
    val deltaX = (random.nextDouble() - 0.5) * 5 // Random horizontal movement

    imageView.layoutX = imageView.layoutX.value + deltaX
    imageView.layoutY.value += 1
  }
}

package com.example.controller

import scalafx.event.ActionEvent
import scalafx.scene.control.Label
import scalafx.scene.layout.{HBox, VBox}
import scalafxml.core.macros.sfxml
import scalafx.stage.Stage
import com.example.util. LeaderboardUtil

@sfxml
class LeaderboardController(
                             private val leaderboardVBox: VBox,
                             var stage: Stage
                           ) {

  def initialize(): Unit = {
    val leaderboard = LeaderboardUtil.loadLeaderboard("leaderboard.txt")
    val entries = leaderboard.getEntries

    entries.foreach { entry =>
      val entryHBox = createEntryHBox(entry.username, entry.difficulty, entry.score.toString)
      leaderboardVBox.getChildren.add(entryHBox)
    }
  }

  private def createEntryHBox(username: String, difficulty: String, score: String): HBox = {
    val usernameLabel = new Label(username) {
      prefWidth = 200
    }

    val difficultyLabel = new Label(difficulty) {
      prefWidth = 200
    }

    val scoreLabel = new Label(score)

    new HBox {
      children = Seq(usernameLabel, difficultyLabel, scoreLabel)
      spacing = 5
    }
  }

  def backToGame(event: ActionEvent): Unit = {
    if (stage != null) stage.close()
  }
}

package com.example.util

import com.example.model.{Leaderboard, LeaderboardEntry}
import java.io._

object LeaderboardUtil {
  def saveLeaderboard(leaderboard: Leaderboard, fileName: String): Unit = {
    val writer = new PrintWriter(new File(fileName))
    leaderboard.getEntries.foreach { entry =>
      writer.println(s"${entry.username},${entry.difficulty},${entry.score}")
    }
    writer.close()
  }

  def loadLeaderboard(fileName: String): Leaderboard = {
    val leaderboard = new Leaderboard()
    val file = new File(fileName)
    if (file.exists()) {
      val source = scala.io.Source.fromFile(fileName)
      source.getLines().foreach { line =>
        val Array(username, difficulty, score) = line.split(",")
        leaderboard.addEntry(LeaderboardEntry(username, difficulty, score.toInt))
      }
      source.close()
    }
    leaderboard
  }
}

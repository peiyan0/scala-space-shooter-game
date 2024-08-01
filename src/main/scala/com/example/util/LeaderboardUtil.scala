package com.example.util


import com.example.model.{Leaderboard, LeaderboardEntry}
import java.io._

object LeaderboardUtil {
  def saveLeaderboard(leaderboard: Leaderboard, fileName: String): Unit = {
    val file = new File(fileName)
    val writer = new PrintWriter(file)
    for (entry <- leaderboard.getEntries) {
      writer.println(s"${entry.username},${entry.difficulty},${entry.score}")
    }
    writer.close()
  }

  def loadLeaderboard(fileName: String): Leaderboard = {
    val leaderboard = new Leaderboard()
    val file = new File(fileName)
    if (file.exists()) {
      val source = scala.io.Source.fromFile(fileName)
      for (line <- source.getLines()) {
        val Array(username, difficulty, scoreStr) = line.split(",")
        leaderboard.addEntry(LeaderboardEntry(username, difficulty, scoreStr.toInt))
      }
      source.close()
    }
    leaderboard
  }
}


package com.example.model

import scala.collection.mutable.ListBuffer

class Leaderboard {
  private val entries: ListBuffer[LeaderboardEntry] = ListBuffer()

  def addEntry(entry: LeaderboardEntry): Unit = {
    entries += entry
    // Sort by score in descending order
    val sortedEntries = entries.toList.sortBy(-_.score)
    // Clear and re-add sorted entries
    entries.clear()
    entries ++= sortedEntries
  }

  def getEntries: List[LeaderboardEntry] = entries.toList
}

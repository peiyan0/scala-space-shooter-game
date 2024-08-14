package com.example.model

import scala.collection.mutable.ListBuffer

class Leaderboard {
  private val entries: ListBuffer[LeaderboardEntry] = ListBuffer()

  def addEntry(entry: LeaderboardEntry): Unit = {
    entries += entry
    val sortedEntries = entries.toList.sortBy(-_.score)
    entries.clear()
    entries ++= sortedEntries
  }

  def getEntries: List[LeaderboardEntry] = entries.toList
}

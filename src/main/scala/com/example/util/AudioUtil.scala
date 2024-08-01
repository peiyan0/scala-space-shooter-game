package com.example.util

import com.example.MainApp
import scalafx.scene.control.Button

object AudioUtil {
  def handleMuteAction(muteBtn: Button, unmuteBtn: Button): Unit = {
    MainApp.mediaPlayer.setMute(true)
    muteBtn.setVisible(true)
    unmuteBtn.setVisible(false)
  }

  def handleUnmuteAction(muteBtn: Button, unmuteBtn: Button): Unit = {
    MainApp.mediaPlayer.setMute(false)
    muteBtn.setVisible(false)
    unmuteBtn.setVisible(true)
  }
}

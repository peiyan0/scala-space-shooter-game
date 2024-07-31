package com.example.util

import com.example.MainApp
import scalafx.scene.control.Button
import scalafx.scene.image.ImageView

object AudioUtil {
  private var isMuted = false

  def handleMuteAction(muteBtn: Button, unmuteBtn: Button): Unit = {
    isMuted = true
    MainApp.mediaPlayer.setMute(isMuted)
    updateMuteButtons(muteBtn, unmuteBtn)
  }

  def handleUnmuteAction(muteBtn: Button, unmuteBtn: Button): Unit = {
    isMuted = false
    MainApp.mediaPlayer.setMute(isMuted)
    updateMuteButtons(muteBtn, unmuteBtn)
  }

  private def updateMuteButtons(muteBtn: Button, unmuteBtn: Button): Unit = {
    unmuteBtn.visible = !isMuted
    muteBtn.visible = isMuted
  }
}

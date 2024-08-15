package com.example.util

import com.example.MainApp
import scalafx.scene.control.Button
import scalafx.scene.input.MouseEvent
import scalafx.scene.media.AudioClip

object AudioUtil {
  val pressedSound = new AudioClip(getClass.getResource("/sounds/pressed.mp3").toString) {volume = 0.3}

  def pressedSoundAction(): Unit = {
     pressedSound.play()
  }

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

package com.example.controller

import scalafx.event.ActionEvent
import scalafxml.core.macros.sfxml
import scalafx.stage.Stage

@sfxml
class InstructionController(var stage: Stage) {

  def backToGame(event: ActionEvent): Unit = {
    if (stage != null) stage.close()
  }
}

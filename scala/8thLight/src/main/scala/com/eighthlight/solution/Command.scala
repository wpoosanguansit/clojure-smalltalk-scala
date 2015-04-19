package com.eighthligth.solution

import scopt._

import java.io.File

case class Config(input: String)

object Command {

  val parser = new scopt.OptionParser[Config]("scopt") {
    head("scopt", "3.x")
    opt[String]('i', "input") required() valueName("<filePath>") action { (x, c) =>
      c.copy(input = x) } text("input is a required file path property")
  }
}

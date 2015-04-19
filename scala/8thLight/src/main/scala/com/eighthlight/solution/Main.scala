package com.eighthlight.solution


import java.io.{FileReader, LineNumberReader, File}

import com.eighthligth.solution._

import scala.util.Random
import scalaz._
import Scalaz._
import Command._
import scalaz.stream._
import scalaz.stream.nio._
import scalaz.concurrent.Task
import scala.language.postfixOps

object Main {

  def main(args: Array[String]): Unit = {
    parser.parse(args, Config(input   = "input.txt")) match {
      case Some(config) =>
        println("sample 100 runs -- ")
        val rl = RandomLine(new File("input.txt"))
        for (i <- 1 to 100) {
          println("----------")
          println(rl.generate)
          println("----------")
        }
      case None         =>
        Console.err.println(
          """
            |Please provide -i <path to file for input> to the command.
            |
            | java -jar <path to solution.jar> -i <path to target text file>
            |
            | i.e.
            |
            | java -jar target/scala-2.11/solution.jar -i input.txt
          """.stripMargin)
    }
  }

  case class Input(index: Int, file: File)

  case class RandomLine(f: File) {
    val reader    = new LineNumberReader(new FileReader(f))
    reader.skip(java.lang.Long.MAX_VALUE)
    val total     = reader.getLineNumber + 1
    reader.close()

    def generate: String = {
      val random  = Random.nextInt(total)
      val input   = Input(random, f)
      output(input)
    }

    def output: Input => String = Memo.mutableHashMapMemo { input =>
      val line    = file.linesR(input.file.getAbsolutePath)
        .filter(s => !s.trim.isEmpty)
        .drop(input.index - 1)
        .once
        .runLast.run
      line.getOrElse("")
    }
  }

}


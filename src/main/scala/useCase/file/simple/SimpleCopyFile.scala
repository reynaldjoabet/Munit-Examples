package useCase.file.simple

import java.io.File

import cats.effect.{ExitCode, IO, IOApp}

import useCase.file.Utils.copy

/**
  * The goal of this program is copies files using Cats-Effect and Functional Programming.
  */

object SimpleCopyFile extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      _ <-
        if (args.length != 2)
          IO.raiseError(new IllegalArgumentException("Add origin and destination files as args"))
        else IO.unit
      originFile      = new File(args.head)
      destinationFile = new File(args.tail.head)
      count          <- copy(originFile, destinationFile)
      _ <-
        IO.println(s"$count bytes copied from ${originFile.getPath} to ${destinationFile.getPath}")

    } yield ExitCode.Success
  }

}

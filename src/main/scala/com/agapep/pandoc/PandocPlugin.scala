package com.agapep.pandoc

import java.io.FileWriter

import sbt.Keys._
import sbt._

import scala.io.Source

object PandocPlugin extends AutoPlugin {

  object autoImport {
    val pandocSrcFolder     = settingKey[String]("position where is markdown documentation")
    val pandocDstFolder     = settingKey[String]("position where put documentation")
    val pandocCmd           = settingKey[String]("command used for generating docs")
    val pandocParams        = settingKey[List[String]]("params for markdown cmd")
    val pandocDstFiles      = settingKey[List[String]]("what files'll be generated")
    val pandocGenerate      = inputKey[Unit]("Generates Markdown pdf file documentation")
    val mergeMarkdownFiles    = settingKey[Boolean]("if .md files should be combined (merged) previously (internal).")

  }

  // If you change your auto import then the change is automatically reflected here..
  import autoImport._


  override def projectSettings: Seq[Setting[_]] = Seq(
    pandocSrcFolder := baseDirectory.value.getAbsolutePath + "/doc/",

    pandocDstFolder := "/web/public/main/doc/",

    pandocCmd       := "pandoc",

    pandocDstFiles  := List("doc.pdf", "doc.html"),

    pandocParams := List(
      "--table-of-contents",
      "--number-sections",
      "--standalone",                //it'll compile to full html document (with headers and so on)
      "--highlight-style=tango",
      "-V geometry:margin=1in"
    ),

    pandocGenerate := {
      // Sbt provided logger.
      val log = streams.value.log

      val hf = "*.md"
      val mdPaths = file(pandocSrcFolder.value).**(hf).getPaths.sorted.toList

      //create destination folder
      val df =  target.value.getAbsolutePath + pandocDstFolder.value
      file(df).getParentFile.mkdirs()
      Process("mkdir", "-p" :: df :: Nil).!




      //write every .md file to one bigger
      val inFiles:List[String] = mergeMarkdownFiles.value  match {
        case true  => List(mergeAllMarkdownFiles(df, mdPaths, log))
        case false => mdPaths
      }

      for (dstFile <- pandocDstFiles.value) {
        log.debug(s"creating $dstFile in $df...")

        Process(
          pandocCmd.value :: inFiles ::: "-o" :: df + dstFile :: "--data-dir=" + pandocSrcFolder.value :: pandocParams.value,
          new File(pandocSrcFolder.value)
        ).!!
      }

      ()
    },


    mergeMarkdownFiles := true
  )


  def mergeAllMarkdownFiles(destinationFolder:String, mdFiles:Seq[String], log:Logger):String = {
    log.debug(s"merging files to doc.md in $destinationFolder...")
    val accuMdFile = destinationFolder + "doc.md"
    val fw = new FileWriter(accuMdFile, false)

    mdFiles.foreach { f =>
      Source.fromFile(f).getLines().foreach(l => fw.write(l  + "\n"))
      fw.write('\n')
    }
    fw.close()
    accuMdFile
  }
}

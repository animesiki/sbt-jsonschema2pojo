package sbtjsonschema2pojo

import java.io.{PrintStream, File, FilenameFilter}

import com.sun.codemodel.JCodeModel
import org.jsonschema2pojo.SchemaMapper
import sbt.Keys._
import sbt._

object JsonSchema2PoJoPlugin extends AutoPlugin {
  override def requires = plugins.JvmPlugin

  override def trigger = allRequirements

  object autoImport {
    lazy val jsonSchema2PoJo = taskKey[Seq[File]]("Generate Java PoJo from Json Schemas")
    lazy val jsonSchemas = settingKey[Seq[File]]("Json Schemas")
    lazy val pojoPackage = settingKey[String]("PoJo package")
    lazy val pojoFiles = settingKey[File]("PoJo output files path")
  }

  import sbtjsonschema2pojo.JsonSchema2PoJoPlugin.autoImport._

  lazy val jsonSchema2PoJoSettings: Seq[Def.Setting[_]] = Seq(
    pojoPackage in jsonSchema2PoJo := "PoJo",
    pojoFiles in jsonSchema2PoJo <<= (sourceManaged in Compile)(sourceManaged =>
      sourceManaged / "jsnomschema2pojo"
    ),
    jsonSchemas in jsonSchema2PoJo <<= (resourceDirectory in Compile)(resources =>
      (resources / "json-schemas").listFiles(new FilenameFilter {
        override def accept(dir: File, name: String): Boolean = name.endsWith(".json")
      }) match {
        case null => Seq[File]()
        case files => files
      }
    ),
    jsonSchema2PoJo :=
      JsonSchema2PoJo((jsonSchemas in jsonSchema2PoJo).value,
        (pojoPackage in jsonSchema2PoJo).value,
        (pojoFiles in jsonSchema2PoJo).value,
        streams.value.log("jsonschema2pojo")),
    javaSource <<= (pojoFiles in jsonSchema2PoJo),
    sourceGenerators in Compile += jsonSchema2PoJo.taskValue
  )

  override lazy val projectSettings = inConfig(Compile)(jsonSchema2PoJoSettings)
}

object JsonSchema2PoJo {
  def apply(sources: Seq[File], pkg: String, outputs: File, logger: sbt.Logger): Seq[File] = {
    if (sources.isEmpty) sources
    else {
      logger.info(f"Generating PoJo from ${sources.size} Json Schemas")
      if (!outputs.exists())
        outputs.mkdirs()

      val codeModel = new JCodeModel()
      val schemaMapper = new SchemaMapper()

      sources.map { source =>
        val name = source.getName.split('.')(0)
        logger.info(f"Generate PoJo: $pkg.$name")
        schemaMapper.generate(codeModel, name, pkg, source.toURI.toURL)
      }

      codeModel.build(outputs, null.asInstanceOf[PrintStream])
      
      def recursiveListFiles(f: File): Array[File] = {
        val these = f.listFiles
        these.filter(_.isFile) ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
      }
      recursiveListFiles(outputs)
    }
  }
}
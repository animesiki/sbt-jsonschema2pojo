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

    lazy val jsonSchemasDefault = settingKey[Seq[File]]("Default Json Schemas")
    lazy val jsonSchemas = settingKey[Seq[File]]("Json Schemas")

    lazy val pojoPackageDefault = settingKey[String]("Default PoJo package")
    lazy val pojoPackage = settingKey[String]("PoJo package")

    lazy val pojoFilesDefault = settingKey[File]("Default PoJo output files path")
    lazy val pojoFiles = settingKey[File]("PoJo output files path")
  }

  import sbtjsonschema2pojo.JsonSchema2PoJoPlugin.autoImport._

  lazy val jsonSchema2PoJoSettings: Seq[Def.Setting[_]] = Seq(
    pojoPackageDefault in jsonSchema2PoJo := "PoJo",
    pojoPackage in jsonSchema2PoJo <<= (pojoPackage in jsonSchema2PoJo) or (pojoPackageDefault in jsonSchema2PoJo),
  
    pojoFilesDefault in jsonSchema2PoJo <<= (sourceManaged in Compile)(sourceManaged =>
      sourceManaged / "jsnomschema2pojo"
    ),
    pojoFiles in jsonSchema2PoJo <<= (pojoFiles in jsonSchema2PoJo) or (pojoFilesDefault in jsonSchema2PoJo),
  
    jsonSchemasDefault in jsonSchema2PoJo <<= (resourceDirectory in Compile)(resources =>
      (resources / "json-schemas").listFiles(new FilenameFilter {
        override def accept(dir: File, name: String): Boolean = name.endsWith(".json")
      }) match {
        case null => Seq[File]()
        case files => files
      }
    ),
    jsonSchemas in jsonSchema2PoJo <<= (jsonSchemas in jsonSchema2PoJo) or (jsonSchemasDefault in jsonSchema2PoJo),

    jsonSchema2PoJo :=
      JsonSchema2PoJo((jsonSchemas in jsonSchema2PoJo).value,
        (pojoPackage in jsonSchema2PoJo).value,
        (pojoFiles in jsonSchema2PoJo).value,
        streams.value.log("jsonschema2pojo")),
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
        schemaMapper.generate(codeModel, name, f"$pkg.$name", source.toURI.toURL)
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
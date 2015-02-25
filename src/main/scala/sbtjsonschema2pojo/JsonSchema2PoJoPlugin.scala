package sbtjsonschema2pojo

import java.{io, util}

import org.jsonschema2pojo._
import org.jsonschema2pojo.rules.RuleFactory
import sbt.Keys._
import sbt._

import scala.collection.JavaConversions._

object JsonSchema2PoJoPlugin extends AutoPlugin {

  object autoImport {
    lazy val jsonSchema2PoJo = taskKey[Seq[File]]("Task for generate Java PoJo from Json Schemas")
    
    lazy val generationConfig = settingKey[GenerationConfig]("Generation config")

    lazy val generateBuilders = settingKey[Boolean]("The 'generateBuilders' configuration option")

    lazy val usePrimitives = settingKey[Boolean]("The 'usePrimitives' configuration option")

    lazy val jsonSchemas = settingKey[Seq[File]]("The 'sources' configuration option")

    lazy val targetDirectory = settingKey[File]("The 'targetDirectory' configuration option")

    lazy val targetPackage = settingKey[String]("The 'targetPackage' configuration option")

    lazy val propertyWordDelimiters = settingKey[Array[Char]]("The 'propertyWordDelimiters' configuration option")

    lazy val useLongIntegers = settingKey[Boolean]("The 'useLongIntegers' configuration option")

    lazy val useDoubleNumbers = settingKey[Boolean]("The 'useDoubleNumbers' configuration option")

    lazy val includeHashcodeAndEquals = settingKey[Boolean]("The 'includeHashcodeAndEquals' configuration option")

    lazy val includeToString = settingKey[Boolean]("The 'includeToString' configuration option")

    lazy val annotationStyle = settingKey[AnnotationStyle]("The 'annotationStyle' configuration option")

    lazy val customAnnotator = settingKey[Class[_ <: Annotator]]("The 'customAnnotator' configuration option")

    lazy val customRuleFactory = settingKey[Class[_ <: RuleFactory]]("The 'customRuleFactory' configuration option")

    lazy val includeJsr303Annotations = settingKey[Boolean]("The 'includeJsr303Annotations' configuration option")

    lazy val sourceType = settingKey[SourceType]("The 'sourceType' configuration option")

    lazy val outputEncoding = settingKey[String]("The 'outputEncoding' configuration option")

    lazy val removeOldOutput = settingKey[Boolean]("The 'removeOldOutput' configuration option")

    lazy val useJodaDates = settingKey[Boolean]("The 'useJodaDates' configuration option")

    lazy val useCommonsLang3 = settingKey[Boolean]("The 'useCommonsLang3' configuration option")

    lazy val fileFilter = settingKey[io.FileFilter]("The 'fileFilter' configuration option")

    lazy val initializeCollections = settingKey[Boolean]("The 'initializeCollections' configuration option")

    lazy val classNamePrefix = settingKey[String]("The 'classNamePrefix' configuration option")

    lazy val classNameSuffix = settingKey[String]("The 'classNameSuffix' configuration option")
  }

  import sbtjsonschema2pojo.JsonSchema2PoJoPlugin.autoImport._

  private val defaultConfig = new DefaultGenerationConfig()
  
  override def requires = plugins.JvmPlugin

  override def trigger = allRequirements
  
  lazy val jsonSchema2PoJoSettings: Seq[Def.Setting[_]] = Seq(

    generateBuilders in jsonSchema2PoJo := defaultConfig.isGenerateBuilders,

    usePrimitives in jsonSchema2PoJo := defaultConfig.isUsePrimitives,

    jsonSchemas in jsonSchema2PoJo <<= (resourceDirectory in Compile)(resources =>
      (resources / "json-schemas").listFiles(new io.FilenameFilter {
        override def accept(dir: File, name: String): Boolean = name.endsWith(".json")
      }) match {
        case null => Seq[File]()
        case files => files
      }
    ),

    targetDirectory in jsonSchema2PoJo <<= (sourceManaged in Compile)(sourceManaged =>
      sourceManaged / "jsnomschema2pojo"
    ),

    targetPackage in jsonSchema2PoJo := "PoJo",

    propertyWordDelimiters in jsonSchema2PoJo := defaultConfig.getPropertyWordDelimiters,

    useLongIntegers in jsonSchema2PoJo := defaultConfig.isUseLongIntegers,

    useDoubleNumbers in jsonSchema2PoJo := defaultConfig.isUseDoubleNumbers,

    includeHashcodeAndEquals in jsonSchema2PoJo := defaultConfig.isIncludeHashcodeAndEquals,

    includeToString in jsonSchema2PoJo := defaultConfig.isIncludeToString,
  
    annotationStyle in jsonSchema2PoJo := defaultConfig.getAnnotationStyle,

    customAnnotator in jsonSchema2PoJo := defaultConfig.getCustomAnnotator,
  
    customRuleFactory in jsonSchema2PoJo := defaultConfig.getCustomRuleFactory,

    includeJsr303Annotations in jsonSchema2PoJo := defaultConfig.isIncludeJsr303Annotations,

    sourceType in jsonSchema2PoJo := defaultConfig.getSourceType,
  
    outputEncoding in jsonSchema2PoJo := defaultConfig.getOutputEncoding,

    removeOldOutput in jsonSchema2PoJo := defaultConfig.isRemoveOldOutput,

    useJodaDates in jsonSchema2PoJo := defaultConfig.isUseJodaDates,
  
    useCommonsLang3 in jsonSchema2PoJo := defaultConfig.isUseCommonsLang3,

    fileFilter in jsonSchema2PoJo := defaultConfig.getFileFilter,
  
    initializeCollections in jsonSchema2PoJo := defaultConfig.isInitializeCollections,
  
    classNamePrefix in jsonSchema2PoJo := defaultConfig.getClassNamePrefix,

    classNameSuffix in jsonSchema2PoJo := defaultConfig.getClassNameSuffix,
  
    generationConfig in jsonSchema2PoJo := new GenerationConfig() {
      override def isGenerateBuilders: Boolean =
        (generateBuilders in jsonSchema2PoJo).value

      override def isUsePrimitives: Boolean =
        (usePrimitives in jsonSchema2PoJo).value

      override def getSource: util.Iterator[io.File] =
        (jsonSchemas in jsonSchema2PoJo).value.iterator

      override def getTargetDirectory: io.File =
        (targetDirectory in jsonSchema2PoJo).value

      override def getTargetPackage: String =
        (targetPackage in jsonSchema2PoJo).value

      override def getPropertyWordDelimiters: Array[Char] =
        (propertyWordDelimiters in jsonSchema2PoJo).value

      override def isUseLongIntegers: Boolean =
        (useLongIntegers in jsonSchema2PoJo).value

      override def isUseDoubleNumbers: Boolean =
        (useDoubleNumbers in jsonSchema2PoJo).value

      override def isIncludeHashcodeAndEquals: Boolean =
        (includeHashcodeAndEquals in jsonSchema2PoJo).value

      override def isIncludeToString: Boolean =
        (includeToString in jsonSchema2PoJo).value

      override def getAnnotationStyle: AnnotationStyle =
        (annotationStyle in jsonSchema2PoJo).value

      override def getCustomAnnotator: Class[_ <: Annotator] =
        (customAnnotator in jsonSchema2PoJo).value

      override def getCustomRuleFactory: Class[_ <: RuleFactory] =
        (customRuleFactory in jsonSchema2PoJo).value

      override def isIncludeJsr303Annotations: Boolean =
        (includeJsr303Annotations in jsonSchema2PoJo).value

      override def getSourceType: SourceType =
        (sourceType in jsonSchema2PoJo).value

      override def getOutputEncoding: String =
        (outputEncoding in jsonSchema2PoJo).value

      override def isRemoveOldOutput: Boolean =
        (removeOldOutput in jsonSchema2PoJo).value

      override def isUseJodaDates: Boolean =
        (useJodaDates in jsonSchema2PoJo).value

      override def isUseCommonsLang3: Boolean =
        (useCommonsLang3 in jsonSchema2PoJo).value

      override def getFileFilter: io.FileFilter =
        (fileFilter in jsonSchema2PoJo).value

      override def isInitializeCollections: Boolean =
        (initializeCollections in jsonSchema2PoJo).value

      override def getClassNamePrefix: String =
        (classNamePrefix in jsonSchema2PoJo).value

      override def getClassNameSuffix: String =
        (classNameSuffix in jsonSchema2PoJo).value
    },
  
    jsonSchema2PoJo := JsonSchema2PoJoGenerate((generationConfig in jsonSchema2PoJo).value),
  
    sourceGenerators in Compile += jsonSchema2PoJo.taskValue
  )

  override lazy val projectSettings = jsonSchema2PoJoSettings
}

object JsonSchema2PoJoGenerate {
  
  def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these.filter(_.isFile) ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }

  def apply(config: GenerationConfig): Seq[File] = {
    if (!config.getSource.hasNext) Seq[File]()
    else {
      Jsonschema2Pojo.generate(config)
      recursiveListFiles(config.getTargetDirectory)
    }
  }
}
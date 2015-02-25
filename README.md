# sbt-jsonschema2pojo
sbt plugin for generating PoJo from Json Schema.

## Setup

Create at your sbt project file `project/jsonschema2pojo.sbt` with following contents:

```scala
resolvers += Resolver.url("sbt-jsonschema2pojo-plugin-releases",
   url("http://dl.bintray.com/catap/sbt-plugins/"))(Resolver.ivyStylePatterns)
       
addSbtPlugin("com.github.catap" % "sbt-jsonschema2pojo" % "0.3")
```

Generated PoJo required [commons-lang](http://commons.apache.org/proper/commons-lang/) and [jackson-databind](https://github.com/FasterXML/jackson-databind/).

For example you can add it to yuor project by adding following lines to `build.sbt`: 

```scala
libraryDependencies += "commons-lang" % "commons-lang" % "2.6"

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.5.1"
```

Now yout json schemas to `src/main/resources/json-schemas` and after you can found generated PoJo classes at PoJo package.

## Customization

### Json Schemas path

You can change list of json schemas files. By default plugin use all files that ended by `.json` on `src/main/resources/json-schemas`.
If you would like to use all files that ended by `.schema.json` from `shemas` folder, you must add a following code to your `build.sbt`:

```scala
jsonSchemas in jsonSchema2PoJo := (baseDirectory.value / "schemas").listFiles(new FilenameFilter {
  override def accept(dir: File, name: String): Boolean = name.endsWith(".schema.json")
})
```

### PoJo package

All generated files that include to `PoJo` package. You can specialized it by following command:

```scala
targetPackage in jsonSchema2PoJo := "com.company.PoJo"
```

### All options

This plugin support following options. For detail information about options please looking at [JsonSchema2PoJo documentation](https://github.com/joelittlejohn/jsonschema2pojo).
```
generateBuilders
usePrimitives
schemas
targetDirectory
targetPackage
propertyWordDelimiters
useLongIntegers
useDoubleNumbers
includeHashcodeAndEquals
includeToString
annotationStyle
customAnnotator
customRuleFactory
includeJsr303Annotations
sourceType
outputEncoding
removeOldOutput
useJodaDates
useCommonsLang3
fileFilter
initializeCollections
classNamePrefix
classNameSuffix
```

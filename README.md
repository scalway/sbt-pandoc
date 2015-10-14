# SBT PANDOC

This is an SBT 0.13 plugin which uses AutoPlugin and the PANDOC application to 
build markdown documentation artifacts from SBT.

This is a fairly simple SBT plugin, whitch I wrote to learn how to write plugins.

## Building 

You can build and publish the plugin in the normal way to your local Ivy repository:

```
sbt publish-local
```

## Installation

You must first download the git project and build it.  It is not available in the maven repository yet (it'll probably in future).

In `project/plugins.sbt`:

```
addSbtPlugin("com.agapep.sbt" % "sbt-pandoc" % "0.0.1")
```

In `build.sbt`:

```
pandocSrcFolder := "/doc/"
pandocDstFolder := "/web/public/main/doc/"
```

## Usage

Once you have it installed, typing `pandocGenerate` at an SBT prompt will generate documentation artifacts.

## Testing

TODO in future



# SmallRye Reactive Converters

[![SmallRye Build](https://github.com/smallrye/smallrye-reactive-converters/actions/workflows/build.yml/badge.svg)](https://github.com/smallrye/smallrye-reactive-converters/actions/workflows/build.yml)
[![License](https://img.shields.io/github/license/smallrye/smallrye-fault-tolerance.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Maven](https://img.shields.io/maven-central/v/io.smallrye.reactive/smallrye-reactive-utilities-projects?color=green)]()

This contains a set of modules helping the development of reactive applications in SmallRye 

Reactive converters are a set of library to convert types uses by various libraries from/to `Publisher` and `CompletionStage`.
Documentation is available in [the reactive-converters directory](./reactive-converters/readme.adoc).

## Build

`mvn clean install`

## Release

- open a pull request updating the `.github/project.yml` file with the desired release version and next development version.
- once the pull request is merged, the release will be cut (tag, deployment...)

**IMPORTANT**: After the release, you must deploy the Javadoc:

1. checkout the created tag (`$VERSION`)
2. run `mvn javadoc:aggregate -DskipTests`
3. clone the website: `cd target  && git clone git@github.com:smallrye/smallrye-reactive-converters.git gh-pages  && cd gh-pages && git checkout gh-pages`
4. create a repository with the version name: `mkdir apidocs/$VERSION`
5. copy the generated content into the new directory `cp -R ../site/apidocs/* apidocs/$VERSION`
6. commit and push: `git add -A  && git commit -am "Publish javadoc for version $VERSION" && git push origin gh-pages`


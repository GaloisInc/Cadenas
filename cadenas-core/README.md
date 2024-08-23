# Cadenas-Core

`cadenas-core` is an implementation of
_Model-Based Format-Transforming Encryption (MB-FTE)_, a form of text
steganography utilizing  generative language models such as GPT-2.

**Disclaimer:** This library should be treated as actively-developed research
software and used with caution until formal security assessments/proofs have
been completed.

This repository is merely the library implementing the MB-FTE operations -
there is [a CLI](https://gitlab-ext.galois.com/rocky/cadenas-cli) and
[an Android app](https://gitlab-ext.galois.com/rocky/cadenas) utilizing it.

## Building

`cadenas-core` is implemented using Kotlin Multiplatform, and can be used in
both generic JVM and Android projects.

To build locally, you will need:

- [Gradle](https://gradle.org/)
- JDK 17 (e.g. from [AdoptOpenJDK](https://adoptopenjdk.net/))
- Android SDK 26+

Installations of Android Studio provide essentially everything you need out
of the box, but manual installations of the above should suffice. Android
SDKs may be installed through the [command-line tools](https://developer.android.com/studio#command-line-tools-only).

Building the library is as simple as executing:

```bash
./gradlew assemble
```

Which assembles all targets without running tests (see 
[Running Tests](#running-tests) for additional setup needed to run the test
suite.)

As of this writing, we do not provide distributions of the library on Maven
or a similar package repository, so if you wish to use `cadenas-core` in your
own projects, we recommend using a
[composite build](https://docs.gradle.org/current/userguide/composite_builds.html).
For an example of this, see the above-linked CLI or Android application. In
summary, to your project's `settings.gradle.kts`, add the line:

```kotlin
includeBuild("path/to/this/repo")
```

And add the dependency to `build.gradle.kts` as:

```kotlin
dependencies {
    // ...
    implementation("org.galois:cadenas-core:1.0-SNAPSHOT")    
}
```

## Running Tests

In addition to the build requirements listed above, running the tests requires
an installation of PyTorch - specifically, your platform's `libtorch` shared
libraries.

As of this writing, running the `cadenas-core` test suite requires `libtorch`
version 1.13.1. You can download the CPU-only libraries
[here](https://download.pytorch.org/libtorch/cpu/libtorch-shared-with-deps-1.13.1%2Bcpu.zip).

Extract the ZIP, and set the environment variable `LIBTORCH_HOME` to this
directory's path.

Now, you should be able to run the test suite from this repository with:

```bash
./gradlew check
```

**Note:** The JVM tests are quite slow, as they execute a round-trip of MB-FTE
on ~500 strings. Furthermore, these tests _may_ fail stochastically. As noted
above, this library should be used with caution!
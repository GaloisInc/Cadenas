# Cadenas

Cadenas is an application for _obfuscated communication_ via "whiteboard"
platforms such as Mastodon, Twitter, and even GitLab/GitHub comment threads.

The obfuscation is provided by _model-based format-transforming encryption
(MB-FTE)_.

This repository hosts the source for the Cadenas Android application, which
provides a user-friendly interface to MB-FTE, and is not tied to any specific
social media platform. The remainder of this README gives a high-level overview
of the app features and briefly discusses how to get started developing on this
work.

## Features

- Material3 design
- Independence from social media - no data shared with other platforms
- Dark and light themes based on system settings
- Simple QR configuration sharing

## Building

Cadenas is a standard Material Design application developed in Android Studio.
There are a few odds and ends to configure if you'd like to build the app for
yourself, though:

### `gradle.properties`

The Gradle properties file in this repository is incomplete - To speed up CI,
we provide credentials for an Artifactory instance for build caching. We also
provide signed APK artifacts from CI on the `main` branch, which requires the
configuration of a signing key.

#### Artifactory caching

You will need to do one of two things:

1. Modify `settings.gradle.kts` to disable build caching/use local rather than
   remote, or:
2. Provide your own Artifactory credentials in `gradle.properties`.

Note that you should _never_ commit credentials to a public repository, so it
is recommended that if you take option (2), you place the `artifactory*`
variables in your user-level `gradle.properties` file, rather than that of the
project. The variables you must provide are:

- `artifactoryUser`: The username of a maintainer of the Artifactory repo
- `artifactoryPassword`: The corresponding password
- `artifactoryUrl`: The top-level URL of the Artifactory instance

You may also need to change the repository name in `settings.gradle`; we assume
a generic Artifactory repo named `cadenas_generic-local`, but you/your
organization may use a different naming scheme.

#### Release builds

You will need to do one of two things:

1. Modify `app/build.gradle.kts` to disable signing for the `release` build, or:
2. Configure a keystore and provide the appropriate details in
   `gradle.properties`

Note that you should _never_ commit credentials to a public repository, so it
is recommended that if you take option (2), you place the `release*` variables
in your user-level `gradle.properties` file, rather than that of the project.
The variables you must provide are:

- `releaseStoreFile`: The path to the keystore file
- `releaseStorePassword`: The passphrase needed to use the keystore
- `releaseKeyAlias`: The key alias configured
- `releaseKeyPassword`: The passphrase needed to use the key

### `cadenas-core`

The implementation of MB-FTE, `cadenas-core`, can be built outside of an Android
context for use in your own Kotlin applications. Please see the README for that
repository for build/installation instructions.

## Installation

As of this writing, Cadenas is not distributed on the Google Play Store or
F-Droid. To install, you will need to allow unknown applications to be
installed from an appropriate source (e.g., Google Drive), make sure the APK
is available at that source, and proceed with installation via that APK. **We
also recommend disabling the Play Protect feature that sends unknown
applications to Google.**

## Support

We're always looking to improve the user experience of Cadenas. Please feel free
to open tickets if you encounter problems.

If you'd like to contribute, fork the repository and create merge requests.
Guidelines for contributing to the project can be found in `CONTRIBUTING.md`.

## Authors and acknowledgment

Our deep gratitude to all of the following for their contributions to Cadenas,
both technical and theoretical:

- David Archer
- Luke Bauer
- Himanshu Goyal
- Alex Grushin
- Rawane Issa
- Andrew Lauziere
- Alex J Malozemoff
- Hari Menon
- Chris Phifer
- Hazel Shackleton
- Tom Shrimpton
- Shauna Sweet

This material is based upon work supported by the Defense Advanced Research Projects Agency (DARPA) under Contract Number FA8750-19-C-0501. Any opinions, findings and conclusions or recommendations expressed in this material are those of the author(s) and do not necessarily reflect the views of the DARPA.
DISTRIBUTION STATEMENT A: Approved for public release

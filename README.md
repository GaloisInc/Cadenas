# Cadenas

Cadenas is an application for _obfuscated communication_ via "whiteboard"
platforms such as Mastodon, Twitter, and even GitLab/GitHub comment threads.

The obfuscation is provided by _model-based format-transforming encryption
(MB-FTE)_.  See `STRATEGY.md` for a high-level description of the approach.

This repository hosts the source for the Cadenas Android application, which
provides a user-friendly interface to MB-FTE, and is not tied to any specific
social media platform. For an introduction to the app and its use, see
`USER-GUIDE.md`. The remainder of this README gives a high-level overview of
the app features and briefly discusses how to get started developing on this
work.

## Features

- Material Design
- Independence from social media - no account(s) needed for use
- Dark and light themes based on system settings
- Configurable (language model, seed text)
- Simple text/QR configuration sharing

## Building

Cadenas is a standard Material Design application developed in Android Studio.
With a standard installation of Android Studio, it should be sufficient to
clone this repository (and its submodules) and open the root in Android Studio.

The implementation of MB-FTE, `butkuscore`, and be built outside of an Android
context for use in your own Kotlin applications. Please see the README for that
repository for build/installation instructions; the Cadenas application's Gradle
configuration handles building this dependency appropriate for use in the app.

## Installation

As of this writing, Cadenas is not distributed on the Google Play Store or
F-Droid. To install, you will need to:

1. Enable developer options on your Android device
2. Package an APK via Android Studio
3. Upload the APK to your device, and install

The details of these steps vary depending on the device/version of Android
you're using.

## Support

We're always looking to improve the user experience of Cadenas. Please feel free
to open tickets if you encounter problems.

If you'd like to contribute, fork the repository and create merge requests.
Guidelines for contributing to the project can be found in `CONTRIBUTING.md`.

TODO: Write a `CONTRIBUTING.md`

## Authors and acknowledgment

TODO: DARPA blurb / Galois credits

## License

TODO: Pick a license

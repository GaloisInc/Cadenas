# Contributing

Thanks for your interest in making Cadenas better! To start, please make a
ticket outlining the issue you've run into or feature you'd like to contribute.
You should start with one of the issue templates provided - they will help you
provide the information necessary for a productive discussion and resolution.

Please note that we have a [code of conduct](#code-of-conduct); please follow
it in all your interactions with the project.

## Merge Request Process

Cadenas development should follow, as strictly as possible, Vincent Driessen's
[git flow](https://nvie.com/posts/a-successful-git-branching-model/). You're
encouraged to read that article in its entirety, but here's a quick-and-dirty
summary:

### `main` and `develop`

The `main` branch is, aptly, the main branch of the repository where `HEAD` is
considered release-ready. Every merge to `main` corresponds to a release /
increase in version number.

In parallel, the branch `develop` is the main branch where `HEAD` contains the
latest-and-greatest approved work on Cadenas - in other words, the nightly
builds of the application.

The core Cadenas team will, as appropriate, merge `develop` into `main`, which
constitutes a new version release. The release process is outlined 
[below](#release--and-hotfix--branches) for  the sake of documentation
completeness.

Before that, we outline the process of creating and merging feature branches,
which is useful to all contributors.

### Feature branches

Feature branches always branch off `develop`, and will ultimately be merged
back into `develop`. They can be named anything other than `main`, `develop`,
`release-*`, or `hotfix-*` (where the `*` is a wildcard, not a literal
asterisk.)

To create a feature branch, assuming you have the latest `develop` from
`origin`:

```bash
git checkout -b my-feature develop
```

When your work is completed, open a merge request (make sure the target branch
is `develop`), and provide all information requested in the feature template.
The request  must be approved by at least one Cadenas core team member - this
will typically involve some amount of code review.

Feature branches are merged such that a new commit object is always created.
Though this results in many empty commit objects, the history of `develop` will
always clearly delineate the incorporation of individual feature branches,
making the process of whole-feature removal much simpler.

Merged feature branches are automatically deleted on `origin`, and you should
feel free to delete your local copy as well once the merge request is complete:

```bash
git branch -d my-feature
```

Keep reading for information on how new releases of Cadenas are created by the
core team. If this isn't interesting to you, feel free to skip ahead to the
[code of conduct](#code-of-conduct) mentioned earlier!

### `release-*` and `hotfix-*` branches

Release branches always branch off of `develop`, and will be merged into both
`develop` and `main`, the latter of which will be tagged with an updated
version number. They should always be named `release-*`, where `*` is the new
version number.

We attempt to use [semantic versioning](https://semver.org/) - in brief, we use
`MAJOR.MINOR.PATCH` versioning, subject to the following increment rules:

1. Increment `MAJOR` when making incompatible API changes
2. Increment `MINOR` when adding backwards-compatible functionality
3. Increment `PATCH` when implementing backwards-compatible bug fixes

Incrementing `MAJOR` resets `MINOR` and `PATCH` to `0`; incrementing `MINOR`
just resets `PATCH`. When `PATCH` is `0`, it may be omitted from the version in
source code, release branch names, and tags of `main`.

The first commit on a release branch should update the `versionCode` and
`versionName` in the application-level `build.gradle` file - the former
should simply be incremented, and the latter updated as determined by the
rules above for the release being made.

Subsequent commits may be made on a release branch to address any last-minute
needs (e.g. documentation updates, small bug fixes.) Significant new features
should _not_ be implemented on the release branch - they should target `develop`
for the _next_ release instead.

To create a release branch (for, say, release `1.2`):

```bash
git checkout -b release-1.2 develop
```

Once you've committed new versioning information and any final small changes,
open a merge request and set the target branch to `main`. Fill out the template
for releases, and await approval from at least one other core team member.

Once merged, you should then create a new tag in the GitLab UI. Following this
example, the tag should be named `v1.2`, and be based off of `main`. Provide a
summary of the release changes in the optional notes.

The release branch must also be merged into `develop`; this should be done once
the tag is created, and does not require a merge request (since the changes
were already approved for merge into `main`):

```bash
git checkout develop
git merge --no-ff release-1.2
```

Resolve any merge conflicts, commit, and push. You can then delete the release
branch.

Hotfix branches are just like release branches, except:

1. They branch off of `main` instead of `develop`
2. They should only be used for unplanned releases resulting from critical bugs
3. If there is a `release-*` branch active at the time of a `hotfix-*`, the
   completed hotfix should be merged into the `release-*` branch rather than
   `develop` after merging to `main` and tagging the new version. It doesn't
   _hurt_ to merge to `develop` as well, but you **must** merge to the active
   `release-*` branch!

If a hotfix is necessary, please start by opening an incident ticket, following
the template. This will help other core team members reproduce the bug / better
contribute to a fix.

## Code of Conduct

### Our Pledge

We as members, contributors, and leaders pledge to make participation in our
community a harassment-free experience for everyone, regardless of age, body
size, visible or invisible disability, ethnicity, sex characteristics, gender
identity and expression, level of experience, education, socio-economic status,
nationality, personal appearance, race, caste, color, religion, or sexual
identity and orientation.

We pledge to act and interact in ways that contribute to an open, welcoming,
diverse, inclusive, and healthy community.

### Our Standards

Examples of behavior that contributes to a positive environment for our
community include:

* Demonstrating empathy and kindness toward other people
* Being respectful of differing opinions, viewpoints, and experiences
* Giving and gracefully accepting constructive feedback
* Accepting responsibility and apologizing to those affected by our mistakes,
  and learning from the experience
* Focusing on what is best not just for us as individuals, but for the overall
  community

Examples of unacceptable behavior include:

* The use of sexualized language or imagery, and sexual attention or advances of
  any kind
* Trolling, insulting or derogatory comments, and personal or political attacks
* Public or private harassment
* Publishing others' private information, such as a physical or email address,
  without their explicit permission
* Other conduct which could reasonably be considered inappropriate in a
  professional setting

### Enforcement Responsibilities

Community leaders are responsible for clarifying and enforcing our standards of
acceptable behavior and will take appropriate and fair corrective action in
response to any behavior that they deem inappropriate, threatening, offensive,
or harmful.

Community leaders have the right and responsibility to remove, edit, or reject
comments, commits, code, wiki edits, issues, and other contributions that are
not aligned to this Code of Conduct, and will communicate reasons for moderation
decisions when appropriate.

### Scope

This Code of Conduct applies within all community spaces, and also applies when
an individual is officially representing the community in public spaces.
Examples of representing our community include using an official e-mail address,
posting via an official social media account, or acting as an appointed
representative at an online or offline event.

### Enforcement

Instances of abusive, harassing, or otherwise unacceptable behavior may be
reported to the community leaders responsible for enforcement at
[cphifer@galois.com].
All complaints will be reviewed and investigated promptly and fairly.

All community leaders are obligated to respect the privacy and security of the
reporter of any incident.

### Enforcement Guidelines

Community leaders will follow these Community Impact Guidelines in determining
the consequences for any action they deem in violation of this Code of Conduct:

#### 1. Correction

**Community Impact**: Use of inappropriate language or other behavior deemed
unprofessional or unwelcome in the community.

**Consequence**: A private, written warning from community leaders, providing
clarity around the nature of the violation and an explanation of why the
behavior was inappropriate. A public apology may be requested.

#### 2. Warning

**Community Impact**: A violation through a single incident or series of
actions.

**Consequence**: A warning with consequences for continued behavior. No
interaction with the people involved, including unsolicited interaction with
those enforcing the Code of Conduct, for a specified period of time. This
includes avoiding interactions in community spaces as well as external channels
like social media. Violating these terms may lead to a temporary or permanent
ban.

#### 3. Temporary Ban

**Community Impact**: A serious violation of community standards, including
sustained inappropriate behavior.

**Consequence**: A temporary ban from any sort of interaction or public
communication with the community for a specified period of time. No public or
private interaction with the people involved, including unsolicited interaction
with those enforcing the Code of Conduct, is allowed during this period.
Violating these terms may lead to a permanent ban.

#### 4. Permanent Ban

**Community Impact**: Demonstrating a pattern of violation of community
standards, including sustained inappropriate behavior, harassment of an
individual, or aggression toward or disparagement of classes of individuals.

**Consequence**: A permanent ban from any sort of public interaction within the
community.

### Attribution

This Code of Conduct is adapted from the [Contributor Covenant][homepage],
version 2.1, available at
[https://www.contributor-covenant.org/version/2/1/code_of_conduct.html][v2.1].

Community Impact Guidelines were inspired by
[Mozilla's code of conduct enforcement ladder][Mozilla CoC].

For answers to common questions about this code of conduct, see the FAQ at
[https://www.contributor-covenant.org/faq][FAQ]. Translations are available at
[https://www.contributor-covenant.org/translations][translations].

[homepage]: https://www.contributor-covenant.org
[v2.1]: https://www.contributor-covenant.org/version/2/1/code_of_conduct.html
[Mozilla CoC]: https://github.com/mozilla/diversity
[FAQ]: https://www.contributor-covenant.org/faq
[translations]: https://www.contributor-covenant.org/translations
# Contributing

Thanks for your interest in making Cadenas better! To start, please make a
ticket outlining the issue you've run into or feature you'd like to contribute.
You should start with one of the issue templates provided - they will help you
provide the information necessary for a productive discussion and resolution.

Please note that we have a [code of conduct](CODE_OF_CONDUCT.md); please follow
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
core team. Otherwise, please take this opportunity to familiarize yourself with
our [code of conduct](CODE_OF_CONDUCT.md).

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
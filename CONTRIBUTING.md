# Contributing to protobuf-dt
Want to contribute? Great! First, read this page (including the small print at
the end).

## Before you contribute
**Before we can use your code, you must sign the
[Google Individual Contributor License Agreement](https://developers.google.com/open-source/cla/individual?csw=1)
(CLA), which you can do online.**

The CLA is necessary mainly because you own the copyright to your changes,
even after your contribution becomes part of our codebase, so we need your
permission to use and distribute your code. We also need to be sure of
various other things — for instance that you'll tell us if you know that
your code infringes on other people's patents. You don't have to sign
the CLA until after you've submitted your code for review and a member has
approved it, but you must do it before we can put your code into our codebase.

Before you start working on a larger contribution, you should get in touch
with us first. Use the issue tracker to explain your idea so we can help and
possibly guide you.

## Code reviews and other contributions
**All submissions, including submissions by project members, require review.**

Changes to protobuf-dt must be reviewed before they are accepted, no matter who
makes the change. A custom git command called git-codereview, discussed below,
helps manage the code review process through a Google-hosted instance of the
code review system called Gerrit.

### Set up authentication for code review
Gerrit uses Google Accounts for authentication. If you don't have a Google
Account, you can create an account which includes a new Gmail email account or
create an account associated with your existing email address.

The email address associated with the Google Account you use will be recorded
in the change log and in the contributors file.

To set up your account in Gerrit, visit
[protobuf-dt.googlesource.com](https://protobuf-dt.googlesource.com)
and click on "Generate Password" in the page's top right menu bar.

You will be redirected to the google accounts page to sign in.

Once signed in, you are returned back to
[protobuf-dt.googlesource.com](https://protobuf-dt.googlesource.com)
to "Configure Git". Follow the instructions on the page. (If you are on a
Windows computer, you should instead follow the instructions in the yellow box
to run the command.)

Your secret authentication token is now in a .gitcookie file and Git is
configured to use this file.

### Register with Gerrit

Now that you have your authentication token, you need to register your account
with Gerrit. To do this, visit
[protobuf-dt-review.googlesource.com/login](https://protobuf-dt-review.googlesource.com/login/).
You will immediately be redirected to Google Accounts. Sign in using the same
Google Account you used above. That is all that is required.

## The small print
Contributions made by corporations are covered by a different agreement than
the one above, the
[Software Grant and Corporate Contributor License Agreement](https://cla.developers.google.com/about/google-corporate).
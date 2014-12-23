LibVLC on Android demo with multiple instances
======================

This is a sample application demonstrating the use of libVLC on Android.

This repository contains only sample code, and the libVLC on Android sdk (*vlc-sdk.7z*) must be built and placed into the target application's source code directory.

Getting started
---------------
Requirements:

Generate sdk files of patched VLC
You can find the repo here: https://github.com/coderReview/Android-VLC-Patches

Now, just copy them into the app and run it

Debugging
---------

Having problems making some media play?

Use [**adb logcat**](http://developer.android.com/tools/help/logcat.html) to read the debug logs. This is analogous to Tools → Messages (verbosity 2) in desktop VLC.

In addition, try different configurations of hardware acceleration, chroma, and deblocking. Android devices vary greatly in their capabilities.

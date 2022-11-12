MiNKI stands for MInimal Non-Keyboard (Android) IME

I created it after going around in circles for literally DAYS trying to make sense of Android's **ancient**
documentation and examples for making a Soft Keyboard, which was complicated by multiple challenges:

* Google hasn't maintained the old "Soft Keyboard" example project. It's not readily buildable as-was in Android Studio, 
   and massaging it into a form Android Studio CAN deal with is a nontrivial undertaking

* Pretty much EVERY bit of example code from Google is based on the premise  that your "Soft Keyboard" actually IS going to be
   a conventional "virtual keyboard" with tappable buttons representing keys.

* Compounding the problem, Google turned around and deprecated (as of API 29) one of the fundamental classes ("KeyboardView") that almost every
   example project in existence *depends* upon. Yeah, you can hunt down the files from the AOSP repository and put them into 
   your own project... but that's yet *another* thing that can "go wrong" and leave you stumped & frustrated
   while trying to get your own InputMethodService to build and run for the first time.

So, as the first step to creating my own non-keyboard-based Android IME, I set out to create MiNKI.

In theory, you can load MiNKI into Android Studio, let Gradle update itself, and refactor it to
rename the packages and classes into your own.

In reality, if you're first discovering this project after approximately 2024, the
outcome of trying to load it directly into Android Studio will probably be.... lots
of error messages, red-squiggles, and total dysfunction.  Let's face it... Android
has a long history of completely breaking any and all example code every year or two.
So, here's what I recommend doing instead:

1. Create a new Android project with a blank Activity.
2. Copy and paste MiNKI's <service> block from AndroidManifest.xml
3. Copy MinkiService.java and MinkiView.java
4. Copy res/layout/minki.xml
5. copy res/xml/method.xml

Fix any problems you find. Hopefully, there won't be many. 
If you create your new project so its official name is "MiNKI", and its
main Activity has "example.ime.minki" as its package (remember, you
can always use Android Studio's refactoring to rename the package and classes
once it works), you might get lucky and have no problems at all.

Once the obvious problems are fixed, build and deploy it to the emulator.
As of Android 33, once you've deployed it, you need to:
1. go to Settings->System
2. hunt around and add "Minki" as a new virtual keyboard
3. If there's a place in your settings to set a default keyboard, do it. Otherwise,
do something that triggers keyboard input, then use the keyboard selector
   (lower right, on a Pixel 7 Pro with 3 buttons instead of gesture navigation) to pick "MiNKI"

Going forward, whenever you update the Keyboard, Android won't UNINSTALL the old version, but it will de-select
it as the default/current keyboard and revert to the stock virtual keyboard until you re-select it as the current/default
virtual keyboard.
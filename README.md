# Simple App Rating Prompt
A simple library to show app rating prompt in Android.

[![](https://jitpack.io/v/juhchamp/SimpleAppRatingPrompt.svg)](https://jitpack.io/#juhchamp/SimpleAppRatingPrompt)

## Download
Download the latest version via Gradle:

**Step 1.**
Add the JitPack repository to your build file. Add it in your root build.gradle at the end of repositories:

```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

**Step 2.**
Add the SimpleAppRatingPrompt dependency

```
dependencies {
  implementation 'com.github.juhchamp:SimpleAppRatingPrompt:1.0.0'
}
```

## Usage

**Step 1.**
Implement the RatingPromptClickListener interface for the activity overriding the
onRatingPromptButtonClick fun

```kotlin
class MainActivity : AppCompatActivity(), RatingPromptClickListener {

   ...

   override fun onRatingPromptButtonClick(whichButton: Int) {
        when(whichButton) {
            SimpleAppRatingPrompt.POSITIVE_BUTTON -> {
                Toast.makeText(this,"POSITIVE_BUTTON", Toast.LENGTH_SHORT).show()
            }
            SimpleAppRatingPrompt.NEGATIVE_BUTTON -> {
                Toast.makeText(this,"NEGATIVE_BUTTON", Toast.LENGTH_SHORT).show()
            }
            SimpleAppRatingPrompt.REMIND_ME_LATER_BUTTON -> {
                Toast.makeText(this,"REMIND_ME_LATER_BUTTON", Toast.LENGTH_SHORT).show()
            }
        }
   }
}
```


**Step 2.**
Call the SimpleAppRatingPrompt instance passing activity and set the other needed options

```kotlin
SimpleAppRatingPrompt(this)
    .setMinLaunchCountToPrompt(3)
    .setTimeToNextPrompt(3) // In days
    .setTitle("Rate us")
    .setMessage("Did you enjoy the app? Can you rate us on Google Play?")
    .setPositiveButton("Yes", this)
    .setNegativeButton("No", this)
    //.setRemindMeLaterButton(getString(R.string.app_rate_modal_remind_me_later_label), this) // optional
    //.useDontShowAgainCheckbox(getString(R.string.app_rate_modal_don_t_show_again)) // optional
    //.setOnlyOneTime(true) // optional, default is false
    .launch()
```

**Step 3 ( another way to use ).**
```kotlin
 val simpleAppRatingPrompt: SimpleAppRatingPrompt = SimpleAppRatingPrompt(this)
    .setTitle("Rate us")
    .setMessage("Did you enjoy the app? Can you rate us on Google Play?")
    .setPositiveButton("Yes", this)
    .setNegativeButton("No", this)
    .incrementLaunchCount()

val launchCount = simpleAppRatingPrompt.getLaunchCount()
val possibilities = setOf(5, 10, 15)
if(launchCount in possibilities || launchCount % 20 == 0) {
    simpleAppRatingPrompt.showPrompt()
}
```


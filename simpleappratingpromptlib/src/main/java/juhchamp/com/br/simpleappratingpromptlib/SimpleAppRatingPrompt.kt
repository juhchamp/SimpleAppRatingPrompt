package juhchamp.com.br.simpleappratingpromptlib

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.TimeUnit

/**
 * This class serves a simple app rating dialog box.
 * @author José Jailton da Silva Júnior ( JuhChamp )
 */
class SimpleAppRatingPrompt(private var activity: AppCompatActivity) {

    private var prefs: SharedPreferences = activity.getSharedPreferences("simple_app_rating", 0)
    private var title: String = activity.getString(R.string.app_rating_default_prompt_title)
    private var message: String = activity.getString(R.string.app_rating_default_prompt_message)
    private var dontShowAgainText: String = activity.getString(R.string.app_rating_default_don_t_show_again_label)
    private var positiveButtonText: String = activity.getString(R.string.app_rating_default_positive_button_label)
    private var negativeButtonText: String = activity.getString(R.string.app_rating_default_negative_button_label)
    private var remindMeLaterButtonText: String = activity.getString(R.string.app_rating_default_remind_me_later_button_label)
    private var minLaunchCountToPrompt = 3
    private var timeToNextPrompt: Long = 2
    private var savedFirstLaunchTime: Long = 0L
    private var savedLaunchCount: Int = 0
    private var onlyOneTime = false
    private var useRemindLaterButton = false
    private var useDontShowAgainCheckbox = false
    private var positiveButtonListener: RatingPromptClickListener? = null
    private var negativeButtonListener: RatingPromptClickListener? = null
    private var remindMeLaterButtonListener: RatingPromptClickListener? = null

    /**
     * Set the minimum launch count to show the prompt.
     * If the value passed is 0, the prompt show immediately
     * Otherwise, the prompt wait the count to show at first time
     * Example: when the value is 3, the user open the app for the 3 times and the prompt showns
     * When the time to next prompt occurs, the user only see the prompt after 3 times opened app again
     * @param count [Int] value representing the minimum count to show prompt for fist time
     */
    fun setMinLaunchCountToPrompt(count: Int): SimpleAppRatingPrompt {
        this.minLaunchCountToPrompt = count
        return this
    }

    /**
     * Set the max time in days to wait and show prompt again.
     *
     * The prompt shows again when the actual time in milliseconds is
     * larger than the call time in milliseconds + the defined next time days in milliseconds:
     *
     * *System.currentTimeMillis() + TimeUnit.DAYS.toMillis(timeToNextPrompt)*
     *
     * If you set the days value to 0, the prompt will shown immediately,
     * based only in the value that you set in setMinLaunchCountToPrompt(),
     * its means that the prompt wait the defined launch count and shown consecutive
     * when the savedLaunchCount value reach the defined value
     *
     * @param days [Long] value representing the days for the next launch ( default is 2 )
     */
    fun setTimeToNextPrompt(days: Long): SimpleAppRatingPrompt {
        this.timeToNextPrompt = days
        return this
    }

    /**
     * Set the title for the dialog.
     * @param title [String] for the title
     * @return [SimpleAppRatingPrompt]
     */
    fun setTitle(title: String): SimpleAppRatingPrompt {
        this.title = title
        return this
    }

    /**
     * Set the message for the dialog.
     * @param message [String] for the message
     * @return [SimpleAppRatingPrompt]
     */
    fun setMessage(message: String): SimpleAppRatingPrompt {
        this.message = message
        return this
    }

    /**
     * Set the positive button for the dialog.
     * @param text [String] for the button text
     * @param listener The [RatingPromptClickListener] to use
     * @return [SimpleAppRatingPrompt]
     */
    fun setPositiveButton(text: String, listener: RatingPromptClickListener): SimpleAppRatingPrompt {
        this.positiveButtonText = text
        this.positiveButtonListener = listener
        return this
    }

    /**
     * Set the negative button for the dialog.
     * @param text [String] for the button text
     * @param listener The [RatingPromptClickListener] to use
     * @return [SimpleAppRatingPrompt]
     */
    fun setNegativeButton(text: String, listener: RatingPromptClickListener): SimpleAppRatingPrompt {
        this.negativeButtonText = text
        this.negativeButtonListener = listener
        return this
    }

    /**
     * Set the remind-me later button for the dialog.
     * @param text [String] for the button text
     * @param listener The [RatingPromptClickListener] to use
     * @return [SimpleAppRatingPrompt]
     */
    fun setRemindMeLaterButton(text: String, listener: RatingPromptClickListener): SimpleAppRatingPrompt {
        this.remindMeLaterButtonText = text
        this.remindMeLaterButtonListener = listener
        this.useRemindLaterButton = true
        return this
    }

    /**
     * Set the "don't show again" checkbox in dialog.
     * @param text [String] for the "don't show again" checkbox
     * @return [SimpleAppRatingPrompt]
     */
    fun useDontShowAgainCheckbox(text: String): SimpleAppRatingPrompt {
        this.dontShowAgainText = text
        useDontShowAgainCheckbox = true
        return this
    }

    /**
     * Set if the prompt will be displayed only one time.
     * If passed value is true, the dialog shows only one time and never show again later.
     * @param value [Boolean] true or false
     */
    fun setOnlyOneTime(value: Boolean): SimpleAppRatingPrompt {
        this.onlyOneTime = value
        return this
    }

    /**
     * Launch the prompt and apply configurations to show or wait next call.
     */
    fun launch() {
        if (prefs.getBoolean(DON_T_SHOW_AGAIN_KEY, false)) return

        savedFirstLaunchTime = prefs.getLong(FIST_LAUNCH_TIME_KEY, 0L)
        savedLaunchCount = prefs.getInt(LAUNCH_COUNT_KEY, 0) + 1

        val currentTime = System.currentTimeMillis()
        val editor = prefs.edit()

        if (savedFirstLaunchTime == 0L) {
            editor.putLong(FIST_LAUNCH_TIME_KEY, currentTime)
        }

        editor.putInt(LAUNCH_COUNT_KEY, savedLaunchCount)

        if (savedLaunchCount >= minLaunchCountToPrompt) {
            if (currentTime >= savedFirstLaunchTime) {
                savedFirstLaunchTime = currentTime + TimeUnit.DAYS.toMillis(timeToNextPrompt)
                editor.putLong(FIST_LAUNCH_TIME_KEY, savedFirstLaunchTime)
                editor.putInt(LAUNCH_COUNT_KEY, 0)
                editor.putBoolean(DON_T_SHOW_AGAIN_KEY, onlyOneTime)
                showDialog()
            }
        }
        editor.apply()
    }

    private fun showDialog() {

        if(positiveButtonListener == null) {
            throw NullPointerException("positiveButtonListener cannot be null! " +
                    "Use setPositiveButton() to setup positive button")
        }

        if(negativeButtonListener == null) {
            throw NullPointerException("negativeButtonListener cannot be null! " +
                    "Use setNegativeButton() to setup positive button")
        }

        val builder: AlertDialog = AlertDialog.Builder(activity)
            .setTitle(title)
            .setMessage(message)
            .create()

        builder.setButton(
            POSITIVE_BUTTON, positiveButtonText
        ) { dialog, whichButton ->
            positiveButtonListener?.onRatingPromptButtonClick(whichButton)
            dialog.cancel()
        }

        builder.setButton(
            NEGATIVE_BUTTON, negativeButtonText
        ) { dialog, whichButton ->
            negativeButtonListener?.onRatingPromptButtonClick(whichButton)
            dialog.cancel()
        }

        if(this.useRemindLaterButton) {
            builder.setButton(
                REMIND_ME_LATER_BUTTON, remindMeLaterButtonText
            ) { dialog, whichButton ->
                remindMeLaterButtonListener?.onRatingPromptButtonClick(whichButton)
                dialog.cancel()
            }
        }

        if(this.useDontShowAgainCheckbox) {
            if(!this.onlyOneTime && !this.useRemindLaterButton) {
                addCustomView(builder)
            }
        }
        builder.show()
    }

    @SuppressLint("InflateParams")
    private fun addCustomView(builder: AlertDialog) {
        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.layout_rate_prompt, null)
        val dontShowAgainCheckBox = view.findViewById<CheckBox>(R.id.dontShowAgainCheckBox)
        dontShowAgainCheckBox.text = this.dontShowAgainText
        dontShowAgainCheckBox.setOnCheckedChangeListener { _, isChecked ->
            val editor = prefs.edit()
            editor.putBoolean(DON_T_SHOW_AGAIN_KEY, isChecked)
            editor.apply()
        }
        builder.setView(view)
    }

    companion object {
        private const val FIST_LAUNCH_TIME_KEY = "first_launch_time"
        private const val LAUNCH_COUNT_KEY = "launch_count"
        private const val DON_T_SHOW_AGAIN_KEY = "don_t_show_again"

        const val POSITIVE_BUTTON = AlertDialog.BUTTON_POSITIVE
        const val NEGATIVE_BUTTON = AlertDialog.BUTTON_NEGATIVE
        const val REMIND_ME_LATER_BUTTON = AlertDialog.BUTTON_NEUTRAL
    }
}

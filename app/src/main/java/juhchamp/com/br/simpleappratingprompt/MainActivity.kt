package juhchamp.com.br.simpleappratingprompt

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import juhchamp.com.br.simpleappratingpromptlib.RatingPromptClickListener
import juhchamp.com.br.simpleappratingpromptlib.SimpleAppRatingPrompt

class MainActivity : AppCompatActivity(), RatingPromptClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SimpleAppRatingPrompt(this)
            .setMinLaunchCountToPrompt(3)
            .setTimeToNextPrompt(0) // In days
            .setTitle(getString(R.string.app_rate_modal_title))
            .setMessage(getString(R.string.app_rate_modal_message))
            .setPositiveButton(getString(R.string.app_rate_modal_like_label), this)
            .setNegativeButton(getString(R.string.app_rate_modal_don_t_like_label), this)
            //.setRemindMeLaterButton(getString(R.string.app_rate_modal_remind_me_later_label), this) // optional
            //.useDontShowAgainCheckbox(getString(R.string.app_rate_modal_don_t_show_again)) // optional
            //.setOnlyOneTime(true) // optional, default is false
            .launch()
    }

    override fun onRatingPromptButtonClick(whichButton: Int) {
        when(whichButton) {
            SimpleAppRatingPrompt.POSITIVE_BUTTON -> {
                openMarketAppPage()
            }
            SimpleAppRatingPrompt.NEGATIVE_BUTTON -> {
                Toast.makeText(this,"NEGATIVE_BUTTON", Toast.LENGTH_SHORT).show()
            }
            SimpleAppRatingPrompt.REMIND_ME_LATER_BUTTON -> {
                Toast.makeText(this,"REMIND_ME_LATER_BUTTON", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openMarketAppPage() {
        try{
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=${packageName}")
                )
            )
        } catch(e: ActivityNotFoundException) {
            val url = "https://play.google.com/store/apps/details?id=$packageName"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }
    }
}

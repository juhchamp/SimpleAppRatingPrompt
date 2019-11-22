package juhchamp.com.br.simpleappratingpromptlib

/**
 * Interface used to allow to run some code when an button on the dialog is clicked.
 */
interface RatingPromptClickListener {
    fun onRatingPromptButtonClick(whichButton: Int)
}
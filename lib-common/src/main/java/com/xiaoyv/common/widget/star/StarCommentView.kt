package com.xiaoyv.common.widget.star

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.xiaoyv.common.api.parser.entity.TimelineEntity
import com.xiaoyv.common.databinding.ViewStarCommentBinding
import com.xiaoyv.common.kts.inflater

/**
 * Class: [StarCommentView]
 *
 * @author why
 * @since 11/25/23
 */
class StarCommentView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {
    private val binding = ViewStarCommentBinding.inflate(context.inflater, this, true)

    var data: TimelineEntity.CollectionInfo? = null
        set(value) {
            field = value
            refreshRating()
        }

    private fun refreshRating() {
        // starlight stars7
        val score = data?.score.orEmpty()
            .split(" ")
            .find { it.startsWith("stars") }.orEmpty()
            .replace("stars", "")
            .toFloatOrNull() ?: 0f

        binding.ivStar.rating = score / 2f
        binding.tvComment.text = data?.comment.orEmpty()
        binding.tvComment.isVisible = data?.comment.orEmpty().isNotBlank()
    }
}
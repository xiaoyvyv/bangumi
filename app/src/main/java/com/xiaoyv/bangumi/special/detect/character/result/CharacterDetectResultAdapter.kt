package com.xiaoyv.bangumi.special.detect.character.result

import android.graphics.Typeface
import com.blankj.utilcode.util.SpanUtils
import com.xiaoyv.bangumi.databinding.ActivityDetectCharacterResultItemBinding
import com.xiaoyv.common.api.response.anime.DetectCharacterEntity
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.widget.binder.BaseQuickBindingAdapter
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.kts.getAttrColor
import com.xiaoyv.widget.kts.loadImage
import com.xiaoyv.widget.kts.spi


/**
 * Class: [CharacterDetectResultAdapter]
 *
 * @author why
 * @since 11/21/23
 */
class CharacterDetectResultAdapter :
    BaseQuickBindingAdapter<DetectCharacterEntity, ActivityDetectCharacterResultItemBinding>() {
    internal var onCharacterClick: ((String, String) -> Unit)? = null

    override fun BaseQuickBindingHolder<ActivityDetectCharacterResultItemBinding>.converted(item: DetectCharacterEntity) {
        val characters = item.char.orEmpty()
        val textColor = context.getAttrColor(GoogleAttr.colorOnSurfaceVariant)

        binding.ivAvatar.loadImage(item.imageFile)

        SpanUtils.with(binding.tvCharacter)
            .also {
                characters.forEachIndexed { index, character ->
                    val from = context.getString(
                        CommonString.anime_character_result_from,
                        character.cartoonname
                    )

                    it.append(character.name.orEmpty())
                        .setFontSize(16.spi)
                        .setTypeface(Typeface.DEFAULT_BOLD)
                        .setClickSpan(textColor, false) {
                            onCharacterClick?.invoke(
                                character.name.orEmpty(),
                                character.cartoonname.orEmpty()
                            )
                        }
                    it.appendLine()
                    it.append(from)
                        .setFontSize(12.spi)
                        .setTypeface(Typeface.DEFAULT)
                        .setClickSpan(textColor, false) {
                            onCharacterClick?.invoke(
                                character.name.orEmpty(),
                                character.cartoonname.orEmpty()
                            )
                        }
                    if (index != characters.size - 1) {
                        it.appendLine()
                        it.appendLine()
                    }
                }
            }.create()
    }
}
package com.xiaoyv.bangumi.special.detect.character

import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.special.detect.ImageDetectActivity
import com.xiaoyv.bangumi.special.detect.character.result.CharacterDetectResultActivity
import com.xiaoyv.blueprint.kts.activity
import com.xiaoyv.blueprint.kts.open
import com.xiaoyv.common.kts.CommonString

/**
 * ImageDetectCharacterActivity
 *
 * @author why
 * @since 11/21/23
 */
class ImageDetectCharacterActivity : ImageDetectActivity<ImageDetectCharacterViewModel>() {
    override val searchEngineUrl: String
        get() = "https://ai.animedb.cn"

    override fun onConfigView() {
        binding.toolbar.title = getString(CommonString.anime_character_title)
        binding.tvUploadTitle.text = getString(CommonString.anime_character_subtitle)
        binding.tvUploadDesc.text = getString(CommonString.anime_character_desc)
        binding.tvUploadCardTitle.text = getString(CommonString.anime_character_upload_image)
        binding.tvUploadCardLimit.text = getString(CommonString.anime_character_upload_limit)
        binding.tvUploadCardBtn.text = getString(CommonString.anime_character_upload_btn)
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onDetectCharacterLiveData.observe(this) {
            if (it == null) {
                Toast.makeText(activity, getString(CommonString.detect_failed), Toast.LENGTH_SHORT)
                    .show()
                return@observe
            }
            CharacterDetectResultActivity::class.open(paramParcelable1 = it)
        }
    }
}
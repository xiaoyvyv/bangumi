package com.xiaoyv.bangumi.special.detect.anime

import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.special.detect.ImageDetectActivity
import com.xiaoyv.bangumi.special.detect.anime.result.AnimeDetectResultActivity
import com.xiaoyv.blueprint.kts.activity
import com.xiaoyv.blueprint.kts.open
import com.xiaoyv.common.kts.CommonString

/**
 * ImageDetectAnimeActivity
 *
 * @author why
 * @since 11/18/23
 */
class ImageDetectAnimeActivity : ImageDetectActivity<ImageDetectAnimeViewModel>() {
    override val searchEngineUrl: String
        get() = "https://trace.moe"

    override fun onConfigView() {
        binding.toolbar.title = getString(CommonString.anime_search_title)
        binding.tvUploadTitle.text = getString(CommonString.anime_search_subtitle)
        binding.tvUploadDesc.text = getString(CommonString.anime_search_desc)
        binding.tvUploadCardTitle.text = getString(CommonString.anime_search_upload_image)
        binding.tvUploadCardLimit.text = getString(CommonString.anime_search_upload_limit)
        binding.tvUploadCardBtn.text = getString(CommonString.anime_search_upload_btn)
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onAnimeSourceLiveData.observe(this) {
            if (it == null) {
                Toast.makeText(activity, getString(CommonString.detect_failed), Toast.LENGTH_SHORT)
                    .show()
                return@observe
            }
            AnimeDetectResultActivity::class.open(paramParcelable1 = it)
        }
    }
}
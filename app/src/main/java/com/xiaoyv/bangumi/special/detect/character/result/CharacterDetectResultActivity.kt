package com.xiaoyv.bangumi.special.detect.character.result

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.blankj.utilcode.util.ClipboardUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xiaoyv.bangumi.databinding.ActivityDetectCharacterResultBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.activity
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.openInBrowser
import com.xiaoyv.widget.kts.getParcelObj

/**
 * Class: [CharacterDetectResultActivity]
 *
 * @author why
 * @since 11/21/23
 */
class CharacterDetectResultActivity :
    BaseViewModelActivity<ActivityDetectCharacterResultBinding, CharacterDetectResultViewModel>() {

    private val itemAdapter by lazy { CharacterDetectResultAdapter() }

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.detectCharacterResult = bundle.getParcelObj(NavKey.KEY_PARCELABLE)
    }

    override fun initView() {
        binding.toolbar.initNavBack(this)

        binding.rvItems.adapter = itemAdapter
    }

    override fun initData() {
        itemAdapter.submitList(viewModel.detectCharacterResult?.list.orEmpty())
    }

    override fun initListener() {
        itemAdapter.onCharacterClick = { name, from ->
            val title = getString(CommonString.anime_character_result_copy)
            val open = getString(CommonString.anime_character_result_bing)
            MaterialAlertDialogBuilder(activity)
                .setTitle(title)
                .setItems(arrayOf(name, from, open)) { _, which ->
                    when (which) {
                        0 -> ClipboardUtils.copyText(name)
                        1 -> ClipboardUtils.copyText(from)
                        2 -> openInBrowser("https://www.bing.com/images/search?q=${name}")
                    }
                }
                .create()
                .show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}
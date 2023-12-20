package com.xiaoyv.bangumi.ui.feature.preview.image

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xiaoyv.bangumi.ui.feature.preview.image.page.PreviewPageFragment

/**
 * Class: [PreviewImageAdapter]
 *
 * @author why
 * @since 12/1/23
 */
class PreviewImageAdapter(fragmentActivity: FragmentActivity, private val imageUrls: List<String>) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return imageUrls.size
    }

    override fun createFragment(position: Int): Fragment {
        return PreviewPageFragment.newInstance(
            imageUrl = imageUrls[position],
            position,
            imageUrls.size
        )
    }
}
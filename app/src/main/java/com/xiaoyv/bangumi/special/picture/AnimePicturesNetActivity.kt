package com.xiaoyv.bangumi.special.picture

import com.xiaoyv.bangumi.databinding.ActivityAnimePicturesBinding
import com.xiaoyv.bangumi.special.picture.gallery.AnimeGalleryActivity
import com.xiaoyv.blueprint.base.binding.BaseBindingActivity
import com.xiaoyv.blueprint.kts.open

/**
 * Class: [AnimePicturesNetActivity]
 *
 * - https://api.anime-pictures.net/api/v3/posts?page=0&lang=zh_CN
 * - https://api.anime-pictures.net/api/v3/posts?page=1&order_by=date&ldate=0&lang=zh_CN
 *
 * @author why
 * @since 12/20/23
 */
class AnimePicturesNetActivity : BaseBindingActivity<ActivityAnimePicturesBinding>() {

    override fun initView() {
        AnimeGalleryActivity::class.open()
    }

    override fun initData() {

    }
}
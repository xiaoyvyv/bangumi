package com.xiaoyv.bangumi.special.thunder.torrent

import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.FileUtils
import com.xiaoyv.bangumi.databinding.ActivityTorrentInfoItemBinding
import com.xiaoyv.common.config.bean.TorrentInfoWrap
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.FileExtension
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.loadFileIconByExtension
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.getAttrColor
import com.xiaoyv.widget.kts.orEmpty
import com.xiaoyv.widget.kts.setImageTintColorInt

/**
 * Class: [TorrentInfoAdapter]
 *
 * @author why
 * @since 3/24/24
 */
class TorrentInfoAdapter :
    BaseQuickDiffBindingAdapter<TorrentInfoWrap, ActivityTorrentInfoItemBinding>(Diff()) {
    private val fileExtension by lazy { FileExtension() }

    override fun BaseQuickBindingHolder<ActivityTorrentInfoItemBinding>.converted(item: TorrentInfoWrap) {
        if (item.isDir) {
            binding.ivIcon.setImageTintColorInt(context.getAttrColor(GoogleAttr.colorPrimary))
            binding.ivIcon.setImageResource(CommonDrawable.ic_folder)
            binding.tvDesc.isVisible = false
            binding.tvTitle.text = item.dirPath.trimEnd('/').substringAfterLast("/")
        } else {
            fileExtension.loadFileIconByExtension(
                context = context,
                extension = "." + FileUtils.getFileExtension(item.data?.mFileName)
            ).apply {
                binding.ivIcon.setImageResource(first)
                binding.ivIcon.setImageTintColorInt(second)
            }

            binding.tvDesc.isVisible = true
            binding.tvTitle.text = item.data?.mFileName
        }

        binding.tvDesc.text = if (item.isDir) "" else {
            ConvertUtils.byte2FitMemorySize(item.data?.mFileSize.orEmpty(), 2)
        }
    }

    private class Diff : DiffUtil.ItemCallback<TorrentInfoWrap>() {
        override fun areItemsTheSame(oldItem: TorrentInfoWrap, newItem: TorrentInfoWrap) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: TorrentInfoWrap, newItem: TorrentInfoWrap) =
            oldItem == newItem
    }
}
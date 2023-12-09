package com.xiaoyv.common.helper.callback

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

/**
 * Class: [IdDiffItemCallback]
 *
 * @author why
 * @since 12/6/23
 */
interface IdEntity {
    var id: String
}

class IdDiffItemCallback<T : IdEntity> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}
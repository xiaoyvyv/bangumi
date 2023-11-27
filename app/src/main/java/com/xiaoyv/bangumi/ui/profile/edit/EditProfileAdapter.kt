package com.xiaoyv.bangumi.ui.profile.edit

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.chad.library.adapter.base.BaseMultiItemAdapter.OnItemViewTypeListener
import com.xiaoyv.bangumi.databinding.ActivityEditProfileAvatarBinding
import com.xiaoyv.bangumi.databinding.ActivityEditProfileInputBinding
import com.xiaoyv.bangumi.databinding.ActivityEditProfileSelectorBinding
import com.xiaoyv.common.api.parser.entity.SettingBaseEntity
import com.xiaoyv.common.config.annotation.EditProfileOptionType
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder

/**
 * Class: [EditProfileAdapter]
 *
 * @author why
 * @since 11/27/23
 */
class EditProfileAdapter : BaseMultiItemAdapter<SettingBaseEntity>() {

    init {
        this.addItemType(TYPE_TEXT, EditProfileInputBinder())
            .addItemType(TYPE_FILE, EditProfileAvatarBinder())
            .addItemType(TYPE_SELECT, EditProfileSelectorBinder())
            .onItemViewType(OnItemViewTypeListener { position, list ->
                return@OnItemViewTypeListener when (list[position].type) {
                    EditProfileOptionType.TYPE_INPUT -> TYPE_TEXT
                    EditProfileOptionType.TYPE_FILE -> TYPE_FILE
                    EditProfileOptionType.TYPE_SELECTOR -> TYPE_SELECT
                    else -> TYPE_TEXT
                }
            })
    }

    class EditProfileSelectorBinder :
        OnMultiItemAdapterListener<SettingBaseEntity, BaseQuickBindingHolder<ActivityEditProfileSelectorBinding>> {
        override fun onBind(
            holder: BaseQuickBindingHolder<ActivityEditProfileSelectorBinding>,
            position: Int,
            item: SettingBaseEntity?
        ) {
            holder.binding.tvName.text = item?.title
            holder.binding.tvValue.text = item?.value.orEmpty().ifBlank { "未设置" }
        }

        override fun onCreate(
            context: Context,
            parent: ViewGroup,
            viewType: Int
        ): BaseQuickBindingHolder<ActivityEditProfileSelectorBinding> {
            return BaseQuickBindingHolder(
                ActivityEditProfileSelectorBinding.inflate(context.inflater, parent, false)
            )
        }
    }

    class EditProfileAvatarBinder :
        OnMultiItemAdapterListener<SettingBaseEntity, BaseQuickBindingHolder<ActivityEditProfileAvatarBinding>> {
        override fun onBind(
            holder: BaseQuickBindingHolder<ActivityEditProfileAvatarBinding>,
            position: Int,
            item: SettingBaseEntity?
        ) {
            holder.binding.tvName.text = item?.title
            holder.binding.tvValue.loadImageAnimate(item?.value)
        }

        override fun onCreate(
            context: Context,
            parent: ViewGroup,
            viewType: Int
        ): BaseQuickBindingHolder<ActivityEditProfileAvatarBinding> {
            return BaseQuickBindingHolder(
                ActivityEditProfileAvatarBinding.inflate(context.inflater, parent, false)
            )
        }
    }

    class EditProfileInputBinder :
        OnMultiItemAdapterListener<SettingBaseEntity, BaseQuickBindingHolder<ActivityEditProfileInputBinding>> {
        override fun onBind(
            holder: BaseQuickBindingHolder<ActivityEditProfileInputBinding>,
            position: Int,
            item: SettingBaseEntity?
        ) {
            holder.binding.tvName.text = item?.title
            holder.binding.tvValue.text = item?.value.orEmpty().ifBlank { "未设置" }
        }

        override fun onCreate(
            context: Context,
            parent: ViewGroup,
            viewType: Int
        ): BaseQuickBindingHolder<ActivityEditProfileInputBinding> {
            return BaseQuickBindingHolder(
                ActivityEditProfileInputBinding.inflate(context.inflater, parent, false)
            )
        }
    }

    companion object {
        const val TYPE_TEXT = 0
        const val TYPE_FILE = 1
        const val TYPE_SELECT = 2
    }
}
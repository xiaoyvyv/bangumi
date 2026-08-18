package com.xiaoyv.bangumi.features.preivew.album

import com.xiaoyv.bangumi.features.preivew.album.business.PreviewAlbumViewModel
import com.xiaoyv.bangumi.shared.data.model.request.list.album.ListAlbumParam
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val previewAlbumModule = module {
    viewModel { (param: ListAlbumParam) ->
        PreviewAlbumViewModel(param = param, imageRepository = get())
    }
}

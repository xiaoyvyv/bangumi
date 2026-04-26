package com.xiaoyv.bangumi.shared.data.di

import com.xiaoyv.bangumi.shared.core.utils.defaultJson
import com.xiaoyv.bangumi.shared.data.api.client.BgmApiClient
import com.xiaoyv.bangumi.shared.data.api.client.cookie.BgmCookieStorage
import com.xiaoyv.bangumi.shared.data.manager.app.PersonalStateStore
import com.xiaoyv.bangumi.shared.data.manager.app.PreferenceStore
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.parser.MikanParser
import com.xiaoyv.bangumi.shared.data.parser.SignParser
import com.xiaoyv.bangumi.shared.data.parser.bgm.BlogParser
import com.xiaoyv.bangumi.shared.data.parser.bgm.CommentParser
import com.xiaoyv.bangumi.shared.data.parser.bgm.GroupParser
import com.xiaoyv.bangumi.shared.data.parser.bgm.HomeParser
import com.xiaoyv.bangumi.shared.data.parser.bgm.IndexParser
import com.xiaoyv.bangumi.shared.data.parser.bgm.MonoParser
import com.xiaoyv.bangumi.shared.data.parser.bgm.NotificationParser
import com.xiaoyv.bangumi.shared.data.parser.bgm.SubjectParser
import com.xiaoyv.bangumi.shared.data.parser.bgm.TimelineParser
import com.xiaoyv.bangumi.shared.data.parser.bgm.TopicParser
import com.xiaoyv.bangumi.shared.data.parser.bgm.TopicTableParser
import com.xiaoyv.bangumi.shared.data.parser.bgm.UserParser
import com.xiaoyv.bangumi.shared.data.repository.BlogRepository
import com.xiaoyv.bangumi.shared.data.repository.CacheRepository
import com.xiaoyv.bangumi.shared.data.repository.ChoreRepository
import com.xiaoyv.bangumi.shared.data.repository.CollectionRepository
import com.xiaoyv.bangumi.shared.data.repository.DatabaseRepository
import com.xiaoyv.bangumi.shared.data.repository.GroupRepository
import com.xiaoyv.bangumi.shared.data.repository.ImageRepository
import com.xiaoyv.bangumi.shared.data.repository.IndexRepository
import com.xiaoyv.bangumi.shared.data.repository.MikanRepository
import com.xiaoyv.bangumi.shared.data.repository.MonoRepository
import com.xiaoyv.bangumi.shared.data.repository.PixivRepository
import com.xiaoyv.bangumi.shared.data.repository.SignRepository
import com.xiaoyv.bangumi.shared.data.repository.SubjectRepository
import com.xiaoyv.bangumi.shared.data.repository.TopicRepository
import com.xiaoyv.bangumi.shared.data.repository.TraceRepository
import com.xiaoyv.bangumi.shared.data.repository.UgcRepository
import com.xiaoyv.bangumi.shared.data.repository.UserRepository
import com.xiaoyv.bangumi.shared.data.repository.datasource.createPagingConfig
import com.xiaoyv.bangumi.shared.data.repository.impl.BlogRepositoryImpl
import com.xiaoyv.bangumi.shared.data.repository.impl.CacheRepositoryImpl
import com.xiaoyv.bangumi.shared.data.repository.impl.ChoreRepositoryImpl
import com.xiaoyv.bangumi.shared.data.repository.impl.CollectionRepositoryImpl
import com.xiaoyv.bangumi.shared.data.repository.impl.DatabaseRepositoryImpl
import com.xiaoyv.bangumi.shared.data.repository.impl.GroupRepositoryImpl
import com.xiaoyv.bangumi.shared.data.repository.impl.ImageRepositoryImpl
import com.xiaoyv.bangumi.shared.data.repository.impl.IndexRepositoryImpl
import com.xiaoyv.bangumi.shared.data.repository.impl.MikanRepositoryImpl
import com.xiaoyv.bangumi.shared.data.repository.impl.MonoRepositoryImpl
import com.xiaoyv.bangumi.shared.data.repository.impl.PixivRepositoryImpl
import com.xiaoyv.bangumi.shared.data.repository.impl.SignRepositoryImpl
import com.xiaoyv.bangumi.shared.data.repository.impl.SubjectRepositoryImpl
import com.xiaoyv.bangumi.shared.data.repository.impl.TopicRepositoryImpl
import com.xiaoyv.bangumi.shared.data.repository.impl.TraceRepositoryImpl
import com.xiaoyv.bangumi.shared.data.repository.impl.UgcRepositoryImpl
import com.xiaoyv.bangumi.shared.data.repository.impl.UserRepositoryImpl
import com.xiaoyv.bangumi.shared.data.usecase.ImageRepoUseCase
import com.xiaoyv.bangumi.shared.data.usecase.MonoRepoUseCase
import com.xiaoyv.bangumi.shared.data.usecase.PixivRepoUseCase
import com.xiaoyv.bangumi.shared.data.usecase.SubjectRepoUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

private val dataModule = module {
    singleOf(::PreferenceStore)
    singleOf(::BgmApiClient)
    singleOf(::BgmCookieStorage)
    singleOf(::UserManager)
    singleOf(::PersonalStateStore)
    single { createPagingConfig(20) }
}

private val repositoryModules = module {
    single<SignRepository> { SignRepositoryImpl(get(), get()) }
    single<PixivRepository> { PixivRepositoryImpl(get(), get()) }
    single<TraceRepository> { TraceRepositoryImpl(get(), get()) }
    single<DatabaseRepository> { DatabaseRepositoryImpl(get()) }
    single<ImageRepository> { ImageRepositoryImpl(get(), get(), get()) }
    single<CacheRepository> { CacheRepositoryImpl() }
    single<UserRepository> { UserRepositoryImpl(get(), get(), get(), get(), get()) }
    single<MikanRepository> { MikanRepositoryImpl(get(), get(), get()) }
    single<SubjectRepository> { SubjectRepositoryImpl(get(), get(), get(), get(), get()) }
    single<MonoRepository> { MonoRepositoryImpl(get(), get(), get()) }
    single<BlogRepository> { BlogRepositoryImpl(get(), get()) }
    single<UgcRepository> { UgcRepositoryImpl(get(), get(), get(), get(), get(), get(), get(), get()) }
    single<ChoreRepository> { ChoreRepositoryImpl(get()) }
    single<GroupRepository> { GroupRepositoryImpl(get(), get(), get()) }
    single<TopicRepository> { TopicRepositoryImpl(get(), get()) }
    single<CollectionRepository> { CollectionRepositoryImpl(get(), get(), get(), get()) }
    single<IndexRepository> { IndexRepositoryImpl(get(), get(), get()) }
}

private val useCaseModules = module {
    factoryOf(::MonoRepoUseCase)
    factoryOf(::SubjectRepoUseCase)
    factoryOf(::ImageRepoUseCase)
    factoryOf(::PixivRepoUseCase)
}

private val converterModules = module {
    singleOf(::CommentParser)
    singleOf(::SignParser)
    singleOf(::UserParser)
    singleOf(::SubjectParser)
    singleOf(::HomeParser)
    singleOf(::MikanParser)
    singleOf(::MonoParser)
    singleOf(::TimelineParser)
    singleOf(::TopicParser)
    singleOf(::TopicTableParser)
    singleOf(::GroupParser)
    singleOf(::BlogParser)
    singleOf(::IndexParser)
    singleOf(::NotificationParser)
    single { defaultJson }
}

val dataModules = arrayOf(
    dataModule,
    repositoryModules,
    converterModules,
    useCaseModules
)
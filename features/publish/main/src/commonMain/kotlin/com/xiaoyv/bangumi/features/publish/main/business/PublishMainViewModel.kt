package com.xiaoyv.bangumi.features.publish.main.business

import androidx.compose.ui.text.input.TextFieldValue
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.publish_attach_subject_cannot_remove
import com.xiaoyv.bangumi.core_resource.resources.publish_not_supported
import com.xiaoyv.bangumi.core_resource.resources.publish_title_blog
import com.xiaoyv.bangumi.core_resource.resources.publish_title_comment
import com.xiaoyv.bangumi.core_resource.resources.publish_title_group
import com.xiaoyv.bangumi.core_resource.resources.publish_title_subject
import com.xiaoyv.bangumi.core_resource.resources.timeline_add
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.postEffect
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.withActionLoading
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.PublishPostType
import com.xiaoyv.bangumi.shared.core.utils.BBCode
import com.xiaoyv.bangumi.shared.core.utils.asTextFieldValue
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.core.utils.insertBBCode
import com.xiaoyv.bangumi.shared.core.utils.sanitizeImageUrl
import com.xiaoyv.bangumi.shared.data.manager.app.PersonalStateStore
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.repository.BlogRepository
import com.xiaoyv.bangumi.shared.data.repository.ChoreRepository
import com.xiaoyv.bangumi.shared.data.repository.GroupRepository
import com.xiaoyv.bangumi.shared.data.repository.IndexRepository
import com.xiaoyv.bangumi.shared.data.repository.MonoRepository
import com.xiaoyv.bangumi.shared.data.repository.TimelineRepository
import com.xiaoyv.bangumi.shared.data.repository.TopicRepository
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.collections.immutable.toPersistentList
import org.jetbrains.compose.resources.getString

/**
 * [PublishMainViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class PublishMainViewModel(
    private val args: Screen.PublishMain,
    private val timelineRepository: TimelineRepository,
    private val choreRepository: ChoreRepository,
    private val monoRepository: MonoRepository,
    private val topicRepository: TopicRepository,
    private val blogRepository: BlogRepository,
    private val indexRepository: IndexRepository,
    private val groupRepository: GroupRepository,
    private val personalStateStore: PersonalStateStore,
) : BaseViewModel<PublishMainState, PublishMainSideEffect, PublishMainEvent.Action>() {

    override fun createInitialState() = PublishMainState(
        type = args.type,
        subject = args.publishAttachTitle.asTextFieldValue(),
        attachSubjects = args.attachSubjects.toPersistentList(),
        title = when (args.type) {
            PublishPostType.TIMELINE_STATUS -> Res.string.timeline_add
            PublishPostType.TOPIC_GROUP -> Res.string.publish_title_group
            PublishPostType.TOPIC_SUBJECT -> Res.string.publish_title_subject
            PublishPostType.BLOG -> Res.string.publish_title_blog
            PublishPostType.COMMENT_CHARACTER -> Res.string.publish_title_comment
            PublishPostType.COMMENT_PERSON -> Res.string.publish_title_comment
            PublishPostType.COMMENT_EP -> Res.string.publish_title_comment
            PublishPostType.COMMENT_INDEX -> Res.string.publish_title_comment
            else -> Res.string.timeline_add
        }
    )

    override fun onEvent(event: PublishMainEvent.Action) {
        when (event) {
            is PublishMainEvent.Action.OnRefresh -> refresh(contentLoading = event.loading)
            is PublishMainEvent.Action.OnTitleChange -> onTitleChange(event.title)
            is PublishMainEvent.Action.OnContentChange -> onContentChange(event.content)
            is PublishMainEvent.Action.OnAddSubject -> onAddSubject(event.subject)
            is PublishMainEvent.Action.OnRemoveSubject -> onRemoveSubject(event.subjectId)
            is PublishMainEvent.Action.OnAddTags -> onAddTags(event.tags)
            is PublishMainEvent.Action.OnRemoveTag -> onRemoveTag(event.tag)
            is PublishMainEvent.Action.OnPublicChange -> onPublicChange(event.public)
            is PublishMainEvent.Action.OnReceiveTurnstileToken -> onReceiveTurnstileToken(event.token)
            is PublishMainEvent.Action.OnPublish -> onPublish()
            is PublishMainEvent.Action.OnImagePickResult -> onImagePickResult(event.path)
        }
    }


    private fun onReceiveTurnstileToken(token: String) = intent {
        reduceData { state.copy(turnstileToken = token) }
    }

    private fun onTitleChange(title: TextFieldValue) = intent {
        reduceData { state.copy(subject = title) }
    }

    private fun onContentChange(content: TextFieldValue) = intent {
        reduceData { state.copy(content = content) }
    }

    private fun onPublicChange(public: Boolean) = intent {
        reduceData { state.copy(public = public) }
    }

    private fun onRemoveSubject(subjectId: Long) = intent {
        if (args.attachSubjects.any { it.id == subjectId }) {
            postToast { getString(Res.string.publish_attach_subject_cannot_remove) }
            return@intent
        }

        reduceData { state.copy(attachSubjects = state.attachSubjects.filter { it.id != subjectId }.toPersistentList()) }
    }

    private fun onAddSubject(subject: ComposeSubject) = intent {
        if (state.data.attachSubjects.size >= PublishMainState.MAX_ATTACH_SUBJECT_COUNT || state.data.attachSubjects.any { it.id == subject.id }) {
            return@intent
        }
        reduceData { state.copy(attachSubjects = (state.attachSubjects + subject).toPersistentList()) }
    }

    private fun onAddTags(tags: String) = intent {
        reduceData {
            state.copy(
                attachTags = (state.attachTags + tags
                    .split("[,，\\s]+".toRegex())
                    .filter(String::isNotBlank))
                    .distinct()
                    .toPersistentList()
            )
        }
    }

    private fun onRemoveTag(tag: String) = intent {
        reduceData { state.copy(attachTags = state.attachTags.filter { it != tag }.toPersistentList()) }
    }

    fun onImagePickResult(file: PlatformFile) = intent {
        withActionLoading { choreRepository.compressImageAndUpload(file) }
            .onFailure { postToast { it.errMsg } }
            .onSuccess {
                val imageUrl = it.thumbUrl.sanitizeImageUrl()
                val code = BBCode(hint = imageUrl, code = "img")

                reduceData {
                    state.copy(
                        content = state.content.insertBBCode(
                            prefix = if (state.content.text.isNotBlank()) "\n" else "",
                            suffix = "\n",
                            bbCode = code
                        )
                    )
                }
            }
    }

    private fun onPublish() = intent {
        withActionLoading {
            val title = state.subject.text.trim()
            val content = state.content.text.trim()
            val turnstile = state.turnstileToken
            val targetId = args.publishAttachId

            when (args.type) {
                PublishPostType.BLOG -> blogRepository.submitCreateBlog(
                    title = title,
                    content = content,
                    turnstile = turnstile,
                    tags = state.attachTags,
                    public = state.public,
                    subjectIDs = state.attachSubjects.map { it.id }
                )

                PublishPostType.COMMENT_CHARACTER -> monoRepository.submitMonoComment(
                    type = MonoType.CHARACTER,
                    monoId = targetId.toLong(),
                    content = content,
                    turnstile = turnstile
                )

                PublishPostType.COMMENT_PERSON -> monoRepository.submitMonoComment(
                    type = MonoType.PERSON,
                    monoId = targetId.toLong(),
                    content = content,
                    turnstile = turnstile
                )

                PublishPostType.COMMENT_EP -> topicRepository.submitSubjectEpisodeComment(
                    episodeId = targetId.toLong(),
                    content = content,
                    turnstile = turnstile
                )

                PublishPostType.COMMENT_INDEX -> indexRepository.submitIndexComment(
                    indexId = targetId.toLong(),
                    content = content,
                    turnstile = turnstile
                )

                PublishPostType.TIMELINE_STATUS -> timelineRepository.submitCreateTimeline(
                    content = content,
                    turnstileToken = turnstile
                )

                PublishPostType.TOPIC_GROUP -> groupRepository.submitCreateGroupTopic(
                    groupName = targetId,
                    title = title,
                    content = content,
                    turnstile = turnstile
                )

                PublishPostType.TOPIC_SUBJECT -> topicRepository.submitCreateSubjectTopic(
                    subjectId = targetId.toInt(),
                    title = title,
                    content = content,
                    turnstile = turnstile
                )

                else -> Result.failure(IllegalStateException(getString(Res.string.publish_not_supported)))
            }
        }.onFailure {
            reduceData { state.copy(turnstileRefreshKey = System.currentTimeMillis()) }
        }.onSuccess {
            personalStateStore.emitPublishSuccess(
                type = args.type,
                publishAttachId = args.publishAttachId,
                publishSuccessId = it.id.toString()
            )

            postEffect { PublishMainSideEffect.OnCreatePostSuccess(it.id) }
        }
    }
}

package com.xiaoyv.common.api.response

import android.os.Parcelable

import kotlinx.parcelize.Parcelize

import androidx.annotation.Keep

import com.google.gson.annotations.SerializedName


@Keep
@Parcelize
data class GithubActionEntity(
    @SerializedName("artifacts") var artifacts: List<Artifact>? = null,
    @SerializedName("total_count") var totalCount: Int = 0
) : Parcelable {

    @Keep
    @Parcelize
    data class Artifact(
        @SerializedName("archive_download_url") var archiveDownloadUrl: String? = null,
        @SerializedName("created_at") var createdAt: String? = null,
        @SerializedName("expired") var expired: Boolean = false,
        @SerializedName("expires_at") var expiresAt: String? = null,
        @SerializedName("id") var id: Long = 0,
        @SerializedName("name") var name: String? = null,
        @SerializedName("node_id") var nodeId: String? = null,
        @SerializedName("size_in_bytes") var sizeInBytes: Long = 0,
        @SerializedName("updated_at") var updatedAt: String? = null,
        @SerializedName("url") var url: String? = null,
        @SerializedName("workflow_run") var workflowRun: WorkflowRun? = null
    ) : Parcelable

    @Keep
    @Parcelize
    data class WorkflowRun(
        @SerializedName("head_branch") var headBranch: String? = null,
        @SerializedName("head_repository_id") var headRepositoryId: Long = 0,
        @SerializedName("head_sha") var headSha: String? = null,
        @SerializedName("id") var id: Long = 0,
        @SerializedName("repository_id") var repositoryId: Long = 0
    ) : Parcelable
}
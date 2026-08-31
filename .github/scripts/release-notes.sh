#!/usr/bin/env bash

set -euo pipefail

channel="$1"
release_tag="$2"
previous_commit=""
short_sha="${GITHUB_SHA:0:7}"
commit_timestamp="$(git show -s --format=%ct "$GITHUB_SHA")"

if [[ "$channel" == "pre-release" ]]; then
  previous_commit="$(gh release view "$release_tag" --json targetCommitish --jq '.targetCommitish' 2>/dev/null || true)"
  channel_notice="> 这是持续更新的 Preview 通道；GitHub 显示的 Release 创建时间不会刷新，请以本说明中的构建时间为准。"
else
  channel_notice=""
  previous_tag="$(gh release list --exclude-drafts --exclude-pre-releases --limit 100 --json tagName --jq '.[].tagName' \
    | grep -Fxv "$release_tag" \
    | head -n 1 || true)"
  if [[ -n "$previous_tag" ]]; then
    previous_commit="$(git rev-list -n 1 "$previous_tag" 2>/dev/null || true)"
  fi
fi

if [[ -n "$previous_commit" ]] && git cat-file -e "${previous_commit}^{commit}" 2>/dev/null; then
  commit_range="${previous_commit}..${GITHUB_SHA}"
else
  commit_range="$GITHUB_SHA"
fi

changes="$(git log --format="- [%h](${GITHUB_SERVER_URL}/${GITHUB_REPOSITORY}/commit/%H) %s" "$commit_range")"
if [[ -z "$changes" ]]; then
  changes="- 无新增提交"
fi

cat <<EOF
### 构建信息
- channel：${channel}
- versionCode：${VERSION_CODE}
- versionName：${VERSION_NAME}

${channel_notice}

构建自 [${short_sha}](${GITHUB_SERVER_URL}/${GITHUB_REPOSITORY}/commit/${GITHUB_SHA})

### 构建时间
$(TZ=Asia/Shanghai date -d "@${commit_timestamp}" '+%Y-%m-%d %H:%M:%S UTC+8')

### 提交记录
${changes}
EOF

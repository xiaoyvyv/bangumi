#!/usr/bin/env bash
openapi-generator generate \
  -i ./openapi.json \
  -g kotlin \
  -o ./pixiv-client \
  --api-package com.xiaoyv.bangumi.shared.data.model.response.pixiv.api \
  --model-package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model \
  --additional-properties=library=jvm-retrofit2,serializationLibrary=kotlinx_serialization,dateLibrary=kotlinx-datetime
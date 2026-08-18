#!/usr/bin/env bash
openapi-generator generate \
  -i https://next.bgm.tv/p1/openapi.json \
  -g kotlin \
  -o ./bgm-ktor-client \
  --api-package tv.bgm.client.api \
  --model-package tv.bgm.client.model \
  --additional-properties=library=multiplatform,serializationLibrary=kotlinx_serialization,dateLibrary=kotlinx-datetime
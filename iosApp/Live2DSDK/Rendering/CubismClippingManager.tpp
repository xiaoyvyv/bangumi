/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#pragma once

// template で宣言下 CubismClippingManager の実装を記述
template<class T_ClippingContext, class T_OffscreenSurface>
CubismClippingManager<T_ClippingContext, T_OffscreenSurface>::CubismClippingManager() :
        _clippingMaskBufferSize(256, 256) {
    CubismRenderer::CubismTextureColor *tmp = NULL;
    tmp = CSM_NEW
    CubismRenderer::CubismTextureColor();
    tmp->R = 1.0f;
    tmp->G = 0.0f;
    tmp->B = 0.0f;
    tmp->A = 0.0f;
    _channelColors.PushBack(tmp);
    tmp = CSM_NEW
    CubismRenderer::CubismTextureColor();
    tmp->R = 0.0f;
    tmp->G = 1.0f;
    tmp->B = 0.0f;
    tmp->A = 0.0f;
    _channelColors.PushBack(tmp);
    tmp = CSM_NEW
    CubismRenderer::CubismTextureColor();
    tmp->R = 0.0f;
    tmp->G = 0.0f;
    tmp->B = 1.0f;
    tmp->A = 0.0f;
    _channelColors.PushBack(tmp);
    tmp = CSM_NEW
    CubismRenderer::CubismTextureColor();
    tmp->R = 0.0f;
    tmp->G = 0.0f;
    tmp->B = 0.0f;
    tmp->A = 1.0f;
    _channelColors.PushBack(tmp);
}

template<class T_ClippingContext, class T_OffscreenSurface>
CubismClippingManager<T_ClippingContext, T_OffscreenSurface>::~CubismClippingManager() {
    for (csmUint32 i = 0; i < _clippingContextListForMask.GetSize(); i++) {
        if (_clippingContextListForMask[i]) CSM_DELETE_SELF(T_ClippingContext, _clippingContextListForMask[i]);
        _clippingContextListForMask[i] = NULL;
    }

    // _clippingContextListForDrawは_clippingContextListForMaskにあるインスタンスを指している。上記の処理により要素ごとのDELETEは不要。
    for (csmUint32 i = 0; i < _clippingContextListForDraw.GetSize(); i++) {
        _clippingContextListForDraw[i] = NULL;
    }

    for (csmUint32 i = 0; i < _channelColors.GetSize(); i++) {
        if (_channelColors[i]) CSM_DELETE(_channelColors[i]);
        _channelColors[i] = NULL;
    }

    if (_clearedMaskBufferFlags.GetSize() != 0) {
        _clearedMaskBufferFlags.Clear();
        _clearedMaskBufferFlags = NULL;
    }
}

template<class T_ClippingContext, class T_OffscreenSurface>
void CubismClippingManager<T_ClippingContext, T_OffscreenSurface>::Initialize(CubismModel &model, const csmInt32 maskBufferCount) {
    _renderTextureCount = maskBufferCount;

    // レンダーテクスチャのクリアフラグの設定
    for (csmInt32 i = 0; i < _renderTextureCount; ++i) {
        _clearedMaskBufferFlags.PushBack(false);
    }

    //クリッピングマスクを使う描画オブジェクトを全て登録する
    //クリッピングマスクは、通常数個程度に限定して使うものとする
    for (csmInt32 i = 0; i < model.GetDrawableCount(); i++) {
        if (model.GetDrawableMaskCounts()[i] <= 0) {
            //クリッピングマスクが使用されていないアートメッシュ（多くの場合使用しない）
            _clippingContextListForDraw.PushBack(NULL);
            continue;
        }

        // 既にあるClipContextと同じかチェックする
        T_ClippingContext *cc = FindSameClip(model.GetDrawableMasks()[i], model.GetDrawableMaskCounts()[i]);
        if (cc == NULL) {
            // 同一のマスクが存在していない場合は生成する
            cc = CSM_NEW
            T_ClippingContext(this, model, model.GetDrawableMasks()[i], model.GetDrawableMaskCounts()[i]);
            _clippingContextListForMask.PushBack(cc);
        }

        cc->AddClippedDrawable(i);

        _clippingContextListForDraw.PushBack(cc);
    }
}

template<class T_ClippingContext, class T_OffscreenSurface>
T_ClippingContext *CubismClippingManager<T_ClippingContext, T_OffscreenSurface>::FindSameClip(const csmInt32 *drawableMasks, csmInt32 drawableMaskCounts) const {
    // 作成済みClippingContextと一致するか確認
    for (csmUint32 i = 0; i < _clippingContextListForMask.GetSize(); i++) {
        T_ClippingContext *cc = _clippingContextListForMask[i];
        const csmInt32 count = cc->_clippingIdCount;
        if (count != drawableMaskCounts) continue; //個数が違う場合は別物
        csmInt32 samecount = 0;

        // 同じIDを持つか確認。配列の数が同じなので、一致した個数が同じなら同じ物を持つとする。
        for (csmInt32 j = 0; j < count; j++) {
            const csmInt32 clipId = cc->_clippingIdList[j];
            for (csmInt32 k = 0; k < count; k++) {
                if (drawableMasks[k] == clipId) {
                    samecount++;
                    break;
                }
            }
        }
        if (samecount == count) {
            return cc;
        }
    }
    return NULL; //見つからなかった
}

template<class T_ClippingContext, class T_OffscreenSurface>
void CubismClippingManager<T_ClippingContext, T_OffscreenSurface>::SetupMatrixForHighPrecision(CubismModel &model, csmBool isRightHanded) {
    // 全てのクリッピングを用意する
    // 同じクリップ（複数の場合はまとめて１つのクリップ）を使う場合は１度だけ設定する
    csmInt32 usingClipCount = 0;
    for (csmUint32 clipIndex = 0; clipIndex < _clippingContextListForMask.GetSize(); clipIndex++) {
        // １つのクリッピングマスクに関して
        T_ClippingContext *cc = _clippingContextListForMask[clipIndex];

        // このクリップを利用する描画オブジェクト群全体を囲む矩形を計算
        CalcClippedDrawTotalBounds(model, cc);

        if (cc->_isUsing) {
            usingClipCount++; //使用中としてカウント
        }
    }

    if (usingClipCount <= 0) {
        return;
    }
    // マスク行列作成処理
    SetupLayoutBounds(0);

    // サイズがレンダーテクスチャの枚数と合わない場合は合わせる
    if (_clearedMaskBufferFlags.GetSize() != _renderTextureCount) {
        _clearedMaskBufferFlags.Clear();

        for (csmInt32 i = 0; i < _renderTextureCount; ++i) {
            _clearedMaskBufferFlags.PushBack(false);
        }
    } else {
        // マスクのクリアフラグを毎フレーム開始時に初期化
        for (csmInt32 i = 0; i < _renderTextureCount; ++i) {
            _clearedMaskBufferFlags[i] = false;
        }
    }

    // 実際にマスクを生成する
    // 全てのマスクをどの様にレイアウトして描くかを決定し、ClipContext , ClippedDrawContext に記憶する
    for (csmUint32 clipIndex = 0; clipIndex < _clippingContextListForMask.GetSize(); clipIndex++) {
        // --- 実際に１つのマスクを描く ---
        T_ClippingContext *clipContext = _clippingContextListForMask[clipIndex];
        csmRectF *allClippedDrawRect = clipContext->_allClippedDrawRect; //このマスクを使う、全ての描画オブジェクトの論理座標上の囲み矩形
        csmRectF *layoutBoundsOnTex01 = clipContext->_layoutBounds; //この中にマスクを収める
        const csmFloat32 MARGIN = 0.05f;
        csmFloat32 scaleX = 0.0f;
        csmFloat32 scaleY = 0.0f;
        const csmFloat32 ppu = model.GetPixelsPerUnit();
        const csmFloat32 maskPixelWidth = clipContext->GetClippingManager()->_clippingMaskBufferSize.X;
        const csmFloat32 maskPixelHeight = clipContext->GetClippingManager()->_clippingMaskBufferSize.Y;
        const csmFloat32 physicalMaskWidth = layoutBoundsOnTex01->Width * maskPixelWidth;
        const csmFloat32 physicalMaskHeight = layoutBoundsOnTex01->Height * maskPixelHeight;

        _tmpBoundsOnModel.SetRect(allClippedDrawRect);
        if (_tmpBoundsOnModel.Width * ppu > physicalMaskWidth) {
            _tmpBoundsOnModel.Expand(allClippedDrawRect->Width * MARGIN, 0.0f);
            scaleX = layoutBoundsOnTex01->Width / _tmpBoundsOnModel.Width;
        } else {
            scaleX = ppu / physicalMaskWidth;
        }

        if (_tmpBoundsOnModel.Height * ppu > physicalMaskHeight) {
            _tmpBoundsOnModel.Expand(0.0f, allClippedDrawRect->Height * MARGIN);
            scaleY = layoutBoundsOnTex01->Height / _tmpBoundsOnModel.Height;
        } else {
            scaleY = ppu / physicalMaskHeight;
        }


        // マスク生成時に使う行列を求める
        createMatrixForMask(isRightHanded, layoutBoundsOnTex01, scaleX, scaleY);

        clipContext->_matrixForMask.SetMatrix(_tmpMatrixForMask.GetArray());
        clipContext->_matrixForDraw.SetMatrix(_tmpMatrixForDraw.GetArray());
    }
}

template<class T_ClippingContext, class T_OffscreenSurface>
void CubismClippingManager<T_ClippingContext, T_OffscreenSurface>::createMatrixForMask(csmBool isRightHanded, csmRectF *layoutBoundsOnTex01, csmFloat32 scaleX, csmFloat32 scaleY) {
    _tmpMatrix.LoadIdentity();
    {
        // Layout0..1 を -1..1に変換
        _tmpMatrix.TranslateRelative(-1.0f, -1.0f);
        _tmpMatrix.ScaleRelative(2.0f, 2.0f);
    }
    {
        // view to Layout0..1
        _tmpMatrix.TranslateRelative(layoutBoundsOnTex01->X, layoutBoundsOnTex01->Y); //new = [translate]
        _tmpMatrix.ScaleRelative(scaleX, scaleY); //new = [translate][scale]
        _tmpMatrix.TranslateRelative(-_tmpBoundsOnModel.X, -_tmpBoundsOnModel.Y); //new = [translate][scale][translate]
    }
    // tmpMatrixForMask が計算結果
    _tmpMatrixForMask.SetMatrix(_tmpMatrix.GetArray());

    _tmpMatrix.LoadIdentity();
    {
        _tmpMatrix.TranslateRelative(layoutBoundsOnTex01->X, layoutBoundsOnTex01->Y * ((isRightHanded) ? -1.0f : 1.0f)); //new = [translate]
        _tmpMatrix.ScaleRelative(scaleX, scaleY * ((isRightHanded) ? -1.0f : 1.0f)); //new = [translate][scale]
        _tmpMatrix.TranslateRelative(-_tmpBoundsOnModel.X, -_tmpBoundsOnModel.Y); //new = [translate][scale][translate]
    }

    _tmpMatrixForDraw.SetMatrix(_tmpMatrix.GetArray());
}

template<class T_ClippingContext, class T_OffscreenSurface>
void CubismClippingManager<T_ClippingContext, T_OffscreenSurface>::SetupLayoutBounds(csmInt32 usingClipCount) const {
    const csmInt32 useClippingMaskMaxCount = _renderTextureCount <= 1
            ? ClippingMaskMaxCountOnDefault
            : ClippingMaskMaxCountOnMultiRenderTexture * _renderTextureCount;

    if (usingClipCount <= 0 || usingClipCount > useClippingMaskMaxCount) {
        if (usingClipCount > useClippingMaskMaxCount) {
            // マスクの制限数の警告を出す
            csmInt32 count = usingClipCount - useClippingMaskMaxCount;
            CubismLogError("not supported mask count : %d\n[Details] render texture count: %d\n, mask count : %d", count, _renderTextureCount, usingClipCount);
        }

        // この場合は一つのマスクターゲットを毎回クリアして使用する
        for (csmUint32 index = 0; index < _clippingContextListForMask.GetSize(); index++) {
            T_ClippingContext *cc = _clippingContextListForMask[index];
            cc->_layoutChannelIndex = 0; // どうせ毎回消すので固定で良い
            cc->_layoutBounds->X = 0.0f;
            cc->_layoutBounds->Y = 0.0f;
            cc->_layoutBounds->Width = 1.0f;
            cc->_layoutBounds->Height = 1.0f;
            cc->_bufferIndex = 0;
        }
        return;
    }

    // レンダーテクスチャが1枚なら9分割する（最大36枚）
    const csmInt32 layoutCountMaxValue = _renderTextureCount <= 1 ? 9 : 8;

    // ひとつのRenderTextureを極力いっぱいに使ってマスクをレイアウトする
    // マスクグループの数が4以下ならRGBA各チャンネルに１つずつマスクを配置し、5以上6以下ならRGBAを2,2,1,1と配置する
    const csmInt32 countPerSheetDiv = (usingClipCount + _renderTextureCount - 1) / _renderTextureCount; // レンダーテクスチャ1枚あたり何枚割り当てるか（切り上げ）
    const csmInt32 reduceLayoutTextureCount = usingClipCount % _renderTextureCount; // レイアウトの数を1枚減らすレンダーテクスチャの数（この数だけのレンダーテクスチャが対象）

    // RGBAを順番に使っていく
    const csmInt32 divCount = countPerSheetDiv / ColorChannelCount; //１チャンネルに配置する基本のマスク個数
    const csmInt32 modCount = countPerSheetDiv % ColorChannelCount; //余り、この番号のチャンネルまでに１つずつ配分する

    // RGBAそれぞれのチャンネルを用意していく(0:R , 1:G , 2:B, 3:A, )
    csmInt32 curClipIndex = 0; //順番に設定していく

    for (csmInt32 renderTextureIndex = 0; renderTextureIndex < _renderTextureCount; renderTextureIndex++) {
        for (csmInt32 channelIndex = 0; channelIndex < ColorChannelCount; channelIndex++) {
            // このチャンネルにレイアウトする数
            // NOTE: レイアウト数 = 1チャンネルに配置する基本のマスク + 余りのマスクを置くチャンネルなら1つ追加
            csmInt32 layoutCount = divCount + (channelIndex < modCount ? 1 : 0);

            // レイアウトの数を1枚減らす場合にそれを行うチャンネルを決定
            // divが0の時は正常なインデックスの範囲内になるように調整
            const csmInt32 checkChannelIndex = modCount + (divCount < 1 ? -1 : 0);

            // 今回が対象のチャンネルかつ、レイアウトの数を1枚減らすレンダーテクスチャが存在する場合
            if (channelIndex == checkChannelIndex && reduceLayoutTextureCount > 0) {
                // 現在のレンダーテクスチャが、対象のレンダーテクスチャであればレイアウトの数を1枚減らす
                layoutCount -= !(renderTextureIndex < reduceLayoutTextureCount) ? 1 : 0;
            }

            // 分割方法を決定する
            if (layoutCount == 0) {
                // 何もしない
            } else if (layoutCount == 1) {
                //全てをそのまま使う
                T_ClippingContext *cc = _clippingContextListForMask[curClipIndex++];
                cc->_layoutChannelIndex = channelIndex;
                cc->_layoutBounds->X = 0.0f;
                cc->_layoutBounds->Y = 0.0f;
                cc->_layoutBounds->Width = 1.0f;
                cc->_layoutBounds->Height = 1.0f;
                cc->_bufferIndex = renderTextureIndex;
            } else if (layoutCount == 2) {
                for (csmInt32 i = 0; i < layoutCount; i++) {
                    const csmInt32 xpos = i % 2;

                    T_ClippingContext *cc = _clippingContextListForMask[curClipIndex++];
                    cc->_layoutChannelIndex = channelIndex;

                    cc->_layoutBounds->X = xpos * 0.5f;
                    cc->_layoutBounds->Y = 0.0f;
                    cc->_layoutBounds->Width = 0.5f;
                    cc->_layoutBounds->Height = 1.0f;
                    cc->_bufferIndex = renderTextureIndex;
                    //UVを2つに分解して使う
                }
            } else if (layoutCount <= 4) {
                //4分割して使う
                for (csmInt32 i = 0; i < layoutCount; i++) {
                    const csmInt32 xpos = i % 2;
                    const csmInt32 ypos = i / 2;

                    T_ClippingContext *cc = _clippingContextListForMask[curClipIndex++];
                    cc->_layoutChannelIndex = channelIndex;

                    cc->_layoutBounds->X = xpos * 0.5f;
                    cc->_layoutBounds->Y = ypos * 0.5f;
                    cc->_layoutBounds->Width = 0.5f;
                    cc->_layoutBounds->Height = 0.5f;
                    cc->_bufferIndex = renderTextureIndex;
                }
            } else if (layoutCount <= layoutCountMaxValue) {
                //9分割して使う
                for (csmInt32 i = 0; i < layoutCount; i++) {
                    const csmInt32 xpos = i % 3;
                    const csmInt32 ypos = i / 3;

                    T_ClippingContext *cc = _clippingContextListForMask[curClipIndex++];
                    cc->_layoutChannelIndex = channelIndex;

                    cc->_layoutBounds->X = xpos / 3.0f;
                    cc->_layoutBounds->Y = ypos / 3.0f;
                    cc->_layoutBounds->Width = 1.0f / 3.0f;
                    cc->_layoutBounds->Height = 1.0f / 3.0f;
                    cc->_bufferIndex = renderTextureIndex;
                }
            }
                // マスクの制限枚数を超えた場合の処理
            else {
                csmInt32 count = usingClipCount - useClippingMaskMaxCount;


                CubismLogError("not supported mask count : %d\n[Details] render texture count: %d\n, mask count : %d", count, _renderTextureCount, usingClipCount);

                // 開発モードの場合は停止させる
                CSM_ASSERT(0);

                // 引き続き実行する場合、 SetupShaderProgramでオーバーアクセスが発生するので仕方なく適当に入れておく
                // もちろん描画結果はろくなことにならない
                for (csmInt32 i = 0; i < layoutCount; i++) {
                    T_ClippingContext *cc = _clippingContextListForMask[curClipIndex++];
                    cc->_layoutChannelIndex = 0;
                    cc->_layoutBounds->X = 0.0f;
                    cc->_layoutBounds->Y = 0.0f;
                    cc->_layoutBounds->Width = 1.0f;
                    cc->_layoutBounds->Height = 1.0f;
                    cc->_bufferIndex = 0;
                }
            }
        }
    }
}

template<class T_ClippingContext, class T_OffscreenSurface>
void CubismClippingManager<T_ClippingContext, T_OffscreenSurface>::CalcClippedDrawTotalBounds(CubismModel &model, T_ClippingContext *clippingContext) {
    // 被クリッピングマスク（マスクされる描画オブジェクト）の全体の矩形
    csmFloat32 clippedDrawTotalMinX = FLT_MAX, clippedDrawTotalMinY = FLT_MAX;
    csmFloat32 clippedDrawTotalMaxX = -FLT_MAX, clippedDrawTotalMaxY = -FLT_MAX;

    // このマスクが実際に必要か判定する
    // このクリッピングを利用する「描画オブジェクト」がひとつでも使用可能であればマスクを生成する必要がある

    const csmInt32 clippedDrawCount = clippingContext->_clippedDrawableIndexList->GetSize();
    for (csmInt32 clippedDrawableIndex = 0; clippedDrawableIndex < clippedDrawCount; clippedDrawableIndex++) {
        // マスクを使用する描画オブジェクトの描画される矩形を求める
        const csmInt32 drawableIndex = (*clippingContext->_clippedDrawableIndexList)[clippedDrawableIndex];

        csmInt32 drawableVertexCount = model.GetDrawableVertexCount(drawableIndex);
        csmFloat32 *drawableVertexes = const_cast<csmFloat32 *>(model.GetDrawableVertices(drawableIndex));

        csmFloat32 minX = FLT_MAX, minY = FLT_MAX;
        csmFloat32 maxX = -FLT_MAX, maxY = -FLT_MAX;

        csmInt32 loop = drawableVertexCount * Constant::VertexStep;
        for (csmInt32 pi = Constant::VertexOffset; pi < loop; pi += Constant::VertexStep) {
            csmFloat32 x = drawableVertexes[pi];
            csmFloat32 y = drawableVertexes[pi + 1];
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }

        //
        if (minX == FLT_MAX) continue; //有効な点がひとつも取れなかったのでスキップする

        // 全体の矩形に反映
        if (minX < clippedDrawTotalMinX) clippedDrawTotalMinX = minX;
        if (minY < clippedDrawTotalMinY) clippedDrawTotalMinY = minY;
        if (maxX > clippedDrawTotalMaxX) clippedDrawTotalMaxX = maxX;
        if (maxY > clippedDrawTotalMaxY) clippedDrawTotalMaxY = maxY;
    }
    if (clippedDrawTotalMinX == FLT_MAX) {
        clippingContext->_allClippedDrawRect->X = 0.0f;
        clippingContext->_allClippedDrawRect->Y = 0.0f;
        clippingContext->_allClippedDrawRect->Width = 0.0f;
        clippingContext->_allClippedDrawRect->Height = 0.0f;
        clippingContext->_isUsing = false;
    } else {
        clippingContext->_isUsing = true;
        csmFloat32 w = clippedDrawTotalMaxX - clippedDrawTotalMinX;
        csmFloat32 h = clippedDrawTotalMaxY - clippedDrawTotalMinY;
        clippingContext->_allClippedDrawRect->X = clippedDrawTotalMinX;
        clippingContext->_allClippedDrawRect->Y = clippedDrawTotalMinY;
        clippingContext->_allClippedDrawRect->Width = w;
        clippingContext->_allClippedDrawRect->Height = h;
    }
}

template<class T_ClippingContext, class T_OffscreenSurface>
csmVector<T_ClippingContext *> *CubismClippingManager<T_ClippingContext, T_OffscreenSurface>::GetClippingContextListForDraw() {
    return &_clippingContextListForDraw;
}

template<class T_ClippingContext, class T_OffscreenSurface>
CubismVector2 CubismClippingManager<T_ClippingContext, T_OffscreenSurface>::GetClippingMaskBufferSize() const {
    return _clippingMaskBufferSize;
}

template<class T_ClippingContext, class T_OffscreenSurface>
csmInt32 CubismClippingManager<T_ClippingContext, T_OffscreenSurface>::GetRenderTextureCount() {
    return _renderTextureCount;
}

template<class T_ClippingContext, class T_OffscreenSurface>
CubismRenderer::CubismTextureColor *CubismClippingManager<T_ClippingContext, T_OffscreenSurface>::GetChannelFlagAsColor(csmInt32 channelIndex) {
    return _channelColors[channelIndex];
}

template<class T_ClippingContext, class T_OffscreenSurface>
void CubismClippingManager<T_ClippingContext, T_OffscreenSurface>::SetClippingMaskBufferSize(csmFloat32 width, csmFloat32 height) {
    _clippingMaskBufferSize = CubismVector2(width, height);
}

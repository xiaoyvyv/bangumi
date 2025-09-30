/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#include "CubismShader_Metal.hpp"
#include "CubismRenderer_Metal.hpp"
#include "CubismRenderingInstanceSingleton_Metal.h"

//------------ LIVE2D NAMESPACE ------------
namespace Live2D {
    namespace Cubism {
        namespace Framework {
            namespace Rendering {

/*********************************************************************************************************************
*                                       CubismShader_Metal
********************************************************************************************************************/
                namespace {
                    const csmInt32 ShaderCount = 19; ///< シェーダの数 = マスク生成用 + (通常 + 加算 + 乗算) * (マスク無 + マスク有 + マスク有反転 + マスク無の乗算済アルファ対応版 + マスク有の乗算済アルファ対応版 + マスク有反転の乗算済アルファ対応版)
                    CubismShader_Metal *s_instance;
                }

                enum ShaderNames {
                    // SetupMask
                    ShaderNames_SetupMask,

                    //Normal
                    ShaderNames_Normal,
                    ShaderNames_NormalMasked,
                    ShaderNames_NormalMaskedInverted,
                    ShaderNames_NormalPremultipliedAlpha,
                    ShaderNames_NormalMaskedPremultipliedAlpha,
                    ShaderNames_NormalMaskedInvertedPremultipliedAlpha,

                    //Add
                    ShaderNames_Add,
                    ShaderNames_AddMasked,
                    ShaderNames_AddMaskedInverted,
                    ShaderNames_AddPremultipliedAlpha,
                    ShaderNames_AddMaskedPremultipliedAlpha,
                    ShaderNames_AddMaskedPremultipliedAlphaInverted,

                    //Mult
                    ShaderNames_Mult,
                    ShaderNames_MultMasked,
                    ShaderNames_MultMaskedInverted,
                    ShaderNames_MultPremultipliedAlpha,
                    ShaderNames_MultMaskedPremultipliedAlpha,
                    ShaderNames_MultMaskedPremultipliedAlphaInverted,
                };

//// SetupMask
                static const csmChar *VertShaderSrcSetupMask = "VertShaderSrcSetupMask";

                static const csmChar *FragShaderSrcSetupMask = "FragShaderSrcSetupMask";

//----- バーテックスシェーダプログラム -----
// Normal & Add & Mult 共通
                static const csmChar *VertShaderSrc = "VertShaderSrc";

// Normal & Add & Mult 共通（クリッピングされたものの描画用）
                static const csmChar *VertShaderSrcMasked = "VertShaderSrcMasked";

//----- フラグメントシェーダプログラム -----
// Normal & Add & Mult 共通
                static const csmChar *FragShaderSrc = "FragShaderSrc";

// Normal & Add & Mult 共通 （PremultipliedAlpha）
                static const csmChar *FragShaderSrcPremultipliedAlpha = "FragShaderSrcPremultipliedAlpha";

// Normal & Add & Mult 共通（クリッピングされたものの描画用）
                static const csmChar *FragShaderSrcMask = "FragShaderSrcMask";

// Normal & Add & Mult 共通（クリッピングされて反転使用の描画用）
                static const csmChar *FragShaderSrcMaskInverted = "FragShaderSrcMaskInverted";

// Normal & Add & Mult 共通（クリッピングされたものの描画用、PremultipliedAlphaの場合）
                static const csmChar *FragShaderSrcMaskPremultipliedAlpha = "FragShaderSrcMaskPremultipliedAlpha";

// Normal & Add & Mult 共通（クリッピングされて反転使用の描画用、PremultipliedAlphaの場合）
                static const csmChar *FragShaderSrcMaskInvertedPremultipliedAlpha = "FragShaderSrcMaskInvertedPremultipliedAlpha";

                CubismShader_Metal::CubismShader_Metal() {
                }

                CubismShader_Metal::~CubismShader_Metal() {
                }

                CubismShader_Metal *CubismShader_Metal::GetInstance() {
                    if (s_instance == NULL) {
                        s_instance = CSM_NEW
                        CubismShader_Metal();
                    }
                    return s_instance;
                }

                void CubismShader_Metal::DeleteInstance() {
                    if (s_instance) {
                        CSM_DELETE_SELF(CubismShader_Metal, s_instance);
                        s_instance = NULL;
                    }
                }


                void CubismShader_Metal::GenerateShaders(CubismRenderer_Metal *renderer) {
                    for (csmInt32 i = 0; i < ShaderCount; i++) {
                        _shaderSets.PushBack(CSM_NEW
                        CubismShaderSet());
                    }

                    //シェーダライブラリのロード（.metal）
                    CubismRenderingInstanceSingleton_Metal *single = [CubismRenderingInstanceSingleton_Metal sharedManager];
                    id <MTLDevice> device = [single getMTLDevice];
                    _shaderLib = [device newDefaultLibrary];

                    if (!_shaderLib) {
                        NSLog(@" ERROR: Couldnt create a default shader library");
                        // assert here because if the shader libary isn't loading, nothing good will happen
                        return;
                    }

                    _shaderSets[0]->ShaderProgram = LoadShaderProgram(VertShaderSrcSetupMask, FragShaderSrcSetupMask);

                    _shaderSets[1]->ShaderProgram = LoadShaderProgram(VertShaderSrc, FragShaderSrc);
                    _shaderSets[2]->ShaderProgram = LoadShaderProgram(VertShaderSrcMasked, FragShaderSrcMask);
                    _shaderSets[3]->ShaderProgram = LoadShaderProgram(VertShaderSrcMasked, FragShaderSrcMaskInverted);
                    _shaderSets[4]->ShaderProgram = LoadShaderProgram(VertShaderSrc, FragShaderSrcPremultipliedAlpha);
                    _shaderSets[5]->ShaderProgram = LoadShaderProgram(VertShaderSrcMasked, FragShaderSrcMaskPremultipliedAlpha);
                    _shaderSets[6]->ShaderProgram = LoadShaderProgram(VertShaderSrcMasked, FragShaderSrcMaskInvertedPremultipliedAlpha);

                    _shaderSets[0]->RenderPipelineState = MakeRenderPipelineState(device, _shaderSets[0]->ShaderProgram, -1);
                    _shaderSets[0]->SamplerState = MakeSamplerState(device, renderer);

                    _shaderSets[1]->RenderPipelineState = MakeRenderPipelineState(device, _shaderSets[1]->ShaderProgram, CubismRenderer::CubismBlendMode_Normal);
                    _shaderSets[2]->RenderPipelineState = MakeRenderPipelineState(device, _shaderSets[2]->ShaderProgram, CubismRenderer::CubismBlendMode_Normal);
                    _shaderSets[3]->RenderPipelineState = MakeRenderPipelineState(device, _shaderSets[3]->ShaderProgram, CubismRenderer::CubismBlendMode_Normal);
                    _shaderSets[4]->RenderPipelineState = MakeRenderPipelineState(device, _shaderSets[4]->ShaderProgram, CubismRenderer::CubismBlendMode_Normal);
                    _shaderSets[5]->RenderPipelineState = MakeRenderPipelineState(device, _shaderSets[5]->ShaderProgram, CubismRenderer::CubismBlendMode_Normal);
                    _shaderSets[6]->RenderPipelineState = MakeRenderPipelineState(device, _shaderSets[6]->ShaderProgram, CubismRenderer::CubismBlendMode_Normal);

                    _shaderSets[1]->DepthStencilState = MakeDepthStencilState(device);
                    _shaderSets[2]->DepthStencilState = _shaderSets[1]->DepthStencilState;
                    _shaderSets[3]->DepthStencilState = _shaderSets[1]->DepthStencilState;
                    _shaderSets[4]->DepthStencilState = _shaderSets[1]->DepthStencilState;
                    _shaderSets[5]->DepthStencilState = _shaderSets[1]->DepthStencilState;
                    _shaderSets[6]->DepthStencilState = _shaderSets[1]->DepthStencilState;

                    _shaderSets[1]->SamplerState = _shaderSets[0]->SamplerState;
                    _shaderSets[2]->SamplerState = _shaderSets[0]->SamplerState;
                    _shaderSets[3]->SamplerState = _shaderSets[0]->SamplerState;
                    _shaderSets[4]->SamplerState = _shaderSets[0]->SamplerState;
                    _shaderSets[5]->SamplerState = _shaderSets[0]->SamplerState;
                    _shaderSets[6]->SamplerState = _shaderSets[0]->SamplerState;

                    // 加算も通常と同じシェーダーを利用する
                    _shaderSets[7]->ShaderProgram = _shaderSets[1]->ShaderProgram;
                    _shaderSets[8]->ShaderProgram = _shaderSets[2]->ShaderProgram;
                    _shaderSets[9]->ShaderProgram = _shaderSets[3]->ShaderProgram;
                    _shaderSets[10]->ShaderProgram = _shaderSets[4]->ShaderProgram;
                    _shaderSets[11]->ShaderProgram = _shaderSets[5]->ShaderProgram;
                    _shaderSets[12]->ShaderProgram = _shaderSets[6]->ShaderProgram;

                    _shaderSets[7]->RenderPipelineState = MakeRenderPipelineState(device, _shaderSets[7]->ShaderProgram, CubismRenderer::CubismBlendMode_Additive);
                    _shaderSets[8]->RenderPipelineState = MakeRenderPipelineState(device, _shaderSets[8]->ShaderProgram, CubismRenderer::CubismBlendMode_Additive);
                    _shaderSets[9]->RenderPipelineState = MakeRenderPipelineState(device, _shaderSets[9]->ShaderProgram, CubismRenderer::CubismBlendMode_Additive);
                    _shaderSets[10]->RenderPipelineState = MakeRenderPipelineState(device, _shaderSets[10]->ShaderProgram, CubismRenderer::CubismBlendMode_Additive);
                    _shaderSets[11]->RenderPipelineState = MakeRenderPipelineState(device, _shaderSets[11]->ShaderProgram, CubismRenderer::CubismBlendMode_Additive);
                    _shaderSets[12]->RenderPipelineState = MakeRenderPipelineState(device, _shaderSets[12]->ShaderProgram, CubismRenderer::CubismBlendMode_Additive);

                    _shaderSets[7]->DepthStencilState = _shaderSets[1]->DepthStencilState;
                    _shaderSets[8]->DepthStencilState = _shaderSets[1]->DepthStencilState;
                    _shaderSets[9]->DepthStencilState = _shaderSets[1]->DepthStencilState;
                    _shaderSets[10]->DepthStencilState = _shaderSets[1]->DepthStencilState;
                    _shaderSets[11]->DepthStencilState = _shaderSets[1]->DepthStencilState;
                    _shaderSets[12]->DepthStencilState = _shaderSets[1]->DepthStencilState;

                    _shaderSets[7]->SamplerState = _shaderSets[0]->SamplerState;
                    _shaderSets[8]->SamplerState = _shaderSets[0]->SamplerState;
                    _shaderSets[9]->SamplerState = _shaderSets[0]->SamplerState;
                    _shaderSets[10]->SamplerState = _shaderSets[0]->SamplerState;
                    _shaderSets[11]->SamplerState = _shaderSets[0]->SamplerState;
                    _shaderSets[12]->SamplerState = _shaderSets[0]->SamplerState;

                    // 乗算も通常と同じシェーダーを利用する
                    _shaderSets[13]->ShaderProgram = _shaderSets[1]->ShaderProgram;
                    _shaderSets[14]->ShaderProgram = _shaderSets[2]->ShaderProgram;
                    _shaderSets[15]->ShaderProgram = _shaderSets[3]->ShaderProgram;
                    _shaderSets[16]->ShaderProgram = _shaderSets[4]->ShaderProgram;
                    _shaderSets[17]->ShaderProgram = _shaderSets[5]->ShaderProgram;
                    _shaderSets[18]->ShaderProgram = _shaderSets[6]->ShaderProgram;

                    _shaderSets[13]->RenderPipelineState = MakeRenderPipelineState(device, _shaderSets[13]->ShaderProgram, CubismRenderer::CubismBlendMode_Multiplicative);
                    _shaderSets[14]->RenderPipelineState = MakeRenderPipelineState(device, _shaderSets[14]->ShaderProgram, CubismRenderer::CubismBlendMode_Multiplicative);
                    _shaderSets[15]->RenderPipelineState = MakeRenderPipelineState(device, _shaderSets[15]->ShaderProgram, CubismRenderer::CubismBlendMode_Multiplicative);
                    _shaderSets[16]->RenderPipelineState = MakeRenderPipelineState(device, _shaderSets[16]->ShaderProgram, CubismRenderer::CubismBlendMode_Multiplicative);
                    _shaderSets[17]->RenderPipelineState = MakeRenderPipelineState(device, _shaderSets[17]->ShaderProgram, CubismRenderer::CubismBlendMode_Multiplicative);
                    _shaderSets[18]->RenderPipelineState = MakeRenderPipelineState(device, _shaderSets[18]->ShaderProgram, CubismRenderer::CubismBlendMode_Multiplicative);

                    _shaderSets[13]->DepthStencilState = _shaderSets[1]->DepthStencilState;
                    _shaderSets[14]->DepthStencilState = _shaderSets[1]->DepthStencilState;
                    _shaderSets[15]->DepthStencilState = _shaderSets[1]->DepthStencilState;
                    _shaderSets[16]->DepthStencilState = _shaderSets[1]->DepthStencilState;
                    _shaderSets[17]->DepthStencilState = _shaderSets[1]->DepthStencilState;
                    _shaderSets[18]->DepthStencilState = _shaderSets[1]->DepthStencilState;

                    _shaderSets[13]->SamplerState = _shaderSets[0]->SamplerState;
                    _shaderSets[14]->SamplerState = _shaderSets[0]->SamplerState;
                    _shaderSets[15]->SamplerState = _shaderSets[0]->SamplerState;
                    _shaderSets[16]->SamplerState = _shaderSets[0]->SamplerState;
                    _shaderSets[17]->SamplerState = _shaderSets[0]->SamplerState;
                    _shaderSets[18]->SamplerState = _shaderSets[0]->SamplerState;
                }

                simd::float4x4 CubismShader_Metal::ConvertCubismMatrix44IntoSimdFloat4x4(CubismMatrix44 &matrix) {
                    csmFloat32 *srcArray = matrix.GetArray();
                    return simd::float4x4(simd::float4{srcArray[0], srcArray[1], srcArray[2], srcArray[3]},
                            simd::float4{srcArray[4], srcArray[5], srcArray[6], srcArray[7]},
                            simd::float4{srcArray[8], srcArray[9], srcArray[10], srcArray[11]},
                            simd::float4{srcArray[12], srcArray[13], srcArray[14], srcArray[15]});
                }

                void CubismShader_Metal::SetColorChannel(CubismShaderUniforms &shaderUniforms, CubismClippingContext_Metal *contextBuffer) {
                    const csmInt32 channelIndex = contextBuffer->_layoutChannelIndex;
                    CubismRenderer::CubismTextureColor *colorChannel = contextBuffer->GetClippingManager()->GetChannelFlagAsColor(channelIndex);
                    shaderUniforms.channelFlag = (vector_float4) {colorChannel->R, colorChannel->G, colorChannel->B, colorChannel->A};
                }

                void CubismShader_Metal::SetFragmentModelTexture(CubismCommandBuffer_Metal::DrawCommandBuffer *drawCommandBuffer, id <MTLRenderCommandEncoder> renderEncoder, CubismRenderer_Metal *renderer, const CubismModel &model, const csmInt32 index) {
                    const id <MTLTexture> texture = renderer->GetBindedTextureId(model.GetDrawableTextureIndex(index));
                    [renderEncoder setFragmentTexture:texture atIndex:0];
                }

                void CubismShader_Metal::SetVertexBufferForVerticesAndUvs(CubismCommandBuffer_Metal::DrawCommandBuffer *drawCommandBuffer, id <MTLRenderCommandEncoder> renderEncoder) {
                    [renderEncoder setVertexBuffer:(drawCommandBuffer->GetVertexBuffer()) offset:0 atIndex:MetalVertexInputIndexVertices];
                    [renderEncoder setVertexBuffer:(drawCommandBuffer->GetUvBuffer()) offset:0 atIndex:MetalVertexInputUVs];
                }

                void CubismShader_Metal::SetupShaderProgramForDraw(CubismCommandBuffer_Metal::DrawCommandBuffer *drawCommandBuffer, id <MTLRenderCommandEncoder> renderEncoder, CubismRenderer_Metal *renderer, const CubismModel &model, const csmInt32 index) {
                    // シェーダー生成
                    if (_shaderSets.GetSize() == 0) {
                        GenerateShaders(renderer);
                    }

                    // シェーダーセットの設定
                    const csmBool masked = renderer->GetClippingContextBufferForDraw() != NULL;
                    const csmBool invertedMask = model.GetDrawableInvertedMask(index);
                    const csmBool isPremultipliedAlpha = renderer->IsPremultipliedAlpha();
                    const csmInt32 offset = (masked ? (invertedMask ? 2 : 1) : 0) + (isPremultipliedAlpha ? 3 : 0);

                    CubismShaderSet *shaderSet;

                    //_shaderSetsにshaderが入っていて、それをブレンドモードごとに切り替えている
                    switch (model.GetDrawableBlendMode(index)) {
                        case CubismRenderer::CubismBlendMode_Normal:
                        default:
                            shaderSet = _shaderSets[ShaderNames_Normal + offset];
                            break;

                        case CubismRenderer::CubismBlendMode_Additive:
                            shaderSet = _shaderSets[ShaderNames_Add + offset];
                            break;

                        case CubismRenderer::CubismBlendMode_Multiplicative:
                            shaderSet = _shaderSets[ShaderNames_Mult + offset];
                            break;
                    }

                    //テクスチャ設定
                    SetFragmentModelTexture(drawCommandBuffer, renderEncoder, renderer, model, index);

                    // 頂点・テクスチャバッファの設定
                    SetVertexBufferForVerticesAndUvs(drawCommandBuffer, renderEncoder);

                    CubismShaderUniforms shaderUniforms;
                    if (masked) {
                        // frameBufferに書かれたテクスチャ
                        id <MTLTexture> tex = renderer->GetOffscreenSurface(renderer->GetClippingContextBufferForDraw()->_bufferIndex)->GetColorBuffer();

                        //テクスチャ設定
                        [renderEncoder setFragmentTexture:tex atIndex:1];

                        // 使用するカラーチャンネルを設定
                        SetColorChannel(shaderUniforms, renderer->GetClippingContextBufferForDraw());

                        // クリッピング変換行列設定
                        shaderUniforms.clipMatrix = ConvertCubismMatrix44IntoSimdFloat4x4(renderer->GetClippingContextBufferForDraw()->_matrixForDraw);
                    }

                    // MVP行列設定
                    CubismMatrix44 mvp = renderer->GetMvpMatrix();
                    shaderUniforms.matrix = ConvertCubismMatrix44IntoSimdFloat4x4(mvp);

                    // 色定数バッファの設定
                    CubismRenderer::CubismTextureColor baseColor = renderer->GetModelColorWithOpacity(model.GetDrawableOpacity(index));
                    CubismRenderer::CubismTextureColor multiplyColor = model.GetMultiplyColor(index);
                    CubismRenderer::CubismTextureColor screenColor = model.GetScreenColor(index);

                    shaderUniforms.baseColor = (vector_float4) {baseColor.R, baseColor.G, baseColor.B, baseColor.A};
                    shaderUniforms.multiplyColor = (vector_float4) {multiplyColor.R, multiplyColor.G, multiplyColor.B, multiplyColor.A};
                    shaderUniforms.screenColor = (vector_float4) {screenColor.R, screenColor.G, screenColor.B, screenColor.A};

                    // 転送
                    [renderEncoder setVertexBytes:&shaderUniforms length:sizeof(CubismShaderUniforms) atIndex:MetalVertexInputIndexUniforms];
                    [renderEncoder setFragmentBytes:&shaderUniforms length:sizeof(CubismShaderUniforms) atIndex:MetalVertexInputIndexUniforms];
                    [renderEncoder setFragmentSamplerState:shaderSet->SamplerState atIndex:0];
                    [renderEncoder setDepthStencilState:shaderSet->DepthStencilState];

                    drawCommandBuffer->GetCommandDraw()->SetRenderPipelineState(shaderSet->RenderPipelineState);
                }

                void CubismShader_Metal::SetupShaderProgramForMask(CubismCommandBuffer_Metal::DrawCommandBuffer *drawCommandBuffer, id <MTLRenderCommandEncoder> renderEncoder, CubismRenderer_Metal *renderer, const CubismModel &model, const csmInt32 index) {
                    // シェーダー生成
                    if (_shaderSets.GetSize() == 0) {
                        GenerateShaders(renderer);
                    }

                    // シェーダーセットの設定
                    CubismShaderSet *shaderSet = _shaderSets[ShaderNames_SetupMask];

                    // テクスチャ設定
                    SetFragmentModelTexture(drawCommandBuffer, renderEncoder, renderer, model, index);

                    // 頂点・テクスチャバッファの設定
                    SetVertexBufferForVerticesAndUvs(drawCommandBuffer, renderEncoder);

                    CubismShaderUniforms shaderUniforms;

                    // 使用するカラーチャンネルを設定
                    SetColorChannel(shaderUniforms, renderer->GetClippingContextBufferForMask());

                    // クリッピング変換行列設定
                    shaderUniforms.clipMatrix = ConvertCubismMatrix44IntoSimdFloat4x4(renderer->GetClippingContextBufferForMask()->_matrixForMask);

                    // 色定数バッファの設定
                    csmRectF *rect = renderer->GetClippingContextBufferForMask()->_layoutBounds;
                    shaderUniforms.baseColor = (vector_float4) {rect->X * 2.0f - 1.0f,
                            rect->Y * 2.0f - 1.0f,
                            rect->GetRight() * 2.0f - 1.0f,
                            rect->GetBottom() * 2.0f - 1.0f};

                    // 転送
                    [renderEncoder setVertexBytes:&shaderUniforms length:sizeof(CubismShaderUniforms) atIndex:MetalVertexInputIndexUniforms];
                    [renderEncoder setFragmentBytes:&shaderUniforms length:sizeof(CubismShaderUniforms) atIndex:MetalVertexInputIndexUniforms];
                    [renderEncoder setFragmentSamplerState:shaderSet->SamplerState atIndex:0];

                    drawCommandBuffer->GetCommandDraw()->SetRenderPipelineState(shaderSet->RenderPipelineState);
                }

                CubismShader_Metal::ShaderProgram *CubismShader_Metal::LoadShaderProgram(const csmChar *vertShaderSrc, const csmChar *fragShaderSrc) {
                    // Create shader program.
                    ShaderProgram *shaderProgram = new ShaderProgram;

                    //頂点シェーダの取得
                    NSString *vertShaderStr = [NSString stringWithCString:vertShaderSrc encoding:NSUTF8StringEncoding];

                    shaderProgram->vertexFunction = [_shaderLib newFunctionWithName:vertShaderStr];
                    if (!shaderProgram->vertexFunction) {
                        delete shaderProgram;
                        NSLog(@">> ERROR: Couldn't load vertex function from default library");
                        return nil;
                    }

                    //フラグメントシェーダの取得
                    NSString *fragShaderStr = [NSString stringWithCString:fragShaderSrc encoding:NSUTF8StringEncoding];
                    shaderProgram->fragmentFunction = [_shaderLib newFunctionWithName:fragShaderStr];
                    if (!shaderProgram->fragmentFunction) {
                        delete shaderProgram;
                        NSLog(@" ERROR: Couldn't load fragment function from default library");
                        return nil;
                    }

                    return shaderProgram;
                }

                id <MTLRenderPipelineState> CubismShader_Metal::MakeRenderPipelineState(id <MTLDevice> device, CubismShader_Metal::ShaderProgram *shaderProgram, int blendMode) {
                    MTLRenderPipelineDescriptor *renderPipelineDescriptor = [[MTLRenderPipelineDescriptor alloc] init];
                    NSError *error;

                    renderPipelineDescriptor.vertexFunction = shaderProgram->vertexFunction;
                    renderPipelineDescriptor.fragmentFunction = shaderProgram->fragmentFunction;
                    renderPipelineDescriptor.colorAttachments[0].blendingEnabled = true;

                    switch (blendMode) {
                        default:
                            // only Setup masking
                            renderPipelineDescriptor.colorAttachments[0].sourceRGBBlendFactor = MTLBlendFactorZero;
                            renderPipelineDescriptor.colorAttachments[0].destinationRGBBlendFactor = MTLBlendFactorOneMinusSourceColor;
                            renderPipelineDescriptor.colorAttachments[0].sourceAlphaBlendFactor = MTLBlendFactorZero;
                            renderPipelineDescriptor.colorAttachments[0].destinationAlphaBlendFactor = MTLBlendFactorOneMinusSourceAlpha;
                            renderPipelineDescriptor.colorAttachments[0].pixelFormat = MTLPixelFormatRGBA8Unorm;
                            break;

                        case CubismRenderer::CubismBlendMode_Normal:
                            renderPipelineDescriptor.colorAttachments[0].sourceRGBBlendFactor = MTLBlendFactorOne;
                            renderPipelineDescriptor.colorAttachments[0].destinationRGBBlendFactor = MTLBlendFactorOneMinusSourceAlpha;
                            renderPipelineDescriptor.colorAttachments[0].sourceAlphaBlendFactor = MTLBlendFactorOne;
                            renderPipelineDescriptor.colorAttachments[0].destinationAlphaBlendFactor = MTLBlendFactorOneMinusSourceAlpha;
                            renderPipelineDescriptor.colorAttachments[0].pixelFormat = MTLPixelFormatBGRA8Unorm;
                            renderPipelineDescriptor.depthAttachmentPixelFormat = MTLPixelFormatDepth32Float;

                            break;

                        case CubismRenderer::CubismBlendMode_Additive:
                            renderPipelineDescriptor.colorAttachments[0].sourceRGBBlendFactor = MTLBlendFactorOne;
                            renderPipelineDescriptor.colorAttachments[0].destinationRGBBlendFactor = MTLBlendFactorOne;
                            renderPipelineDescriptor.colorAttachments[0].sourceAlphaBlendFactor = MTLBlendFactorZero;
                            renderPipelineDescriptor.colorAttachments[0].destinationAlphaBlendFactor = MTLBlendFactorOne;
                            renderPipelineDescriptor.colorAttachments[0].pixelFormat = MTLPixelFormatBGRA8Unorm;
                            renderPipelineDescriptor.depthAttachmentPixelFormat = MTLPixelFormatDepth32Float;

                            break;

                        case CubismRenderer::CubismBlendMode_Multiplicative:
                            renderPipelineDescriptor.colorAttachments[0].sourceRGBBlendFactor = MTLBlendFactorDestinationColor;
                            renderPipelineDescriptor.colorAttachments[0].destinationRGBBlendFactor = MTLBlendFactorOneMinusSourceAlpha;
                            renderPipelineDescriptor.colorAttachments[0].sourceAlphaBlendFactor = MTLBlendFactorZero;
                            renderPipelineDescriptor.colorAttachments[0].destinationAlphaBlendFactor = MTLBlendFactorOne;
                            renderPipelineDescriptor.colorAttachments[0].pixelFormat = MTLPixelFormatBGRA8Unorm;
                            renderPipelineDescriptor.depthAttachmentPixelFormat = MTLPixelFormatDepth32Float;

                            break;
                    }

                    return [device newRenderPipelineStateWithDescriptor:renderPipelineDescriptor error:&error];
                }

                id <MTLDepthStencilState> CubismShader_Metal::MakeDepthStencilState(id <MTLDevice> device) {
                    MTLDepthStencilDescriptor *depthStencilDescriptor = [[MTLDepthStencilDescriptor alloc] init];

                    depthStencilDescriptor.depthCompareFunction = MTLCompareFunctionAlways;
                    depthStencilDescriptor.depthWriteEnabled = YES;

                    return [device newDepthStencilStateWithDescriptor:depthStencilDescriptor];
                }

                id <MTLSamplerState> CubismShader_Metal::MakeSamplerState(id <MTLDevice> device, CubismRenderer_Metal *renderer) {
                    MTLSamplerDescriptor *samplerDescriptor = [[MTLSamplerDescriptor alloc] init];

                    samplerDescriptor.rAddressMode = MTLSamplerAddressModeRepeat;
                    samplerDescriptor.sAddressMode = MTLSamplerAddressModeRepeat;
                    samplerDescriptor.tAddressMode = MTLSamplerAddressModeRepeat;
                    samplerDescriptor.minFilter = MTLSamplerMinMagFilterLinear;
                    samplerDescriptor.magFilter = MTLSamplerMinMagFilterLinear;
                    samplerDescriptor.mipFilter = MTLSamplerMipFilterLinear;

                    //異方性フィルタリング
                    if (renderer->GetAnisotropy() >= 1.0f) {
                        samplerDescriptor.maxAnisotropy = renderer->GetAnisotropy();
                    }

                    return [device newSamplerStateWithDescriptor:samplerDescriptor];
                }

            }
        }
    }
}
//------------ LIVE2D NAMESPACE ------------

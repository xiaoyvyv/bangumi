/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#pragma once

#include "CubismFramework.hpp"
#include "Type/csmMap.hpp"
#include "Type/csmVector.hpp"
#include "Rendering/CubismRenderer.hpp"
#include "Id/CubismId.hpp"

namespace Live2D {
    namespace Cubism {
        namespace Framework {

            class CubismMoc;

/**
 * Handles models created from MOC data.
 */
            class CubismModel {
                friend class CubismMoc;

            public:
                /**
                 * Structure for color information of drawing object
                 */
                struct DrawableColorData {
                    /**
                     * Constructor
                     */
                    DrawableColorData()
                            : IsOverridden(false), Color() {
                    };

                    /**
                     * Constructor
                     *
                     * @param isOverridden whether to be overridden
                     * @param color Texture color
                     */
                    DrawableColorData(csmBool isOverridden, Rendering::CubismRenderer::CubismTextureColor color)
                            : IsOverridden(isOverridden), Color(color) {
                    };

                    /**
                     * Destructor
                     */
                    virtual ~DrawableColorData() {
                    };

                    csmBool IsOverwritten;                                     ///< (deprecated) Whether to be overwritten
                    csmBool IsOverridden;                                      ///< Whether to be overridden
                    Rendering::CubismRenderer::CubismTextureColor Color;        ///< Color

                };

                /**
                 * Structure to manage texture culling settings
                 */
                struct DrawableCullingData {
                    /**
                     * Constructor
                     */
                    DrawableCullingData()
                            : IsOverridden(false), IsCulling(0) {
                    };

                    /**
                     * Constructor
                     *
                     * @param isOverridden whether to be overridden
                     * @param isCulling Culling information
                     */
                    DrawableCullingData(csmBool isOverridden, csmInt32 isCulling)
                            : IsOverridden(isOverridden), IsCulling(isCulling) {
                    };

                    /**
                     * Destructor
                     */
                    virtual ~DrawableCullingData() {
                    };

                    csmBool IsOverwritten;      ///< (deprecated) Whether to be overwritten
                    csmBool IsOverridden;      ///< Whether to be overridden
                    csmInt32 IsCulling;         ///< Culling information

                };

                /**
                 * Structure to handle texture color in RGBA
                 */
                struct PartColorData {
                    /**
                     * Constructor
                     */
                    PartColorData()
                            : IsOverridden(false), Color() {
                    };

                    /**
                     * Constructor
                     *
                     * @param isOverridden whether to be overridden
                     * @param color Texture color
                     */
                    PartColorData(csmBool isOverridden, Rendering::CubismRenderer::CubismTextureColor color)
                            : IsOverridden(isOverridden), Color(color) {
                    };

                    /**
                     * Destructor
                     */
                    virtual ~PartColorData() {
                    };

                    csmBool IsOverwritten;                                     ///< (deprecated) Whether to be overwritten
                    csmBool IsOverridden;                                      ///< Whether to be overridden
                    Rendering::CubismRenderer::CubismTextureColor Color;        ///< Color
                };

                /**
                 * Structure for managing the override of parameter repetition settings
                 */
                struct ParameterRepeatData {
                    /**
                     * Constructor
                     */
                    ParameterRepeatData()
                            : IsOverridden(false), IsParameterRepeated(false) {
                    }

                    /**
                     * Constructor
                     *
                     * @param isOverridden whether to be overriden
                     * @param isParameterRepeated override flag for settings
                     */
                    ParameterRepeatData(csmBool isOverridden, csmBool isParameterRepeated)
                            : IsOverridden(isOverridden), IsParameterRepeated(isParameterRepeated) {
                    }

                    /**
                     * Destructor
                     */
                    virtual ~ParameterRepeatData() {
                    }

                    csmBool IsOverridden;            ///< Whether to be overridden
                    csmBool IsParameterRepeated;     ///< Override flag for settings
                };

                /**
                 * Calculates and updates the model state based on the set parameters.
                 */
                void Update() const;

                /**
                 * Returns the width of the canvas.
                 *
                 * @return Width of the canvas in pixels
                 */
                csmFloat32 GetCanvasWidthPixel() const;

                /**
                 * Returns the height of the canvas.
                 *
                 * @return Height of the canvas in pixels
                 */
                csmFloat32 GetCanvasHeightPixel() const;

                /**
                 * Returns the pixels per unit (PPU).
                 *
                 * @return Pixels per unit
                 */
                csmFloat32 GetPixelsPerUnit() const;

                /**
                 * Returns the width of the canvas.
                 *
                 * @return Width of the canvas in PPU (pixels per unit)
                 */
                csmFloat32 GetCanvasWidth() const;

                /**
                 * Returns the height of the canvas.
                 *
                 * @return Height of the canvas in PPU (pixels per unit)
                 */
                csmFloat32 GetCanvasHeight() const;

                /**
                 * Returns the index of the part.
                 *
                 * @param partId Part ID
                 *
                 * @return Index of the part
                 */
                csmInt32 GetPartIndex(CubismIdHandle partId);

                /**
                 * Returns the ID of the part.
                 *
                 * @param partIndex Index of the part
                 * @return Part ID
                 */
                CubismIdHandle GetPartId(csmUint32 partIndex);

                /**
                 * Returns the number of parts.
                 *
                 * @return Number of parts
                 */
                csmInt32 GetPartCount() const;

                /**
                 * Returns the index of the parent parts for the parts.
                 *
                 * @return Index of parent parts for the parts.
                 */
                const csmInt32 *GetPartParentPartIndices() const;

                /**
                 * Sets the opacity of the part.
                 *
                 * @param partId Part ID
                 * @param opacity Opacity
                 */
                void SetPartOpacity(CubismIdHandle partId, csmFloat32 opacity);

                /**
                 * Sets the opacity of the part.
                 *
                 * @param partIndex Part index
                 * @param opacity Part opacity
                 */
                void SetPartOpacity(csmInt32 partIndex, csmFloat32 opacity);

                /**
                 * Returns the opacity of the part.
                 *
                 * @param partId Part ID
                 *
                 * @return Part opacity
                 */
                csmFloat32 GetPartOpacity(CubismIdHandle partId);

                /**
                 * Returns the opacity of the part.
                 *
                 * @param partIndex Part index
                 *
                 * @return Part opacity
                 */
                csmFloat32 GetPartOpacity(csmInt32 partIndex);

                /**
                 * Returns the index of the parameter.
                 *
                 * @param parameterId Parameter ID
                 *
                 * @return Parameter index
                 */
                csmInt32 GetParameterIndex(CubismIdHandle parameterId);

                /**
                 * Returns the ID of the parameter
                 *
                 * Retrieves the ID of the parameter.
                 *
                 * @param parameterIndex Index of the parameter
                 * @return Parameter ID
                 */
                CubismIdHandle GetParameterId(csmUint32 parameterIndex);

                /**
                 * Returns the number of parameters.
                 *
                 * @return Number of parameters
                 */
                csmInt32 GetParameterCount() const;

                /**
                 * Returns the type of the parameter.
                 *
                 * @param parameterIndex Parameter index
                 *
                 * @return Parameter type
                 */
                Core::csmParameterType GetParameterType(csmUint32 parameterIndex) const;

                /**
                 * Returns the maximum value of the parameter.
                 *
                 * @param parameterIndex Parameter index
                 *
                 * @return Maximum value of the parameter
                 */
                csmFloat32 GetParameterMaximumValue(csmUint32 parameterIndex) const;

                /**
                 * Returns the minimum value of the parameter.
                 *
                 * @param parameterIndex Parameter index
                 *
                 * @return Minimum value of the parameter
                 */
                csmFloat32 GetParameterMinimumValue(csmUint32 parameterIndex) const;

                /**
                 * Returns the default value of the parameter.
                 *
                 * @param parameterIndex Parameter index
                 *
                 * @return Default value of the parameter
                 */
                csmFloat32 GetParameterDefaultValue(csmUint32 parameterIndex) const;

                /**
                 * Returns the value of the parameter.
                 *
                 * @param parameterId Parameter ID
                 *
                 * @return Parameter value
                 */
                csmFloat32 GetParameterValue(CubismIdHandle parameterId);

                /**
                 * Returns the value of the parameter.
                 *
                 * @param parameterIndex Parameter index
                 *
                 * @return Parameter value
                 */
                csmFloat32 GetParameterValue(csmInt32 parameterIndex);

                /**
                 * Sets the value of the parameter.
                 *
                 * @param parameterId Parameter ID
                 * @param value Parameter value
                 * @param weight Weight
                 */
                void SetParameterValue(CubismIdHandle parameterId, csmFloat32 value, csmFloat32 weight = 1.0f);

                /**
                 * Sets the value of the parameter.
                 *
                 * @param parameterIndex Parameter index
                 * @param value Parameter value
                 * @param weight Weight
                 */
                void SetParameterValue(csmInt32 parameterIndex, csmFloat32 value, csmFloat32 weight = 1.0f);

                /**
                 * Gets whether the parameter has the repeat setting.
                 *
                 * @param parameterIndex Parameter index
                 *
                 * @return true if it is set, otherwise returns false.
                 */
                csmBool IsRepeat(csmInt32 parameterIndex) const;

                /**
                 * Returns the calculated result ensuring the value falls within the parameter's range.
                 *
                 * @param parameterIndex Parameter index
                 * @param value Parameter value
                 *
                 * @return a value that falls within the parameter’s range. If the parameter does not exist, returns it as is.
                 */
                csmFloat32 GetParameterRepeatValue(csmInt32 parameterIndex, csmFloat32 value) const;

                /**
                 * Returns the result of clamping the value to ensure it falls within the parameter's range.
                 *
                 * @param parameterIndex Parameter index
                 * @param value Parameter value
                 *
                 * @return the clamped value. If the parameter does not exist, returns it as is.
                 */
                csmFloat32 GetParameterClampValue(csmInt32 parameterIndex, csmFloat32 value) const;

                /**
                 * Returns the repeat of the parameter.
                 *
                 * @param parameterIndex Parameter index
                 *
                 * @return the raw data parameter repeat from the Cubism Core.
                 */
                csmBool GetParameterRepeats(csmUint32 parameterIndex) const;

                /**
                 * Adds to the value of the parameter.
                 *
                 * @param parameterId Parameter ID
                 * @param value Value to be added
                 * @param weight Weight
                 */
                void AddParameterValue(CubismIdHandle parameterId, csmFloat32 value, csmFloat32 weight = 1.0f);

                /**
                 * Adds to the value of the parameter.
                 *
                 * @param parameterIndex Parameter index
                 * @param value Value to be added
                 * @param weight Weight
                 */
                void AddParameterValue(csmInt32 parameterIndex, csmFloat32 value, csmFloat32 weight = 1.0f);

                /**
                 * Multiplies the value of the parameter.
                 *
                 * @param parameterId Parameter ID
                 * @param value Value to be multiplied
                 * @param weight Weight
                 */
                void MultiplyParameterValue(CubismIdHandle parameterId, csmFloat32 value, csmFloat32 weight = 1.0f);

                /**
                 * Multiplies the value of the parameter.
                 *
                 * @param parameterIndex Parameter index
                 * @param value Value to be multiplied
                 * @param weight Weight
                 */
                void MultiplyParameterValue(csmInt32 parameterIndex, csmFloat32 value, csmFloat32 weight = 1.0f);

                /**
                 * Returns the index of the drawable.
                 *
                 * @param drawableId Drawable ID
                 *
                 * @return Index of the drawable
                 */
                csmInt32 GetDrawableIndex(CubismIdHandle drawableId) const;

                /**
                 * Returns the number of drawables.
                 *
                 * @return Number of drawables
                 */
                csmInt32 GetDrawableCount() const;

                /**
                 * Returns the ID of the drawable.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return Drawable ID
                 */
                CubismIdHandle GetDrawableId(csmInt32 drawableIndex) const;

                /**
                 * Returns the list of drawable render orders.
                 *
                 * @return List of drawable render orders
                 */
                const csmInt32 *GetDrawableRenderOrders() const;

                /**
                 * Returns the list of texture indices attached to the drawable.
                 *
                 * @deprecated This function is deprecated due to a naming error, use getDrawableTextureIndex instead.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return List of texture indices
                 */
                csmInt32 GetDrawableTextureIndices(csmInt32 drawableIndex) const;

                /**
                 * Returns the texture index attached to the drawable.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return Texture index attached to the drawable
                 */
                csmInt32 GetDrawableTextureIndex(csmInt32 drawableIndex) const;

                /**
                 * Returns the number of vertex indices in the drawable.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return Number of vertex indices in the drawable
                 */
                csmInt32 GetDrawableVertexIndexCount(csmInt32 drawableIndex) const;

                /**
                 * Returns the number of vertices in the drawable.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return Number of vertices in the drawable
                 */
                csmInt32 GetDrawableVertexCount(csmInt32 drawableIndex) const;

                /**
                 * Returns the list of vertices in the drawable.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return List of vertices in the drawable
                 */
                const csmFloat32 *GetDrawableVertices(csmInt32 drawableIndex) const;

                /**
                 * Returns the list of vertex indices in the drawable.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return List of vertex indices in the drawable
                 */
                const csmUint16 *GetDrawableVertexIndices(csmInt32 drawableIndex) const;

                /**
                 * Returns the list of vertices in the drawable.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return List of vertices in the drawable
                 */
                const Core::csmVector2 *GetDrawableVertexPositions(csmInt32 drawableIndex) const;

                /**
                 * Returns the list of vertex UVs in the drawable.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return List of vertex UVs in the drawable
                 */
                const Core::csmVector2 *GetDrawableVertexUvs(csmInt32 drawableIndex) const;

                /**
                 * Returns the opacity of the drawable.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return Opacity of the drawable
                 */
                csmFloat32 GetDrawableOpacity(csmInt32 drawableIndex) const;

                /**
                 * Returns the multiply color of the drawable.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return Multiply color of the drawable
                 */
                Core::csmVector4 GetDrawableMultiplyColor(csmInt32 drawableIndex) const;

                /**
                 * Returns the screen color of the drawable.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return Screen color of the drawable
                 */
                Core::csmVector4 GetDrawableScreenColor(csmInt32 drawableIndex) const;

                /**
                 * Returns the index of the parent part of the drawable.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return Index of the parent part of the drawable
                 */
                csmInt32 GetDrawableParentPartIndex(csmUint32 drawableIndex) const;

                /**
                 * Returns the blend mode of the drawable.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return Blend mode of the drawable
                 */
                Rendering::CubismRenderer::CubismBlendMode GetDrawableBlendMode(csmInt32 drawableIndex) const;

                /**
                 * Returns the inverted mask setting for the drawable.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return Inverted mask setting of the drawable. true if inverted.
                 *
                 * @note Ignored if the mask is not used.
                 */
                csmBool GetDrawableInvertedMask(csmInt32 drawableIndex) const;

                /**
                 * Returns the visibility information of the drawable.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return Visibility state of the drawable. true if visible.
                 */
                csmBool GetDrawableDynamicFlagIsVisible(csmInt32 drawableIndex) const;

                /**
                 * Returns whether the visibility state of the drawable has changed from the dynamic flag.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return true if the visibility state of the drawable has changed.
                 */
                csmBool GetDrawableDynamicFlagVisibilityDidChange(csmInt32 drawableIndex) const;

                /**
                 * Returns whether the opacity of the drawable has changed from the dynamic flag.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return true if the opacity of the drawable has changed.
                 */
                csmBool GetDrawableDynamicFlagOpacityDidChange(csmInt32 drawableIndex) const;

                /**
                 * Returns whether the draw order of the drawable has changed from the dynamic flag.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return true if the draw order of the drawable has changed.
                 *
                 * @note Draw order is information specified from 0 to 1000 on the ArtMesh.
                 */
                csmBool GetDrawableDynamicFlagDrawOrderDidChange(csmInt32 drawableIndex) const;

                /**
                 * Returns whether the render order of the drawable has changed from the dynamic flag.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return true if the render order of the drawable has changed.
                 */
                csmBool GetDrawableDynamicFlagRenderOrderDidChange(csmInt32 drawableIndex) const;

                /**
                 * Returns whether the vertex information of the drawable has changed from the dynamic flag.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return true if the vertex information of the drawable has changed.
                 */
                csmBool GetDrawableDynamicFlagVertexPositionsDidChange(csmInt32 drawableIndex) const;

                /**
                 * Returns whether the multiply or screen color of the drawable has changed from the dynamic flag.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return true if the multiply or screen color of the drawable has changed.
                 */
                csmBool GetDrawableDynamicFlagBlendColorDidChange(csmInt32 drawableIndex) const;

                /**
                 * Returns the list of clipping masks of the drawables.
                 *
                 * @return List of clipping masks of the drawables
                 */
                const csmInt32 **GetDrawableMasks() const;

                /**
                 * Returns the list of the number of clipping masks of the drawables.
                 *
                 * @return List of the number of clipping masks of the drawables
                 */
                const csmInt32 *GetDrawableMaskCounts() const;

                /**
                 * Checks whether the model uses clipping masks.
                 *
                 * @return true if the model uses clipping masks.
                 */
                csmBool IsUsingMasking() const;

                /**
                 * Loads temporarily stored parameter values.
                 */
                void LoadParameters();

                /**
                 * Stores the value of the parameter temporarily.
                 */
                void SaveParameters();

                /**
                 * Returns the multiply color from the list of drawables.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return Multiply color (CubismTextureColor)
                 */
                Rendering::CubismRenderer::CubismTextureColor GetMultiplyColor(csmInt32 drawableIndex) const;

                /**
                 * Returns the screen color from the list of drawables.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return Screen color (CubismTextureColor)
                 */
                Rendering::CubismRenderer::CubismTextureColor GetScreenColor(csmInt32 drawableIndex) const;

                /**
                 * Sets the multiply color of the drawable.
                 *
                 * @param drawableIndex Drawable index
                 * @param color Multiply color to be set (CubismTextureColor)
                 */
                void SetMultiplyColor(csmInt32 drawableIndex, const Rendering::CubismRenderer::CubismTextureColor &color);

                /**
                 * Sets the multiply color of the drawable.
                 *
                 * @param drawableIndex Drawable index
                 * @param r Red value of the multiply color to be set
                 * @param g Green value of the multiply color to be set
                 * @param b Blue value of the multiply color to be set
                 * @param a Alpha value of the multiply color to be set
                 */
                void SetMultiplyColor(csmInt32 drawableIndex, csmFloat32 r, csmFloat32 g, csmFloat32 b, csmFloat32 a = 1.0f);

                /**
                 * Sets the screen color of the drawable.
                 *
                 * @param drawableIndex Drawable index
                 * @param color Screen color to be set (CubismTextureColor)
                 */
                void SetScreenColor(csmInt32 drawableIndex, const Rendering::CubismRenderer::CubismTextureColor &color);

                /**
                 * Sets the screen color of the drawable.
                 *
                 * @param drawableIndex Drawable index
                 * @param r Red value of the screen color to be set
                 * @param g Green value of the screen color to be set
                 * @param b Blue value of the screen color to be set
                 * @param a Alpha value of the screen color to be set
                 */
                void SetScreenColor(csmInt32 drawableIndex, csmFloat32 r, csmFloat32 g, csmFloat32 b, csmFloat32 a = 1.0f);

                /**
                 * Returns the multiply color of the part.
                 */
                Rendering::CubismRenderer::CubismTextureColor GetPartMultiplyColor(csmInt32 partIndex) const;

                /**
                 * Returns the screen color of the part.
                 */
                Rendering::CubismRenderer::CubismTextureColor GetPartScreenColor(csmInt32 partIndex) const;

                /**
                 * Sets the multiply color of the part.
                 */
                void SetPartMultiplyColor(csmInt32 partIndex, const Rendering::CubismRenderer::CubismTextureColor &color);

                /**
                 * Sets the multiply color of the part.
                 */
                void SetPartMultiplyColor(csmInt32 partIndex, csmFloat32 r, csmFloat32 g, csmFloat32 b, csmFloat32 a = 1.0f);

                /**
                 * Sets the screen color of the part.
                 */
                void SetPartScreenColor(csmInt32 partIndex, const Rendering::CubismRenderer::CubismTextureColor &color);

                /**
                 * Sets the screen color of the part.
                 */
                void SetPartScreenColor(csmInt32 partIndex, csmFloat32 r, csmFloat32 g, csmFloat32 b, csmFloat32 a = 1.0f);

                /**
                 * Checks whether parameter repetition is performed for the entire model.
                 *
                 * @return true if parameter repetition is performed for the entire model; otherwise returns false.
                 */
                csmBool GetOverrideFlagForModelParameterRepeat() const;

                /**
                 * Sets whether parameter repetition is performed for the entire model.
                 * Use true to perform parameter repetition for the entire model, or false to not perform it.
                 */
                void SetOverrideFlagForModelParameterRepeat(csmBool isRepeat);

                /**
                 * Returns the flag indicating whether to override the parameter repeat.
                 *
                 * @param parameterIndex Parameter index
                 *
                 * @return true if the parameter repeat is overridden, false otherwise.
                 */
                csmBool GetOverrideFlagForParameterRepeat(csmInt32 parameterIndex) const;

                /**
                 * Sets the flag indicating whether to override the parameter repeat.
                 *
                 * @param parameterIndex Parameter index
                 * @param value true if it is to be overridden; otherwise, false.
                 */
                void SetOverrideFlagForParameterRepeat(csmInt32 parameterIndex, csmBool value);

                /**
                 * Returns the repeat flag.
                 *
                 * @param parameterIndex Parameter index
                 *
                 * @return true if repeating, false otherwise.
                 */
                csmBool GetRepeatFlagForParameterRepeat(csmInt32 parameterIndex) const;

                /**
                 * Sets the repeat flag.
                 *
                 * @param parameterIndex Parameter index
                 * @param value true to enable repeating, false otherwise.
                 */
                void SetRepeatFlagForParameterRepeat(csmInt32 parameterIndex, csmBool value);

                /**
                 * Returns the flag indicating whether the color set at runtime is used as the multiply color for the entire model during rendering.
                 *
                 * @deprecated This function is deprecated due to a naming change, use GetOverrideFlagForModelMultiplyColors instead.
                 *
                 * @return true if the color set at runtime is used; otherwise false.
                 */
                csmBool GetOverwriteFlagForModelMultiplyColors() const;

                /**
                 * Returns the flag indicating whether the color set at runtime is used as the multiply color for the entire model during rendering.
                 *
                 * @return true if the color set at runtime is used; otherwise false.
                 */
                csmBool GetOverrideFlagForModelMultiplyColors() const;

                /**
                 * Returns the flag indicating whether the color set at runtime is used as the screen color for the entire model during rendering.
                 *
                 * @deprecated This function is deprecated due to a naming change, use GetOverrideFlagForModelScreenColors instead.
                 *
                 * @return true if the color set at runtime is used; otherwise false.
                 */
                csmBool GetOverwriteFlagForModelScreenColors() const;

                /**
                 * Returns the flag indicating whether the color set at runtime is used as the screen color for the entire model during rendering.
                 *
                 * @return true if the color set at runtime is used; otherwise false.
                 */
                csmBool GetOverrideFlagForModelScreenColors() const;

                /**
                 * Sets the flag indicating whether the color set at runtime is used as the multiply color for the entire model during rendering.
                 *
                 * @deprecated This function is deprecated due to a naming change, use SetOverrideFlagForModelMultiplyColors instead.
                 *
                 * @param value true if the color set at runtime is to be used; otherwise false.
                 */
                void SetOverwriteFlagForModelMultiplyColors(csmBool value);

                /**
                 * Sets the flag indicating whether the color set at runtime is used as the multiply color for the entire model during rendering.
                 *
                 * @param value true if the color set at runtime is to be used; otherwise false.
                 */
                void SetOverrideFlagForModelMultiplyColors(csmBool value);

                /**
                 * Sets the flag indicating whether the color set at runtime is used as the screen color for the entire model during rendering.
                 *
                 * @deprecated This function is deprecated due to a naming change, use SetOverrideFlagForModelScreenColors instead.
                 *
                 * @param value true if the color set at runtime is to be used; otherwise false.
                 */
                void SetOverwriteFlagForModelScreenColors(csmBool value);

                /**
                 * Sets the flag indicating whether the color set at runtime is used as the screen color for the entire model during rendering.
                 *
                 * @param value true if the color set at runtime is to be used; otherwise false.
                 */
                void SetOverrideFlagForModelScreenColors(csmBool value);

                /**
                 * Returns the flag indicating whether the color set at runtime is used as the multiply color for the drawable during rendering.
                 *
                 * @deprecated This function is deprecated due to a naming change, use GetOverrideFlagForDrawableMultiplyColors instead.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return true if the color set at runtime is used; otherwise false.
                 */
                csmBool GetOverwriteFlagForDrawableMultiplyColors(csmInt32 drawableIndex) const;

                /**
                 * Returns the flag indicating whether the color set at runtime is used as the multiply color for the drawable during rendering.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return true if the color set at runtime is used; otherwise false.
                 */
                csmBool GetOverrideFlagForDrawableMultiplyColors(csmInt32 drawableIndex) const;

                /**
                 * Returns the flag indicating whether the color set at runtime is used as the screen color for the drawable during rendering.
                 *
                 * @deprecated This function is deprecated due to a naming change, use GetOverrideFlagForDrawableScreenColors instead.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return true if the color set at runtime is used; otherwise false.
                 */
                csmBool GetOverwriteFlagForDrawableScreenColors(csmInt32 drawableIndex) const;

                /**
                 * Returns the flag indicating whether the color set at runtime is used as the screen color for the drawable during rendering.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return true if the color set at runtime is used; otherwise false.
                 */
                csmBool GetOverrideFlagForDrawableScreenColors(csmInt32 drawableIndex) const;

                /**
                 * Sets the flag indicating whether the color set at runtime is used as the multiply color for the drawable during rendering.
                 *
                 * @deprecated This function is deprecated due to a naming change, use SetOverrideFlagForDrawableMultiplyColors instead.
                 *
                 * @param drawableIndex Drawable index
                 * @param value true if the color set at runtime is to be used; otherwise false.
                 */
                void SetOverwriteFlagForDrawableMultiplyColors(csmUint32 drawableIndex, csmBool value);

                /**
                 * Sets the flag indicating whether the color set at runtime is used as the multiply color for the drawable during rendering.
                 *
                 * @param drawableIndex Drawable index
                 * @param value true if the color set at runtime is to be used; otherwise false.
                 */
                void SetOverrideFlagForDrawableMultiplyColors(csmUint32 drawableIndex, csmBool value);

                /**
                 * Sets the flag indicating whether the color set at runtime is used as the screen color for the drawable during rendering.
                 *
                 * @deprecated This function is deprecated due to a naming change, use SetOverrideFlagForDrawableScreenColors instead.
                 *
                 * @param drawableIndex Drawable index
                 * @param value true if the color set at runtime is to be used; otherwise false.
                 */
                void SetOverwriteFlagForDrawableScreenColors(csmUint32 drawableIndex, csmBool value);

                /**
                 * Sets the flag indicating whether the color set at runtime is used as the screen color for the drawable during rendering.
                 *
                 * @param drawableIndex Drawable index
                 * @param value true if the color set at runtime is to be used; otherwise false.
                 */
                void SetOverrideFlagForDrawableScreenColors(csmUint32 drawableIndex, csmBool value);

                /**
                 * Checks whether the part multiply color is overridden by the SDK.
                 *
                 * @deprecated This function is deprecated due to a naming change, use GetOverrideColorForPartMultiplyColors instead.
                 *
                 * @return true if the color information from the SDK is used; otherwise false.
                 */
                csmBool GetOverwriteColorForPartMultiplyColors(csmInt32 partIndex) const;

                /**
                 * Checks whether the part multiply color is overridden by the SDK.
                 *
                 * @return true if the color information from the SDK is used; otherwise false.
                 */
                csmBool GetOverrideColorForPartMultiplyColors(csmInt32 partIndex) const;

                /**
                 * Checks whether the part screen color is overridden by the SDK.
                 *
                 * @deprecated This function is deprecated due to a naming change, use GetOverrideColorForPartScreenColors instead.
                 *
                 * @return true if the color information from the SDK is used; otherwise false.
                 */
                csmBool GetOverwriteColorForPartScreenColors(csmInt32 partIndex) const;

                /**
                 * Checks whether the part screen color is overridden by the SDK.
                 *
                 * @return true if the color information from the SDK is used; otherwise false.
                 */
                csmBool GetOverrideColorForPartScreenColors(csmInt32 partIndex) const;

                /**
                 * Sets whether the part multiply color is overridden by the SDK.
                 * Use true to use the color information from the SDK, or false to use the color information from the model.
                 *
                 * @deprecated This function is deprecated due to a naming change, use SetOverrideColorForPartMultiplyColors instead.
                 */
                void SetOverwriteColorForPartMultiplyColors(csmUint32 partIndex, csmBool value);

                /**
                 * Sets whether the part multiply color is overridden by the SDK.
                 * Use true to use the color information from the SDK, or false to use the color information from the model.
                 */
                void SetOverrideColorForPartMultiplyColors(csmUint32 partIndex, csmBool value);

                /**
                 * Sets whether the part screen color is overridden by the SDK.
                 * Use true to use the color information from the SDK, or false to use the color information from the model.
                 *
                 * @deprecated This function is deprecated due to a naming change, use SetOverrideColorForPartScreenColors instead.
                 */
                void SetOverwriteColorForPartScreenColors(csmUint32 partIndex, csmBool value);

                /**
                 * Sets whether the part screen color is overridden by the SDK.
                 * Use true to use the color information from the SDK, or false to use the color information from the model.
                 */
                void SetOverrideColorForPartScreenColors(csmUint32 partIndex, csmBool value);

                /**
                 * Returns the culling information of the drawable.
                 *
                 * @param drawableIndex Drawable index
                 *
                 * @return Culling information of the drawable
                 */
                csmInt32 GetDrawableCulling(csmInt32 drawableIndex) const;

                /**
                 * Sets the culling information of the drawable.
                 */
                void SetDrawableCulling(csmInt32 drawableIndex, csmInt32 isCulling);

                /**
                 * Checks whether the culling settings for the entire model are overridden by the SDK.
                 *
                 * @deprecated This function is deprecated due to a naming change, use GetOverwriteFlagForModelCullings instead.
                 *
                 * @return true if the culling settings from the SDK are used; otherwise false.
                 */
                csmBool GetOverwriteFlagForModelCullings() const;

                /**
                 * Checks whether the culling settings for the entire model are overridden by the SDK.
                 *
                 * @return true if the culling settings from the SDK are used; otherwise false.
                 */
                csmBool GetOverrideFlagForModelCullings() const;

                /**
                 * Sets whether the culling settings for the entire model are overridden by the SDK.
                 * Use true to use the culling settings from the SDK, or false to use the culling settings from the model.
                 *
                 * @deprecated This function is deprecated due to a naming change, use SetOverrideFlagForModelCullings instead.
                 */
                void SetOverwriteFlagForModelCullings(csmBool value);

                /**
                 * Sets whether the culling settings for the entire model are overridden by the SDK.
                 * Use true to use the culling settings from the SDK, or false to use the culling settings from the model.
                 */
                void SetOverrideFlagForModelCullings(csmBool value);

                /**
                 * Checks whether the culling settings for the drawable are overridden by the SDK.
                 *
                 * @deprecated This function is deprecated due to a naming change, use GetOverrideFlagForDrawableCullings instead.
                 *
                 * @return true if the culling settings from the SDK are used; otherwise false.
                 */
                csmBool GetOverwriteFlagForDrawableCullings(csmInt32 drawableIndex) const;

                /**
                 * Checks whether the culling settings for the drawable are overridden by the SDK.
                 *
                 * @return true if the culling settings from the SDK are used; otherwise false.
                 */
                csmBool GetOverrideFlagForDrawableCullings(csmInt32 drawableIndex) const;

                /**
                 * Sets whether the culling settings for the drawable are overridden by the SDK.
                 * Use true to use the culling settings from the SDK, or false to use the culling settings from the model.
                 *
                 * @deprecated This function is deprecated due to a naming change, use SetOverrideFlagForDrawableCullings instead.
                 */
                void SetOverwriteFlagForDrawableCullings(csmUint32 drawableIndex, csmBool value);

                /**
                 * Sets whether the culling settings for the drawable are overridden by the SDK.
                 * Use true to use the culling settings from the SDK, or false to use the culling settings from the model.
                 */
                void SetOverrideFlagForDrawableCullings(csmUint32 drawableIndex, csmBool value);

                /**
                 * Returns the opacity of the model.
                 *
                 * @return Opacity value
                 */
                csmFloat32 GetModelOpacity();

                /**
                 * Sets the opacity of the model.
                 *
                 * @param value Opacity value
                 */
                void SetModelOpacity(csmFloat32 value);

                Core::csmModel *GetModel() const;

            private:
                CubismModel(Core::csmModel *model);

                virtual ~CubismModel();

                CubismModel(const CubismModel &);

                CubismModel &operator=(const CubismModel &);

                void Initialize();

                void SetPartColor(
                        csmUint32 partIndex,
                        csmFloat32 r, csmFloat32 g, csmFloat32 b, csmFloat32 a,
                        csmVector <PartColorData> &partColors,
                        csmVector <DrawableColorData> &drawableColors);

                void SetOverrideColorForPartColors(
                        csmUint32 partIndex,
                        csmBool value,
                        csmVector <CubismModel::PartColorData> &partColors,
                        csmVector <CubismModel::DrawableColorData> &drawableColors);

                csmMap <csmInt32, csmFloat32> _notExistPartOpacities;
                csmMap <CubismIdHandle, csmInt32> _notExistPartId;

                csmMap <csmInt32, csmFloat32> _notExistParameterValues;
                csmMap <CubismIdHandle, csmInt32> _notExistParameterId;

                csmVector <csmFloat32> _savedParameters;

                Core::csmModel *_model;

                csmFloat32 *_parameterValues;
                const csmFloat32 *_parameterMaximumValues;
                const csmFloat32 *_parameterMinimumValues;

                csmFloat32 *_partOpacities;

                csmFloat32 _modelOpacity;

                csmVector <CubismIdHandle> _parameterIds;
                csmVector <CubismIdHandle> _partIds;
                csmVector <CubismIdHandle> _drawableIds;
                csmVector <ParameterRepeatData> _userParameterRepeatDataList;
                csmVector <DrawableColorData> _userScreenColors;
                csmVector <DrawableColorData> _userMultiplyColors;
                csmVector <DrawableCullingData> _userCullings;
                csmVector <PartColorData> _userPartScreenColors;
                csmVector <PartColorData> _userPartMultiplyColors;
                csmVector <csmVector<csmUint32>> _partChildDrawables;
                csmBool _isOverriddenParameterRepeat;
                csmBool _isOverriddenModelMultiplyColors;
                csmBool _isOverriddenModelScreenColors;
                csmBool _isOverriddenCullings;
            };

        }
    }
}

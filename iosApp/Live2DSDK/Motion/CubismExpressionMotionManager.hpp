/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#pragma once

#include "Model/CubismModel.hpp"
#include "ACubismMotion.hpp"
#include "CubismMotionQueueManager.hpp"

namespace Live2D {
    namespace Cubism {
        namespace Framework {

/**
 * Handles the management of facial expression motions.
 */
            class CubismExpressionMotionManager : public CubismMotionQueueManager {
            public:
                /**
                 * Structure that holds facial expression values to apply to parameters
                 */
                struct ExpressionParameterValue {
                    CubismIdHandle ParameterId;        ///< Parameter ID
                    csmFloat32 AdditiveValue;      ///< Added value
                    csmFloat32 MultiplyValue;      ///< Multiplied value
                    csmFloat32 OverwriteValue;     ///< Overwritten value
                };

                /**
                 * Constructor
                 */
                CubismExpressionMotionManager();

                /**
                 * Destructor
                 */
                virtual ~CubismExpressionMotionManager();

                /**
                 * Returns the priority of the playing facial expression motion.
                 *
                 * @deprecated This function is deprecated because a priority value is not actually used during expression motion playback.
                 *
                 * @return priority of the facial expression motion
                 */
                csmInt32 GetCurrentPriority() const;

                /**
                 * Returns the priority of the reserved facial expression motion.
                 *
                 * @deprecated This function is deprecated because a priority value is not actually used during expression motion playback.
                 *
                 * @return priority of the facial expression motion
                 */
                csmInt32 GetReservePriority() const;

                /**
                 * Sets the priority of the reserved facial expression motion.
                 *
                 * @deprecated This function is deprecated because a priority value is not actually used during expression motion playback.
                 *
                 * @param[in] priority priority to set
                 */
                void SetReservePriority(csmInt32 priority);

                /**
                 * Starts the facial expression motion with the specified priority.
                 *
                 * @deprecated This function is deprecated because a priority value is not actually used during expression motion playback.
                 *             Use CubismMotionQueueManager::StartMotion(ACubismMotion* motion, csmBool autoDelete) instead.
                 *
                 * @param[in] motion motion
                 * @param[in] autoDelete true to delete the instance of the motion when playback ends
                 * @param[in] priority priority
                 * @return identifier of the started motion. Use this as an argument to IsFinished() to determine if the individual motion has finished. Returns -1 if the motion could not be started.
                 */
                CubismMotionQueueEntryHandle StartMotionPriority(ACubismMotion *motion, csmBool autoDelete, csmInt32 priority);

                /**
                 * Updates the facial expression motion and reflects the parameter values on the model.
                 *
                 * @param[in] model target model
                 * @param[in] deltaTimeSeconds delta time in seconds
                 * @return true if updated; otherwise false.
                 */
                csmBool UpdateMotion(CubismModel *model, csmFloat32 deltaTimeSeconds);

                /**
                 * @param index index of the facial expression motion to retrieve
                 *
                 * @return fade weight value of the facial expression
                 */
                csmFloat32 GetFadeWeight(csmInt32 index);

            private:

                /**
                 * @brief Set the weight of expression fade
                 *
                 * Set the weight of expression fade.
                 *
                 * @param[in]    index  Index of the expression motion to be set
                 * @param[in]    expressionFadeWeight   Weight value of expression fade
                 */
                void SetFadeWeight(csmInt32 index, csmFloat32 expressionFadeWeight);

                // Values of each parameter to be applied to the model
                csmVector <ExpressionParameterValue> *_expressionParameterValues;

                // Weights of the currently playing expression
                csmVector <csmFloat32> *_fadeWeights;

                csmInt32 _currentPriority;    ///< @deprecated This variable is deprecated because a priority value is not actually used during expression motion playback.
                csmInt32 _reservePriority;    ///< @deprecated This variable is deprecated because a priority value is not actually used during expression motion playback.
            };

        }
    }
}

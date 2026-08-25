#include "live2d_renderer.h"

#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>
#include <map>
#include <memory>
#include <algorithm>
#include <cstdlib>
#include <sys/stat.h>
#include <sys/types.h>
#include <dirent.h>
#include "miniz.h"

#include <mutex>

#if defined(ANDROID) || defined(__ANDROID__)
#include <GLES2/gl2.h>
#include <android/log.h>
#include <android/asset_manager.h>
#define LOG_TAG "Live2DNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#elif defined(IOS_BUILD) || defined(CSM_TARGET_IPHONE_ES2)
#include <OpenGLES/ES2/gl.h>
#include <cstdio>
#define LOGI(...) do { printf("[Live2DNative] "); printf(__VA_ARGS__); printf("\n"); fflush(stdout); } while(0)
#define LOGE(...) do { fprintf(stderr, "[Live2DNative] "); fprintf(stderr, __VA_ARGS__); fprintf(stderr, "\n"); fflush(stderr); } while(0)
#elif defined(MACOS_DESKTOP_BUILD) || defined(CSM_TARGET_MAC_GL)
#include <OpenGL/gl.h>
#include <OpenGL/OpenGL.h>
#include <cstdio>
#define LOGI(...) do { printf("[Live2DNative] "); printf(__VA_ARGS__); printf("\n"); fflush(stdout); } while(0)
#define LOGE(...) do { fprintf(stderr, "[Live2DNative] "); fprintf(stderr, __VA_ARGS__); fprintf(stderr, "\n"); fflush(stderr); } while(0)
#else
#include <OpenGLES/ES2/gl.h>
#include <cstdio>
#define LOGI(...) do { printf("[Live2DNative] "); printf(__VA_ARGS__); printf("\n"); fflush(stdout); } while(0)
#define LOGE(...) do { fprintf(stderr, "[Live2DNative] "); fprintf(stderr, __VA_ARGS__); fprintf(stderr, "\n"); fflush(stderr); } while(0)
#endif

// Cubism SDK Headers
#include <CubismFramework.hpp>
#include <CubismModelSettingJson.hpp>
#include <Model/CubismUserModel.hpp>
#include <Motion/CubismMotion.hpp>
#include <Motion/CubismMotionManager.hpp>
#include <Motion/CubismExpressionMotion.hpp>
#include <Motion/CubismExpressionMotionManager.hpp>
#include <Motion/CubismMotionQueueEntry.hpp>
#include <Rendering/OpenGL/CubismRenderer_OpenGLES2.hpp>
#include <Utils/CubismString.hpp>
#include <Id/CubismIdManager.hpp>

#define STB_IMAGE_IMPLEMENTATION
#include <stb/stb_image.h>

using namespace Csm;



#include "live2d_shaders.h"


// Universal file loader with candidate path fallback
static bool LoadFileBuffer(const std::string& rawPath, std::vector<char>& outBuffer) {
    std::string cleanPath = rawPath;
    const std::string fileScheme = "file://";

    if (cleanPath.rfind(fileScheme, 0) == 0) {
        cleanPath = cleanPath.substr(fileScheme.length());
    }

    std::string baseFilename = cleanPath;
    size_t lastSlashPos = cleanPath.find_last_of("/");
    if (lastSlashPos != std::string::npos) {
        baseFilename = cleanPath.substr(lastSlashPos + 1);
    }

    bool loaded = false;
    // Check embedded shaders first for 100% fail-safe GLSL compilation
    auto embedIt = g_embeddedShaders.find(baseFilename);
    if (embedIt != g_embeddedShaders.end()) {
        outBuffer.assign(embedIt->second.begin(), embedIt->second.end());
        loaded = true;
    } else {
        std::ifstream file(cleanPath.c_str(), std::ios::binary | std::ios::ate);
        if (file.is_open()) {
            std::streamsize size = file.tellg();
            file.seekg(0, std::ios::beg);
            outBuffer.resize(static_cast<size_t>(size));
            if (file.read(outBuffer.data(), size)) {
                loaded = true;
            }
        }
    }

    if (!loaded) {
        LOGE("Failed to open or read file: %s (cleanPath: %s)", rawPath.c_str(), cleanPath.c_str());
        return false;
    }

#if defined(MACOS_DESKTOP_BUILD) || defined(CSM_TARGET_MAC_GL)
    // Desktop GLSL cleanup for macOS OpenGL:
    if (baseFilename.find(".vert") != std::string::npos || baseFilename.find(".frag") != std::string::npos) {
        std::string shaderStr(outBuffer.data(), outBuffer.size());

        // 1. Remove any #version directives so concatenated shaders don't put #version in the middle of code
        size_t verPos = 0;
        while ((verPos = shaderStr.find("#version", verPos)) != std::string::npos) {
            size_t eol = shaderStr.find('\n', verPos);
            if (eol != std::string::npos) {
                shaderStr.replace(verPos, eol - verPos + 1, "//version removed\n");
            } else {
                shaderStr.replace(verPos, 8, "//version");
            }
            verPos += 18;
        }

        // 2. Comment out "precision ...;" statements on Desktop GLSL (where precision is a reserved word)
        size_t precPos = 0;
        while ((precPos = shaderStr.find("precision ", precPos)) != std::string::npos) {
            shaderStr.replace(precPos, 9, "//precision");
            precPos += 11;
        }

        // 3. Add precision macros
        shaderStr = "#define lowp\n#define mediump\n#define highp\n" + shaderStr;

        outBuffer.assign(shaderStr.begin(), shaderStr.end());
    }
#endif

    return true;
}

// POSIX-compliant Aligned Allocator for Cubism Framework
class SimpleCubismAllocator : public ICubismAllocator {
public:
    void* Allocate(const csmSizeType size) override {
        return malloc(size);
    }

    void Deallocate(void* memory) override {
        if (memory) {
            free(memory);
        }
    }

    void* AllocateAligned(const csmSizeType size, const csmUint32 alignment) override {
        void* ptr = nullptr;
        size_t align = alignment < sizeof(void*) ? sizeof(void*) : alignment;
        if (posix_memalign(&ptr, align, size) != 0) {
            return nullptr;
        }
        return ptr;
    }

    void DeallocateAligned(void* memory) override {
        if (memory) {
            free(memory);
        }
    }
};

static SimpleCubismAllocator g_cubismAllocator;
static CubismFramework::Option g_cubismOption;

static void InitCubismFrameworkStartUpIfNeeded() {
    if (CubismFramework::IsStarted()) {
        return;
    }

    g_cubismOption.LoggingLevel = CubismFramework::Option::LogLevel_Error;
    g_cubismOption.LogFunction = [](const char* message) {
        LOGE("[CubismSDK Error] %s", message);
    };
    g_cubismOption.LoadFileFunction = [](const std::string path, csmSizeInt* outSize) -> csmByte* {
        std::vector<char> buffer;
        if (LoadFileBuffer(path, buffer)) {
            *outSize = static_cast<csmSizeInt>(buffer.size());
            csmByte* data = new csmByte[buffer.size() + 1];
            memcpy(data, buffer.data(), buffer.size());
            data[buffer.size()] = '\0';
            return data;
        }
        *outSize = 0;
        return nullptr;
    };
    g_cubismOption.ReleaseBytesFunction = [](csmByte* byteData) {
        if (byteData) {
            delete[] byteData;
        }
    };

    CubismFramework::StartUp(&g_cubismAllocator, &g_cubismOption);
    LOGI("CubismFramework StartUp Completed");
}

static void InitCubismFrameworkInitializeIfNeeded() {
    InitCubismFrameworkStartUpIfNeeded();
    if (!CubismFramework::IsInitialized()) {
        CubismFramework::Initialize();
        LOGI("CubismFramework Initialize Completed on GL Thread");
    }
}

// Custom Live2D Model Wrapper following Cubism SDK LAppMinimumModel
class Live2DModelWrapper : public CubismUserModel {
public:
    Live2DModelWrapper() : _modelSetting(nullptr), _mocBuffer(nullptr), _viewportWidth(1024), _viewportHeight(1024), _frameCount(0) {
        _motionManager = CSM_NEW CubismMotionManager();
        _expressionManager = CSM_NEW CubismExpressionMotionManager();
    }

    ~Live2DModelWrapper() {
        ReleaseModel();
    }

    void ReleaseModel() {
        if (_motionManager) {
            _motionManager->StopAllMotions();
            _motionManager->SetReservePriority(0);
        }
        if (_expressionManager) {
            _expressionManager->StopAllMotions();
        }
        DeleteRenderer();
        if (_modelSetting) {
            delete _modelSetting;
            _modelSetting = nullptr;
        }
        for (auto& pair : _motions) {
            ACubismMotion::Delete(pair.second);
        }
        _motions.clear();
        for (auto& pair : _expressions) {
            ACubismMotion::Delete(pair.second);
        }
        _expressions.clear();
        for (GLuint texId : _textureIds) {
            glDeleteTextures(1, &texId);
        }
        _textureIds.clear();
        _motionGroupNames.clear();
        _expressionNames.clear();
        if (_moc) {
            if (_model) {
                _moc->DeleteModel(_model);
                _model = nullptr;
            }
            CubismMoc::Delete(_moc);
            _moc = nullptr;
        }
        if (_mocBuffer) {
            CubismFramework::DeallocateAligned(_mocBuffer);
            _mocBuffer = nullptr;
        }
    }

    bool LoadAssets(const std::string& modelDir, const std::string& modelJsonName) {
        std::lock_guard<std::recursive_mutex> lock(_mutex);
        ReleaseModel();
        _modelDir = modelDir;
        if (!_modelDir.empty() && _modelDir.back() != '/') {
            _modelDir += '/';
        }

        std::string jsonPath = _modelDir + modelJsonName;
        std::vector<char> buffer;
        if (!LoadFileBuffer(jsonPath, buffer)) {
            LOGE("Failed to open Live2D model JSON: %s", jsonPath.c_str());
            return false;
        }

        _modelSetting = new CubismModelSettingJson(reinterpret_cast<csmByte*>(buffer.data()), static_cast<csmSizeInt>(buffer.size()));

        // Load Model MOC3 with persistent aligned buffer memory
        std::string mocFileName = _modelSetting->GetModelFileName();
        if (!mocFileName.empty()) {
            std::string mocPath = _modelDir + mocFileName;
            std::vector<char> mocBuffer;
            if (LoadFileBuffer(mocPath, mocBuffer)) {
                _mocBuffer = static_cast<csmByte*>(
                    CubismFramework::AllocateAligned(mocBuffer.size(), Live2D::Cubism::Core::csmAlignofMoc)
                );
                if (_mocBuffer) {
                    memcpy(_mocBuffer, mocBuffer.data(), mocBuffer.size());
                    LoadModel(_mocBuffer, static_cast<csmSizeInt>(mocBuffer.size()), true);
                }
            }
        }

        if (!_model) {
            LOGE("Failed to load model MOC3");
            return false;
        }

        LOGI("Model MOC3 Loaded. Canvas Width: %.2f, Height: %.2f", _model->GetCanvasWidth(), _model->GetCanvasHeight());

        // Setup Layout Matrix
        csmMap<csmString, csmFloat32> layout;
        if (_modelSetting->GetLayoutMap(layout)) {
            _modelMatrix->SetupFromLayout(layout);
        }

#if defined(MACOS_DESKTOP_BUILD) || defined(CSM_TARGET_MAC_GL)
        bool hasGLContext = (CGLGetCurrentContext() != nullptr);
#else
        bool hasGLContext = true;
#endif

        if (hasGLContext) {
            // Ensure static shaders are initialized for current GL context
            Rendering::CubismShader_OpenGLES2::GetInstance();

            // Create Renderer & Initialize Shaders
            CreateRenderer(_viewportWidth, _viewportHeight);
            GetRenderer<Rendering::CubismRenderer_OpenGLES2>()->IsPremultipliedAlpha(true);

            // Load Textures
            int textureCount = _modelSetting->GetTextureCount();
            LOGI("Loading %d textures for Live2D model...", textureCount);
            for (int i = 0; i < textureCount; i++) {
                std::string texFile = _modelSetting->GetTextureFileName(i);
                if (texFile.empty()) continue;
                std::string texPath = _modelDir + texFile;

                std::vector<char> texBuffer;
                if (LoadFileBuffer(texPath, texBuffer)) {
                    int w, h, comp;
                    unsigned char* pixels = stbi_load_from_memory(reinterpret_cast<const unsigned char*>(texBuffer.data()), static_cast<int>(texBuffer.size()), &w, &h, &comp, STBI_rgb_alpha);
                    if (pixels) {
                        for (int pIdx = 0; pIdx < w * h; pIdx++) {
                            unsigned char* p = pixels + pIdx * 4;
                            float alphaFactor = static_cast<float>(p[3]) / 255.0f;
                            p[0] = static_cast<unsigned char>(p[0] * alphaFactor);
                            p[1] = static_cast<unsigned char>(p[1] * alphaFactor);
                            p[2] = static_cast<unsigned char>(p[2] * alphaFactor);
                        }

                        GLuint texId;
                        glGenTextures(1, &texId);
                        glBindTexture(GL_TEXTURE_2D, texId);
                        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
                        glGenerateMipmap(GL_TEXTURE_2D);
                        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
                        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
                        glBindTexture(GL_TEXTURE_2D, 0);
                        stbi_image_free(pixels);

                        GetRenderer<Rendering::CubismRenderer_OpenGLES2>()->BindTexture(i, texId);
                        _textureIds.push_back(texId);
                    } else {
                        LOGE("Failed to decode PNG texture via stb_image: %s", texPath.c_str());
                    }
                }
            }
        }

        // Preload Physics
        std::string physicsFile = _modelSetting->GetPhysicsFileName();
        if (!physicsFile.empty()) {
            std::string physicsPath = _modelDir + physicsFile;
            std::vector<char> pbuf;
            if (LoadFileBuffer(physicsPath, pbuf)) {
                LoadPhysics(reinterpret_cast<csmByte*>(pbuf.data()), static_cast<csmSizeInt>(pbuf.size()));
            }
        }

        // Preload Pose
        std::string poseFile = _modelSetting->GetPoseFileName();
        if (!poseFile.empty()) {
            std::string posePath = _modelDir + poseFile;
            std::vector<char> pbuf;
            if (LoadFileBuffer(posePath, pbuf)) {
                LoadPose(reinterpret_cast<csmByte*>(pbuf.data()), static_cast<csmSizeInt>(pbuf.size()));
            }
        }

        // Preload Expressions
        int exprCount = _modelSetting->GetExpressionCount();
        for (int i = 0; i < exprCount; i++) {
            std::string exprName = _modelSetting->GetExpressionName(i);
            std::string exprFile = _modelSetting->GetExpressionFileName(i);
            std::string exprPath = _modelDir + exprFile;

            std::vector<char> ebuf;
            if (LoadFileBuffer(exprPath, ebuf)) {
                ACubismMotion* motion = LoadExpression(reinterpret_cast<csmByte*>(ebuf.data()), static_cast<csmSizeInt>(ebuf.size()), exprName.c_str());
                if (motion) {
                    _expressions[exprName] = motion;
                    _expressionNames.push_back(exprName);
                }
            }
        }

        // Preload Motions
        int groupCount = _modelSetting->GetMotionGroupCount();
        for (int i = 0; i < groupCount; i++) {
            std::string groupName = _modelSetting->GetMotionGroupName(i);
            _motionGroupNames.push_back(groupName);
            int mcount = _modelSetting->GetMotionCount(groupName.c_str());
            for (int j = 0; j < mcount; j++) {
                std::string mfile = _modelSetting->GetMotionFileName(groupName.c_str(), j);
                std::string mpath = _modelDir + mfile;

                std::vector<char> mbuf;
                if (LoadFileBuffer(mpath, mbuf)) {
                    std::string key = groupName + "_" + std::to_string(j);
                    ACubismMotion* motion = LoadMotion(reinterpret_cast<csmByte*>(mbuf.data()), static_cast<csmSizeInt>(mbuf.size()), key.c_str());
                    if (motion) {
                        _motions[key] = motion;
                    }
                }
            }
        }

        // Start Idle Motion
        StartMotion("Idle", 0, 1);

        LOGI("Live2D Model Loaded Successfully: %s (Textures: %zu, Motions: %zu)", modelJsonName.c_str(), _textureIds.size(), _motions.size());
        return true;
    }

    void StartMotion(const std::string& group, int index, int priority = 3 /* PriorityForce */) {
        std::lock_guard<std::recursive_mutex> lock(_mutex);
        if (!_motionManager || _motions.empty()) return;

        std::string key = group + "_" + std::to_string(index);
        auto it = _motions.find(key);
        if (it == _motions.end()) {
            // Case-insensitive / prefix search fallback for group names like "idle" vs "Idle"
            for (auto mIt = _motions.begin(); mIt != _motions.end(); ++mIt) {
                if (mIt->first.rfind(group, 0) == 0 ||
                    #if defined(_WIN32)
                    _stricmp(mIt->first.c_str(), group.c_str()) == 0
                    #else
                    strcasecmp(mIt->first.c_str(), group.c_str()) == 0
                    #endif
                ) {
                    it = mIt;
                    break;
                }
            }
        }

        // Fallback to first available motion if requested group was not found
        if (it == _motions.end() && !_motions.empty()) {
            it = _motions.begin();
        }

        if (it == _motions.end()) return;

        if (priority == 3 /* PriorityForce */) {
            _motionManager->SetReservePriority(priority);
        } else if (!_motionManager->ReserveMotion(priority)) {
            return;
        }

        // 官方 Cubism SDK 优雅平滑过渡：FadeOut 前一个动作并 FadeIn 新动作，保留部件透明度与 Pose
        _motionManager->StartMotionPriority(it->second, false, priority);
    }

    void SetExpression(const std::string& expressionId) {
        std::lock_guard<std::recursive_mutex> lock(_mutex);
        auto it = _expressions.find(expressionId);
        if (it != _expressions.end() && _expressionManager) {
            // 官方 Cubism SDK 表情管理器自动渐变切换，不重置基本参数
            _expressionManager->StartMotion(it->second, false);
        }
    }

    const std::vector<std::string>& GetMotionGroups() const { return _motionGroupNames; }
    const std::vector<std::string>& GetExpressions() const { return _expressionNames; }

    void OnSurfaceCreated() {
        std::lock_guard<std::recursive_mutex> lock(_mutex);
        Rendering::CubismShader_OpenGLES2::GetInstance();

        if (_modelSetting && _model) {
            DeleteRenderer();
            CreateRenderer(_viewportWidth, _viewportHeight);
            GetRenderer<Rendering::CubismRenderer_OpenGLES2>()->IsPremultipliedAlpha(true);

            _textureIds.clear();
            int textureCount = _modelSetting->GetTextureCount();
            for (int i = 0; i < textureCount; i++) {
                std::string texFile = _modelSetting->GetTextureFileName(i);
                if (texFile.empty()) continue;
                std::string texPath = _modelDir + texFile;

                std::vector<char> texBuffer;
                if (LoadFileBuffer(texPath, texBuffer)) {
                    int w, h, comp;
                    unsigned char* pixels = stbi_load_from_memory(reinterpret_cast<const unsigned char*>(texBuffer.data()), static_cast<int>(texBuffer.size()), &w, &h, &comp, STBI_rgb_alpha);
                    if (pixels) {
                        for (int pIdx = 0; pIdx < w * h; pIdx++) {
                            unsigned char* p = pixels + pIdx * 4;
                            float alphaFactor = static_cast<float>(p[3]) / 255.0f;
                            p[0] = static_cast<unsigned char>(p[0] * alphaFactor);
                            p[1] = static_cast<unsigned char>(p[1] * alphaFactor);
                            p[2] = static_cast<unsigned char>(p[2] * alphaFactor);
                        }

                        GLuint texId;
                        glGenTextures(1, &texId);
                        glBindTexture(GL_TEXTURE_2D, texId);
                        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
                        glGenerateMipmap(GL_TEXTURE_2D);
                        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
                        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
                        glBindTexture(GL_TEXTURE_2D, 0);
                        stbi_image_free(pixels);

                        GetRenderer<Rendering::CubismRenderer_OpenGLES2>()->BindTexture(i, texId);
                        _textureIds.push_back(texId);
                    }
                }
            }
        }
    }

    void OnSurfaceChanged(int w, int h) {
        _viewportWidth = w;
        _viewportHeight = h;
        glViewport(0, 0, w, h);
        SetRenderTargetSize(w, h);
    }

    void OnDrawFrame() {
        std::unique_lock<std::recursive_mutex> lock(_mutex, std::try_to_lock);
        if (!lock.owns_lock() || !_model) return;

        _frameCount++;
        glViewport(0, 0, _viewportWidth, _viewportHeight);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        float deltaTime = 0.016f; // ~60fps
        _model->LoadParameters();

        if (_motionManager->IsFinished()) {
            StartMotion("Idle", 0, 1);
        }

        _motionManager->UpdateMotion(_model, deltaTime);

        if (_expressionManager) {
            _expressionManager->UpdateMotion(_model, deltaTime);
        }

        if (_physics) {
            _physics->Evaluate(_model, deltaTime);
        }

        if (_pose) {
            _pose->UpdateParameters(_model, deltaTime);
        }

        _model->SaveParameters();
        _model->Update();

        // Calculate aspect-ratio projection matrix matching LAppMinimumLive2DManager
        CubismMatrix44 projection;
        if (_viewportWidth > 0 && _viewportHeight > 0) {
            float displayRatio = static_cast<float>(_viewportHeight) / static_cast<float>(_viewportWidth);
            float canvasWidth = _model->GetCanvasWidth();
            float canvasHeight = _model->GetCanvasHeight();
            float canvasRatio = (canvasWidth > 0.0f) ? (canvasHeight / canvasWidth) : 1.0f;

            if (canvasRatio < displayRatio) {
                _modelMatrix->SetWidth(2.0f);
                projection.Scale(1.0f, static_cast<float>(_viewportWidth) / static_cast<float>(_viewportHeight));
            } else {
                _modelMatrix->SetHeight(2.0f);
                projection.Scale(static_cast<float>(_viewportHeight) / static_cast<float>(_viewportWidth), 1.0f);
            }
        }

        projection.MultiplyByMatrix(_modelMatrix);

        while (glGetError() != GL_NO_ERROR); // Clear pre-existing GL errors

        GetRenderer<Rendering::CubismRenderer_OpenGLES2>()->SetMvpMatrix(&projection);
        GetRenderer<Rendering::CubismRenderer_OpenGLES2>()->DrawModel();
    }

    void EnsureFBO(int w, int h) {
        if (w <= 0 || h <= 0) return;
        if (_fbo != 0 && _fboW == w && _fboH == h) return;

        if (_fbo != 0) {
            glDeleteFramebuffers(1, &_fbo);
            glDeleteTextures(1, &_fboColorTex);
            glDeleteRenderbuffers(1, &_fboDepthRb);
            _fbo = 0;
        }

        _fboW = w;
        _fboH = h;

        glGenFramebuffers(1, &_fbo);
        glBindFramebuffer(GL_FRAMEBUFFER, _fbo);

        glGenTextures(1, &_fboColorTex);
        glBindTexture(GL_TEXTURE_2D, _fboColorTex);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, nullptr);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, _fboColorTex, 0);

        glGenRenderbuffers(1, &_fboDepthRb);
        glBindRenderbuffer(GL_RENDERBUFFER, _fboDepthRb);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT16, w, h);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, _fboDepthRb);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    bool RenderToPixels(int w, int h, uint32_t* outPixels) {
        if (w <= 0 || h <= 0 || !outPixels) return false;
        std::lock_guard<std::recursive_mutex> lock(_mutex);

        EnsureFBO(w, h);
        if (_fbo == 0) return false;

        OnSurfaceCreated();

        glBindFramebuffer(GL_FRAMEBUFFER, _fbo);
        glViewport(0, 0, w, h);
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        OnSurfaceChanged(w, h);
        OnDrawFrame();

        glReadPixels(0, 0, w, h, GL_RGBA, GL_UNSIGNED_BYTE, outPixels);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        // Convert GL_RGBA to Java TYPE_INT_ARGB_PRE and flip Y vertically in-place
        for (int y = 0; y < h / 2; ++y) {
            uint32_t* rowTop = outPixels + y * w;
            uint32_t* rowBottom = outPixels + (h - 1 - y) * w;
            for (int x = 0; x < w; ++x) {
                uint32_t top = rowTop[x];
                uint32_t bot = rowBottom[x];

                uint32_t topArgb = (top & 0xFF00FF00) | ((top & 0x00FF0000) >> 16) | ((top & 0x000000FF) << 16);
                uint32_t botArgb = (bot & 0xFF00FF00) | ((bot & 0x00FF0000) >> 16) | ((bot & 0x000000FF) << 16);

                rowTop[x] = botArgb;
                rowBottom[x] = topArgb;
            }
        }
        if (h % 2 != 0) {
            int midY = h / 2;
            uint32_t* rowMid = outPixels + midY * w;
            for (int x = 0; x < w; ++x) {
                uint32_t val = rowMid[x];
                rowMid[x] = (val & 0xFF00FF00) | ((val & 0x00FF0000) >> 16) | ((val & 0x000000FF) << 16);
            }
        }

        return true;
    }

private:
    std::string _modelDir;
    ICubismModelSetting* _modelSetting;
    csmByte* _mocBuffer;
    std::map<std::string, ACubismMotion*> _motions;
    std::map<std::string, ACubismMotion*> _expressions;
    std::vector<std::string> _motionGroupNames;
    std::vector<std::string> _expressionNames;
    std::vector<GLuint> _textureIds;
    int _viewportWidth;
    int _viewportHeight;
    int _frameCount;
    GLuint _fbo = 0;
    GLuint _fboColorTex = 0;
    GLuint _fboDepthRb = 0;
    int _fboW = 0;
    int _fboH = 0;
    std::recursive_mutex _mutex;
};

// C API implementation
Live2DHandle live2d_create() {
    InitCubismFrameworkStartUpIfNeeded();
    return new Live2DModelWrapper();
}

void live2d_destroy(Live2DHandle handle) {
    if (!handle) return;
    delete static_cast<Live2DModelWrapper*>(handle);
}

bool live2d_load_model(Live2DHandle handle, const char* model_dir, const char* model_json_name) {
    if (!handle || !model_dir || !model_json_name) return false;
    InitCubismFrameworkInitializeIfNeeded();

    std::string dirStr = model_dir;
    std::string jsonStr = model_json_name;

    const std::string fileScheme = "file://";
    if (dirStr.rfind(fileScheme, 0) == 0) {
        dirStr = dirStr.substr(fileScheme.length());
    }

    LOGI("Loading model directly: dir=%s, json=%s", dirStr.c_str(), jsonStr.c_str());
    return static_cast<Live2DModelWrapper*>(handle)->LoadAssets(dirStr, jsonStr);
}


static bool CreateDirectoryRecursive(const std::string& path) {
    if (path.empty()) return false;
    struct stat st;
    if (stat(path.c_str(), &st) == 0) {
        return S_ISDIR(st.st_mode);
    }
    size_t pos = 0;
    while ((pos = path.find_first_of("/\\", pos + 1)) != std::string::npos) {
        std::string sub = path.substr(0, pos);
        if (!sub.empty() && sub != "." && sub != "..") {
            if (stat(sub.c_str(), &st) != 0) {
                #if defined(_WIN32)
                _mkdir(sub.c_str());
                #else
                mkdir(sub.c_str(), 0755);
                #endif
            }
        }
    }
    #if defined(_WIN32)
    return _mkdir(path.c_str()) == 0 || errno == EEXIST;
    #else
    return mkdir(path.c_str(), 0755) == 0 || errno == EEXIST;
    #endif
}

static std::string GetZipUniqueId(const std::string& zipFilePath) {
    std::string cleanPath = zipFilePath;
    const std::string fileScheme = "file://";
    if (cleanPath.rfind(fileScheme, 0) == 0) {
        cleanPath = cleanPath.substr(fileScheme.length());
    }
    struct stat st;
    if (stat(cleanPath.c_str(), &st) == 0) {
        return std::to_string(st.st_size) + "_" + std::to_string(st.st_mtime);
    }
    return "";
}

static bool ExtractZipFile(const std::string& rawZipPath, const std::string& destDir) {
    std::string cleanZipPath = rawZipPath;
    const std::string fileScheme = "file://";
    if (cleanZipPath.rfind(fileScheme, 0) == 0) {
        cleanZipPath = cleanZipPath.substr(fileScheme.length());
    }

    mz_zip_archive zipArchive;
    memset(&zipArchive, 0, sizeof(zipArchive));

    if (!mz_zip_reader_init_file(&zipArchive, cleanZipPath.c_str(), 0)) {
        LOGE("Failed to open ZIP file for reading: %s", cleanZipPath.c_str());
        return false;
    }

    if (!CreateDirectoryRecursive(destDir)) {
        LOGE("Failed to create destination directory: %s", destDir.c_str());
        mz_zip_reader_end(&zipArchive);
        return false;
    }

    mz_uint numFiles = mz_zip_reader_get_num_files(&zipArchive);
    LOGI("Extracting %u files from ZIP: %s to %s", numFiles, cleanZipPath.c_str(), destDir.c_str());

    for (mz_uint i = 0; i < numFiles; i++) {
        mz_zip_archive_file_stat fileStat;
        if (!mz_zip_reader_file_stat(&zipArchive, i, &fileStat)) {
            continue;
        }

        std::string filename = fileStat.m_filename;
        if (filename.empty()) continue;

        std::string outPath = destDir + "/" + filename;

        if (mz_zip_reader_is_file_a_directory(&zipArchive, i)) {
            CreateDirectoryRecursive(outPath);
        } else {
            size_t lastSlash = outPath.find_last_of("/\\");
            if (lastSlash != std::string::npos) {
                CreateDirectoryRecursive(outPath.substr(0, lastSlash));
            }
            if (!mz_zip_reader_extract_to_file(&zipArchive, i, outPath.c_str(), 0)) {
                LOGE("Failed to extract ZIP entry %s to %s", filename.c_str(), outPath.c_str());
            }
        }
    }

    mz_zip_reader_end(&zipArchive);
    return true;
}

bool live2d_load_model_from_zip(Live2DHandle handle, const char* zip_file_path, const char* work_dir, const char* model_name) {
    if (!handle || !zip_file_path || !work_dir || !model_name) return false;
    InitCubismFrameworkInitializeIfNeeded();

    std::string zipPathStr = zip_file_path;
    std::string workDirStr = work_dir;
    std::string modelNameStr = model_name;

    const std::string fileScheme = "file://";
    if (workDirStr.rfind(fileScheme, 0) == 0) {
        workDirStr = workDirStr.substr(fileScheme.length());
    }
    while (!workDirStr.empty() && workDirStr.back() == '/') {
        workDirStr.pop_back();
    }

    std::string targetDir = workDirStr + "/" + modelNameStr;
    std::string markerFile = targetDir + "/.zip_id";
    std::string currentZipId = GetZipUniqueId(zipPathStr);

    bool needExtract = true;
    if (!currentZipId.empty()) {
        std::ifstream markerIn(markerFile);
        if (markerIn.is_open()) {
            std::string savedZipId;
            markerIn >> savedZipId;
            if (savedZipId == currentZipId) {
                needExtract = false;
                LOGI("Zip file unchanged (ID: %s). Reusing extracted model directory: %s", currentZipId.c_str(), targetDir.c_str());
            }
        }
    }

    if (needExtract) {
        if (!ExtractZipFile(zipPathStr, targetDir)) {
            LOGE("Failed to extract model ZIP: %s", zipPathStr.c_str());
            return false;
        }
        if (!currentZipId.empty()) {
            std::ofstream markerOut(markerFile);
            if (markerOut.is_open()) {
                markerOut << currentZipId;
            }
        }
    }

    std::string jsonFilename = "";
    std::string expectedJson = modelNameStr + ".model3.json";
    std::string checkExpectedPath = targetDir + "/" + expectedJson;
    struct stat st;
    if (stat(checkExpectedPath.c_str(), &st) == 0) {
        jsonFilename = expectedJson;
    } else {
        DIR* dir = opendir(targetDir.c_str());
        if (dir) {
            struct dirent* entry;
            while ((entry = readdir(dir)) != nullptr) {
                std::string fname = entry->d_name;
                if (fname.find(".model3.json") != std::string::npos) {
                    jsonFilename = fname;
                    break;
                }
            }
            closedir(dir);
        }
    }

    if (jsonFilename.empty()) {
        LOGE("No .model3.json found in extracted directory: %s", targetDir.c_str());
        return false;
    }

    LOGI("Loading extracted model: dir=%s, json=%s", targetDir.c_str(), jsonFilename.c_str());
    return live2d_load_model(handle, targetDir.c_str(), jsonFilename.c_str());
}

void live2d_set_motion(Live2DHandle handle, const char* group, int index) {
    if (!handle || !group) return;
    static_cast<Live2DModelWrapper*>(handle)->StartMotion(group, index);
}

void live2d_set_expression(Live2DHandle handle, const char* expression_id) {
    if (!handle || !expression_id) return;
    static_cast<Live2DModelWrapper*>(handle)->SetExpression(expression_id);
}

int live2d_get_motion_count(Live2DHandle handle) {
    if (!handle) return 0;
    return static_cast<int>(static_cast<Live2DModelWrapper*>(handle)->GetMotionGroups().size());
}

const char* live2d_get_motion_group_at(Live2DHandle handle, int index) {
    if (!handle) return "";
    const auto& list = static_cast<Live2DModelWrapper*>(handle)->GetMotionGroups();
    if (index >= 0 && index < static_cast<int>(list.size())) {
        return list[index].c_str();
    }
    return "";
}

int live2d_get_expression_count(Live2DHandle handle) {
    if (!handle) return 0;
    return static_cast<int>(static_cast<Live2DModelWrapper*>(handle)->GetExpressions().size());
}

const char* live2d_get_expression_id_at(Live2DHandle handle, int index) {
    if (!handle) return "";
    const auto& list = static_cast<Live2DModelWrapper*>(handle)->GetExpressions();
    if (index >= 0 && index < static_cast<int>(list.size())) {
        return list[index].c_str();
    }
    return "";
}

#if defined(_WIN32) || defined(WIN32)
#include <windows.h>
#include <GL/gl.h>
static HGLRC g_wglContext = NULL;
static HDC g_wglDC = NULL;
static HWND g_dummyHwnd = NULL;

static bool InitOffscreenDesktopGLContext() {
    if (g_wglContext != NULL) {
        wglMakeCurrent(g_wglDC, g_wglContext);
        return true;
    }
    WNDCLASS wc = {0};
    wc.lpfnWndProc = DefWindowProc;
    wc.hInstance = GetModuleHandle(NULL);
    wc.lpszClassName = "Live2DDummyWindow";
    RegisterClass(&wc);

    g_dummyHwnd = CreateWindow("Live2DDummyWindow", "Live2D", WS_OVERLAPPEDWINDOW, 0, 0, 1, 1, NULL, NULL, wc.hInstance, NULL);
    g_wglDC = GetDC(g_dummyHwnd);

    PIXELFORMATDESCRIPTOR pfd = { sizeof(PIXELFORMATDESCRIPTOR), 1, PFD_DRAW_TO_WINDOW | PFD_SUPPORT_OPENGL | PFD_DOUBLEBUFFER, PFD_TYPE_RGBA, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 0, 0, PFD_MAIN_PLANE, 0, 0, 0, 0 };
    int pf = ChoosePixelFormat(g_wglDC, &pfd);
    SetPixelFormat(g_wglDC, pf, &pfd);

    g_wglContext = wglCreateContext(g_wglDC);
    wglMakeCurrent(g_wglDC, g_wglContext);
    return true;
}
#elif defined(__linux__) && !defined(__ANDROID__)
#include <X11/Xlib.h>
#include <GL/glx.h>
static GLXContext g_glxContext = NULL;
static Display* g_xDisplay = NULL;
static Window g_dummyWin = 0;

static bool InitOffscreenDesktopGLContext() {
    if (g_glxContext != NULL) {
        glXMakeCurrent(g_xDisplay, g_dummyWin, g_glxContext);
        return true;
    }
    g_xDisplay = XOpenDisplay(NULL);
    if (!g_xDisplay) return false;
    int screen = DefaultScreen(g_xDisplay);
    int attribs[] = { GLX_RGBA, GLX_DEPTH_SIZE, 16, None };
    XVisualInfo* vi = glXChooseVisual(g_xDisplay, screen, attribs);
    if (!vi) return false;

    g_dummyWin = XCreateSimpleWindow(g_xDisplay, RootWindow(g_xDisplay, screen), 0, 0, 1, 1, 0, 0, 0);
    g_glxContext = glXCreateContext(g_xDisplay, vi, NULL, GL_TRUE);
    glXMakeCurrent(g_xDisplay, g_dummyWin, g_glxContext);
    return true;
}
#elif defined(MACOS_DESKTOP_BUILD) || defined(CSM_TARGET_MAC_GL)
static CGLContextObj g_offscreenCGLContext = nullptr;

static bool InitOffscreenDesktopGLContext() {
    if (g_offscreenCGLContext != nullptr) {
        CGLSetCurrentContext(g_offscreenCGLContext);
        return true;
    }

    CGLPixelFormatAttribute attribs[] = {
        kCGLPFAAccelerated,
        kCGLPFAColorSize, (CGLPixelFormatAttribute)24,
        kCGLPFAAlphaSize, (CGLPixelFormatAttribute)8,
        kCGLPFADepthSize, (CGLPixelFormatAttribute)16,
        (CGLPixelFormatAttribute)0
    };

    CGLPixelFormatObj pix = nullptr;
    GLint npix = 0;
    CGLError err = CGLChoosePixelFormat(attribs, &pix, &npix);
    if (err != kCGLNoError || !pix) {
        LOGE("CGLChoosePixelFormat failed: %d", err);
        return false;
    }

    err = CGLCreateContext(pix, nullptr, &g_offscreenCGLContext);
    CGLDestroyPixelFormat(pix);

    if (err != kCGLNoError || !g_offscreenCGLContext) {
        LOGE("CGLCreateContext failed: %d", err);
        return false;
    }

    CGLSetCurrentContext(g_offscreenCGLContext);
    LOGI("Created offscreen CGL context successfully for Desktop Live2D!");
    return true;
}
#else
static bool InitOffscreenDesktopGLContext() { return true; }
#endif

void live2d_on_surface_created(Live2DHandle handle) {
    if (!InitOffscreenDesktopGLContext()) return;

    InitCubismFrameworkInitializeIfNeeded();
    Rendering::CubismShader_OpenGLES2::DeleteInstance();
    Rendering::CubismShader_OpenGLES2::GetInstance();
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    glEnable(GL_BLEND);
    glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

    if (handle) {
        static_cast<Live2DModelWrapper*>(handle)->OnSurfaceCreated();
    }
}

void live2d_on_surface_changed(Live2DHandle handle, int width, int height) {
    if (!InitOffscreenDesktopGLContext()) return;

    if (!handle) return;
    static_cast<Live2DModelWrapper*>(handle)->OnSurfaceChanged(width, height);
}

void live2d_on_draw_frame(Live2DHandle handle) {
    if (!InitOffscreenDesktopGLContext()) return;

    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    if (!handle) return;
    static_cast<Live2DModelWrapper*>(handle)->OnDrawFrame();
}

bool live2d_render_pixels(Live2DHandle handle, int width, int height, unsigned int* out_pixels) {
    if (!handle || !out_pixels || width <= 0 || height <= 0) return false;
    if (!InitOffscreenDesktopGLContext()) return false;

    return static_cast<Live2DModelWrapper*>(handle)->RenderToPixels(width, height, out_pixels);
}

void live2d_on_touch(Live2DHandle handle, float x, float y, int phase) {
    if (!handle) return;
    static_cast<Live2DModelWrapper*>(handle)->StartMotion("Tap", 0);
}

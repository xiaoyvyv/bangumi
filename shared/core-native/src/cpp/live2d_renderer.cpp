#include "live2d_renderer.h"

#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>
#include <map>
#include <memory>
#include <algorithm>
#include <cstdlib>
#include <mutex>

#if defined(ANDROID) || defined(__ANDROID__)
#include <GLES2/gl2.h>
#include <android/log.h>
#include <android/asset_manager.h>
#define LOG_TAG "Live2DNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
static AAssetManager* g_assetManager = nullptr;
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

#if defined(ANDROID) || defined(__ANDROID__)
void live2d_set_asset_manager(AAssetManager* asset_manager) {
    g_assetManager = asset_manager;
    LOGI("AAssetManager set successfully");
}
#endif

#include "live2d_shaders.h"


// Universal file loader with candidate path fallback
static bool LoadFileBuffer(const std::string& rawPath, std::vector<char>& outBuffer) {
    std::string cleanPath = rawPath;
    const std::string assetPrefix = "file:///android_asset/";
    const std::string fileScheme = "file://";

    if (cleanPath.rfind(assetPrefix, 0) == 0) {
        cleanPath = cleanPath.substr(assetPrefix.length());
    } else if (cleanPath.rfind(fileScheme, 0) == 0) {
        cleanPath = cleanPath.substr(fileScheme.length());
    } else if (cleanPath.rfind("assets/", 0) == 0) {
        cleanPath = cleanPath.substr(7);
    }

    while (!cleanPath.empty() && (cleanPath.front() == '/' || (cleanPath.length() >= 2 && cleanPath[0] == '.' && cleanPath[1] == '/'))) {
        if (cleanPath.length() >= 2 && cleanPath[0] == '.' && cleanPath[1] == '/') {
            cleanPath = cleanPath.substr(2);
        } else if (cleanPath.front() == '/') {
            cleanPath = cleanPath.substr(1);
        }
    }

    std::string baseFilename = cleanPath;
    size_t lastSlashPos = cleanPath.find_last_of('/');
    if (lastSlashPos != std::string::npos) {
        baseFilename = cleanPath.substr(lastSlashPos + 1);
    }

    // Check embedded shaders first for 100% fail-safe GLSL compilation
    auto embedIt = g_embeddedShaders.find(baseFilename);
    if (embedIt != g_embeddedShaders.end()) {
        outBuffer.assign(embedIt->second.begin(), embedIt->second.end());
        return true;
    }

    std::vector<std::string> candidates;
    if (!cleanPath.empty()) {
        candidates.push_back(cleanPath);
        candidates.push_back(baseFilename);
        candidates.push_back("FrameworkShaders/" + baseFilename);

        // Add common Compose Multiplatform asset search fallbacks
        if (cleanPath.find("composeResources/") != 0) {
            candidates.push_back("composeResources/com.xiaoyv.bangumi.core_resource.resources/" + cleanPath);
            candidates.push_back("composeResources/com.xiaoyv.bangumi.core_resource.resources/files/live2d/" + cleanPath);
            candidates.push_back("files/live2d/" + cleanPath);
            candidates.push_back("live2d/" + cleanPath);
        }
    }

#if defined(ANDROID) || defined(__ANDROID__)
    if (g_assetManager != nullptr) {
        for (const auto& path : candidates) {
            AAsset* asset = AAssetManager_open(g_assetManager, path.c_str(), AASSET_MODE_BUFFER);
            if (asset) {
                size_t size = AAsset_getLength(asset);
                outBuffer.resize(size);
                AAsset_read(asset, outBuffer.data(), size);
                AAsset_close(asset);
                LOGI("Successfully loaded asset via AAssetManager: %s (size: %zu)", path.c_str(), size);
                return true;
            }
        }
    }
#endif

    // Fallback to std::ifstream for disk paths (/data/user/0/..., etc.)
    for (const auto& path : candidates) {
        std::ifstream file(path, std::ios::binary | std::ios::ate);
        if (file.is_open()) {
            std::streamsize size = file.tellg();
            file.seekg(0, std::ios::beg);
            outBuffer.resize(size);
            if (file.read(outBuffer.data(), size)) {
                LOGI("Successfully loaded disk file: %s (size: %zu)", path.c_str(), static_cast<size_t>(size));
                return true;
            }
        }
    }

    if (rawPath != cleanPath) {
        std::ifstream file(rawPath, std::ios::binary | std::ios::ate);
        if (file.is_open()) {
            std::streamsize size = file.tellg();
            file.seekg(0, std::ios::beg);
            outBuffer.resize(size);
            if (file.read(outBuffer.data(), size)) {
                LOGI("Successfully loaded raw disk file: %s (size: %zu)", rawPath.c_str(), static_cast<size_t>(size));
                return true;
            }
        }
    }

    LOGE("Failed to open or read file: %s (cleanPath: %s)", rawPath.c_str(), cleanPath.c_str());
    return false;
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

    g_cubismOption.LoggingLevel = CubismFramework::Option::LogLevel_Verbose;
    g_cubismOption.LogFunction = [](const char* message) {
        LOGI("[CubismSDK] %s", message);
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
                    LOGI("Successfully bound OpenGL ES texture Index %d -> Texture ID %d (%dx%d)", i, texId, w, h);
                } else {
                    LOGE("Failed to decode PNG texture via stb_image: %s", texPath.c_str());
                }
            } else {
                LOGE("Failed to load texture file buffer: %s", texPath.c_str());
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

    void StartMotion(const std::string& group, int index, int priority = 2) {
        std::lock_guard<std::recursive_mutex> lock(_mutex);
        std::string key = group + "_" + std::to_string(index);
        auto it = _motions.find(key);
        if (it != _motions.end()) {
            _motionManager->StartMotionPriority(it->second, false, priority);
        }
    }

    void SetExpression(const std::string& expressionId) {
        std::lock_guard<std::recursive_mutex> lock(_mutex);
        auto it = _expressions.find(expressionId);
        if (it != _expressions.end()) {
            _expressionManager->StartMotion(it->second, false);
        }
    }

    const std::vector<std::string>& GetMotionGroups() const { return _motionGroupNames; }
    const std::vector<std::string>& GetExpressions() const { return _expressionNames; }

    void OnSurfaceChanged(int w, int h) {
        _viewportWidth = w;
        _viewportHeight = h;
        glViewport(0, 0, w, h);
        SetRenderTargetSize(w, h);
        LOGI("OnSurfaceChanged Viewport: %dx%d", w, h);
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
        } else {
            _motionManager->UpdateMotion(_model, deltaTime);
        }
        _expressionManager->UpdateMotion(_model, deltaTime);
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

        GetRenderer<Rendering::CubismRenderer_OpenGLES2>()->SetMvpMatrix(&projection);
        GetRenderer<Rendering::CubismRenderer_OpenGLES2>()->DrawModel();

        if (_frameCount % 60 == 0) {
            GLenum err = glGetError();
            LOGI("OnDrawFrame #%d completed. Viewport: %dx%d, GLErr: 0x%x", _frameCount, _viewportWidth, _viewportHeight, err);
        }
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
    std::recursive_mutex _mutex;
};

// C API implementation
Live2DHandle live2d_create() {
    InitCubismFrameworkStartUpIfNeeded();
    return new Live2DModelWrapper();
}

void live2d_destroy(Live2DHandle handle) {
    if (handle) {
        delete static_cast<Live2DModelWrapper*>(handle);
    }
}

bool live2d_load_model(Live2DHandle handle, const char* model_dir, const char* model_json_name) {
    if (!handle || !model_dir || !model_json_name) return false;
    InitCubismFrameworkInitializeIfNeeded();
    return static_cast<Live2DModelWrapper*>(handle)->LoadAssets(model_dir, model_json_name);
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

void live2d_on_surface_created(Live2DHandle handle) {
    LOGI("live2d_on_surface_created called");
    InitCubismFrameworkInitializeIfNeeded();
    Rendering::CubismShader_OpenGLES2::GetInstance();
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    glEnable(GL_BLEND);
    glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
}

void live2d_on_surface_changed(Live2DHandle handle, int width, int height) {
    LOGI("live2d_on_surface_changed called (%dx%d)", width, height);
    if (!handle) return;
    static_cast<Live2DModelWrapper*>(handle)->OnSurfaceChanged(width, height);
}

void live2d_on_draw_frame(Live2DHandle handle) {
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    if (!handle) return;
    static_cast<Live2DModelWrapper*>(handle)->OnDrawFrame();
}

void live2d_on_touch(Live2DHandle handle, float x, float y, int phase) {
    if (!handle) return;
    static_cast<Live2DModelWrapper*>(handle)->StartMotion("Tap", 0);
}

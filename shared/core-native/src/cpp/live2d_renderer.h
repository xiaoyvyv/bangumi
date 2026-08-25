#ifndef LIVE2D_RENDERER_H
#define LIVE2D_RENDERER_H

#include <stdbool.h>

#if defined(ANDROID) || defined(__ANDROID__)
#include <android/asset_manager.h>
#endif

#ifdef __cplusplus
extern "C" {
#endif

typedef void* Live2DHandle;



Live2DHandle live2d_create(void);
void live2d_destroy(Live2DHandle handle);
bool live2d_load_model(Live2DHandle handle, const char* model_dir, const char* model_json_name);
bool live2d_load_model_from_zip(Live2DHandle handle, const char* zip_file_path, const char* work_dir, const char* model_name);
void live2d_set_motion(Live2DHandle handle, const char* group, int index);
void live2d_set_expression(Live2DHandle handle, const char* expression_id);
int live2d_get_motion_count(Live2DHandle handle);
const char* live2d_get_motion_group_at(Live2DHandle handle, int index);
int live2d_get_expression_count(Live2DHandle handle);
const char* live2d_get_expression_id_at(Live2DHandle handle, int index);
void live2d_on_surface_created(Live2DHandle handle);
void live2d_on_surface_changed(Live2DHandle handle, int width, int height);
void live2d_on_draw_frame(Live2DHandle handle);
bool live2d_render_pixels(Live2DHandle handle, int width, int height, unsigned int* out_pixels);
void live2d_on_touch(Live2DHandle handle, float x, float y, int phase);

#ifdef __cplusplus
}
#endif

#endif // LIVE2D_RENDERER_H

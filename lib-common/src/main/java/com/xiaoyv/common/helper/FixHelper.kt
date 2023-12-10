package com.xiaoyv.common.helper

import android.widget.ImageView
import androidx.core.view.doOnPreDraw
import androidx.core.view.updateLayoutParams
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.BarUtils
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.widget.kts.getAttrDimensionPixelSize

/**
 * Class: [FixHelper]
 *
 * @author why
 * @since 11/24/23
 */
object FixHelper {

    fun fixCool(ivBanner: ImageView, toolbarLayout: CollapsingToolbarLayout, contentHeight: Int) {
        ivBanner.updateLayoutParams {
            height =
                contentHeight + ivBanner.context.getAttrDimensionPixelSize(GoogleAttr.actionBarSize)
        }

        toolbarLayout.doOnPreDraw {
            toolbarLayout.updateLayoutParams {
                height = toolbarLayout.height
            }

            ivBanner.updateLayoutParams {
                height = ivBanner.height + BarUtils.getStatusBarHeight()
            }
        }
    }

    /**
     * 傻逼小米魔改 Android SDK 导致 fling 有闪退 BUG
     */
    fun fuckMiuiOverScroller(instance: RecyclerView) {
        runCatching {
            val v = RecyclerView::class.java.getDeclaredField("mViewFlinger").apply {
                isAccessible = true
            }.get(instance)!!

            val mOverScroller = v.javaClass.getDeclaredField("mOverScroller").apply {
                isAccessible = true
            }.get(v)!!

            val mFlingAnimationStub =
                mOverScroller.javaClass.getDeclaredField("mFlingAnimationStub").apply {
                    isAccessible = true
                }.get(mOverScroller)!!

            val mIsOptimizeEnable =
                mFlingAnimationStub.javaClass.getDeclaredField("mIsOptimizeEnable").apply {
                    isAccessible = true
                }

            mIsOptimizeEnable.set(mFlingAnimationStub, false)
        }
    }

    /**
     * 傻逼小米魔改 Android SDK 导致 fling 有闪退 BUG
     */
    fun fuckMiuiOverScroller(instance: NestedScrollView) {
        runCatching {
            val mOverScroller =
                NestedScrollView::class.java.getDeclaredField("mScroller").apply {
                    isAccessible = true
                }.get(instance)!!

            val mFlingAnimationStub =
                mOverScroller.javaClass.getDeclaredField("mFlingAnimationStub").apply {
                    isAccessible = true
                }.get(mOverScroller)!!

            val mIsOptimizeEnable =
                mFlingAnimationStub.javaClass.getDeclaredField("mIsOptimizeEnable").apply {
                    isAccessible = true
                }

            mIsOptimizeEnable.set(mFlingAnimationStub, false)
        }
    }
}
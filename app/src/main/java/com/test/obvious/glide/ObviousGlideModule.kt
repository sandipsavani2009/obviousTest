package com.test.obvious.glide

import android.content.Context
import android.util.Log
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule


/**
 * This is require to use glide for fetching & caching of images
 */

@GlideModule
class ObviousGlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setLogLevel(Log.VERBOSE).build(context)
    }
}
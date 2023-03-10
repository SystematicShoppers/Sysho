package com.systematicshoppers.sysho

/** This class is necessary to use Glide API to convert Firebase Storage Reference
 * to a usable URL path to get images
 * **/
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.google.firebase.storage.StorageReference
import java.io.InputStream
import com.firebase.ui.storage.images.FirebaseImageLoader

@GlideModule
class AppGlide: AppGlideModule(){

    override fun registerComponents(
        context: android.content.Context,
        glide: Glide,
        registry: Registry
    ) {
        super.registerComponents(context, glide, registry)
        registry.append(
            StorageReference::class.java, InputStream::class.java,
            FirebaseImageLoader.Factory()
        )

    }
}
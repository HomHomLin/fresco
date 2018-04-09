/*
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.drawee.backends.pipeline;

import javax.annotation.Nullable;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.drawable.Rounded;
import com.facebook.drawee.drawable.RoundedBitmapDrawable;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.generic.WrappingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.animated.base.AnimatedDrawable;
import com.facebook.imagepipeline.animated.base.AnimatedDrawableSupport;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImagePipelineFactory;

/**
 * Fresco entry point.
 *
 * <p/> You must initialize this class before use. The simplest way is to just do
 * {#code Fresco.initialize(Context)}.
 */
public class Fresco {

  private static final Class<?> TAG = Fresco.class;

  private static PipelineDraweeControllerBuilderSupplier sDraweeControllerBuilderSupplier;
  private static volatile boolean sIsInitialized = false;

  private Fresco() {}

  /** Initializes Fresco with the default config. */
  public static void initialize(Context context) {
    initialize(context, null, null);
  }

  /** Initializes Fresco with the default Drawee config. */
  public static void initialize(
      Context context,
      @Nullable ImagePipelineConfig imagePipelineConfig) {
    initialize(context, imagePipelineConfig, null);
  }

  /** Initializes Fresco with the specified config. */
  public static void initialize(
      Context context,
      @Nullable ImagePipelineConfig imagePipelineConfig,
      @Nullable DraweeConfig draweeConfig) {
    if (sIsInitialized) {
      FLog.w(
          TAG,
          "Fresco has already been initialized! `Fresco.initialize(...)` should only be called " +
            "1 single time to avoid memory leaks!");
    } else {
      sIsInitialized = true;
    }
    // we should always use the application context to avoid memory leaks
    context = context.getApplicationContext();
    if (imagePipelineConfig == null) {
      ImagePipelineFactory.initialize(context);
    } else {
      ImagePipelineFactory.initialize(imagePipelineConfig);
    }
    initializeDrawee(context, draweeConfig);
    initRoundInterceptor();
  }


  static void applyRoundingParams(Rounded rounded, RoundingParams roundingParams) {
    rounded.setCircle(roundingParams.getRoundAsCircle());
    rounded.setRadii(roundingParams.getCornersRadii());
    rounded.setBorder(roundingParams.getBorderColor(), roundingParams.getBorderWidth());
    rounded.setPadding(roundingParams.getPadding());
  }

  private static void initRoundInterceptor(){
    /*WrappingUtils.setRoundDrawableInterceptor(new WrappingUtils.RoundDrawableInterceptor() {
      @Override
      public RoundedBitmapDrawable intercept(Resources resources, Drawable drawable,RoundingParams roundingParams) {
        try {
          if (drawable instanceof AnimatedDrawable) {
            final AnimatedDrawable bitmapDrawable = (AnimatedDrawable) drawable;
            RoundedBitmapDrawable roundedBitmapDrawable =
                    new RoundedBitmapDrawable(
                            resources,
                            bitmapDrawable.getAnimatedDrawableBackend().getPreviewBitmap().get(),
                            bitmapDrawable.getPaint());
            applyRoundingParams(roundedBitmapDrawable, roundingParams);
            return roundedBitmapDrawable;
          }
          if (drawable instanceof AnimatedDrawableSupport) {
            final AnimatedDrawableSupport bitmapDrawable = (AnimatedDrawableSupport) drawable;
            RoundedBitmapDrawable roundedBitmapDrawable =
                    new RoundedBitmapDrawable(
                            resources,
                            bitmapDrawable.getAnimatedDrawableBackend().getPreviewBitmap().get(),
                            bitmapDrawable.getPaint());
            applyRoundingParams(roundedBitmapDrawable, roundingParams);
            return roundedBitmapDrawable;
          }
        }catch (Exception ex){
          ex.printStackTrace();
        }
        return null;
      }
    });*/
  }
  /** Initializes Drawee with the specified config. */
  private static void initializeDrawee(
      Context context,
      @Nullable DraweeConfig draweeConfig) {
    sDraweeControllerBuilderSupplier =
        new PipelineDraweeControllerBuilderSupplier(context, draweeConfig);
    SimpleDraweeView.initialize(sDraweeControllerBuilderSupplier);
  }

  /** Gets the supplier of Fresco Drawee controller builders. */
  public static PipelineDraweeControllerBuilderSupplier getDraweeControllerBuilderSupplier() {
    return sDraweeControllerBuilderSupplier;
  }

  /** Returns a new instance of Fresco Drawee controller builder. */
  public static PipelineDraweeControllerBuilder newDraweeControllerBuilder() {
    return sDraweeControllerBuilderSupplier.get();
  }

  public static ImagePipelineFactory getImagePipelineFactory() {
    return ImagePipelineFactory.getInstance();
  }

  /** Gets the image pipeline instance. */
  public static ImagePipeline getImagePipeline() {
    return getImagePipelineFactory().getImagePipeline();
  }

  /** Shuts Fresco down. */
  public static void shutDown() {
    sDraweeControllerBuilderSupplier = null;
    SimpleDraweeView.shutDown();
    ImagePipelineFactory.shutDown();
  }

  /** Returns true if Fresco has been initialized. */
  public static boolean hasBeenInitialized() {
    return sIsInitialized;
  }



}

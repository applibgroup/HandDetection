package com.example.handdetectionapp.sample.slice;

import com.example.handdetection.detector.Detector;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.DirectionalLayout;
import ohos.agp.components.Image;
import ohos.agp.components.Text;
import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.render.Texture;
import ohos.agp.utils.Color;
import ohos.agp.utils.LayoutAlignment;
import ohos.app.Environment;
import ohos.global.resource.RawFileEntry;
import ohos.global.resource.Resource;
import ohos.media.image.ImagePacker;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * HandDetectionSliceUtils.
 */

public class HandDetectionSliceUtils {

    private HandDetectionSliceUtils() {
        /* Do Nothing */
    }

    public static File getFileFromRawFile(String filename, RawFileEntry rawFileEntry, File cacheDir)
            throws IOException {
        byte[] buf = null;
        File file;
        file = new File(cacheDir, filename);
        try (FileOutputStream output = new FileOutputStream(file)) {
            Resource resource = rawFileEntry.openRawFile();
            buf = new byte[(int) rawFileEntry.openRawFileDescriptor().getFileSize()];
            int bytesRead = resource.read(buf);
            if (bytesRead != buf.length) {
                throw new IOException("Asset Read failed!!!");
            }
            output.write(buf, 0, bytesRead);
            return file;
        }
    }
}

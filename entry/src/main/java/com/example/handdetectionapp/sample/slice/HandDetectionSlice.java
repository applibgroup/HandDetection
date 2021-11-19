package com.example.handdetectionapp.sample.slice;

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
import com.example.handdetection.detector.Detector;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * HandDetectionSlice.
 */

public class HandDetectionSlice extends AbilitySlice {

    private Text resourcesText;
    private static final String MODEL_INPUT_IMAGE_PATH = "entry/resources/base/media/sign.jpg";
    private static final String MODEL_INPUT_IMAGE_NAME = "sign.jpg";
    private static final String MODEL_INPUT_IPATH = "entry/resources/rawfile/sign_img.txt";
    private static final String MODEL_INPUT_INAME = "sign_img.txt";
    private static final String MODEL_OUTPUT_IMAGE_NAME = "out.jpg";
    private static final int IMGW = 1600;
    private static final int IMGH = 1168;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        initComponents();
    }

    private void initComponents() {
        resourcesText = new Text(getContext());
        try {
            handdetectionrun();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handdetectionrun() throws IOException {
        int[][] handcoordinates;
        Detector mydetector = new Detector(MODEL_INPUT_IPATH, MODEL_INPUT_INAME,
                IMGH, IMGW, getResourceManager(), getCacheDir());
        handcoordinates = mydetector.get_output();
        resourcesText.setText(" ");
        resourcesText.setText(
                "Predicted co-ordinates are : "
                        + handcoordinates[0][0] + ", " + handcoordinates[0][1]
                        + ", " + handcoordinates[1][0] + ", " + handcoordinates[1][1]
                        + System.lineSeparator() + " Finish !");
        RawFileEntry rawfileimg = getResourceManager().getRawFileEntry(MODEL_INPUT_IMAGE_PATH);
        File fileImg = null;
        fileImg = getFileFromRawFile(MODEL_INPUT_IMAGE_NAME, rawfileimg, getCacheDir());

        ImageSource imagesource = ImageSource.create(fileImg, null);
        PixelMap pixelmap = imagesource.createPixelmap(null);

        Texture texture = new Texture(pixelmap);
        Canvas mcanvas = new Canvas(texture);
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);

        int x1 = handcoordinates[0][0];
        int y1 = handcoordinates[0][1];
        int x2 = handcoordinates[1][0];
        int y2 = handcoordinates[1][1];

        mcanvas.drawLine(x1, y1, x2, y1, paint);
        mcanvas.drawLine(x1, y1, x1, y2, paint);
        mcanvas.drawLine(x1, y2, x2, y2, paint);
        mcanvas.drawLine(x2, y1, x2, y2, paint);

        Image img2 = new Image(this);
        img2.setPixelMap(texture.getPixelMap());
        img2.setHeight(800);
        img2.setWidth(1500);
        DirectionalLayout layout = new DirectionalLayout(getContext());
        layout.addComponent(img2);
        resourcesText.setTextSize(60);
        resourcesText.setId(20);
        DirectionalLayout.LayoutConfig layoutConfig = new DirectionalLayout.LayoutConfig(
                ComponentContainer.LayoutConfig.MATCH_PARENT,
                ComponentContainer.LayoutConfig.MATCH_CONTENT);
        layoutConfig.alignment = LayoutAlignment.HORIZONTAL_CENTER;
        resourcesText.setLayoutConfig(layoutConfig);

        layout.addComponent(resourcesText);
        super.setUIContent(layout);

        FileOutputStream outputstream = null;
        outputstream = new FileOutputStream(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                + "/" + MODEL_OUTPUT_IMAGE_NAME);
        ImagePacker imagepacker = ImagePacker.create();
        ImagePacker.PackingOptions packingoptions = new ImagePacker.PackingOptions();
        packingoptions.format = "image/jpeg";
        packingoptions.quality = 90;
        imagepacker.initializePacking(outputstream, packingoptions);
        imagepacker.addImage(texture.getPixelMap());
        imagepacker.finalizePacking();
    }

    private static File getFileFromRawFile(String filename, RawFileEntry rawFileEntry, File cacheDir)
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

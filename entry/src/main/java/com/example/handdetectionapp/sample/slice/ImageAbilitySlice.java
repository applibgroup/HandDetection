package com.example.handdetectionapp.sample.slice;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.colors.RgbColor;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.DirectionalLayout;
import ohos.agp.components.Image;
import ohos.agp.components.Text;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.utils.Color;
import ohos.agp.utils.LayoutAlignment;
import com.example.handdetectionapp.sample.ResourceTable;

/**
 * imageAbilitySlice.
 */
public class ImageAbilitySlice extends AbilitySlice {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);

        Image img = new Image(this);
        img.setPixelMap(ResourceTable.Media_sign);
        img.setHeight(800);
        img.setWidth(1500);
        DirectionalLayout layout = new DirectionalLayout(getContext());
        layout.addComponent(img);
        img.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                String imagePath = "entry/resources/base/media/sign.jpg";
                String imageName = "sign.jpg";
                String imageIPath = "entry/resources/rawfile/sign_img.txt";
                String imageIName = "sign_img.txt";
                String outImageName = "out.jpg";
                int imgWidth = 1600;
                int imgHeight = 1168;
                present(new HandDetectionSlice(imagePath, imageName, imageIPath, imageIName, outImageName,
                imgWidth, imgHeight), new Intent());
            }
        });

        Image img1 = new Image(this);
        img1.setPixelMap(ResourceTable.Media_sample);
        img1.setHeight(1000);
        img1.setWidth(1500);
        layout.addComponent(img1);
        img1.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                String imagePath1 = "entry/resources/base/media/sample.jpg";
                String imageName1 = "sample.jpg";
                String imageIPath1 = "entry/resources/rawfile/sample_img.txt";
                String imageIName1 = "sample_img.txt";
                String outImageName1 = "out_sample.jpg";
                int imgWidth1 = 640;
                int imgHeight1 = 480;
                present(new HandDetectionSlice(imagePath1, imageName1, imageIPath1, imageIName1, outImageName1,
                        imgWidth1, imgHeight1), new Intent());
            }
        });

        Text text = new Text(getContext());
        text.setText("Click on the image you want to detect hand");
        text.setTextSize(80);
        text.setId(20);
        text.setHeight(1500);
        text.setTextColor(Color.WHITE);
        ShapeElement background = new ShapeElement();
        background.setRgbColor(new RgbColor(0, 0, 255));
        text.setBackground(background);

        DirectionalLayout.LayoutConfig layoutConfig = new DirectionalLayout.LayoutConfig(
                ComponentContainer.LayoutConfig.MATCH_CONTENT,
                ComponentContainer.LayoutConfig.MATCH_CONTENT);
        layoutConfig.alignment = LayoutAlignment.HORIZONTAL_CENTER;
        text.setLayoutConfig(layoutConfig);

        layout.addComponent(text);
        super.setUIContent(layout);

    }
}

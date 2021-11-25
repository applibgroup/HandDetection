package com.example.handdetectionapp.sample;

import org.junit.Test;
import ohos.aafwk.ability.delegation.AbilityDelegatorRegistry;
import ohos.app.Context;
import com.example.handdetection.detector.Detector;

import static org.junit.Assert.assertEquals;

public class DetectorTest {
	
    private static String imgPath = "entry/resources/rawfile/sign_img.txt";
    private static String imgName = "sign_img.txt";
    private static final int IMGW = 1600;
    private static final int IMGH = 1168;

    @Test
    public void test() {

        Context mContext = AbilityDelegatorRegistry.getAbilityDelegator().getAppContext();

        Detector mydetector = new Detector(imgPath, imgName, IMGH, IMGW,
                           mContext.getResourceManager(), mContext.getCacheDir());
    	
    	int[][] output = mydetector.get_output();
    	
        assertEquals(892, output[0][0]);
        assertEquals(333, output[0][1]);
        assertEquals(1222, output[1][0]);
        assertEquals(809, output[1][1]);
    }
}

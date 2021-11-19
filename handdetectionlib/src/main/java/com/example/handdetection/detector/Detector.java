package com.example.handdetection.detector;

import ohos.global.resource.RawFileEntry;
import ohos.global.resource.Resource;
import ohos.global.resource.ResourceManager;
import org.apache.tvm.Device;
import org.apache.tvm.Function;
import org.apache.tvm.Module;
import org.apache.tvm.NDArray;
import org.apache.tvm.TVMType;
import org.apache.tvm.TVMValue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Main Class for Hand Detection.
 */

public class Detector {
    private static final String MODEL_GRAPH_FILE_PATH = "resources/rawfile/graph.json";

    private static final String MODEL_CPU_LIB_FILE_PATH = "resources/rawfile/deploy_lib.so";
    private static final String MODEL_CPU_LIB_FILE_NAME = "deploy_lib.so";

    private static final String MODEL_PARAMS_FILE_PATH = "resources/rawfile/params.bin";

    // TVM constants
    private int outputindex = 0;
    private int imgchannel = 3;
    private String inputname = "input_1";
    private int modelinputsize = 224;
    private String imagepath;
    private String imagename;
    private int origimgw;
    private int origimgh;
    int[][] output;
    ohos.global.resource.ResourceManager resManager;
    File cachedir;

    /**
     * Hand Detector Constructor.
     *
     * @param path   - path for input image text file.
     * @param name   - name of input image text file.
     * @param resm   - ResourceManager GetResourceManager()
     * @param f      - CacheDir()
     * @param height - original image height;
     * @param width  - original image width;
     */

    public Detector(String path, String name, int height, int width, ResourceManager resm, File f) {
        this.imagepath = path;
        this.imagename = name;
        this.resManager = resm;
        this.cachedir = f;
        this.origimgh = height;
        this.origimgw = width;
        this.output = run_hand_detection();
    }

    public int[][] get_output() {
        return output;
    }

    /**
     * Main Function to run hand detection model.
     */
    public int[][] run_hand_detection() {

        // load json graph
        String modelGraph = null;
        RawFileEntry rawFileEntryModel = resManager.getRawFileEntry(MODEL_GRAPH_FILE_PATH);
        try {
            modelGraph = new String(getBytesFromRawFile(rawFileEntryModel));
        } catch (IOException e) {
            return null; //failure
        }

        // create java tvm device
        Device tvmDev = Device.cpu();

        RawFileEntry rawFileEntryModelLib = resManager.getRawFileEntry(MODEL_CPU_LIB_FILE_PATH);
        File file = null;
        Module modelLib = null;
        try {
            file = getFileFromRawFile(MODEL_CPU_LIB_FILE_NAME, rawFileEntryModelLib, cachedir);
            modelLib = Module.load(file.getAbsolutePath());
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }

        Function runtimeCreFun = Function.getFunction("tvm.graph_executor.create");
        TVMValue runtimeCreFunRes = runtimeCreFun.pushArg(modelGraph)
                .pushArg(modelLib)
                .pushArg(tvmDev.deviceType)
                .pushArg(tvmDev.deviceId)
                .invoke();
        Module graphExecutorModule = runtimeCreFunRes.asModule();

        // load parameters
        byte[] modelParams = null;
        RawFileEntry rawFileEntryModelParams = resManager.getRawFileEntry(MODEL_PARAMS_FILE_PATH);
        try {
            modelParams = getBytesFromRawFile(rawFileEntryModelParams);
        } catch (IOException e) {
            e.printStackTrace();
            return null; //failure
        }

        // get the function from the module(load parameters)
        Function loadParamFunc = graphExecutorModule.getFunction("load_params");
        loadParamFunc.pushArg(modelParams).invoke();

        RawFileEntry rawFileEntryImage = resManager.getRawFileEntry(imagepath);
        File fileImage = null;
        FileInputStream fin = null;
        try {
            fileImage = getFileFromRawFile(imagename, rawFileEntryImage, cachedir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fin = new FileInputStream(fileImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        InputStreamReader inputstreamreader = new InputStreamReader(fin);
        int size = modelinputsize * modelinputsize * imgchannel;
        float[] data = new float[size];
        int i = 0;
        try (BufferedReader bufferedReader = new BufferedReader(inputstreamreader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                data[i++] = Float.parseFloat(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // get the function from the module(set input data)
        NDArray inputNdArray = NDArray.empty(new long[]{1, imgchannel, modelinputsize, modelinputsize},
                new TVMType("float32"));
        inputNdArray.copyFrom(data);

        Function setInputFunc = graphExecutorModule.getFunction("set_input");
        setInputFunc.pushArg(inputname).pushArg(inputNdArray).invoke();
        // release tvm local variables
        inputNdArray.release();
        setInputFunc.release();

        // get the function from the module(run it)
        Function runFunc = graphExecutorModule.getFunction("run");
        runFunc.invoke();
        // release tvm local variables
        runFunc.release();

        // get the function from the module(get output data)
        NDArray outputNdArray = NDArray.empty(new long[]{1, 5, 7, 7}, new TVMType("float32"));
        Function getOutputFunc = graphExecutorModule.getFunction("get_output");
        getOutputFunc.pushArg(outputindex).pushArg(outputNdArray).invoke();
        float[] output = outputNdArray.asFloatArray();
        // release tvm local variables
        outputNdArray.release();
        getOutputFunc.release();

        // display the result from extracted output data
        float[][] finalarr = new float[7][7];
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 7; y++) {
                finalarr[x][y] = output[(7 * x) + y];
            }
        }

        int w = 7;
        int h = 7;
        float max = finalarr[0][0];
        float temp = 0;
        int x = 0;
        int y = 0;
        for (i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                temp = finalarr[i][j];
                if (max < temp) {
                    max = temp;
                    x = i;
                    y = j;
                }
            }
        }

        int loc = (7 * x) + y;
        float[] bbox = new float[4];
        bbox[0] = output[loc + 49];
        bbox[1] = output[loc + 2 * 49];
        bbox[2] = output[loc + 3 * 49];
        bbox[3] = output[loc + 4 * 49];

        int[][] coordinates = new int[2][2];

        coordinates[0][0] = (int) (bbox[0] * origimgw);
        coordinates[0][1] = (int) (bbox[1] * origimgh);
        coordinates[1][0] = (int) (bbox[2] * origimgw);
        coordinates[1][1] = (int) (bbox[3] * origimgh);

        return coordinates;
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

    private static byte[] getBytesFromRawFile(RawFileEntry rawFileEntry)
            throws IOException {
        byte[] buf = null;
        try {
            Resource resource = rawFileEntry.openRawFile();
            buf = new byte[(int) rawFileEntry.openRawFileDescriptor().getFileSize()];
            int bytesRead = resource.read(buf);
            if (bytesRead != buf.length) {
                throw new IOException("Asset Read failed!!!");
            }
            return buf;
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
}

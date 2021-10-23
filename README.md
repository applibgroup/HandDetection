# Hand Detection
An ai based hand detection model which can detection location of hand in an image. The model returns top left and bottom right co-ordinates of the hand.

### App Screenshots
<p>
	<img src="/screenshots/handdetection_1.jpeg" width = 350 ></img>
        <img src="/screenshots/handdetection_2.jpeg" width = 350 ></img>
        <img src="/screenshots/handdetection_3.jpeg" width = 350 ></img>
</p>

# Source
This library has been inspired by YOLO object detection model.

## Integration
 1. For using handdetection module in sample app, include the source code and add the below dependencies in entry/build.gradle to generate hap/support.har.

```
	implementation project(path: ':handdetectionlib')
```

 2. For using handdetection module in separate application using har file, add the har file in the entry/libs folder and add the dependencies in entry/build.gradle file.

```
	implementation fileTree(dir: 'libs', include: ['*.har'])
```
 3. For using handdetection module from a remote repository in separate application, add the below dependencies in entry/build.gradle file.

```
	implementation 'dev.applibgroup:handdetectionlib:1.0.0'
```

## Usage
 1. Initialise the constructor of HandDetection with the image path, image name, image height, image width, output image path, getResourceManager() and getCacheDir() arguments.
 
 2. Use get_output() to get the co-ordinates of the hand position in the image.
Example:

```slice
    	Detector mydetector = new Detector(MODEL_INPUT_IMAGE_PATH, MODEL_INPUT_IMAGE_NAME, 
				img_h, img_w, getResourceManager(), getCacheDir());
    	handcoordinates = mydetector.get_output();
```
Check the example app for more information.

## License

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.


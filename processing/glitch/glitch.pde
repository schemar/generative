import java.util.UUID;

// Applies three layers of distortion to the given images:
// 1. RGB noise
// 2. Pixel shift
// 3. Pixel scrumble
//
// See below functions for details on their inner workings.

String projectName = "glitch";

// The code will load all images `{prefix}{id}{postfix}` using the array of IDs.
// The distortions will be applied to all the images.
// Each run will use a different random seed.
// All outputs will be written to new files.
String imageNamePrefix = "glitch_";
String imageNamePostfix = ".jpg";
String[] imageIds = {
  "1",
  "2",
  "3"
};
// How often each image will be processed with different output.
int runs = 5;

// The bottom of the image won't be distorted according to the following ratios:
float cleanBottomRgb = 1/3.0;
float cleanBottomPixelShift = 1/1.5;
float cleanBottomScrumble = 1/2.0;

// The strengths of the three passes:
float rgbStrength = 0.001;
float shiftStrength = 0.03;
float scrumbleStrength = 0.001;

float rgbMax = 256f;

void setup() {
  // The input images will be rescaled to the size given here:
  size(1433, 1790);
  colorMode(RGB, rgbMax);
  
  generate();
}

void generate() {
  for (int i = 0; i < imageIds.length; i = i+1) {
    for (int run = 1; run <= runs; run = run + 1) {
      noiseSeed(run);
      randomSeed(run);
      String imageName = imageNamePrefix + imageIds[i] + imageNamePostfix;
      PImage input = load(imageName);
      PImage output = generateFrom(input);

      image(output, 0, 0);
      save("../renders/" + projectName + "_" + imageName + "_run_" + run + ".png");
    }
  }
}

/**
 * Loads an image from the data folder and resizes it.
 */
PImage load(String imageName) { 
  PImage input = loadImage(imageName);
  input.resize(width, height);
  
  return input;
}

/**
 * Generates an image by randomizing colors of the input image.
 */
PImage generateFrom(PImage input) {
  PImage output = createImage(width, height, RGB);

  output = rgbNoisePass(input);
  output = pixelShiftPass(output);
  output = pixelScrumblePass(output);
  
  return output;
}

PImage rgbNoisePass(PImage input) {
  PImage output = createImage(width, height, RGB);
  
  for (int y = 0; y < height; y = y+1) {
    float heightModifier = (height - y - height*cleanBottomRgb);
    heightModifier = max(heightModifier, 0);
    int redOffset = (int) (noise(y*rgbStrength*100) * heightModifier);
    int greenOffset = (int) (noise(y*rgbStrength*10+height) * heightModifier);
    int blueOffset = (int) (noise(y*rgbStrength+2*height) * heightModifier);

    for (int x = 0; x < width; x = x+1) {
      color redInputPixel = input.get((x + redOffset) % width, y);
      color greenInputPixel = input.get((x - greenOffset) % width, y);
      color blueInputPixel = input.get((x + blueOffset) % width, y);
      
      color inputPixel = color(red(redInputPixel), green(greenInputPixel), blue(blueInputPixel));
      output.set(x, y, inputPixel);
    }
  }
  
  output.updatePixels();
  
  return output;
}

PImage pixelShiftPass(PImage input) {
  PImage output = createImage(width, height, RGB);
  
  for (int y = 0; y < height; y = y+1) {
    float heightModifier = (height - y - height*cleanBottomPixelShift);
    heightModifier = max(heightModifier, 0);
    int offset = (int) (noise(y*shiftStrength) * heightModifier);

    for (int x = 0; x < width; x = x+1) {      
      color inputPixel = input.get(x, (y+offset)%height);
      output.set(x, y, inputPixel);
    }
  }
  
  output.updatePixels();
  
  return output;
}

PImage pixelScrumblePass(PImage input) {
  PImage output = createImage(width, height, RGB);
  
  for (int y = 0; y < height; y = y+1) {
    for (int x = 0; x < width; x = x+1) {
      float heightModifier = (height - y - height*cleanBottomScrumble);
      heightModifier = (max(heightModifier, 0) * scrumbleStrength)/3.0;
      
      float noiseValueX = noise(x*scrumbleStrength,y*scrumbleStrength);
      float noiseValueY = noise(y*scrumbleStrength,x*scrumbleStrength);
      int xOffset = (int) (noiseValueX * width * heightModifier);
      int yOffset = (int) (noiseValueY * height * heightModifier);
      
      color inputPixel = input.get((x+xOffset)%width, (y+yOffset)%height);
      output.set(x, y, inputPixel);
    }
  }
  
  output.updatePixels();
  
  return output;
}

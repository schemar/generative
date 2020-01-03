import java.util.UUID;

String projectName = "mosaicing";

int mosaicsPerSide = 5;
String imageName = "matt-reames-_4nDSLIvPO4-unsplash.jpg";

float hsbMax = 256f;
float minSaturation = hsbMax / 1.2;
float minBrightness = hsbMax / 1.5;

void setup() {
  size(825, 1238);
  colorMode(HSB, hsbMax);
  generate();
}

void generate() {
  PImage input = load(imageName);
  PImage output = generateFrom(input);
  
  image(output, 0, 0);
  save("../renders/" + projectName + "_" + UUID.randomUUID() + ".png");
}

/**
 * Loads an image from the data folder.
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
  PImage output = createImage(width, height, HSB);
  HashMap<Integer, HashMap<Integer, Float>> offsets = new HashMap<Integer, HashMap<Integer, Float>>();

  for (int x = 0; x < width; x = x+1) {
    for (int y = 0; y < height; y = y+1) {
      color colorAtPixel = input.get(x, y);
      
      float offset = getOffset(offsets, x, y);
      color newColorAtPixel = offsetColor(colorAtPixel, offset);
      output.set(x, y, newColorAtPixel);
    }
  }
  
  output.updatePixels();
  
  return output;
}

float getOffset(HashMap<Integer, HashMap<Integer, Float>> offsets, int x, int y) {
  int xAccessor = x / (width / mosaicsPerSide);
      int yAccessor = y / (height / mosaicsPerSide);
      
      if(!offsets.containsKey(xAccessor)) {
        offsets.put(xAccessor, new HashMap<Integer, Float>());
      }
      
      HashMap xMap = offsets.get(xAccessor);
      
      if(!xMap.containsKey(yAccessor)) {
        xMap.put(yAccessor, random(hsbMax));
      }
      
      float offset = offsets.get(xAccessor).get(yAccessor);
      
      return offset;
}

/**
 * Offsets a color value based on the input and offset.
 */
color offsetColor(color input, float offset) {
  color output = color(
    offsetChannel(hue(input), offset),
    (offsetChannel(saturation(input), offset) % (hsbMax - minSaturation)) + minSaturation,
    (offsetChannel(brightness(input), offset) % (hsbMax - minBrightness)) + minBrightness
  );
  
  return output;
}

/**
 * Offsets a floart value based on the input with the result in range [0..256[.
 */
float offsetChannel(float input, float offset) {
  float output = (input + offset) % hsbMax;
  
  return output;
}

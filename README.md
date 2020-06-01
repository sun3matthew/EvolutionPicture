# EvolutionPicture

java EvoPicture filename

The images (in .jpg format) are added into a folder in the Input_Images, the two images should be named One.jpg and Two.jpg, the "Two" image acts as the world and the "One" image acts as the sample population that will be selected to populate the world.

Random pixels from the the "One" image are selected and put into the world. For the first 15 generation, each pixel will look at the 8 pixels around it to find the "Best" home, this goes on for about 40 frames. Then after it the 40 frames, the pixels who are too diffrent from the current world pixel they are currently on are deleted. The remaining pixels have create children who are slightly mutated versions of themselves(colorwise). This process goes on for 15 more generations, then after the 15 generations the pixels will no longer look/move and just produce more child pixels. Even the majority of the pixels on the screen is covered, the program will end, exporting the images into the Output_Images.

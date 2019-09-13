# PlantPlanet
PlantPlanet is an Android application for plant disease detection. This project is part of [my participation](https://eucys.eu/projects-2019/plantplanet/) in EU Contest for Young Scientists 2019 in Sofia, Bulgaria.

## Screenshots

<p align="center">
  <img src="demo/Screenshot_20230429_014446.png" width="25%" />
  <img src="demo/Screenshot_20230429_014500.png" width="25%" />
  <img src="demo/Screenshot_20230429_014558.png" width="25%" />

  <img src="demo/Screenshot_20230429_014527.png" width="25%" />
  <img src="demo/Screenshot_20230429_014534.png" width="25%" />
  <img src="demo/Screenshot_20230429_014540.png" width="25%" />

  <img src="demo/Screenshot_20230429_020025.png" width="25%" />
  <img src="demo/Screenshot_20230429_020043.png" width="25%" />
  <img src="demo/Screenshot_20230429_020055.png" width="25%" />
</p>

## Features
- Plant disease detection
- Plant disease library
- Plant disease map
- Plant disease history
- Offline functionality
- Text-to-speech functionality

## Plant disease detection
[The app's plant disease detection model](https://www.kaggle.com/code/abdallahalidev/plantplanet) was trained based on [InceptionV3](https://github.com/MarkoArsenovic/DeepLearning_PlantDiseases) using [PlantVillage Dataset](https://github.com/spMohanty/PlantVillage-Dataset). The trained model was converted into a [TensorFlow Lite](https://www.tensorflow.org/lite) model, which is compatible to be run on mobile devices.

## Libraries

- [Firebase Firestore](https://firebase.google.com/docs/firestore)
- [Firebase Storage](https://firebase.google.com/docs/storage)
- [Firebase MLKit](https://firebase.google.com/docs/ml-kit)
- [Google Maps API](https://developers.google.com/maps)
- [Fotoapparat](https://github.com/RedApparat/Fotoapparat)
- [Picasso](https://github.com/square/picasso)

# Installation

[<img src="https://user-images.githubusercontent.com/69304392/148696068-0cfea65d-b18f-4685-82b5-329a330b1c0d.png" alt='Get it on GitHub' height="80">](https://github.com/abdallahalidev/plant-planet/raw/main/PlantPlanet_1.0.apk)
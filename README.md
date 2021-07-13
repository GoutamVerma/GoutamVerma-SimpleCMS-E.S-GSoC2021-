
# Liquid Galaxy SimpleCMS E.S

## Discription

SimpleCMS E.S is an android application which is able to mimics the functionality of Google Earth Studio and export files in .esp format. After exporting file it also save it locally under application specific folder.  

*This application have following features*

* **Zoom-To**  A simple camera move to a point from a higher altitude. Your first and last frames will match the exact framing of your camera in their respective steps.

* **Orbit** A perfectly smooth circular orbit around a point of interest.

* **Spiral** A gradually shrinking, descending orbit around a point of interest.

This application is developed as part of Google Summer of Code 2021 by Goutam Verma, with Diego reviros as main-mentor, and Andreu Ibañez and Ivan J as co-mentos.

Documentation is [here](https://docs.google.com/document/d/1ctusDehQJA2rD2hkLhemHXaoJVU2jk1SpHPEBQ8IngI/edit?usp=sharing)


![logo](https://4.bp.blogspot.com/-n-vRn12_mEk/XLr2zIDgUnI/AAAAAAAHBTI/69TPLvy-nsg9OPNC15bZB3-WzSX8m0PrwCLcBGAs/s1600/LOGO_LIQUID_GALAXY-sq300x300-pngtranspOK.png)

## Deployment

To deploy this project it is necessary to have Android 10(API Level 29) and 10 inch tablet
.At last remember to install the USB of [OEM](https://developer.android.com/studio/run/oem-usb) controllers of your tablet

## Installation
*There is two ways to install this application :*

* **Running with APK**
1. Download the latest release of apk from [here](https://drive.google.com/file/d/1Pp6kPdrDql9gvjeJuOzrSJW5mrM-cMB_/view?usp=sharing). 
2. Install it in device or emulator
3. Create a POI, fill the necessary details of location.
4. Click on the “Export” button, select the feature you want to use.
5. Finally, [open Google Earth Studio](https://earth.google.com/studio/) in the web browser. Select “open project“ then locate the esp file and enjoy the animation. 

* **Running from source code**
1. Import the project from version control in android. 
2. If you have a physical android tablet use that otherwise create a new AVD.(for more information click here)
3. Connect android device or emulator
4. Click on the “Run” button(Apk will install and show UI of Apk)
5. Create a POI
6. Click on “Export” button 
7. Select the feature you wants to use
8. Then finally select apk from which you wants to export esp file 
9. Finally, [open Google Earth Studio](https://earth.google.com/studio/) in the web browser. Select “open project“ then locate the esp file and enjoy the animation. 


*For more information*

* How to import project in android studio? click [here](https://developer.android.com/studio/intro/migrate)
* How to create Android virtual device? click [here](https://developer.android.com/studio/run/managing-avds)
* How to run project in android studio? click [here](https://developer.android.com/studio/run)
* How to import project in Google Earth Studio? click [here](https://earth.google.com/studio/docs/the-basics/project-management/)
## SimpleCMS E.S (gif)
   <img src="https://raw.githubusercontent.com/GoutamVerma/GoutamVerma-SimpleCMS-E.S-GSoC2021-/main/screenshot.gif"/>
   
   
## Tech Stack

* Java
* ESP,KML   
* Android Studio
* Google Earth Studio

## Authors

* [Goutam Verma](https://github.com/GoutamVerma)😎

## License

Licensed by [MIT](https://raw.githubusercontent.com/GoutamVerma/GoutamVerma-SimpleCMS-E.S-GSoC2021-/main/MIT%20License)

  

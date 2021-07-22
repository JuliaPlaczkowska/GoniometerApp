# Goniometer App
The goal of this app is to enable user to measure range-of-motion of joints from the point of their smartphone.

## Table of Contents

* [General info](#general-info)
* [Technologies](#general-info)
* [Setup](#setup)
* [Instruction](#instruction)
* [Measurement](#measurement)

## General info
This project is a mobile version of goniometer device for Android. It measures angles in a vertical plane based on accelerometer indications.

## Technologies
Project is created with:
* Android SDK 30
* Java 8
* Gradle 6.5

## Setup
To run this project, you can clone it from this GitHub repository by executing the following command in your local command shell or terminal:

```
$ git clone https://github.com/JuliaPlaczkowska/GoniometerApp
```
Then open the project with Android Studio and run it on physical device.

## Instruction
After running the project you will get the home activity: 

![Home view](https://i.imgur.com/CV7yCNV.png)

Here you can choose whether you want to start a new measurement or view the history of your measurements.

By clicking the MEASUREMENT button you will be redirected to the measurement activity:

![Measurement1](https://i.imgur.com/uFaUnyE.png)

Screen above shows the initial appearance of the goniometer - before taking a measurement. When the initial plane is ready, tap anywhere on the screen and a second arm will appear as shown in the next figure.

![Measurement2](https://i.imgur.com/pZ3wLdX.png)

After the phone is properly positioned along the second plane, click
anywhere on the screen to stop the arms - then a green save button will appear (to opt out of saving the measurement click anywhere else). 
After clicking save, we are transferred to the save-result view.

![Save screen](https://i.imgur.com/kdZOaZV.png)

Here we can see the value of the measurement as well as the date and time in which the measurement was taken. Now you can
enter the name of the measurement (e.g. name of the measured joint) and click the SUBMIT button. 
Now you should see your measurement in measurements history:

![History](https://i.imgur.com/XaD75z0.png)

## Measurement

Here is the instruction on how to take a proper measurement in 4 simple steps.

### Step 1

![Step1](https://i.imgur.com/vc25TQu.png)

Put the phone to the first plane and confirm the initial position by clicking on the screen

### Step 2

![Step2](https://i.imgur.com/5Ukl9fb.png)

Put the phone to the second plane, but now with the opposite edge of the phone


### Step 3

![Step3](https://i.imgur.com/XmnsCne.png)

Confirm the second position by clicking on the screen

### Step 4

![Step4](https://i.imgur.com/BRN4Ey8.png)

Save the measurement

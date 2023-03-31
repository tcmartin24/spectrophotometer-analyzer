# Portable Spectrophotometer Analyzer

This project is an implementation (or an attempt) of a spectrophotometer that is capable of analyzing a sample and identifying whether it contains air, water, or lead. It is built using Arduino and Java, with the help of the Spring Framework, Pi4J, and Encog Machine Learning Framework.

## Overview

The system consists of the following components:

1. **Arduino**: An Arduino microcontroller that controls the LEDs and records the intensity of light transmitted through the sample.
2. **Java Application**: A Java-based Spring Boot application that communicates with the Arduino, processes the data, and applies machine learning to determine the sample's content.

## Files and Their Roles

### Arduino

- `spectrophotometer.ino`: The Arduino sketch responsible for controlling the LED light source and measuring the intensity of light transmitted through the sample.

### Java Application

- `SPApplication.java`: Main entry point into the Spring Boot application. Exposes a RESTful endpoint to permit clients to access sample data and results
- `AnalysisService.java`: The core service class that initializes the I2C communication with the Arduino, sends control signals, collects data, and triggers the machine learning analysis.
- `RGBCalc.java`: A utility class that converts wavelengths (in nanometers) to their corresponding RGB values. This class is used to set the LED color during the analysis.
- `WaterAnalyzer.java`: The machine learning class that trains a neural network model using Encog and classifies the sample based on its spectrophotometer readings.

## Basic Approach

1. Once the Raspberry Pi (Pi from here) has finished booting (which can take some time), it starts the Java application.
2. The application exposes an endpoint, then sends a data code to the Arduino to indicate that the system is ready
3. Arduino receives this signal and presents a ready message on the LCD
4. A user can load a sample (or not) into the device and press the button to begin analysis
5. The button press results in a signal sent back to Pi and a listener method being called which in turn calls the service to begin analysis
6. The analyze method 
    - loops through wavelengths from 400-700
    - converts them to RGB values
    - sends those values to arduino
    - arduino illuminates LED with those RGB values
    - arduino takes a reading and sends that reading back to Pi
    - Pi reads the reading and stores in a map of wavelength->value
    - Once all samples have been taken, an array of the values (from the map) is sent to the WaterAnalyzer for analysis using machine learning from pretrained data for classification
- Bob's your uncle!!!

<div align="center">
    <table>
        <tr>
            <td><img src="assets/IMG_4142 Medium.jpeg" alt="IMG_4142 Medium" width="300"/></td>
            <td><img src="assets/IMG_4144 Medium.jpeg" alt="IMG_4144 Medium" width="300"/></td>
            <td><img src="assets/IMG_4145 Medium.jpeg" alt="IMG_4145 Medium" width="300"/></td>
        </tr>
        <tr>
            <td><img src="assets/IMG_4146 Medium.jpeg" alt="IMG_4146 Medium" width="300"/></td>
            <td><img src="assets/IMG_4147 Medium.jpeg" alt="IMG_4147 Medium" width="300"/></td>
            <td><img src="assets/IMG_4149 Medium.jpeg" alt="IMG_4149 Medium" width="300"/></td>
        </tr>
        <tr>
            <td><img src="assets/IMG_4150 Medium.jpeg" alt="IMG_4150 Medium" width="300"/></td>
            <td><img src="assets/IMG_4151 Medium.jpeg" alt="IMG_4151 Medium" width="300"/></td>
            <td><img src="assets/IMG_4152 Medium.jpeg" alt="IMG_4152 Medium" width="300"/></td>
        </tr>
        <tr>
            <td><img src="assets/IMG_4153 Medium.jpeg" alt="IMG_4153 Medium" width="300"/></td>
            <td><img src="assets/IMG_4154 Medium.jpeg" alt="IMG_4154 Medium" width="300"/></td>
            <td></td>
        </tr>
    </table>
</div>

### Videos

[//]: # (<div align="center">)

[//]: # (    <table>)

[//]: # (        <tr>)

[//]: # (            <td>)

[//]: # (                <video width="300" controls>)

[//]: # (                    <source src="assets/IMG_4143-HD%20720p.mov" type="video/mp4">)

[//]: # (                    Your browser does not support the video tag.)

[//]: # (                </video>)

[//]: # (            </td>)

[//]: # (            <td>)

[//]: # (                <video width="300" controls>)

[//]: # (                    <source src="assets/IMG_4148-HD%20720p.mov" type="video/mp4">)

[//]: # (                    Your browser does not support the video tag.)

[//]: # (                </video>)

[//]: # (            </td>)

[//]: # (            <td>)

[//]: # (               <video width="300" controls>)

[//]: # (                    <source src="assets/spectrophotometer-cad.mov" type="video/mp4">)

[//]: # (                    Your browser does not support the video tag.)

[//]: # (                </video>)

[//]: # (            </td>)

[//]: # (        </tr>)

[//]: # (    </table>)

[//]: # (</div>)

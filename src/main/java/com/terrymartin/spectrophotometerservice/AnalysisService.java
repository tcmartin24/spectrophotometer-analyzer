package com.terrymartin.spectrophotometerservice;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class AnalysisService {

    private I2CBus i2c;
    private I2CDevice arduino;
    private Map<Integer, Integer> results = new TreeMap<>();
    private WaterAnalyzer analyzer = new WaterAnalyzer();

    @PostConstruct
    public void setup() throws IOException {
        i2c = I2CFactory.getInstance(I2CBus.BUS_1);
        arduino = i2c.getDevice(0x04);
        analyzer.setup();
    }

    public void sendStartMessageToArduino() throws IOException {
        arduino.write((byte) 10);
    }

    @Async
    public void analyze() throws Exception {
        System.out.println("Starting data capture");
        results.clear();
        int lightLevel = -1;
        List<String> valueStrings = new ArrayList<>();
        for (int wavelength=400; wavelength < 700; wavelength+=10) {
            int[] rgbValues = RGBCalc.convertWavelengthToRGB(wavelength);
            byte[] vals = new byte[3];
            vals[0] = (byte)rgbValues[0];
            vals[1] = (byte)rgbValues[1];
            vals[2] = (byte)rgbValues[2];

            arduino.write(vals, 0, vals.length);
            Thread.sleep(2000);
            lightLevel = arduino.read();
            System.out.println(String.format("%d %d", wavelength, lightLevel));
            results.put(wavelength, lightLevel);
            valueStrings.add(Integer.toString(lightLevel));
        }
        String sampleType = analyzer.determineCompound(valueStrings.toArray(new String[]{}));
        if (sampleType.equals("Air")) {
            arduino.write((byte) 20);
        } else if(sampleType.equals("Water")){
            arduino.write((byte) 21);
        } else {
            arduino.write((byte) 22);
        }
    }

    public Map<Integer, Integer> getResults() {
        return results;
    }
}

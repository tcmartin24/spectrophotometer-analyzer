package com.terrymartin.spectrophotometerservice;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@SpringBootApplication
@RestController
@RequestMapping("analyzer")
@EnableAsync
public class SPApplication implements ApplicationRunner {

	@Autowired
	private AnalysisService service;

	public static void main(String[] args) {
		SpringApplication.run(SPApplication.class, args);
	}

	@RequestMapping("start")
	public String startAnalysis() throws Exception {
		System.out.println("Service ref is: " + service);
		service.analyze();
		return "Done";
	}

	@RequestMapping("results")
	public Map<Integer, Integer> getResults() {
		return service.getResults();
	}

	@RequestMapping("test")
	public String test() {
		return "Date/Time: " + LocalDateTime.now();
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		final GpioController gpio = GpioFactory.getInstance();
		final GpioPinDigitalInput arduinoPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);
		arduinoPin.setShutdownOptions(true);
		arduinoPin.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				// display pin state on console
				System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
				if (event.getState().isHigh()) {
                    try {
                        service.analyze();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
			}
		});

		service.sendStartMessageToArduino();
	}
}

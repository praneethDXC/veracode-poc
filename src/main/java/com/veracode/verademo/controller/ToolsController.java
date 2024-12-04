package com.veracode.verademo.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Scope("request")
public class ToolsController {
	private static final Logger logger = LogManager.getLogger("VeraDemo:ToolsController");

	@Autowired
	ServletContext context;

	@RequestMapping(value = "/tools", method = RequestMethod.GET)
	public String tools() {
		return "tools";
	}

	@RequestMapping(value = "/tools", method = RequestMethod.POST)
	public String tools(@RequestParam(value = "host", required = false) String host,
			@RequestParam(value = "fortunefile", required = false) String fortuneFile, Model model) {
		model.addAttribute("ping", host != null ? ping(host) : "");

		if (fortuneFile == null) {
			fortuneFile = "literature";
		}
		model.addAttribute("fortunes", fortune(fortuneFile));

		return "tools";
	}

	private String ping(String host) {
		if (host == null || host.isEmpty() || !host.matches("^[a-zA-Z0-9.-]+$"))
			return "Invalid host";

		String output = "";
		try {
			InetAddress inet = InetAddress.getByName(host);
			if (inet.isReachable(5000)) {
				output = "Host is reachable";
			} else {
				output = "Host is not reachable";
			}
		} catch (IOException ex) {
			logger.error(ex);
			output = "Error occurred while trying to reach the host";
		}

		return output;
	}

	private String fortune(String fortuneFile) {

		String directoryValidationRegex = "^[a-zA-Z0-9_-]+(?:/[a-zA-Z0-9_-]+)*$";

		if (fortuneFile == null || fortuneFile.isEmpty() || !fortuneFile.matches(directoryValidationRegex)) {
			return "Invalid fortune file";
		}

		String output = "";
		try {
			ProcessBuilder pb = new ProcessBuilder("/bin/fortune", fortuneFile);
			Process proc = pb.start();
			proc.waitFor(5, TimeUnit.SECONDS);
			InputStreamReader isr = new InputStreamReader(proc.getInputStream());
			BufferedReader br = new BufferedReader(isr);

			String line;

			while ((line = br.readLine()) != null) {
				output += line + "\n";
			}
		} catch (IOException ex) {
			logger.error(ex);
		} catch (InterruptedException ex) {
			logger.error(ex);
		}

		return output;
	}
}
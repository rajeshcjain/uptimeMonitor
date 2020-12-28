package com.xebia.uptime.monitor.rest;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xebia.uptime.monitor.enums.Filters;
import com.xebia.uptime.monitor.model.UptimeMonitoringDBObj;
import com.xebia.uptime.monitor.model.UptimeMonitoringObj;
import com.xebia.uptime.monitor.model.UptimeReq;
import com.xebia.uptime.monitor.model.UptimeUrl;
import com.xebia.uptime.monitor.service.UptimeControllerService;

@RestController
@RequestMapping(value = "/api/v1", consumes = "application/json", produces = "application/json")
public class UptimeController {

	@Autowired
	UptimeControllerService uptimeControllerService;

	private static String ERR_CODE = "The Time units can only be hours and minutes. Minutes can only be between 1 and 59.Hours can only be between 1 and 24.";

	private static String ERR_ACTIVE_INACTIVE = "Status could be active/inactive only.";

	@PostMapping("/uptime")
	public ResponseEntity<String> createUptimeRequest(@RequestBody final UptimeReq newUptime) {

		if (newUptime == null || areParamsInvalid(newUptime)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		final int freq = Math.round(newUptime.getFreq());
		System.out.println("The frequence is " + freq);

		final String units = newUptime.getTime_units() == null ? "minutes" : newUptime.getTime_units();

		if (units.equalsIgnoreCase("minutes") && (freq < 59 || freq > 1) || units.equalsIgnoreCase("hours") && (freq < 24 || freq > 1)) {
			uptimeControllerService.saveUptimeMonitoringCheck(new UptimeMonitoringObj(newUptime.getName(), newUptime.getUrl(), freq, units, "active"));
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(ERR_CODE, HttpStatus.BAD_REQUEST);
		}
	}

	Filters getFilters(final String filter) {
		return filter.equalsIgnoreCase("all") ? Filters.ALL : filter.equalsIgnoreCase("name") ? Filters.NAME : Filters.INTERVAL;
	}

	@GetMapping("/uptime")
	public List<UptimeMonitoringObj> getCheckPoints(@RequestParam(defaultValue = "all") final String filter, @RequestParam(required = false) final String checkval) {
		if (filter.equalsIgnoreCase("all") || filter.equalsIgnoreCase("name") && checkval != null || filter.equalsIgnoreCase("freq") && checkval != null) {
			return uptimeControllerService.getUptimeMonitoringCheck(getFilters(filter), checkval);
		}

		return Collections.EMPTY_LIST;
	}

	@PostMapping("/uptime/url")
	public UptimeMonitoringDBObj getDetails4Url(@RequestBody final UptimeUrl uptimeUrl) {
		return uptimeControllerService.getUrlDetails(uptimeUrl.getUrl());
	}

	private boolean areParamsInvalid(final UptimeReq newUptime) {
		if (newUptime.getName() == null || newUptime.getFreq() == null || newUptime.getUrl() == null) {
			return true;
		}
		return false;
	}

	@PutMapping("/uptime/status/{status}")
	public ResponseEntity<String> updateCheckPoint(@RequestBody final UptimeReq newUptime, @PathVariable("status") final String status) {
		if (newUptime == null || areParamsInvalid(newUptime)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		if (status.equalsIgnoreCase("active") || status.equalsIgnoreCase("inactive")) {
			uptimeControllerService.saveUptimeMonitoringCheck(new UptimeMonitoringObj(newUptime.getName(), newUptime.getUrl(), Math.round(newUptime.getFreq()), newUptime.getTime_units(), status));
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(ERR_ACTIVE_INACTIVE, HttpStatus.BAD_REQUEST);
		}
	}

}

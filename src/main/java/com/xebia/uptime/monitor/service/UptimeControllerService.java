package com.xebia.uptime.monitor.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xebia.uptime.monitor.enums.Filters;
import com.xebia.uptime.monitor.model.UptimeMonitoringDBObj;
import com.xebia.uptime.monitor.model.UptimeMonitoringObj;
import com.xebia.uptime.monitor.persistence.UptimePersistenceService;

@Service("uptimeControllerService")
public class UptimeControllerService {

	@Autowired
	private UptimePersistenceService uptimePersistenceService;

	public void saveUptimeMonitoringCheck(final UptimeMonitoringObj uptimeMonitoringObj) {
		uptimePersistenceService.save(uptimeMonitoringObj);
	}

	public void updateUptimeMonitoringCheck(final UptimeMonitoringObj uptimeMonitoringObj) {
		uptimePersistenceService.save(uptimeMonitoringObj);
	}

	public UptimeMonitoringDBObj getUrlDetails(final String url) {
		return uptimePersistenceService.getEntry4Url(url).get();
	}

	public List<UptimeMonitoringObj> getUptimeMonitoringCheck(final Filters filters, final String checkPointVal) {
		if (filters == Filters.ALL) {
			return uptimePersistenceService.findCheckPoints(filters);
		} else if (filters == Filters.INTERVAL) {
			return uptimePersistenceService.findCheckPointsForFreq(Integer.parseInt(checkPointVal));
		} else {
			return uptimePersistenceService.findCheckPointsForName(checkPointVal);
		}
	}

	public List<UptimeMonitoringObj> getAllActiveCheckpoints() {
		return uptimePersistenceService.getAllActiveCheckPoints();
	}

}

package com.xebia.uptime.monitor.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.uptime.monitor.model.UptimeMonitoringDBObj;
import com.xebia.uptime.monitor.model.UptimeMonitoringObj;
import com.xebia.uptime.monitor.model.UptimeReq;
import com.xebia.uptime.monitor.model.UptimeUrl;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UptimeControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	private final ObjectMapper mp = new ObjectMapper();

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	//@@@@@@@@@@@@@POST@@@@@@@@@@@@@

	@Test
	public void createUptimeRequestTest_200OK() throws Exception {
		final UptimeReq uptimeReq = new UptimeReq("chk1", "google.com", (float) 2.0, "minutes");
		final String jsonRequest = mp.writeValueAsString(uptimeReq);

		final MvcResult result = mockMvc.perform(post("/api/v1/uptime").content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();

		Assert.assertTrue(result.getResponse().getStatus() == HttpStatus.OK.value());
	}

	@Test
	public void createUptimeRequestTest_200OK_TimeUnitsInFraction() throws Exception {
		final UptimeReq uptimeReq = new UptimeReq("chk1", "google.com", new Float(2.20), "minutes");
		final String jsonRequest = mp.writeValueAsString(uptimeReq);

		final MvcResult result = mockMvc.perform(post("/api/v1/uptime").content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();

		Assert.assertTrue(result.getResponse().getStatus() == HttpStatus.OK.value());
	}

	@Test
	public void createUptimeRequestTest_400BadRequest_TimeInSeconds() throws Exception {
		final UptimeReq uptimeReq = new UptimeReq("chk1", "google.com", (float) 2.0, "seconds");
		final String jsonRequest = mp.writeValueAsString(uptimeReq);

		final MvcResult result = mockMvc.perform(post("/api/v1/uptime").content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isBadRequest()).andReturn();
		result.getResponse().getContentAsString();

		Assert.assertTrue(result.getResponse().getStatus() == HttpStatus.BAD_REQUEST.value());
	}

	@Test
	public void createUptimeRequestTest_400BadRequest_NoPayload() throws Exception {
		final UptimeReq uptimeReq = new UptimeReq();
		final String jsonRequest = mp.writeValueAsString(uptimeReq);
		final MvcResult result = mockMvc.perform(post("/api/v1/uptime").content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isBadRequest()).andReturn();
		final int val = result.getResponse().getStatus();

		Assert.assertTrue(val == HttpStatus.BAD_REQUEST.value());
	}

	//@@@@@@@@@@@@GET@@@@@@@@@@@@

	@Test
	public void updateDb4RestOfTheTests() throws Exception {

		UptimeReq uptimeReq = new UptimeReq("chk1", "google.com", (float) 5.0, "hours");
		String jsonRequest = mp.writeValueAsString(uptimeReq);

		mockMvc.perform(post("/api/v1/uptime").content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();

		uptimeReq = new UptimeReq("chk2", "riviers.com", (float) 2.0, "minutes");
		jsonRequest = mp.writeValueAsString(uptimeReq);
		mockMvc.perform(post("/api/v1/uptime").content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();

		uptimeReq = new UptimeReq("chk1", "https://google.com", (float) 5.0, "minutes");
		jsonRequest = mp.writeValueAsString(uptimeReq);
		mockMvc.perform(post("/api/v1/uptime").content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();

		uptimeReq = new UptimeReq("chk2", "https://paypal.com", (float) 1.0, "minutes");
		jsonRequest = mp.writeValueAsString(uptimeReq);
		mockMvc.perform(post("/api/v1/uptime").content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();

		uptimeReq = new UptimeReq("chk2", "https://xyz.com", (float) 5.0, "hours");
		jsonRequest = mp.writeValueAsString(uptimeReq);
		mockMvc.perform(post("/api/v1/uptime").content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();

	}

	@Test
	public void getCheckPointsTest_200OK_ALL() throws Exception {

		final MvcResult result = mockMvc.perform(get("/api/v1/uptime").contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();
		final String response = result.getResponse().getContentAsString();

		final List<UptimeMonitoringObj> res = mp.readValue(response, mp.getTypeFactory().constructCollectionType(List.class, UptimeMonitoringObj.class));

		Assert.assertTrue(res.get(0).getUrl().equals("google.com"));
		Assert.assertTrue(result.getResponse().getStatus() == HttpStatus.OK.value());
	}

	@Test
	public void getCheckPointsTest_200OK_Freq() throws Exception {

		final MvcResult result = mockMvc.perform(get("/api/v1/uptime?filter=freq&&checkval=1").contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();
		final String response = result.getResponse().getContentAsString();

		final List<UptimeMonitoringObj> res = mp.readValue(response, mp.getTypeFactory().constructCollectionType(List.class, UptimeMonitoringObj.class));

		Assert.assertTrue(res.get(0).getUrl().equals("https://paypal.com"));
		Assert.assertTrue(result.getResponse().getStatus() == HttpStatus.OK.value());
	}

	@Test
	public void getCheckPointsTest_200OK_Name() throws Exception {

		final MvcResult result = mockMvc.perform(get("/api/v1/uptime?filter=name&&checkval=chk").contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();
		final String response = result.getResponse().getContentAsString();

		final List<UptimeMonitoringObj> res = mp.readValue(response, mp.getTypeFactory().constructCollectionType(List.class, UptimeMonitoringObj.class));

		Assert.assertTrue(res.get(0).getName().equals("chk1"));
		Assert.assertTrue(result.getResponse().getStatus() == HttpStatus.OK.value());
	}

	//@@@@@@@@@@@@POST For GET@@@@@@@@@@@@

	@Test
	public void getDetails4UrlTest_200OK() throws Exception {

		final UptimeUrl uptimeUrl = new UptimeUrl("google.com");

		final String jsonRequest = mp.writeValueAsString(uptimeUrl);

		final MvcResult result = mockMvc.perform(post("/api/v1/uptime/url").content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();
		final String response = result.getResponse().getContentAsString();

		final UptimeMonitoringDBObj res = mp.readValue(response, UptimeMonitoringDBObj.class);

		Assert.assertTrue(res.getName().equals("chk1"));
		Assert.assertTrue(result.getResponse().getStatus() == HttpStatus.OK.value());
	}

	@Test
	public void updateCheckPointTest_200OK() throws Exception {

		final UptimeReq uptimeReq = new UptimeReq("chk1", "https://google.com", (float) 5.0, "seconds");
		final String jsonRequest = mp.writeValueAsString(uptimeReq);

		final MvcResult result = mockMvc.perform(put("/api/v1/uptime/status/active").content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();
		Assert.assertTrue(result.getResponse().getStatus() == HttpStatus.OK.value());

	}

	@Test
	public void updateCheckPointTest_400OK() throws Exception {

		final UptimeReq uptimeReq = new UptimeReq("chk1", "https://google.com", (float) 5.0, "seconds");
		final String jsonRequest = mp.writeValueAsString(uptimeReq);

		final MvcResult result = mockMvc.perform(put("/api/v1/uptime/status/act").content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isBadRequest()).andReturn();
		Assert.assertTrue(result.getResponse().getStatus() == HttpStatus.BAD_REQUEST.value());

	}

}

package anode.tool.zabbix.api.service;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import anode.tool.zabbix.api.exception.ZabbixException;
import anode.tool.zabbix.api.model.Request;

public class ZabbixApiImpl implements ZabbixApi {
	private static final Logger logger = LoggerFactory.getLogger(ZabbixApiImpl.class);

	private CloseableHttpClient httpClient;

	private URI uri;

	private volatile String auth;

	public ZabbixApiImpl(String url) {
		try {
			uri = new URI(url.trim());
		} catch (URISyntaxException e) {
			throw new ZabbixException("url invalid", e);
		}
	}

	public ZabbixApiImpl(URI uri) {
		this.uri = uri;
	}

	public ZabbixApiImpl(String url, CloseableHttpClient httpClient) {
		this(url);
		this.httpClient = httpClient;
	}

	public ZabbixApiImpl(URI uri, CloseableHttpClient httpClient) {
		this(uri);
		this.httpClient = httpClient;
	}

	@Override
	public void init() {
		if (httpClient == null) {
			httpClient = HttpClients.custom().build();
		}
	}

	@Override
	public void destroy() {
		if (httpClient != null) {
			try {
				httpClient.close();
			} catch (Exception e) {
				logger.error("close httpclient error!", e);
			}
		}
	}

	@Override
	public boolean login(String user, String password) {
		this.auth = null;
		Request request = ZabbixRequestBuilder.newBuilder().paramEntry("user", user).paramEntry("password", password)
				.method("user.login").build();
		Map<String,Object> response = call(request);
		String tempAuth = (String) response.get(Request.RESULT);
		if (tempAuth != null && !tempAuth.isEmpty()) {
			this.auth = tempAuth;
			return true;
		}
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String,Object> call(Request request) {
	  if (request.getAuth() == null) {
		request.setAuth(this.auth);
	  }
	  HttpUriRequest httpRequest;
	  try {
		httpRequest =
		  RequestBuilder
			.post()
			.setUri(uri)
			.addHeader("Content-Type", "application/json")
			.setEntity(
			  new StringEntity(request.toJson(), ContentType.APPLICATION_JSON)
			)
			.build();
	  } catch (UnsupportedCharsetException | JsonProcessingException exception) {
		throw new ZabbixException("ZabbixApi call exception!", exception);
	  }
  
	  try (CloseableHttpResponse response = httpClient.execute(httpRequest)) {
		HttpEntity entity = response.getEntity();
		return new ObjectMapper().readValue(EntityUtils.toString(entity), Map.class);
	  } catch (IOException exception) {
		throw new ZabbixException("ZabbixApi call exception!", exception);
	  }
	}
  
	@Override
	public String apiVersion() {
	  Request request = ZabbixRequestBuilder
		.newBuilder()
		.method("apiinfo.version")
		.build();
	  Map<String,Object> response = call(request);
	  return (String) response.get(Request.RESULT);
	}
  
	public boolean hostgroupExists(String name) {
	  Request request = ZabbixRequestBuilder
		.newBuilder()
		.method("hostgroup.exists")
		.paramEntry("name", name)
		.build();
	  Map<String,Object> response = call(request);
	  return (boolean) response.get(Request.RESULT);
	}
  
	public boolean hostExists(String name) {
	  Request request = ZabbixRequestBuilder
		.newBuilder()
		.method("host.exists")
		.paramEntry("name", name)
		.build();
	  Map<String,Object> response = call(request);
	  return (boolean) response.get(Request.RESULT);
	}
  

	@SuppressWarnings("unchecked")
	public List<Object> getAlertsFromActionId(Integer actionId) {
	  Request request = ZabbixRequestBuilder
		.newBuilder()
		.method("alert.get")
		.paramEntry("actionids", actionId.toString())
		.paramEntry("output", "extends")
		.build();
	  Map<String,Object> response = call(request);
	  return (List<Object>) response.get(Request.RESULT);
	}
  
	@SuppressWarnings("unchecked")
	public List<Object>  getEvents() {
	  Request request = ZabbixRequestBuilder
		.newBuilder()
		.method("event.get")
		.paramEntry("output", "extend")
		.build();
	  Map<String,Object> response = call(request);
	  return (List<Object>) response.get(Request.RESULT);
	}
  }
  
  
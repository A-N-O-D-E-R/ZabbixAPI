package anode.tool.zabbix.api.service;
import java.util.Map;

import anode.tool.zabbix.api.model.Request;

public interface ZabbixApi {

	void init();

	void destroy();

	String apiVersion();

	Map<String,Object> call(Request request);

	boolean login(String user, String password);
}
package anode.tool.zabbix.api.service;

import java.util.concurrent.atomic.AtomicInteger;
import anode.tool.zabbix.api.model.Request;

/**
 * Builder to Simplify the jsonrpc-2 request construction (anode.tool.zabbix.api.model.Request)
 */
public class ZabbixRequestBuilder {
    private static final AtomicInteger nextId = new AtomicInteger(1);

	private Request request = new Request();
	
	private ZabbixRequestBuilder(){}
	
	static public ZabbixRequestBuilder newBuilder(){
		return new ZabbixRequestBuilder();
	}
	
	public Request build(){
		if(request.getId() == null){
			request.setId(nextId.getAndIncrement());
		}
		return request;
	}
	
    /**
     * set the Jsonrpc version should be 2 for zabbix 
     * @param id
	 * @return
	 */
	public ZabbixRequestBuilder version(String version){
		request.setJsonrpc(version);
		return this;
	}
	/**
     * Add the body of the request to nest the value add Map<String,Object> as value
     * @param Key
     * @param Value
	 * @return
	 */
	public ZabbixRequestBuilder paramEntry(String key, Object value){
		request.putParam(key, value);
		return this;
	}
	
	/**
	 * Do not necessary to call this method.If don not set id, ZabbixApi will auto set request auth.. 
	 * @param auth
	 * @return RequestBuilder
	 */
	public ZabbixRequestBuilder auth(String auth){
		request.setAuth(auth);
		return this;
	}
	
	public ZabbixRequestBuilder method(String method){
		request.setMethod(method);
		return this;
	}
	
	/**
	 * Do not necessary to call this method.If don not set id, RequestBuilder will auto generate.
	 * @param id
	 * @return
	 */
	public ZabbixRequestBuilder id(Integer id){
		request.setId(id);
		return this;
	}
}
package anode.tool.zabbix.api.exception;

public class ZabbixException extends RuntimeException{
    public ZabbixException(String msg){
        super(msg);
    }

    public ZabbixException(String msg, Throwable cause){
        super(msg, cause);
    }
}

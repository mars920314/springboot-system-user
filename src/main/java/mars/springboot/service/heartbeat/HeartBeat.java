package mars.springboot.service.heartbeat;

import java.util.Date;
import java.util.List;

enum CodeEnum {
	success(1), warning(2), error(2), RunTimeError(-1);

	private int code;

	CodeEnum(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}

public class HeartBeat {

	public static final long warningTime = Long.MAX_VALUE;
	public static final long errorTime = Long.MAX_VALUE;

	private int status;
	private String message;
	private long timestamp;
	public List<HeartBeat> services;
	public String service;

	public HeartBeat() {

	}

	public HeartBeat(List<HeartBeat> services) {
		this.services = services;
		status = CodeEnum.success.getCode();
		message = "";
		timestamp = 0;
	}

	public HeartBeat(String service, int status, String message, long timestamp) {
		this.service = service;
		this.status = status;
		this.message = message;
		this.timestamp = timestamp;
	}

	public void collectStatus() {
		// update heartBeat status
		for (HeartBeat service : this.services) {
			if (service.getStatus() > this.status)
				this.status = service.getStatus();
		}
		// update message
		if (this.status == CodeEnum.success.getCode()) {
			this.message = "OK";
		} else if (this.status == CodeEnum.warning.getCode()) {
			this.message = "WARN: program has risk";
		} else if (this.status == CodeEnum.error.getCode()) {
			this.message = "ERROR: program is down";
		}
		this.timestamp = new Date().getTime();
        this.service = "heartbeat";
	}

	public void checkStatus() {
		long diff = new Date().getTime() - timestamp;
		if (diff < warningTime) {
			this.status = CodeEnum.success.getCode();
			this.message = "OK";
		} else if (diff < errorTime && diff >= warningTime) {
			this.status = CodeEnum.warning.getCode();
			this.message = "Warning: no update in " + warningTime + "s";
		} else if (diff >= errorTime) {
			this.status = CodeEnum.error.getCode();
			this.message = "Error: no update in " + errorTime + "s";
		}
	}

	public String getService() {
		return this.service;
	}

	public void setServices(String service) {
		this.service = service;
	}

	public List<HeartBeat> getServices() {
		return this.services;
	}

	public void setServices(List<HeartBeat> services) {
		this.services = services;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}

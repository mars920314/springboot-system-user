package mars.springboot.service.heartbeat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HeartBeatService {
	Logger logger = LoggerFactory.getLogger(getClass());

	public static Map<String, HeartBeat> serviceMap = new HashMap<String, HeartBeat>();

	/**
	 * service invoke to record timestamp
	 */
	public static void updateServiceTime(HeartBeat heartbeat) {
		heartbeat.checkStatus();
		serviceMap.put(heartbeat.getService(), heartbeat);
	}

	public static void updateServiceTime(String serviceName, long timestamp) {
		HeartBeat heartbeat = new HeartBeat(serviceName, CodeEnum.success.getCode(), "", timestamp);
		updateServiceTime(heartbeat);
	}

	/**
	 * run scheduled to get responds for services
	 */
	public static void requestService(){
		List<String> serviceNames = new ArrayList<String>();
		for (String serviceName : serviceNames) {
			HeartBeat heartbeat = null;
			try {
				boolean responds = true;	//judge if serviceName has responds
				if(responds)
					heartbeat = new HeartBeat(serviceName, CodeEnum.success.getCode(), "Success: get responds for service " + serviceNames, new Date().getTime());
				else
					heartbeat = new HeartBeat(serviceName, CodeEnum.error.getCode(), "Error: no responds for service " + serviceNames, new Date().getTime());
			} catch (Exception e) {
				heartbeat = new HeartBeat(serviceName, CodeEnum.RunTimeError.getCode(), "Error: get service exception", new Date().getTime());
				e.printStackTrace();
			}
			updateServiceTime(heartbeat);
		}
	}
	
	public static HeartBeat loadHeartBeat() {
		HeartBeat heartBeatModel = new HeartBeat(new ArrayList<HeartBeat>(serviceMap.values()));
		heartBeatModel.collectStatus();
		return heartBeatModel;
	}

}

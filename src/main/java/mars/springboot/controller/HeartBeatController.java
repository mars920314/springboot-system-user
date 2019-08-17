package mars.springboot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import mars.springboot.service.heartbeat.HeartBeatService;

@RestController
public class HeartBeatController {
	Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(value="/heartbeat", method = RequestMethod.GET)
    public Object getHeartBeat() {
        return HeartBeatService.loadHeartBeat();
    }

}

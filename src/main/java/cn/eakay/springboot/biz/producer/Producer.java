package cn.eakay.springboot.biz.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by magic~ on 2018/4/25.
 */
@Component
@EnableScheduling
public class Producer
{

    @Autowired
    private JmsTemplate jmsQueueTemplate;
    @Autowired
    private JmsTemplate jmsTopicTemplate;

    private static int count= 0;

    @Scheduled(fixedDelay=3000)
    public void send(){
        jmsQueueTemplate.convertAndSend("queue","hi.activeMQ,index=="+count);
        jmsTopicTemplate.convertAndSend("VirtualTopic.topic","hi,activeMQ( topic )，index="+count++);

    }
}

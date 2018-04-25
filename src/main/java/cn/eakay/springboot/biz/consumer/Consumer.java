package cn.eakay.springboot.biz.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Created by magic~ on 2018/4/25.
 */
@Component
public class Consumer
{
    private  final Logger logger = LoggerFactory.getLogger(this.getClass());

    @JmsListener(destination = "Consumer.A.VirtualTopic.topic",containerFactory="jmsQueueListener")
    public void receiveTopic(String text){
        logger.info("Consumer.A:"+text);
    }

    @JmsListener(destination = "Consumer.B.VirtualTopic.topic",containerFactory="jmsQueueListener")
    public void receiveTopic2(String text){
        logger.info("Consumer.B:"+text);
    }
    @JmsListener(destination = "queue",containerFactory="jmsQueueListener")
    public void reviceQueue(String text){
        logger.info("Queue Consumer:"+text);
    }
}

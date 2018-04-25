package cn.eakay.springboot.config;


import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Queue;
import javax.jms.Topic;

/**
 * Created by magic~ on 2018/4/24.
 */
@Configuration
public class ActiveMQConfig
{

    @Bean
    public Queue productActiveMQQueue(){
        return new ActiveMQQueue("queue");
    }

    @Bean
    public Topic topic(){
        return new ActiveMQTopic("topic");
    }


    @Bean
    public RedeliveryPolicy redeliveryPolicy(){

        RedeliveryPolicy  redeliveryPolicy=   new RedeliveryPolicy();
        //是否在每次尝试重新发送失败后,增长这个等待时间
        redeliveryPolicy.setUseExponentialBackOff(true);
        //重发次数,默认为6次   这里设置为6次
        redeliveryPolicy.setMaximumRedeliveries(6);
        //重发时间间隔,默认为1秒
        redeliveryPolicy.setInitialRedeliveryDelay(15000);
        //第一次失败后重新发送之前等待500毫秒,第二次失败再等待500 * 2毫秒,这里的3就是value
        redeliveryPolicy.setBackOffMultiplier(3);
        //是否避免消息碰撞
        redeliveryPolicy.setUseCollisionAvoidance(false);
        //设置重发最大拖延时间-1 表示没有拖延只有UseExponentialBackOff(true)为true时生效
        redeliveryPolicy.setMaximumRedeliveryDelay(-1);

        redeliveryPolicy.setQueue("*");

        redeliveryPolicy.setTopic("*");

        return redeliveryPolicy;
    }




    @Bean//(name="activeMQConnectionFactory")
    public ActiveMQConnectionFactory activeMQConnectionFactory (@Value("${activemq.url}")String url,@Value("${activemq.username}")String username, @Value("${activemq.password}")String password,  RedeliveryPolicy redeliveryPolicy){
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(username, password, url);
        activeMQConnectionFactory.setRedeliveryPolicy(redeliveryPolicy);
        return activeMQConnectionFactory;
    }

/*

    @Bean(name="cachingConnectionFactory")
    @Primary
    public CachingConnectionFactory  cachingConnectionFactory(@Qualifier("activeMQConnectionFactory")ActiveMQConnectionFactory activeMQConnectionFactory )
    {
        CachingConnectionFactory cachingConnectionFactory=new CachingConnectionFactory(activeMQConnectionFactory);
        return cachingConnectionFactory;
    }
*/
     @Bean
    public PooledConnectionFactory  pooledConnectionFactory(ActiveMQConnectionFactory activeMQConnectionFactory)
    {
          PooledConnectionFactory  pooledConnectionFactory=new PooledConnectionFactory();
          pooledConnectionFactory.setConnectionFactory(activeMQConnectionFactory);
          pooledConnectionFactory.setMaxConnections(50);
          return pooledConnectionFactory;
    }


    @Bean(name="jmsQueueTemplate")
    public JmsTemplate jmsQueueTemplate(PooledConnectionFactory  pooledConnectionFactory, Queue queue){
        JmsTemplate jmsTemplate=new JmsTemplate();
        jmsTemplate.setDeliveryMode(2);//进行持久化配置 1表示非持久化，2表示持久化</span>
        jmsTemplate.setConnectionFactory(pooledConnectionFactory);
        jmsTemplate.setDefaultDestination(queue); //此处可不设置默认，在发送消息时也可设置队列
       // jmsTemplate.setSessionAcknowledgeModeName();
        jmsTemplate.setSessionAcknowledgeMode(4);//客户端签收模式</span>
         return jmsTemplate;
    }

    @Bean(name="jmsTopicTemplate")
    public JmsTemplate jmsTopicTemplate(PooledConnectionFactory  pooledConnectionFactory, Topic topic){
        JmsTemplate jmsTemplate=new JmsTemplate();
        jmsTemplate.setDeliveryMode(2);//进行持久化配置 1表示非持久化，2表示持久化</span>
        jmsTemplate.setConnectionFactory(pooledConnectionFactory);
        jmsTemplate.setDefaultDestination(topic); //此处可不设置默认，在发送消息时也可设置队列
        jmsTemplate.setPubSubDomain(true);
        // jmsTemplate.setSessionAcknowledgeModeName();
        jmsTemplate.setSessionAcknowledgeMode(4);//客户端签收模式</span>
        return jmsTemplate;
    }

    //定义一个消息监听器连接工厂，使用VirtualTopic这里直接定义的是点对点模式的监听器连接工厂
    @Bean(name = "jmsQueueListener")
    public DefaultJmsListenerContainerFactory jmsQueueListenerContainerFactory(PooledConnectionFactory  pooledConnectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(pooledConnectionFactory);
        //设置连接数
        factory.setConcurrency("1-2");
        //重连间隔时间
        factory.setRecoveryInterval(1000L);
        factory.setSessionAcknowledgeMode(4);
        return factory;
    }


}

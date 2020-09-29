package uk.gorodny.sparemq;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class JmsConfigurator {

  @Value("${ibm.mq.receive-timeout}")
  private String jmsReceiveTimeout;

  public JmsTemplate configureJmsTemplate(JmsProps jmsProps) {
    MQQueueConnectionFactory mqQueueConnectionFactory = mqQueueConnectionFactory(
        jmsProps.getHost(),
        jmsProps.getPort(),
        jmsProps.getQueueManager(),
        jmsProps.getChannel());
    UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter = userCredentialsConnectionFactoryAdapter(
        mqQueueConnectionFactory,
        jmsProps.getUser(),
        jmsProps.getPassword());
    CachingConnectionFactory cachingConnectionFactory = cachingConnectionFactory(userCredentialsConnectionFactoryAdapter);
    JmsTemplate jmsTemplate = new JmsTemplate(cachingConnectionFactory);
    long receiveTimeout = Duration.parse(jmsReceiveTimeout).toMillis();
    jmsTemplate.setReceiveTimeout(receiveTimeout);
    jmsTemplate.setConnectionFactory(cachingConnectionFactory);
    return jmsTemplate;
  }

  private MQQueueConnectionFactory mqQueueConnectionFactory(String host,
                                                            int port,
                                                            String queueManager,
                                                            String channel) {
    MQQueueConnectionFactory mqQueueConnectionFactory = new MQQueueConnectionFactory();
    try {
      mqQueueConnectionFactory.setHostName(host);
      mqQueueConnectionFactory.setPort(port);
      mqQueueConnectionFactory.setQueueManager(queueManager);
      mqQueueConnectionFactory.setChannel(channel);
      mqQueueConnectionFactory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
      mqQueueConnectionFactory.setCCSID(1208);
    } catch (Exception e) {
      throw new RuntimeException(String.format("Cannot connect to message broker with creds:\n" +
              "host - %s\n" +
              "port - %s\n" +
              "queue manager - %s\n" +
              "channel - %s",
          host, port, queueManager, channel));
    }
    return mqQueueConnectionFactory;
  }

  private UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter(
      MQQueueConnectionFactory mqQueueConnectionFactory,
      String user,
      String password) {
    UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter = new UserCredentialsConnectionFactoryAdapter();
    userCredentialsConnectionFactoryAdapter.setTargetConnectionFactory(mqQueueConnectionFactory);
    userCredentialsConnectionFactoryAdapter.setUsername(user);
    userCredentialsConnectionFactoryAdapter.setPassword(password);
    return userCredentialsConnectionFactoryAdapter;
  }


  private CachingConnectionFactory cachingConnectionFactory(UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter) {
    CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
    cachingConnectionFactory.setTargetConnectionFactory(userCredentialsConnectionFactoryAdapter);
    cachingConnectionFactory.setSessionCacheSize(500);
    cachingConnectionFactory.setReconnectOnException(true);
    return cachingConnectionFactory;
  }
}

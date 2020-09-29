package uk.gorodny.sparemq;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class PrimaryJmsProps implements JmsProps {

  @Value("${ibm.mq.primary.host}")
  private String host;
  @Value("${ibm.mq.primary.port}")
  private Integer port;
  @Value("${ibm.mq.primary.queueManager}")
  private String queueManager;
  @Value("${ibm.mq.primary.channel}")
  private String channel;
  @Value("${ibm.mq.primary.user}")
  private String user;
  @Value("${ibm.mq.primary.password}")
  private String password;
}

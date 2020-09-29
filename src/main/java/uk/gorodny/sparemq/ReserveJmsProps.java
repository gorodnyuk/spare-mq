package uk.gorodny.sparemq;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ReserveJmsProps implements JmsProps {

  @Value("${ibm.mq.reserve.host}")
  private String host;
  @Value("${ibm.mq.reserve.port}")
  private Integer port;
  @Value("${ibm.mq.reserve.queueManager}")
  private String queueManager;
  @Value("${ibm.mq.reserve.channel}")
  private String channel;
  @Value("${ibm.mq.reserve.user}")
  private String user;
  @Value("${ibm.mq.reserve.password}")
  private String password;
}

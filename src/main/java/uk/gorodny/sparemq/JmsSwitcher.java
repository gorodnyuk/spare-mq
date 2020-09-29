package uk.gorodny.sparemq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JmsSwitcher {

  private final JmsConfigurator jmsConfigurator;
  private final PrimaryJmsProps primaryJmsProps;
  private final ReserveJmsProps reserveJmsProps;

  private JmsTemplate primaryJmsTemplate;

  private JmsTemplate reserveJmsTemplate;

  public JmsTemplate getPrimaryJmsTemplate() {
    if (primaryJmsTemplate == null) {
      log.info("Configuration primary message broker with creds:\n" +
              "host: {}, port: {}, queue manager: {}, channel: {}",
          primaryJmsProps.getHost(),
          primaryJmsProps.getPort(),
          primaryJmsProps.getQueueManager(),
          primaryJmsProps.getChannel());
      primaryJmsTemplate = jmsConfigurator.configureJmsTemplate(primaryJmsProps);
    }
    return primaryJmsTemplate;
  }

  public JmsTemplate swap(JmsTemplate jmsTemplate) {
    if (reserveJmsTemplate == null) {
      log.info("Configuration reserved message broker with creds:\n" +
              "host: {}, port: {}, queue manager: {}, channel: {}",
          reserveJmsProps.getHost(),
          reserveJmsProps.getPort(),
          reserveJmsProps.getQueueManager(),
          reserveJmsProps.getChannel());
      reserveJmsTemplate = jmsConfigurator.configureJmsTemplate(reserveJmsProps);
    }
    if (jmsTemplate.equals(primaryJmsTemplate)) {
      log.info("Primary message broker with creds:\n" +
              "host: {}, port: {}, queue manager: {}, channel: {}\n" +
              "will changed to reserved message broker with creds:\n" +
              "host: {}, port: {}, queue manager: {}, channel: {}",
          primaryJmsProps.getHost(),
          primaryJmsProps.getPort(),
          primaryJmsProps.getQueueManager(),
          primaryJmsProps.getChannel(),
          reserveJmsProps.getHost(),
          reserveJmsProps.getPort(),
          reserveJmsProps.getQueueManager(),
          reserveJmsProps.getChannel());
      return reserveJmsTemplate;
    }
    if (jmsTemplate.equals(reserveJmsTemplate)) {
      log.info("Reserved message broker with creds:\n" +
              "host: {}, port: {}, queue manager: {}, channel: {}\n" +
              "will changed to primary message broker with creds:\n" +
              "host: {}, port: {}, queue manager: {}, channel: {}",
          reserveJmsProps.getHost(),
          reserveJmsProps.getPort(),
          reserveJmsProps.getQueueManager(),
          reserveJmsProps.getChannel(),
          primaryJmsProps.getHost(),
          primaryJmsProps.getPort(),
          primaryJmsProps.getQueueManager(),
          primaryJmsProps.getChannel());
      return primaryJmsTemplate;
    }
    return null;
  }
}

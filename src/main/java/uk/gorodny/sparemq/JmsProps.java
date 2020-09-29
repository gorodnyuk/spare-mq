package uk.gorodny.sparemq;

public interface JmsProps {

  String getHost();

  Integer getPort();

  String getQueueManager();

  String getChannel();

  String getUser();

  String getPassword();
}

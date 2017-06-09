package net.joaopeixoto.streams;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;



@Component
public class StreamDefinition implements SmartLifecycle, HealthIndicator {

    private static final Logger log = LoggerFactory.getLogger(StreamDefinition.class);

    private final KStreamBuilder builder;
    private final KStream<String, String> source;
    private final KafkaStreams stream;

    public StreamDefinition() {
        this.builder = new KStreamBuilder();
        this.source = builder.stream(Serdes.String(), Serdes.String(), "demotopic");

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "demo-stream");
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "demo-stream");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 2);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 200);


        source.foreach((key, value) -> {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                // noop
            }
            log.debug("Received: {}={}", key, value);
        });

        this.stream = new KafkaStreams(builder, props);
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public void start() {
        stream.start();
    }

    @Override
    public void stop() {
        stream.close();
    }

    @Override
    public boolean isRunning() {
        return stream != null && stream.state().isRunning();
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Health health() {
        return stream.state().isRunning() ? Health.up().build() : Health.down().build();
    }
}

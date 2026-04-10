package rs.yettelbank.notificationservice.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import rs.yettelbank.notificationservice.event.TransactionEventDTO;
import rs.yettelbank.notificationservice.event.TransactionEventDTO.TransactionType;
import java.math.BigDecimal;

@Component
public class TransactionEventListener {

    private static final Logger logger = LoggerFactory.getLogger(TransactionEventListener.class);

    @KafkaListener(topics = "bank-transactions", groupId = "notification-group")
    public void handleTransactionEvent(TransactionEventDTO event) {
       logger.info("Received transaction event: {}", event);


        if (event.getType()== TransactionType.WITHDRAWAL && event.getAmount().compareTo(new BigDecimal("10000")) > 0) {
            logger.info("Sending notification for withdrawal of {}", event.getAmount());
        }
    }
}
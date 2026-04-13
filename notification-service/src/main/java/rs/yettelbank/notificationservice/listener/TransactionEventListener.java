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
       switch (event.getType()) {
           case DEPOSIT:
               logger.info("Deposit of {} to account with ID {} received.", event.getAmount(), event.getAccountId());
               break;
           case WITHDRAWAL:
               logger.info("Withdrawal of {} from account with ID {} received.", event.getAmount(), event.getAccountId());
               break;
           case TRANSFER:
               logger.info("Transfer of {} from account with ID {} received.", event.getAmount(), event.getAccountId());
               break;
           default:
               logger.warn("Unknown transaction type: {}", event.getType());
               break;
       }
    }
}
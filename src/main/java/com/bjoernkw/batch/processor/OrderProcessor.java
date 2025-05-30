package com.bjoernkw.batch.processor;

import com.bjoernkw.batch.model.Order;
import com.bjoernkw.batch.model.ProcessedOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class OrderProcessor implements ItemProcessor<Order, ProcessedOrder> {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderProcessor.class);

    @Override
    public ProcessedOrder process(Order order) throws Exception {
        logger.debug("Processing order: {}", order);
        
        // Validation
        if (order.getQuantity() == null || order.getQuantity() <= 0) {
            logger.warn("Skipping order with invalid quantity: {}", order);
            return null; // Skip this item
        }
        
        if (order.getUnitPrice() == null || order.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Skipping order with invalid unit price: {}", order);
            return null; // Skip this item
        }

        // Transform the order
        ProcessedOrder processedOrder = new ProcessedOrder();
        processedOrder.setOriginalOrderId(order.getId());
        processedOrder.setCustomerId(order.getCustomerId());
        processedOrder.setProductName(order.getProductName());
        processedOrder.setQuantity(order.getQuantity());
        processedOrder.setUnitPrice(order.getUnitPrice());
        processedOrder.setOrderDate(order.getOrderDate());

        // Calculate amounts
        BigDecimal totalAmount = order.getUnitPrice().multiply(BigDecimal.valueOf(order.getQuantity()));
        processedOrder.setTotalAmount(totalAmount);

        // Apply discount based on total amount
        BigDecimal discountRate = calculateDiscountRate(totalAmount);
        BigDecimal discountAmount = totalAmount.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal finalAmount = totalAmount.subtract(discountAmount);

        processedOrder.setDiscountApplied(discountRate.multiply(BigDecimal.valueOf(100))); // Store as percentage
        processedOrder.setFinalAmount(finalAmount);
        processedOrder.setStatus("PROCESSED");

        logger.debug("Processed order: {}", processedOrder);
        return processedOrder;
    }

    private BigDecimal calculateDiscountRate(BigDecimal totalAmount) {
        if (totalAmount.compareTo(BigDecimal.valueOf(1000)) >= 0) {
            return BigDecimal.valueOf(0.15); // 15% discount for orders >= $1000
        } else if (totalAmount.compareTo(BigDecimal.valueOf(500)) >= 0) {
            return BigDecimal.valueOf(0.10); // 10% discount for orders >= $500
        } else if (totalAmount.compareTo(BigDecimal.valueOf(100)) >= 0) {
            return BigDecimal.valueOf(0.05); // 5% discount for orders >= $100
        } else {
            return BigDecimal.ZERO; // No discount for orders < $100
        }
    }
}

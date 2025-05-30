package com.bjoernkw.batch.processor;

import com.bjoernkw.batch.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class CustomerProcessor implements ItemProcessor<Customer, Customer> {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerProcessor.class);

    @Override
    public Customer process(Customer customer) throws Exception {
        logger.debug("Processing customer: {}", customer);
        
        // Validation
        if (customer.getFirstName() == null || customer.getFirstName().trim().isEmpty()) {
            logger.warn("Skipping customer with empty first name: {}", customer);
            return null; // Skip this item
        }
        
        if (customer.getAge() != null && customer.getAge() < 0) {
            logger.warn("Skipping customer with negative age: {}", customer);
            return null; // Skip this item
        }

        // Transform the customer
        Customer processedCustomer = new Customer();
        processedCustomer.setId(customer.getId());
        processedCustomer.setFirstName(capitalizeFirstLetter(customer.getFirstName()));
        processedCustomer.setLastName(capitalizeFirstLetter(customer.getLastName()));
        processedCustomer.setEmail(customer.getEmail().toLowerCase());
        processedCustomer.setAge(customer.getAge());
        
        // Set status based on age
        if (customer.getAge() != null && customer.getAge() >= 65) {
            processedCustomer.setStatus("SENIOR");
        } else if (customer.getAge() != null && customer.getAge() >= 18) {
            processedCustomer.setStatus("ADULT");
        } else {
            processedCustomer.setStatus("MINOR");
        }

        logger.debug("Processed customer: {}", processedCustomer);
        return processedCustomer;
    }

    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}

package com.bjoernkw.batch.config;

import com.bjoernkw.batch.listener.JobCompletionListener;
import com.bjoernkw.batch.model.Customer;
import com.bjoernkw.batch.model.Order;
import com.bjoernkw.batch.model.ProcessedOrder;
import com.bjoernkw.batch.processor.CustomerProcessor;
import com.bjoernkw.batch.processor.OrderProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final CustomerProcessor customerProcessor;
    private final OrderProcessor orderProcessor;
    private final JobCompletionListener jobCompletionListener;

    public BatchConfig(JobRepository jobRepository,
                      PlatformTransactionManager transactionManager,
                      CustomerProcessor customerProcessor,
                      OrderProcessor orderProcessor,
                      JobCompletionListener jobCompletionListener) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.customerProcessor = customerProcessor;
        this.orderProcessor = orderProcessor;
        this.jobCompletionListener = jobCompletionListener;
    }

    // ======================== CSV Job Configuration ========================

    @Bean
    public FlatFileItemReader<Customer> csvReader() {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("csvReader")
                .resource(new ClassPathResource("input/customers.csv"))
                .delimited()
                .names("firstName", "lastName", "email", "age")
                .linesToSkip(1) // Skip header
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Customer>() {{
                    setTargetType(Customer.class);
                }})
                .build();
    }

    @Bean
    public FlatFileItemWriter<Customer> csvWriter() {
        BeanWrapperFieldExtractor<Customer> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"firstName", "lastName", "email", "age", "status"});

        DelimitedLineAggregator<Customer> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        return new FlatFileItemWriterBuilder<Customer>()
                .name("csvWriter")
                .resource(new FileSystemResource("output/processed_customers.csv"))
                .lineAggregator(lineAggregator)
                .headerCallback(writer -> writer.write("FirstName,LastName,Email,Age,Status"))
                .build();
    }

    @Bean
    public Step csvProcessingStep() {
        return new StepBuilder("csvProcessingStep", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(csvReader())
                .processor(customerProcessor)
                .writer(csvWriter())
                .faultTolerant()
                .skipLimit(5)
                .skip(Exception.class)
                .build();
    }

    @Bean
    public Job csvProcessingJob() {
        return new JobBuilder("csvProcessingJob", jobRepository)
                .listener(jobCompletionListener)
                .start(csvProcessingStep())
                .build();
    }

    // ======================== Database Job Configuration ========================

    @Bean
    public JdbcCursorItemReader<Order> databaseReader(@Qualifier("mysqlDataSource") DataSource dataSource) {
        JdbcCursorItemReader<Order> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT id, customer_id, product_name, quantity, unit_price, order_date, status FROM orders WHERE status = 'PENDING'");
        reader.setRowMapper(new BeanPropertyRowMapper<>(Order.class));
        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<ProcessedOrder> databaseWriter(@Qualifier("postgresqlDataSource") DataSource dataSource) {
        JdbcBatchItemWriter<ProcessedOrder> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql("INSERT INTO processed_orders (original_order_id, customer_id, product_name, quantity, " +
                     "unit_price, total_amount, discount_applied, final_amount, order_date, processed_date, status) " +
                     "VALUES (:originalOrderId, :customerId, :productName, :quantity, :unitPrice, :totalAmount, " +
                     ":discountApplied, :finalAmount, :orderDate, :processedDate, :status)");
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        return writer;
    }

    @Bean
    public Step databaseProcessingStep(@Qualifier("mysqlDataSource") DataSource mysqlDataSource,
                                     @Qualifier("postgresqlDataSource") DataSource postgresqlDataSource) {
        return new StepBuilder("databaseProcessingStep", jobRepository)
                .<Order, ProcessedOrder>chunk(10, transactionManager)
                .reader(databaseReader(mysqlDataSource))
                .processor(orderProcessor)
                .writer(databaseWriter(postgresqlDataSource))
                .faultTolerant()
                .skipLimit(10)
                .skip(Exception.class)
                .retryLimit(3)
                .retry(Exception.class)
                .build();
    }

    @Bean
    public Job databaseProcessingJob(@Qualifier("mysqlDataSource") DataSource mysqlDataSource,
                                   @Qualifier("postgresqlDataSource") DataSource postgresqlDataSource) {
        return new JobBuilder("databaseProcessingJob", jobRepository)
                .listener(jobCompletionListener)
                .start(databaseProcessingStep(mysqlDataSource, postgresqlDataSource))
                .build();
    }
}

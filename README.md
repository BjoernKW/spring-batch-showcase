# Spring Batch Showcase

A comprehensive Spring Boot application demonstrating Spring Batch capabilities with two distinct jobs:

1. **CSV Processing Job**: Reads customer data from CSV, processes it, and writes to another CSV
2. **Database Processing Job**: Reads orders from MySQL, transforms them, and saves to PostgreSQL

## Features

- ✅ CSV file processing (read/write)
- ✅ Database-to-database processing (MySQL → PostgreSQL)
- ✅ Data transformation and validation
- ✅ Comprehensive error handling and skip policies
- ✅ Job monitoring and status endpoints
- ✅ Configurable chunk processing
- ✅ Retry mechanisms
- ✅ Detailed logging and metrics

## Quick Start

### Prerequisites
- Java 21+
- Maven 3.6+
- Docker & Docker Compose

### 1. Start Databases
```bash
docker compose up -d
```

### 2. Run Application
```bash
mvn spring-boot:run
```

### 3. Execute Jobs
```bash
# Run CSV processing job
curl http://localhost:8080/api/jobs/csv

# Run database processing job
curl http://localhost:8080/api/jobs/database

# Run both jobs
curl http://localhost:8080/api/jobs/all

# Check job status
curl http://localhost:8080/api/batch/status
```

## API Endpoints

- `GET /api/jobs/csv` - Launch CSV processing job
- `GET /api/jobs/database` - Launch database processing job
- `GET /api/jobs/all` - Launch both jobs
- `GET /api/batch/status` - Get job execution status
- `GET /api/batch/health` - Application health check

## Configuration

Key configurations can be modified in `application.yml`:

- Chunk size: `app.batch.chunk-size`
- Skip limit: `app.batch.skip-limit`
- Retry limit: `app.batch.retry-limit`
- Database connections
- File paths

## Sample Data

- **Input CSV**: `src/main/resources/input/customers.csv`
- **Output CSV**: `output/processed_customers.csv`
- **MySQL Orders**: Sample orders inserted via schema
- **PostgreSQL**: Processed orders with calculated discounts

## Error Handling

The application includes comprehensive error handling:

- **Skip Policy**: Skip invalid records up to configured limit
- **Retry Logic**: Retry failed operations up to 3 times
- **Validation**: Data validation with meaningful error messages
- **Logging**: Detailed logging at all levels

## Monitoring

- Spring Boot Actuator endpoints enabled
- Custom job completion listeners
- Detailed execution metrics
- File-based logging with rotation

## Architecture

```
┌─────────────┐    ┌──────────────┐    ┌─────────────┐
│   CSV File  │───▶│ Spring Batch │───▶│  CSV File   │
│  (Input)    │    │   Job #1     │    │  (Output)   │
└─────────────┘    └──────────────┘    └─────────────┘

┌─────────────┐    ┌──────────────┐    ┌─────────────┐
│   MySQL     │───▶│ Spring Batch │───▶│ PostgreSQL  │
│  (Orders)   │    │   Job #2     │    │(Processed)  │
└─────────────┘    └──────────────┘    └─────────────┘
```

## Development

To extend the application:

1. Add new model classes in `model/` package
2. Create processors in `processor/` package
3. Configure jobs in `BatchConfig.java`
4. Add endpoints in `JobLauncherService.java`

## Testing

```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report
```
```
src/
├── main/
│   ├── java/com/example/batch/
│   │   ├── SpringBatchApplication.java
│   │   ├── config/
│   │   │   ├── BatchConfig.java
│   │   │   └── DatabaseConfig.java
│   │   ├── model/
│   │   │   ├── Customer.java
│   │   │   ├── Order.java
│   │   │   └── ProcessedOrder.java
│   │   ├── processor/
│   │   │   ├── CustomerProcessor.java
│   │   │   └── OrderProcessor.java
│   │   ├── listener/
│   │   │   └── JobCompletionListener.java
│   │   └── service/
│   │       └── JobLauncherService.java
│   └── resources/
│       ├── application.yml
│       ├── schema-mysql.sql
│       ├── schema-postgresql.sql
│       └── input/
│           └── customers.csv
└── pom.xml

## License

[MIT License](https://opensource.org/licenses/MIT)

## Authors

* **[Björn Wilmsmann](https://bjoernkw.com)**

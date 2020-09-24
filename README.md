# Customer Statement Processor Spring-Batch-Service
Statement Processor is a Spring Batch service which reads incoming csv/xml file based on a scheduler job
to process monthly statements of a customer.
- It has 2 jobs enabled based on the files placed in input folder (either csv/xml or both and independent of the filenames)
- Tasklet based processing is applied for CSV processing Job
- Chunk based processing is applied for XML Processing Job
- These can be enabled/disabled through yml properties 

# Application Start up
- App can be started with StatementProcessor
- After context initialization, batch jobs will be launched from StatementProcessJobLauncher based on 
  the scheduled time interval from properties

Technical Info
--
 - Spring Boot 2.3.3 (JDK 11)
 - Maven 3.6.1
 - Spring-Batch
 - Mockito Junit
 - In-Memory (H2 Database)
 
Database Console
--
 - By using below h2 database console, we can see the job details
 - localhost:8080/h2-console
 - In-memory database name is 'statementprocessor' and username as 'sa' and password as empty
 

Running the Service locally
--
Manual:
 - Clone the repository from github 'https://github.com/SarathYRMS/statement-processor.git'
 - mvn clean install
 - Run the spring boot app from target 'java -jar statement-processor-0.0.1-SNAPSHOT.jar'
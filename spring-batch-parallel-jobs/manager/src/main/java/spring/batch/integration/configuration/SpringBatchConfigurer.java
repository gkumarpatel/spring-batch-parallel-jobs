package spring.batch.integration.configuration;

import javax.sql.DataSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SpringBatchConfigurer extends DefaultBatchConfigurer {

  private final DataSource dataSource;
  private final PlatformTransactionManager transactionManager;

  @Bean
  public TaskExecutor springBatchTaskExecutor() {
    SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("spring-batch-");
    taskExecutor.setConcurrencyLimit(5);
    return taskExecutor;
  }

  @Override
  @Bean
  public JobLauncher getJobLauncher() {
    try {
      SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
      jobLauncher.setJobRepository(getJobRepository());
      jobLauncher.setTaskExecutor(springBatchTaskExecutor());
      jobLauncher.afterPropertiesSet();
      return jobLauncher;

    } catch (Exception ex) {
      log.error("Can't load SimpleJobLauncher with SimpleAsyncTaskExecutor: {}", ex.getMessage());
      throw new RuntimeException("Can't load SimpleJobLauncher with SimpleAsyncTaskExecutor: " + ex.getMessage(), ex);
    }
  }

  @Override
  @Bean
  protected JobRepository createJobRepository() throws Exception {
    JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
    factory.setDataSource(dataSource);
    factory.setTransactionManager(transactionManager);
    factory.setIsolationLevelForCreate("ISOLATION_REPEATABLE_READ");
    return factory.getObject();
  }
}

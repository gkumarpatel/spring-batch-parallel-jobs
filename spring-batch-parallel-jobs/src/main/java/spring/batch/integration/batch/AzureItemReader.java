package spring.batch.integration.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AzureItemReader { //implements ItemStreamReader<InvoiceLineItem> {
//  @Value("#{stepExecution}")
//  private StepExecution stepExecution;
//  @Value("${usage.connector.job.monthly.lineItemPageSize:100}")
//  private int invoiceLineItemPageSize;
//
//  private final InvoiceService invoiceService;
//
//  private InvoiceLineItemStream invoiceLineItemStream;
//
//  public InvoiceLineItem read() {
//    InvoiceLineItem invoiceLineItem = null;
//    try {
//      invoiceLineItem = invoiceLineItemStream.getNext();
//   } catch (Exception exception){
//      log.error("Error occurred while fetching usages  :", exception);
//      Throwable rootException = ExceptionUtils.getRootCause(exception);
//      stepExecution.setStatus(BatchStatus.FAILED);
//      stepExecution.addFailureException(rootException);
//      stepExecution.setExitStatus(stepExecution.getExitStatus().addExitDescription(rootException.getClass().getName() + " : " + rootException.getMessage()));
//    }
//    return invoiceLineItem;
//  }
//
//  public void open(ExecutionContext executionContext) throws ItemStreamException {
//    int offset = executionContext.getInt(JobConstants.OFFSET);
//    int pageSize = executionContext.getInt(JobConstants.SIZE);
//
//    JobParameters jobParameters = stepExecution.getJobParameters();
//    String partnerDetails = jobParameters.getString(JobConstants.PARTNER);
//    Partner partner = Partner.fromPartnerDetailsString(partnerDetails);
//
//    // Example: Usage Month: November 2020 so firstDayOfUsageMonth = 2020-11-01
//    // Consider invoice generated between 2020-12-01:00:00:00 and 2021-01-01:00:00:00
//    // This approach allows us to precisely fetch invoices for a single usage month
//    // Else running a job for Nov-2020 in Feb-2021 will fetch all invoices from Dec-2020 and Jan-2021
//    LocalDate firstDayOfUsageMonth = getFirstDayOfUsageMonth();
//    LocalDateTime invoiceStartDateTime = firstDayOfUsageMonth.plusMonths(1).atStartOfDay();
//    LocalDateTime invoiceEndDateTime = firstDayOfUsageMonth.plusMonths(2).atStartOfDay();
//
//    log.info("Fetching invoices for jobId: {} using offset: {}, size: {}", stepExecution.getJobExecutionId(), offset, pageSize);
//    List<Invoice> invoices = this.invoiceService.getInvoices(partner, offset, pageSize);
//
//    log.info("Filtering invoices: {} based on First Day of Usage Month: {}, Invoice Start: {}, Invoice End: {}",
//      invoicesToString(invoices), firstDayOfUsageMonth, invoiceStartDateTime, invoiceEndDateTime);
//    List<Invoice> filteredInvoices = invoices.stream()
//      .filter(invoice -> isInvoiceDateBetween(invoice.getInvoiceDate(), invoiceStartDateTime, invoiceEndDateTime))
//      .filter(invoice -> isAzureModernInvoice(invoice))
//      .collect(Collectors.toList());
//    log.info("Filtered invoices: {}", invoicesToString(filteredInvoices));
//
//    invoiceLineItemStream = new InvoiceLineItemStream(invoiceLineItemPageSize, filteredInvoices, partner, invoiceService);
//  }
//
//  public void update(ExecutionContext executionContext) throws ItemStreamException {
//
//  }
//
//  public void close() throws ItemStreamException {
//
//  }
//
//  private boolean isAzureModernInvoice(Invoice invoice) {
//    return StreamSupport.stream(invoice.getInvoiceDetails().spliterator(), false)
//      .anyMatch(invoiceDetail -> ONE_TIME.equals(invoiceDetail.getBillingProvider())
//        && BILLINGLINEITEMS.equals(invoiceDetail.getInvoiceLineItemType()));
//  }
//
//  private LocalDate getFirstDayOfUsageMonth() {
//    JobParameters jobParameters = stepExecution.getJobParameters();
//    String firstDayOfUsageMonth = jobParameters.getString(JobConstants.FIRST_DAY_OF_USAGE_MONTH);
//    MDC.put("firstDayOfUsageMonth", firstDayOfUsageMonth); // only required for mock service, I seek forgiveness
//    return DateTimeUtils.parseYearMonthDayDate(firstDayOfUsageMonth);
//  }
//
//  private boolean isInvoiceDateBetween(DateTime invoiceDateTime, LocalDateTime invoiceStartDateTime, LocalDateTime invoiceEndDateTime) {
//    LocalDateTime invoiceLocalDateTime = Instant.ofEpochMilli(invoiceDateTime.getMillis()).atZone(ZoneId.systemDefault()).toLocalDateTime();
//    return invoiceLocalDateTime.isAfter(invoiceStartDateTime) && invoiceLocalDateTime.isBefore(invoiceEndDateTime);
//  }
//
//  private String invoicesToString(List<Invoice> invoices) {
//    return invoices.stream().map(this::invoicesToString).collect(Collectors.joining(", ", "[", "]"));
//  }
//  private String invoicesToString(Invoice invoice) {
//    StringJoiner joiner = new StringJoiner("::");
//    return joiner.add(invoice.getId())
//      .add(invoice.getInvoiceDate().toString())
//      .add(invoiceDetailsToString(invoice))
//      .toString();
//  }
//
//  private String invoiceDetailsToString(Invoice invoice) {
//    return StreamSupport.stream(invoice.getInvoiceDetails().spliterator(), false)
//      .map(this::invoiceDetailToString)
//      .collect(Collectors.joining(", ", "[", "]"));
//  }
//
//  private String invoiceDetailToString(InvoiceDetail invoiceDetail) {
//    StringJoiner joiner = new StringJoiner("::");
//    return joiner.add(invoiceDetail.getBillingProvider().toString())
//      .add(invoiceDetail.getInvoiceLineItemType().toString())
//      .toString();
//  }
}

## Address Book

Data stream aggregation with [Reactor](https://projectreactor.io/)

Code snippet

```java
Pipeline<Address> p = Pipeline.<Address>builder().
                csvDataSource(csvDataSource).
                aggregator(new AgeDayDiffAggregator("Bill", "Paul")).
                aggregator(new MaxAgeAggregator()).
                aggregator(new CountMalesAggregator()).
                build();
p.aggregate(path, a -> values.put(a.getName(), a.getValue()));
```

See also

- [Apache Beam](https://beam.apache.org)
- [BigQuery](https://cloud.google.com/bigquery/)





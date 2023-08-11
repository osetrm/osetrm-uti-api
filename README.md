# osetrm-uti-generator

This REST API is used to generate Unique Transaction Identifiers (UTI), also known as Unique Swap Identifier (USI). The UTI is designed to be a globally unique transaction identifier for individual
transactions. It is a string composite of the executing LEI, a three digit application instance specific code and a time-based id with a fraction of a second combined with a global incrementer to
eliminate duplicates within the time.

This service is designed to be a single UTI source for a trading organization which may maintain multiple trade entry systems including vendor systems and bespoke trade capture capabilities.

https://en.wikipedia.org/wiki/Unique_Transaction_Identifier  
https://www.fpml.org/fpml_focus/identifiers-classification/uitusi/  
https://www.cftc.gov/LawRegulation/FederalRegister/proposedrules/2020-04407.html

Inspired by Peter Lawrey's post: http://blog.vanillajava.blog/2022/01/distributed-unique-time-stamp.html

This service is not designed to be used in a HFT environment.

# Structure

The structure of the uti is based on one of two regulatory regimes: Dodd Frank (US) and EMIR (EMEA).

## Dodd Frank
* Total Length: 42 chars (Max Length is 42 chars)
* LEI: 20 chars
* Machine ID: 3 chars
* ID: 19 chars
* Example: 000000000000000000001062023070309364403652

## EMIR
* Total Length: 45 chars (Max Length is 52 chars)
* Prefix: 3 (E02)
* LEI: 20
* Machine ID: 3 chars
* ID: 19
* Example: E02000000000000000000001062023070309364405287

## ID Structure

The ID structure is a composite of a unique id for the instance of the application,
a time value representing the exact time in which the request is executed,
and a global incrementer that prevents duplicates from happening within a particular instant of time.

The unique id of the instance is a three digit identifier, either derived from an environment variable (MACHINE_UNIQUE_ID) in cases where the application
is running on a single host, or as default, we simply use the last field of the IP address for the pod, which
*should* be unique based on the SDN when running in a Kubernetes cluster. When running this application as a deployment with multiple replicas behind a
service, the combination should provide uniqueness in all theoretical scenarios.

# Request

The request for a new UTI includes the Regulatory Regime for which the UTI will be generated, along with the LEI of the
code issuing authority under which the transaction execution happened.

POST /uti
```
{
  "regulatoryRegime": "DODD_FRANK",
  "lei": "string"
}
```

Response
```
{
  "uti": "000000000000000000001062023070309364403652"
}
```

# jmeter

Jmeter performance testing for single application running locally for 200 threads running 10000 requests
```
Label,# Samples,Average,Min,Max,Std. Dev.,Error %,Throughput,Received KB/sec,Sent KB/sec,Avg. Bytes
post-uti,2000000,10,0,400,4.61,0.000%,16424.40667,2197.42,4410.85,137.0
TOTAL,2000000,10,0,400,4.61,0.000%,16424.40667,2197.42,4410.85,137.0
```

Metrics Quantiles
```
http_server_requests_seconds{method="POST",outcome="SUCCESS",status="200",uri="/uti-generator",quantile="0.5",} 0.005500928
http_server_requests_seconds{method="POST",outcome="SUCCESS",status="200",uri="/uti-generator",quantile="0.95",} 0.01153024
http_server_requests_seconds{method="POST",outcome="SUCCESS",status="200",uri="/uti-generator",quantile="0.99",} 0.014675968
http_server_requests_seconds{method="POST",outcome="SUCCESS",status="200",uri="/uti-generator",quantile="0.999",} 0.019918848
```

# Quarkus

## Running the application in dev mode

```shell script
./mvnw compile quarkus:dev
```

# Build container

```shell script
./mvnw package
podman build -f src/main/docker/Dockerfile.jvm -t osetrm/osetrm-uti-generator .
podman run -i --rm -p 8080:8080 localhost/osetrm/osetrm-uti-generator
```
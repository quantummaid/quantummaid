---
title: "Install"
weight: 1010300001
---

All QuantumMaid libraries are available as 
<a href="https://maven.apache.org/what-is-maven.html" target="_blank">Maven</a> modules.

## QuantumMaid as single dependency
The following dependency contains all relevant modules to start with a basic QuantumMaid application.
<!---[CodeSnippet](dependency)-->
```xml
<dependency>
    <groupId>de.quantummaid.quantummaid.packagings</groupId>
    <artifactId>quantummaid-essentials</artifactId>
    <version>1.0.72</version>
</dependency>
```

The `quantummaid-essentials` contains the following dependencies:
 - QuantumMaid core
 - HttpMaid core
 - HttpMaid usecase integration
 - MapMaid core
 - MapMaid Jackson integration

## The individual dependencies
The following three modules are the main components of QuantumMaid. 

### QuantumMaid
<!---[CodeSnippet](quantummaidDependency)-->
```xml
<dependency>
    <groupId>de.quantummaid.quantummaid</groupId>
    <artifactId>core</artifactId>
    <version>1.0.72</version>
</dependency>
```

### HttpMaid
HttpMaid handles everything that is related to the web. <!---[CodeSnippet](httpmaidDependency)-->
```xml
<dependency>
    <groupId>de.quantummaid.httpmaid</groupId>
    <artifactId>core</artifactId>
    <version>0.9.101</version>
</dependency>
```

HttpMaid is covered in detail [here]({{< ref "2_HttpMaid/_index.md" >}}).


### MapMaid
MapMaid adds intelligent serialization and deserialization capabilities
to QuantumMaid. These capabilities are used in HTTP requests and responses as
well as in persistence mechanisms.
<!---[CodeSnippet](mapmaidDependency)-->
```xml
<dependency>
    <groupId>de.quantummaid.mapmaid</groupId>
    <artifactId>core</artifactId>
    <version>0.9.95</version>
</dependency>
```

MapMaid is covered in detail [here]({{< ref "3_MapMaid/_index.md" >}}).



Integrations and auxiliary modules of each component have the same version number as their core module. 
To see the available integrations for each component, please consult the respective module's chapter in this documentaion.

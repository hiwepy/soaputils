# soaputils

[English](./README.md) | [简体中文](./README.zh-CN.md)

[![Java](https://img.shields.io/badge/Java-21-orange)](https://github.com/easy-4-java/soap-utils) [![License](https://img.shields.io/badge/license-Apache%202.0-green)](./LICENSE)

soaputils (artifact soap-utils, "Soap Extend Utils") is a pure-Java utility library for working with SOAP messages built on XMLBeans and the SoapUI en...

## Table of Contents

- [1. Project Overview](#1-project-overview)
- [2. Features & Status](#2-features--status)
- [3. Requirements & Compatibility](#3-requirements--compatibility)
- [4. Architecture & Modules](#4-architecture--modules)
- [5. Installation](#5-installation)
- [6. Quick Start](#6-quick-start)
- [7. Configuration](#7-configuration)
- [8. Core Usage / API](#8-core-usage--api)
- [9. Testing & Build](#9-testing--build)
- [10. Versioning & Branches](#10-versioning--branches)
- [11. Contributing & License](#11-contributing--license)

## 1. Project Overview

`soaputils` (artifact `soap-utils`, "Soap Extend Utils") is a pure-Java utility library for working with SOAP messages built on XMLBeans and the SoapUI engine. It helps developers detect SOAP faults, deduce the SOAP version (1.1 / 1.2) of a message, navigate envelope/header/body elements, build SOAP requests (including typed parameter serialization), sign requests, and parse responses — without depending on Spring or any web framework.

It is a toolkit for SOAP/WSDL integration code — not an HTTP client and not an application framework.

Typical scenarios:

| Scenario | What this module contributes |
|:---|:---|
| Detect a SOAP Fault in a response | `SoapUtils.isSoapFault(...)`, `SoapFaultUtils.checkFault(...)` |
| Determine SOAP 1.1 vs 1.2 | `SoapUtils.deduceSoapVersion(...)` |
| Navigate envelope / header / body | `SoapUtils.getBodyElement(...)`, `getHeaderElement(...)`, `getContentElement(...)` |
| Build and send a SOAP request | `SoapRequestUtils.buildRequest(...)`, `SoapUtils2.soapRequest(...)` |
| Serialize typed parameters | `SoapType` SPI (`StringSoapType`, `NumberSoapType`, `DateSoapType`, `BeanSoapType`, `ListSoapType`) |
| Request signing | `SoapSignature` / `DefaultSoapSignature` |

## 2. Features & Status

Project status: pre-release development line (`1.0.x.*` snapshots); public API is still stabilizing until the first tagged release.

| Capability | Status | Notes |
|:---|:---|:---|
| SOAP version model | Stable | `SoapVersion` interface with `Soap11` / `Soap12` singletons and envelope/body/header/fault QName accessors |
| Fault detection | Stable | `SoapUtils.isSoapFault(content[, version])`; `SoapFaultUtils.checkFault(SOAPMessage)` |
| Message navigation | Stable | `SoapUtils.getBodyElement`, `getHeaderElement`, `getContentElement`, `removeEmptySoapHeaders`, `transferSoapHeaders` |
| SOAP request building | Stable | `SoapRequestUtils.buildRequest(namespace, wsdlUrl, method, protocol, params, signature)`; `SoapUtils2.soapRequest(...)` (sync / typed) |
| Typed parameter serialization | Stable | `SoapType` SPI + `BaseSoapType`, `StringSoapType`, `NumberSoapType`, `DateSoapType`, `BeanSoapType`, `ListSoapType`, `SoapTypes` |
| Response parsing handlers | Stable | `SoapResponseHandler<T>` SPI with `DefaultResponseHandler`, `JSONResponseHandler`, `PlainTextResponseHandler`, `XMLResponseHandler` |
| Request signing | Stable | `SoapSignature` interface + `DefaultSoapSignature` |
| XML utilities | Stable | `SoapXmlUtils`, `XmlUtils`, `SoapMessageBuilder`, `SoapResponseUtils` |
| Typed exceptions | Stable | `InvokeException` for invocation failures |

## 3. Requirements & Compatibility

| Requirement | Version |
|:---|:---|
| JDK | 21+ |
| Maven | 3.6+ |
| XMLBeans | 2.6.0 (xmlbeans, xmlbeans-xpath, xmlbeans-xmlpublic) |
| SoapUI engine | SoapUI (SmartBear) installed in the local Maven repository |
| Other runtime deps | commons-io, commons-beanutils, commons-text, xercesImpl, groovy, Saxon-HE, wsdl4j, `javax.xml.soap-api`, `52n-xml-soap-v11` (declared in the POM) |

Version lines:

| Branch | JDK | Version pattern | Notes |
|:---|:---|:---|:---|
| `feature/1.0.x` | 8 | `1.0.x.*` | Current line |
| `feature/2.0.x` | 17 | `2.0.x.*` | Next line |
| `feature/3.0.x` | 21 | `3.0.x.*` | Future line |

## 4. Architecture & Modules

```
SOAP message / response content
        |
        v
+------------------------------------+
| SoapUtils / SoapUtils2 / SoapFault-|
| Utils: version, fault, navigation  |
+------------------------------------+
        |
        v
+------------------------------------+
| SoapRequestUtils / SoapMessageBuild-|
| er: build request (typed params)   |
+------------------------------------+
        |
        v
SoapResponseHandler SPI
  Default / JSON / PlainText / XML
        |
        v
Parsed result (SOAPMessage, JSON, String, XML)
```

The project is a single jar module (artifact `soap-utils`). Key packages under `io.github.easy4j.soap`:

| Package | Responsibility |
|:---|:---|
| root | `SoapUtils`, `SoapUtils2`, `SoapRequestUtils`, `SoapFaultUtils`, `Constants`, `SoapVersion` / `SoapVersion11` / `SoapVersion12` |
| `handler` | `SoapResponseHandler` SPI + `DefaultResponseHandler`, `JSONResponseHandler`, `PlainTextResponseHandler`, `XMLResponseHandler` |
| `signature` | `SoapSignature` interface + `DefaultSoapSignature` |
| `type` | `SoapType` SPI and the string/number/date/bean/list implementations, `SoapTypes` |
| `utils` | `SoapXmlUtils`, `XmlUtils`, `SoapMessageBuilder`, `SoapResponseUtils` |
| `exception` | `InvokeException` |

## 5. Installation

Artifacts are published to the easy4j private repository and GitHub Releases; the project is not yet on Maven Central.

Maven:

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>soap-utils</artifactId>
    <version>3.0.x.x.20260630-SNAPSHOT</version>
</dependency>
```

Gradle:

```groovy
implementation 'io.github.easy4j:soap-utils:3.0.x.x.20260630-SNAPSHOT'
```

**Prerequisite — SoapUI must be available in your Maven repository.** The POM depends on the SoapUI engine (see the `soapui-template` module README for install-from-source steps).

## 6. Quick Start

Detect a SOAP Fault in a response payload:

```java
import io.github.easy4j.soap.SoapUtils;
import io.github.easy4j.soap.SoapVersion;

String responseContent = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
        + "<soap:Body><soap:Fault><faultstring>Invalid request</faultstring></soap:Fault></soap:Body>"
        + "</soap:Envelope>";

boolean isFault = SoapUtils.isSoapFault(responseContent, SoapVersion.Soap11);
System.out.println("is fault: " + isFault); // true
```

Expected result: `SoapUtils` parses the message with XMLBeans and reports whether the response body carries a SOAP `Fault` element.

## 7. Configuration

Pure library — no configuration files or property prefixes. All behavior is driven by method parameters (e.g. the `SoapVersion` passed to `SoapUtils` methods or the `SoapSignature` passed to `SoapRequestUtils.buildRequest`).

## 8. Core Usage / API

Build and invoke a SOAP request through the high-level helper:

```java
import io.github.easy4j.soap.SoapUtils2;

java.util.Map<String, Object> params = new java.util.HashMap<>();
params.put("name", "world");

// namespace, wsdlUrl, method, params -> SOAPMessage
javax.xml.soap.SOAPMessage request = SoapUtils2.soapRequest(
        "http://example.com/ws", "https://example.com/service?wsdl", "sayHello", params);
```

Deduce the SOAP version of a request:

```java
import io.github.easy4j.soap.SoapUtils;

SoapVersion version = SoapUtils.deduceSoapVersion(requestContentType, requestContent);
System.out.println(version.getEnvelopeQName()); // SOAP envelope QName of the detected version
```

## 9. Testing & Build

Build and run tests:

```bash
./mvnw clean verify
```

- Test sources under `src/test/java` exercise SOAP parsing and web-service client flows (`Soap_Test`, `WebServiceClient`, `SoapParser`, `ParseXml`, ...).
- The build is configured with the JaCoCo Maven plugin: a coverage report is generated at `target/site/jacoco/index.html` and a rule checks the bundle line coverage against a 90% minimum (`haltOnFailure=false`, so the check reports but does not fail the build).
- The `central` Maven profile (`./mvnw -Pcentral deploy`) attaches GPG signatures, sources and Javadoc jars for publishing.

## 10. Versioning & Branches

Three parallel version lines are maintained:

| Branch | JDK | Version pattern |
|:---|:---|:---|
| `feature/1.0.x` | 8 | `1.0.x.*` |
| `feature/2.0.x` | 17 | `2.0.x.*` |
| `feature/3.0.x` | 21 | `3.0.x.*` |

Maintenance policy: the `1.0.x` line is the actively developed line (current snapshot `3.0.x.x.20260630-SNAPSHOT`); `2.0.x` and `3.0.x` are forward porting lines targeting newer JDKs. Snapshots are built on demand; tagged releases are distributed via GitHub Releases.

## 11. Contributing & License

- Fork the repository and open a pull request; keep the `1.0.x` line compatible with JDK 8.
- Bug reports and feature requests are tracked via GitHub Issues.
- Licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0).

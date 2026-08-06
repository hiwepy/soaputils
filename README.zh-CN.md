# soaputils

[English](./README.md) | [简体中文](./README.zh-CN.md)

## 目录

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

`soaputils`（制品名 `soap-utils`，"Soap Extend Utils"）是基于 XMLBeans 与 SoapUI 引擎的纯 Java SOAP 消息工具库。它帮助开发者检测 SOAP Fault、判断消息的 SOAP 版本（1.1 / 1.2）、导航 envelope/header/body 元素、构建 SOAP 请求（含类型化参数序列化）、对请求签名并解析响应——不依赖 Spring 或任何 Web 框架。

它是 SOAP/WSDL 集成代码的工具箱——不是 HTTP 客户端，也不是应用框架。

典型场景：

| 场景 | 本模块提供的组件 |
|:---|:---|
| 检测响应中的 SOAP Fault | `SoapUtils.isSoapFault(...)`、`SoapFaultUtils.checkFault(...)` |
| 判断 SOAP 1.1 还是 1.2 | `SoapUtils.deduceSoapVersion(...)` |
| 导航 envelope / header / body | `SoapUtils.getBodyElement(...)`、`getHeaderElement(...)`、`getContentElement(...)` |
| 构建并发送 SOAP 请求 | `SoapRequestUtils.buildRequest(...)`、`SoapUtils2.soapRequest(...)` |
| 类型化参数序列化 | `SoapType` SPI（`StringSoapType`、`NumberSoapType`、`DateSoapType`、`BeanSoapType`、`ListSoapType`） |
| 请求签名 | `SoapSignature` / `DefaultSoapSignature` |

## 2. Features & Status

项目状态：`1.0.x.*` 预发布开发线（快照版本）；在首个正式 Release 标签之前，公开 API 仍在稳定过程中。

| 能力 | 状态 | 说明 |
|:---|:---|:---|
| SOAP 版本模型 | 稳定 | `SoapVersion` 接口，提供 `Soap11` / `Soap12` 单例与 envelope/body/header/fault 的 QName 访问器 |
| Fault 检测 | 稳定 | `SoapUtils.isSoapFault(content[, version])`；`SoapFaultUtils.checkFault(SOAPMessage)` |
| 消息导航 | 稳定 | `SoapUtils.getBodyElement`、`getHeaderElement`、`getContentElement`、`removeEmptySoapHeaders`、`transferSoapHeaders` |
| SOAP 请求构建 | 稳定 | `SoapRequestUtils.buildRequest(namespace, wsdlUrl, method, protocol, params, signature)`；`SoapUtils2.soapRequest(...)`（同步 / 类型化） |
| 类型化参数序列化 | 稳定 | `SoapType` SPI + `BaseSoapType`、`StringSoapType`、`NumberSoapType`、`DateSoapType`、`BeanSoapType`、`ListSoapType`、`SoapTypes` |
| 响应解析处理器 | 稳定 | `SoapResponseHandler<T>` SPI，实现有 `DefaultResponseHandler`、`JSONResponseHandler`、`PlainTextResponseHandler`、`XMLResponseHandler` |
| 请求签名 | 稳定 | `SoapSignature` 接口 + `DefaultSoapSignature` |
| XML 工具 | 稳定 | `SoapXmlUtils`、`XmlUtils`、`SoapMessageBuilder`、`SoapResponseUtils` |
| 类型化异常 | 稳定 | 调用失败抛出 `InvokeException` |

## 3. Requirements & Compatibility

| 要求 | 版本 |
|:---|:---|
| JDK | 8+ |
| Maven | 3.6+ |
| XMLBeans | 2.6.0（xmlbeans、xmlbeans-xpath、xmlbeans-xmlpublic） |
| SoapUI 引擎 | SoapUI（SmartBear），需安装到本地 Maven 仓库 |
| 其他运行依赖 | commons-io、commons-beanutils、commons-text、xercesImpl、groovy、Saxon-HE、wsdl4j、`javax.xml.soap-api`、`52n-xml-soap-v11`（已在 POM 声明） |

版本线：

| 分支 | JDK | 版本模式 | 说明 |
|:---|:---|:---|:---|
| `feature/1.0.x` | 8 | `1.0.x.*` | 当前开发线 |
| `feature/2.0.x` | 17 | `2.0.x.*` | 下一条版本线 |
| `feature/3.0.x` | 21 | `3.0.x.*` | 未来版本线 |

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

本工程为单 jar 模块（制品名 `soap-utils`），关键包位于 `io.github.easy4j.soap`：

| 包 | 职责 |
|:---|:---|
| 根包 | `SoapUtils`、`SoapUtils2`、`SoapRequestUtils`、`SoapFaultUtils`、`Constants`、`SoapVersion` / `SoapVersion11` / `SoapVersion12` |
| `handler` | `SoapResponseHandler` SPI + `DefaultResponseHandler`、`JSONResponseHandler`、`PlainTextResponseHandler`、`XMLResponseHandler` |
| `signature` | `SoapSignature` 接口 + `DefaultSoapSignature` |
| `type` | `SoapType` SPI 及 string/number/date/bean/list 实现、`SoapTypes` |
| `utils` | `SoapXmlUtils`、`XmlUtils`、`SoapMessageBuilder`、`SoapResponseUtils` |
| `exception` | `InvokeException` |

## 5. Installation

制品发布到 easy4j 私有仓库与 GitHub Releases，暂未发布 Maven Central。

Maven：

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>soap-utils</artifactId>
    <version>1.0.x.20260630-SNAPSHOT</version>
</dependency>
```

Gradle：

```groovy
implementation 'io.github.easy4j:soap-utils:1.0.x.20260630-SNAPSHOT'
```

**前置条件 —— SoapUI 必须存在于你的 Maven 仓库。** POM 依赖 SoapUI 引擎（源码安装步骤参见 `soapui-template` 模块的 README）。

## 6. Quick Start

检测响应负载中的 SOAP Fault：

```java
import io.github.easy4j.soap.SoapUtils;
import io.github.easy4j.soap.SoapVersion;

String responseContent = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
        + "<soap:Body><soap:Fault><faultstring>Invalid request</faultstring></soap:Fault></soap:Body>"
        + "</soap:Envelope>";

boolean isFault = SoapUtils.isSoapFault(responseContent, SoapVersion.Soap11);
System.out.println("is fault: " + isFault); // true
```

预期结果：`SoapUtils` 使用 XMLBeans 解析消息，并报告响应体是否包含 SOAP `Fault` 元素。

## 7. Configuration

纯库——无配置文件与属性前缀。所有行为均由方法参数驱动（如传给 `SoapUtils` 方法的 `SoapVersion`、传给 `SoapRequestUtils.buildRequest` 的 `SoapSignature`）。

## 8. Core Usage / API

通过高层辅助方法构建并调用 SOAP 请求：

```java
import io.github.easy4j.soap.SoapUtils2;

java.util.Map<String, Object> params = new java.util.HashMap<>();
params.put("name", "world");

// namespace, wsdlUrl, method, params -> SOAPMessage
javax.xml.soap.SOAPMessage request = SoapUtils2.soapRequest(
        "http://example.com/ws", "https://example.com/service?wsdl", "sayHello", params);
```

判断请求的 SOAP 版本：

```java
import io.github.easy4j.soap.SoapUtils;

SoapVersion version = SoapUtils.deduceSoapVersion(requestContentType, requestContent);
System.out.println(version.getEnvelopeQName()); // 检测到的版本对应的 SOAP envelope QName
```

## 9. Testing & Build

构建与测试：

```bash
./mvnw clean verify
```

- `src/test/java` 下的测试源码覆盖 SOAP 解析与 WebService 客户端流程（`Soap_Test`、`WebServiceClient`、`SoapParser`、`ParseXml` 等）；
- 构建配置了 JaCoCo Maven 插件：覆盖率报告生成于 `target/site/jacoco/index.html`，并配置了 BUNDLE 行覆盖率 90% 的校验规则（`haltOnFailure=false`，即只报告不阻断构建）；
- `central` Maven Profile（`./mvnw -Pcentral deploy`）附加 GPG 签名、源码包与 Javadoc 包用于发布。

## 10. Versioning & Branches

维护三条并行版本线：

| 分支 | JDK | 版本模式 |
|:---|:---|:---|
| `feature/1.0.x` | 8 | `1.0.x.*` |
| `feature/2.0.x` | 17 | `2.0.x.*` |
| `feature/3.0.x` | 21 | `3.0.x.*` |

维护策略：`1.0.x` 为当前活跃开发线（当前快照 `1.0.x.20260630-SNAPSHOT`）；`2.0.x` 与 `3.0.x` 为面向更新 JDK 的前向移植线。快照按需构建，正式 Release 通过 GitHub Releases 分发。

## 11. Contributing & License

- Fork 仓库并提交 Pull Request；`1.0.x` 版本线保持 JDK 8 兼容；
- Bug 反馈与功能建议通过 GitHub Issues 跟踪；
- 基于 [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0) 开源。

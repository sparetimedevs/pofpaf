# Bow

### By using Bow together with /\rrow you will be able to write safe(r) Azure Functions in Kotlin.

#### What are Azure Functions?
Azure Functions allows you to develop serverless applications on Microsoft Azure. One or more Azure Functions are bundled into an application; a function app.
For more information about Azure Functions, see [the docs](https://docs.microsoft.com/en-us/azure/azure-functions/functions-overview).

#### What is /\rrow?
Arrow is a functional companion to Kotlin's Standard Library.
For more information about Arrow, see [the docs](https://arrow-kt.io).

#### What is Bow?
Bow is a companion for Azure Functions that are written in Kotlin. Bow makes use of Arrow which makes it easier to write a safe(r) function app. It enforces you to encapsulate side-effects. This makes reasoning about the function app easier, because the function app will be deterministic.

#### By using this library you will pull in:
- artifactId: azure-functions-java-library, groupId: com.microsoft.azure.functions
- artifactId: arrow-fx, groupId: io.arrow-kt

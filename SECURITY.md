<!--
SPDX-License-Identifier: Apache-2.0

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

# Security

The Apache Grails Spring Security plugins follow the [Apache Software Foundation security process](https://www.apache.org/security/). Vulnerability reports are handled privately by the ASF Security Team and triaged against the project's threat model.

## Reporting a vulnerability

**Do not** open a public GitHub issue, discussion, or pull request for a suspected vulnerability. Email the ASF Security Team at [security@apache.org](mailto:security@apache.org), and include `grails-spring-security` in the subject line.

A good report includes:

- The plugin and version affected (e.g. `core-plugin 8.0.0-M1`, `spring-security-rest 8.0.0-M1`).
- The configuration in effect at the time (relevant `grails.plugin.springsecurity.*` properties).
- Reproduction steps, the smallest test case that demonstrates the issue, and the observed vs expected behavior.
- The disposition you believe the report should receive (see [THREAT_MODEL.md §13](./THREAT_MODEL.md)). This is not binding on the triagers but it accelerates the back-and-forth.

The ASF Security Team will acknowledge receipt, route the report to the project PMC, and coordinate disclosure once a fix or mitigation is available.

## What is in scope

The [THREAT_MODEL.md](./THREAT_MODEL.md) at the root of this repository is the authoritative reference for what these plugins claim, what they do not claim, and how reports are triaged. It binds the 8.0.x branch; release branches fork their own version of the document.

Before reporting, please skim the threat model for:

- [§3 Out of scope](./THREAT_MODEL.md#§3-out-of-scope-explicit-non-goals) - non-goals the plugins do not defend against.
- [§5a Build-time and configuration variants](./THREAT_MODEL.md#§5a-build-time-and-configuration-variants) - configuration values that change which security properties hold. Reports that require a non-default configuration are typically closed as `OUT-OF-MODEL: non-default-build`.
- [§8 Security properties the plugins provide](./THREAT_MODEL.md#§8-security-properties-the-plugins-provide) - the claims the plugins make. A report against one of these is `VALID` if reproducible.
- [§9 Security properties the plugins do NOT provide](./THREAT_MODEL.md#§9-security-properties-the-plugins-do-not-provide) - disclaimers. Reports against these are `BY-DESIGN: property-disclaimed`.
- [§11 Known misuse patterns](./THREAT_MODEL.md#§11-known-misuse-patterns) - documented anti-patterns. Reports of these are typically `VALID-HARDENING`.
- [§11a Known non-findings](./THREAT_MODEL.md#§11a-known-non-findings-recurring-false-positives) - patterns automated scanners repeatedly flag that are not vulnerabilities given the model.

## Supported versions

| Branch | Compatible with | Security fixes |
|---|---|---|
| 8.0.x | Grails 8 / Spring Boot 4 / Spring Security 7 | Yes |
| 7.0.x | Grails 7 / Spring Boot 3 / Spring Security 6 | Yes |
| 6.0.x | Grails 6 | Best-effort |
| 5.0.x and earlier | Grails 5 and earlier | End of life |

Reports against end-of-life branches are accepted but may not result in a release.

## Additional resources

- [Apache Software Foundation Security](https://www.apache.org/security/)
- [Apache Grails Security Project Page](https://grails.apache.org/security.html)
- [Spring Security advisories](https://spring.io/security) - the plugins inherit Spring Security's vulnerability scope; advisories there often apply transitively here.

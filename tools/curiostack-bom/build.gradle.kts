/*
 * MIT License
 *
 * Copyright (c) 2018 Choko (choko@curioswitch.org)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

plugins {
    `java-platform`
    `maven-publish`
}

val JACKSON_VERSION = "2.9.8"

data class DependencySet(val group: String, val version: String, val modules: List<String>)

val DEPENDENCY_SETS = listOf(
    DependencySet(
        "commons-codec",
        "1.10",
        listOf("commons-codec")
    ),
    DependencySet(
        "commons-logging",
        "1.2",
        listOf("commons-logging")
    ),
    DependencySet(
        "com.auth0",
        "3.8.0",
        listOf("java-jwt")
    ),
    DependencySet(
        "com.google.auth",
        "0.15.0",
        listOf("google-auth-library-oauth2-http")
    ),
    DependencySet(
        "com.google.auto.factory",
        "1.0-beta6",
        listOf("auto-factory")
    ),
    DependencySet(
        "com.google.auto.service",
        "1.0-rc5",
        listOf("auto-service")
    ),
    DependencySet(
        "com.google.auto.value",
        "1.6.4",
        listOf("auto-value", "auto-value-annotations")
    ),
    DependencySet(
        "com.fasterxml.jackson.core",
        JACKSON_VERSION,
        listOf(
            "jackson-annotations",
            "jackson-core",
            "jackson-databind")
    ),
    DependencySet(
        "com.fasterxml.jackson.dataformat",
        JACKSON_VERSION,
        listOf(
            "jackson-dataformat-xml",
            "jackson-dataformat-yaml"
        )
    ),
    DependencySet(
        "com.fasterxml.jackson.datatype",
        JACKSON_VERSION,
        listOf(
            // NB: Not a typo.
            "jackson-datatypes-collections",
            "jackson-datatype-guava",
            "jackson-datatype-jsr310")
    ),
    DependencySet(
        "com.github.ben-manes.caffeine",
        "2.7.0",
        listOf("caffeine")
    ),
    DependencySet(
        "com.google.api-client",
        "1.28.0",
        listOf("google-api-client")
    ),
    DependencySet(
        "com.google.api.grpc",
        "0.49.0",
        listOf("grpc-google-cloud-trace-v2")
    ),
    DependencySet(
        "com.google.api.grpc",
        "1.48.0",
        listOf("grpc-google-cloud-pubsub-v1")
    ),
    DependencySet(
        "com.google.auto",
        "0.10",
        listOf("auto-common")
    ),
    DependencySet(
        "com.google.code.findbugs",
        "3.0.2",
        listOf("jsr305")
    ),
    DependencySet(
        "com.google.cloud",
        "1.66.0",
        listOf(
            "google-cloud-bigquery",
            "google-cloud-core",
            "google-cloud-core-grpc",
            "google-cloud-pubsub",
            "google-cloud-storage")
    ),
    DependencySet(
        "com.google.cloud",
        "0.53.0-alpha",
        listOf("google-cloud-resourcemanager")
    ),
    DependencySet(
        "com.google.cloud",
        "1.5.0",
        listOf("google-cloud-kms")
    ),
    DependencySet(
        "com.google.cloud",
        "0.83.0-beta",
        listOf("google-cloud-iot", "google-cloud-trace")
    ),
    DependencySet(
        "com.google.cloud.sql",
        "1.0.13",
        listOf(
            "mysql-socket-factory",
            "mysql-socket-factory-connector-j-6",
            "mysql-socket-factory-connector-j-8")
    ),
    DependencySet(
        "com.google.cloud.trace.adapters.zipkin",
        "0.3.0",
        listOf("translation")
    ),
    DependencySet(
        "com.google.dagger",
        "2.21",
        listOf("dagger", "dagger-compiler", "dagger-producers")
    ),
    DependencySet(
        "com.google.errorprone",
        "2.3.3",
        listOf("error_prone_annotations", "error_prone_core")
    ),
    DependencySet(
        "com.google.firebase",
        "6.8.0",
        listOf("firebase-admin")
    ),
    DependencySet(
        "com.google.guava",
        "27.1-jre",
        listOf("guava", "guava-testlib")
    ),
    DependencySet(
        "com.google.protobuf",
        "3.7.1",
        listOf("protobuf-java", "protobuf-java-util")
    ),
    // protoc often diverges from protobuf-java
    DependencySet(
        "com.google.protobuf",
        "3.6.1",
        listOf("protoc")
    ),
    DependencySet(
        "com.linecorp.armeria",
        "0.83.0",
        listOf("armeria", "armeria-grpc", "armeria-retrofit2", "armeria-zipkin")
    ),
    DependencySet(
        "com.spotify",
        "4.2.0",
        listOf("futures-extra")
    ),
    DependencySet(
        "com.squareup.retrofit2",
        "2.5.0",
        listOf(
            "adapter-guava",
            "adapter-java8",
            "converter-guava",
            "converter-jackson",
            "converter-java8",
            "retrofit")
    ),
    DependencySet(
        "com.sun.xml.bind",
        "2.3.0.1",
        listOf("jaxb-core", "jaxb-impl")
    ),
    DependencySet(
        "com.typesafe",
        "1.3.3",
        listOf("config")
    ),
    DependencySet(
        "com.zaxxer",
        "3.3.1",
        listOf("HikariCP")
    ),
    DependencySet(
        "info.solidsoft.mockito",
        "2.5.0",
        listOf("mockito-java8")
    ),
    DependencySet(
        "io.dropwizard.metrics",
        "4.0.3",
        listOf("metrics-core", "metrics-jvm", "metrics-json", "metrics-log4j2")
    ),
    DependencySet(
        "io.fabric8",
        "4.2.0",
        listOf("kubernetes-client")
    ),
    DependencySet(
        "io.lettuce",
        "5.1.6.RELEASE",
        listOf("lettuce-core")
    ),
    DependencySet(
        "io.grpc",
        "1.19.0",
        listOf(
            "gall",
            "grpc-alts",
            "grpc-auth",
            "grpc-context",
            "grpc-core",
            "grpc-grpclb",
            "grpc-netty",
            "grpc-netty-shaded",
            "grpc-okhttp",
            "grpc-protobuf",
            "grpc-protobuf-lite",
            "grpc-services",
            "grpc-stub")
    ),
    DependencySet(
        "io.micrometer",
        "1.1.4",
        listOf("micrometer-core", "micrometer-registry-prometheus")
    ),
    DependencySet(
        "io.netty",
        "4.1.34.Final",
        listOf(
            "netty-buffer",
            "netty-codec",
            "netty-codec-dns",
            "netty-codec-haproxy",
            "netty-codec-http2",
            "netty-codec-http",
            "netty-codec-socks",
            "netty-common",
            "netty-handler",
            "netty-handler-proxy",
            "netty-resolver",
            "netty-resolver-dns",
            "netty-transport",
            "netty-transport-native-epoll",
            "netty-transport-native-unix-common")
    ),
    DependencySet(
        "io.netty",
        "2.0.23.Final",
        listOf("netty-tcnative-boringssl-static")
    ),
    DependencySet(
        "io.prometheus",
        "0.6.0",
        listOf("simpleclient", "simpleclient_common", "simpleclient_hotspot", "simpleclient_log4j2")
    ),
    DependencySet(
        "io.zipkin.brave",
        "5.6.3",
        listOf("brave", "brave-instrumentation-mysql", "brave-instrumentation-mysql8")
    ),
    DependencySet(
        "io.zipkin.gcp",
        "0.11.1",
        listOf("brave-propagation-stackdriver", "zipkin-translation-stackdriver")
    ),
    DependencySet(
        "javax.inject",
        "1",
        listOf("javax.inject")
    ),
    DependencySet(
        "junit",
        "4.12",
        listOf("junit")
    ),
    DependencySet(
        "net.bytebuddy",
        "1.9.12",
        listOf("byte-buddy", "byte-buddy-agent")
    ),
    DependencySet(
        "org.apache.beam",
        "2.10.0",
        listOf(
            "beam-runners-direct-java",
            "beam-runners-google-cloud-dataflow-java",
            "beam-sdks-java-io-google-cloud-platform")
    ),
    DependencySet(
        "org.apache.httpcomponents",
        "4.5.3",
        listOf("httpclient")
    ),
    DependencySet(
        "org.apache.httpcomponents",
        "4.4.6",
        listOf("httpcore")
    ),
    DependencySet(
        "org.apache.logging.log4j",
        "2.11.2",
        listOf("log4j-api", "log4j-core", "log4j-jcl", "log4j-jul", "log4j-slf4j-impl")
    ),
    DependencySet(
        "org.assertj",
        "3.12.2",
        listOf("assertj-core")
    ),
    DependencySet(
        "org.assertj",
        "3.2.1",
        listOf("assertj-guava")
    ),
    DependencySet(
        "org.awaitility",
        "3.1.6",
        listOf("awaitility")
    ),
    DependencySet(
        "org.bouncycastle",
        "1.61",
        listOf("bcpkix-jdk15on", "bcprov-jdk15on")
    ),
    DependencySet(
        "org.checkerframework",
        "2.5.5",
        listOf("checker-qual", "checker-compat-qual")
    ),
    DependencySet(
        "org.codehaus.groovy",
        "2.5.4",
        listOf("groovy")
    ),
    DependencySet(
        "org.curioswitch.curiostack",
        "0.0.35",
        listOf(
            "armeria-google-cloud-core",
            "armeria-google-cloud-iam",
            "armeria-google-cloud-pubsub",
            "armeria-google-cloud-storage",
            "armeria-google-cloud-trace")
    ),
    DependencySet(
        "org.curioswitch.curiostack",
        "0.0.106",
        listOf("curio-server-framework")
    ),
    DependencySet(
        "org.curioswitch.curiostack",
        "0.0.14",
        listOf("curio-testing-framework")
    ),
    DependencySet(
        "org.eclipse.jgit",
        "5.3.0.201903130848-r",
        listOf("org.eclipse.jgit")
    ),
    DependencySet(
        "org.glassfish.jaxb",
        "2.3.0.1",
        listOf("jaxb-runtime")
    ),
    DependencySet(
        "org.immutables",
        "2.7.5",
        listOf("builder", "value", "value-annotations")
    ),
    DependencySet(
        "org.javassist",
        "3.22.0-GA",
        listOf("javassist")
    ),
    DependencySet(
        "org.jctools",
        "2.1.2",
        listOf("jctools-core")
    ),
    DependencySet(
        "org.jooq",
        "3.11.10",
        listOf("jooq", "jooq-codegen", "jooq-meta")
    ),
    DependencySet(
        "org.junit.jupiter",
        "5.4.0",
        listOf("junit-jupiter-api", "junit-jupiter-engine", "junit-jupiter-params")
    ),
    DependencySet(
        "org.junit.vintage",
        "5.4.0",
        listOf("junit-vintage-engine")
    ),
    DependencySet(
        "org.mockito",
        "2.26.0",
        listOf("mockito-core", "mockito-junit-jupiter")
    ),
    DependencySet(
        "org.slf4j",
        "1.7.25",
        listOf("slf4j-api")
    ),
    DependencySet(
        "org.simpleflatmapper",
        "6.6.1",
        listOf(
            "sfm-converter", "sfm-jdbc", "sfm-jooq", "sfm-map", "sfm-reflect", "sfm-util")
    ),
    DependencySet(
        "org.snakeyaml",
        "1.19",
        listOf("snakeyaml")
    )
)

val DEPENDENCIES = listOf(
    "com.bmuschko:gradle-docker-plugin:4.6.2",
    "com.diffplug.spotless:spotless-plugin-gradle:3.21.1",
    "com.github.ben-manes:gradle-versions-plugin:0.21.0",
    "com.google.gradle:osdetector-gradle-plugin:1.6.2",
    "com.google.maps:google-maps-services:0.9.3",
    "com.google.protobuf:protobuf-gradle-plugin:0.8.6",
    "com.hubspot.jinjava:jinjava:2.5.0",
    "com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4",
    "com.netflix.nebula:gradle-dependency-lock-plugin:6.0.0",
    "com.netflix.nebula:gradle-resolution-rules-plugin:6.0.5",
    "com.palantir:gradle-baseline-java:0.10.0",
    "de.undercouch:gradle-download-task:3.4.3",
    "gradle.plugin.com.boxfuse.client:gradle-plugin-publishing:5.2.4",
    "gradle.plugin.com.google.cloud.tools:jib-gradle-plugin:1.0.2",
    "gradle.plugin.com.gorylenko.gradle-git-properties:gradle-git-properties:2.0.0",
    "gradle.plugin.com.jetbrains.python:gradle-python-envs:0.0.25",
    "gradle.plugin.com.palantir.graal:gradle-graal:0.2.0-4-g255fd1f",
    "gradle.plugin.nl.javadude.gradle.plugins:license-gradle-plugin:0.14.0",
    "io.sgr:s2-geometry-library-java:1.0.0",
    "io.spring.gradle:dependency-management-plugin:1.0.7.RELEASE",
    "it.unimi.dsi:fastutil:8.2.2",
    "javax.activation:activation:1.1.1",
    "javax.annotation:javax.annotation-api:1.3.2",
    "javax.xml.bind:jaxb-api:2.3.0",
    "me.champeau.gradle:jmh-gradle-plugin:0.4.8",
    "mysql:mysql-connector-java:8.0.15",
    "net.ltgt.gradle:gradle-apt-plugin:0.21",
    "net.ltgt.gradle:gradle-errorprone-plugin:0.7.1",
    "nu.studer:gradle-jooq-plugin:3.0.3",
    "org.jacoco:org.jacoco.agent:0.8.3",
    "org.jacoco:org.jacoco.ant:0.8.3",
    "org.jsoup:jsoup:1.11.3"
)

dependencies {
    constraints {
        for (set in DEPENDENCY_SETS) {
            for (module in set.modules) {
                api("${set.group}:${module}:${set.version}")
            }
        }
        for (dependency in DEPENDENCIES) {
            api(dependency)
        }
    }
}

publishing {
    publications {
        register("maven", MavenPublication::class) {
            pom {
                name.set("Curiostack Bill-of-Materials")
                description.set("BOM specifying versions for all standard Curiostack dependencies.")
                url.set("https://github.com/curioswitch/curiostack/tree/master/tools/" +
                    "curiostack-bom")
                setPackaging("pom")
            }
        }
    }
}

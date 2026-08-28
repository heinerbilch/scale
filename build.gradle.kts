plugins {
	java
	`jvm-test-suite`
	id("org.springframework.boot") version "4.1.1"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "eu.bilch"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-h2console")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

springBoot {
	buildInfo()
}

testing {
    suites {
        val test = named<JvmTestSuite>("test") {
            useJUnitJupiter()
        }

        register<JvmTestSuite>("integrationTest") {
            dependencies {
                implementation("org.springframework.boot:spring-boot-starter-webmvc-test")
                implementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
                implementation("org.springframework.boot:spring-boot-h2console")
                implementation(project())
            }
            // Configure the source set for integration tests
            sources {
                java {
                    setSrcDirs(listOf("src/it/java"))
                }
            }
            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
        }
    }
}

tasks.named("check") { 
    dependsOn(testing.suites.named("integrationTest"))
}

tasks.bootBuildImage {
	builder.set("paketobuildpacks/builder-jammy-buildpackless-tiny")
	buildpacks.add("paketobuildpacks/java")
    imagePlatform.set("linux/amd64")
	imageName.set("rg.fr-par.scw.cloud/funcscwnsfocusedjemisonupa8qmlq/${project.name}")
}

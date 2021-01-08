plugins {
	id("org.springframework.boot") version "2.4.1"
	id("io.spring.dependency-management") version "1.0.10.RELEASE"
	id("com.vaadin") version "0.14.3.7"
	kotlin("plugin.spring") version "1.4.21"
	kotlin("jvm") version "1.4.21"
}

val karibuDslVersion = "1.0.4"
val vaadinVersion = "14.4.4"

defaultTasks("clean", "build")

group = "com.goyobo"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
	maven {
		url = uri("https://maven.vaadin.com/vaadin-addons")
	}
}

extra["vaadinVersion"] = "14.4.4"

dependencies {
	implementation("com.github.mvysny.karibudsl:karibu-dsl:$karibuDslVersion")
	implementation("com.vaadin:vaadin-spring-boot-starter:${vaadinVersion}") {
		// Webjars are only needed when running in Vaadin 13 compatibility mode
		listOf("com.vaadin.webjar", "org.webjars.bowergithub.insites",
			"org.webjars.bowergithub.polymer", "org.webjars.bowergithub.polymerelements",
			"org.webjars.bowergithub.vaadin", "org.webjars.bowergithub.webcomponents")
			.forEach { exclude(group = it) }
	}
	implementation("com.flowingcode.addons:font-awesome-iron-iconset:2.2.0")
	implementation("com.vaadin.componentfactory:enhanced-dialog:1.0.3")

	implementation("guru.nidi:graphviz-java:0.18.0")
	implementation("com.eclipsesource.j2v8:j2v8_linux_x86_64:4.6.0")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude("org.junit.vintage:junit-vintage-engine")
	}
}

dependencyManagement {
	imports {
		mavenBom("com.vaadin:vaadin-bom:${property("vaadinVersion")}")
	}
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.getByName<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
	if (System.getProperty("java.home").contains("dcevm")) {
		jvmArgs("-XX:HotswapAgent=fatjar")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

vaadin {
	pnpmEnable = true
}

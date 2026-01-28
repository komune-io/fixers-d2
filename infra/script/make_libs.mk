VERSION = $(shell cat ../../VERSION)

.PHONY: lint build test publish promote

lint:
	cd ../.. && ./gradlew check

build:
	cd ../.. && VERSION=$(VERSION) ./gradlew clean build publishToMavenLocal -x test

test:
	cd ../.. && ./gradlew test

stage:
	cd ../.. && VERSION=$(VERSION) PKG_MAVEN_REPO=github ./gradlew stage

promote:
	cd ../.. && VERSION=$(VERSION) PKG_MAVEN_REPO=sonatype_oss ./gradlew promote

.PHONY: version
version:
	@echo "$(VERSION)"
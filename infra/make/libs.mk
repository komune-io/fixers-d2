VERSION = $(shell cat VERSION)

.PHONY: lint build test publish promote

lint:
	./gradlew check

build:
	VERSION=$(VERSION) ./gradlew clean build publishToMavenLocal -x test

test:
	./gradlew test

stage:
	VERSION=$(VERSION) PKG_MAVEN_REPO=github ./gradlew stage

promote:
	VERSION=$(VERSION) PKG_MAVEN_REPO=sonatype_oss ./gradlew promote

.PHONY: version
version:
	@echo "$(VERSION)"
VERSION = $(shell cat VERSION)

.PHONY: lint build test publish promote

lint:
	./gradlew detekt

build:
	VERSION=$(VERSION) ./gradlew clean build publishToMavenLocal -x test

test:
	./gradlew test

publish:
	VERSION=$(VERSION) PKG_MAVEN_REPO=github ./gradlew publish --info

promote:
	VERSION=$(VERSION) PKG_MAVEN_REPO=sonatype_oss ./gradlew publish

.PHONY: version
version:
	@echo "$(VERSION)"
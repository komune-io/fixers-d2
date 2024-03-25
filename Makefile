VERSION = $(shell cat VERSION)

lint: lint-libs
build: build-libs
test: test-libs
publish: publish-libs
promote: promote-libs

docs:
	echo 'No Docs'

lint-libs:
	echo 'No Lint'
	#./gradlew detekt

build-libs:
	VERSION=$(VERSION) ./gradlew clean build publishToMavenLocal -x test

test-libs:
	./gradlew test

publish-libs:
	VERSION=$(VERSION) PKG_MAVEN_REPO=github ./gradlew publish --info

promote-libs:
	VERSION=$(VERSION) PKG_MAVEN_REPO=sonatype_oss ./gradlew publish

.PHONY: version
version:
	@echo "$(VERSION)"
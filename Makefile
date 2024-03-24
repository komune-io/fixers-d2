.PHONY: version

lint: lint-libs
build: build-libs
test: test-libs
package: package-libs

docs:
	echo 'No Docs'

lint-libs:
	echo 'No Lint'
	#./gradlew detekt

build-libs:
	./gradlew build publishToMavenLocal -x test

test-libs:
	./gradlew test

package-libs: build-libs
	./gradlew publish

version:
	@VERSION=$$(cat VERSION); \
	echo "$$VERSION"

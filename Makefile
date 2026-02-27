VERSION = $(shell cat VERSION)

.PHONY: clean lint build test stage promote

clean:
	@make -f infra/make/libs.mk clean

lint:
	@make -f infra/make/libs.mk lint

build:
	@make -f infra/make/libs.mk build
	@make -f infra/make/docs.mk build

test:
	@make -f infra/make/libs.mk test
	@make -f infra/make/docs.mk test

stage:
	@make -f infra/make/libs.mk stage

promote:
	@make -f infra/make/libs.mk promote

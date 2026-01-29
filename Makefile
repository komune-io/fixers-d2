VERSION = $(shell cat VERSION)

.PHONY: lint build test publish promote

lint:
	@make -f infra/script/make_libs.mk lint

build:
	@make -f infra/script/make_libs.mk build
	@make -f infra/script/make_docs.mk build

test:
	@make -f infra/script/make_libs.mk test
	@make -f infra/script/make_docs.mk test

publish:
	@make -f infra/script/make_libs.mk publish
	@make -f infra/script/make_docs.mk publish

promote:
	@make -f infra/script/make_libs.mk promote
	@make -f infra/script/make_docs.mk promote

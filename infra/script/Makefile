VERSION = $(shell cat ../../VERSION)

.PHONY: lint build test publish promote

lint:
	@make -f make_libs.mk lint

build:
	@make -f make_libs.mk build
	@make -f make_docs.mk build

test:
	@make -f make_libs.mk test
	@make -f make_docs.mk test

publish:
	@make -f make_libs.mk publish
	@make -f make_docs.mk publish

promote:
	@make -f make_libs.mk promote
	@make -f make_docs.mk promote

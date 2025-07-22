VERSION = $(shell cat VERSION)

.PHONY: lint build test publish promote

## New
lint:
	@make -f make_libs.mk lint
	@#make -f make_docs.mk lint
#
build:
	@make -f make_libs.mk build
	@make -f docs.mk build

test-pre:
	@make -f make_libs.mk test-pre

test:
	@make -f make_libs.mk test
	@make -f make_docs.mk test

publish:
	@make -f make_libs.mk publish
	@make -f make_docs.mk publish

promote:
	@make -f make_libs.mk promote
	@make -f make_docs.mk promote

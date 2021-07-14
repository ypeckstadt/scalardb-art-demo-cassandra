SHELL := /bin/bash

##### BUILD
.PHONY: build
build:
	@echo "Building scalar art demo CLI"
	@./gradlew build

##### ENVIRONMENT
.PHONY: env-up env-up env-reset
env-up:
	@echo "Starting environment"
	@ docker run -d --name cassandra -p 127.0.0.1:9042:9042 cassandra:3.11
	@echo "Environment up"

env-down:
	@echo "Stop environment ..."
	@docker rm -fv cassandra
	@echo "Environment down"

env-reset: env-down env-up



#### Scalar DB
.PHONY: load-schema-storage
load-schema-storage:
	@echo "Loading schema for storage mode ..."
	@java -jar scalar-schema-standalone-3.0.0.jar --cassandra -h localhost -u user -p pass -f fixtures/artDemo.storage.json -R 1
	@echo "Schema loaded"

load-schema-transaction:
	@echo "Loading schema for transaction mode ..."
	@java -jar scalar-schema-standalone-3.0.0.jar --cassandra -h localhost -u user -p pass -f fixtures/artDemo.transaction.json -R 1
	@echo "Schema loaded"

#### Scenario test
.PHONY: run-scenario
run-scenario:
	@./gradlew run --args="account add -id account_1"
	@./gradlew run --args="account add -id account_2"
	@./gradlew run --args="account charge -id account_2 -a 15000"
	@./gradlew run --args="art add -id art_1 -o account_1 -p 10000"
	@./gradlew run --args="art purchase -id art_1 -b account_2 -s account_1"
	@./gradlew run --args="account view -id account_2"
	@./gradlew run --args="art list -o account_2"
	@./gradlew run --args="art changeOwner -id art_1 -co account_2 -no account_1"
	@./gradlew run --args="art list -o account_1"



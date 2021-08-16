# Scalar DB art demo CLI

The following is a simple Java CLI application to try out and test [Scalar DB](https://github.com/scalar-labs/scalardb).  PicoCLI is used as a CLI framework and Guice for Dependency Injection.

Scalar DB has support for multiple database but in this example I am using Cassandra.   Scalar DB supports both storage and transaction mode so this example contains code for both. Each CLI command can be ran in `storage` or `transaction` mode.

As this is just a learning and study project, all commands are simply ran via `gradle` for now.



## Features

The idea is to mimic a simple  art shop where people can buy art with the funds on their account.

The following actions are supported:

- register an account
- charge funds to the account
- view account details
- register art
- view art details
- purchase art
- change the owner of the account



## Prerequisites

- Docker (if running Cassandra via Docker)

- Makefile

- Java

  

## Setup

### Cassandra

A running Cassandra instance or cluster is required.  To make things easy I have added a `Makefile` command that starts the cassandra docker container for you.

```
make env-up (start the cassandra container)
make env-down (stop the cassandra container)
make env-reset  (run both env-down and env-up)
```

Scalar DB requires configuration to be able to connect to the Cassandra database. This config can be found in the `src/main/resources/scalardb.properties` file.

```
# Comma separated contact points
scalar.db.contact_points=localhost

# Port number for all the contact points. Default port number for each database is used if empty.
scalar.db.contact_port=9042

# Credential information to access the database
scalar.db.username=cassandra
scalar.db.password=cassandra

# Storage implementation. Either cassandra or cosmos or dynamo or jdbc can be set. Default storage is cassandra.
scalar.db.storage=cassandra
```

For this project, the properties file can be used as is.

### Database schema loader

Once the Cassandra server is up and running, it is required to load the database schema.  Scalar DB will do this for you by using the Scalar DB schema loader.   The loader can be downloaded from the release [here](https://github.com/scalar-labs/scalardb/releases/tag/v3.0.0). However, to keep things simple, I have already included the file in this repository. 

Before loading the schema we have to decide if we will be using Scalar DB in `storage` or `transaction` mode.  Both require a different schema, even though the difference is only a `transaction` boolean field in the schema.

I have provided two schemas to keep them seperate and two make file commands to launch both.



Run `make load-schema-storage` for storage mode and `make load-schema-transaction` for transaction mode.

Once the schema is loaded, the following message should appear.

```
Loading schema for transaction mode ...
2021-07-07 15:15:15,881 [WARN  com.datastax.driver.core.Cluster] You listed localhost/0:0:0:0:0:0:0:1:9042 in your contact points, but it wasn't found in the control host's system.peers at startup
Schema loaded
```

The warning can be ignored for now.

## Usage

### Build

The application can be build via `./gradlew build` or by running the `make build` Makefile command.

### CLI commands

#### Storage or Transaction mode

As mentioned earlier Scalar DB supports both `storage` and `transaction` mode. 

The mode can be set via the `-m <storage|transaction>` or `--mode <storage|transaction>` argument. By default the CLI runs in `storage` mode.

For example to create an account in transaction mode the following command is required

```
./gradlew run --args="-m transaction account create -id accountName"
```

#### Account

Management of the accounts via CLI.

##### Create account

Create a new account by supplying the `id` which is string and can be anything. The account automatically will have it's `balance` set to 0 and `created_at` set to the current timestamp.

*Command*

```
./gradlew run --args="account add -id <accountName>"
```

*CLI output*

```
[INFO ] 2021-07-07 14:58:14.304 [main] AccountCreateCommand - the account has been created
[INFO ] 2021-07-07 14:58:14.306 [main] AccountCreateCommand - Account : id testAccount, balance 0, createdAt 1625637494
```



##### View account

Retrieve the account details

*Command*

```
./gradlew run --args="account view -id <accountName>"
```

*CLI output*

```
[INFO ] 2021-07-07 14:59:41.109 [main] AccountViewCommand - the account has been found
[INFO ] 2021-07-07 14:59:41.111 [main] AccountViewCommand - Account : id testAccount, balance 0, createdAt 1625637494
```



##### Charge account

Add funds to the account's balance.

*Command*

```
./gradlew run --args="account charge -id <accountName> -a <amount>"
```

*CLI output*

```
[INFO ] 2021-07-07 15:00:15.678 [main] AccountChargeCommand - the account has been charged successfully
[INFO ] 2021-07-07 15:00:15.679 [main] AccountChargeCommand - Account : id testAccount, balance 5002, createdAt 1625637494
```



#### Art

Management of the art via CLI.

##### Create art

Create new art by providing an `id`, a `price` and `owner` which is the account Id.

*Command*

```
./gradlew run --args="art create -id <art id> -p <price> -o <accountId>"
```

*CLI output*

```
[INFO ] 2021-07-07 15:02:47.686 [main] ArtCreateCommand - the art has been created
[INFO ] 2021-07-07 15:02:47.687 [main] ArtCreateCommand - Art : id hola, owner testAccount, price 102, createdAt 1625637767
```



##### View art

View art details

*Command*

```
./gradlew run --args="art view -art <art id> -acc <owner account id>"
```

*CLI output*

```
[INFO ] 2021-07-07 15:03:39.493 [main] ArtViewCommand - the art has been found
[INFO ] 2021-07-07 15:03:39.494 [main] ArtViewCommand - Art : id hola, owner testAccount, price 102, createdAt 1625637767
```



##### Buy art

Buy the art via an account. After the balance of the account is validated and credited, the ownership of the art is transfered to the new account. In case of insufficient funds, an error message is shown in the CLI.

*Command*

```
./gradlew run --args="art buy -id <art id> -b <buyer's account id> -s <seller's account id>"
```

*CLI output*

```
[INFO ] 2021-07-07 15:06:26.533 [main] ArtPurchaseCommand - the purchase of the art has been successfully completed
[INFO ] 2021-07-07 15:06:26.536 [main] ArtPurchaseCommand - Art : id hola, owner testAccount, price 102, createdAt 1625637767
[INFO ] 2021-07-07 15:06:26.536 [main] ArtPurchaseCommand - Account : id testAccount, balance 4900, createdAt 1625637494
```



##### Transfer ownership of the art to a new account

Replace the art's owner id (account id) with a new one. No funds are transferred.

*Command*

```
./gradlew run --args="art changeOwner -id <art id> -co <current owner's account id> -no <new owner's account id>"
```

*CLI output*

```
[INFO ] 2021-07-07 15:08:44.511 [main] ArtChangeOwnerCommand - the art's owner has been updated successfully
[INFO ] 2021-07-07 15:08:44.513 [main] ArtChangeOwnerCommand - Art : id hola, owner testAccount2, price 102, createdAt 1625637767
```

##### List all art for an account

List all the art that belongs to one specific account id.

*Command*

```
./gradlew run --args="art list -o <owner account id>"
```







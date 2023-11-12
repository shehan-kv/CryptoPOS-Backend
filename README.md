# CryptoPOS Point-of-Sale System

A SAAS Point-Of-Sale application for brick-and-mortar stores with multiple locations around the world. Supports multiple currencies (including crypto - planned feature), organizations, locations, inventory management and employees. Features an online terminal for processing transactions.


## Requirements

- Java 17
- PostgreSQL
- MongoDB
- Redis
- RabbitMQ

## Environment Variables

#### Gateway
```
PORT                :   Port the Gateway microservice should listen on
USER_URI            :   URI for the User microservice or loadbalancer
ORG_URI             :   URI for the Orgs microservice or loadbalancer
INVENTORY_URI       :   URI for the Inventory microservice or loadbalancer
ORDERS_URI          :   URI for the Orders microservice or loadbalancer
CORS_ORIGIN         :   CORS origin
CORS_HEADERS        :   CORS headers
```

#### User
```
PORT                :   Port the User microservice should listen on
DB_URL              :   URL of PostgreSQL database for storing user data
DB_USERNAME         :   Username to connect to the database
DB_PASSWORD         :   Password to connect to the database
REDIS_HOST          :   Redis host's IP or FQDN
REDIS_PORT          :   Redis port
RABBITMQ_HOST       :   RabbitMQ host's IP or FQDN
RABBITMQ_PORT       :   RabbitMQ port
RABBITMQ_USERNAME   :   Username to connect to RabbitMQ
RABBITMQ_PASSWORD   :   Password to connect to RabbitMQ
```

#### Orgs
```
PORT                :   Port the Orgs microservice should listen on
DB_URL              :   URL of PostgreSQL database for storing organization data
DB_USERNAME         :   Username to connect to the database
DB_PASSWORD         :   Password to connect to the database
REDIS_HOST          :   Redis host's IP or FQDN
REDIS_PORT          :   Redis port
RABBITMQ_HOST       :   RabbitMQ host's IP or FQDN
RABBITMQ_PORT       :   RabbitMQ port
RABBITMQ_USERNAME   :   Username to connect to RabbitMQ
RABBITMQ_PASSWORD   :   Password to connect to RabbitMQ
```

#### Inventory
```
PORT                :   Port the Inventory microservice should listen on
DB_URL              :   URL of PostgreSQL database for storing inventory data
DB_USERNAME         :   Username to connect to the database
DB_PASSWORD         :   Password to connect to the database
REDIS_HOST          :   Redis host's IP or FQDN
REDIS_PORT          :   Redis port
RABBITMQ_HOST       :   RabbitMQ host's IP or FQDN
RABBITMQ_PORT       :   RabbitMQ port
RABBITMQ_USERNAME   :   Username to connect to RabbitMQ
RABBITMQ_PASSWORD   :   Password to connect to RabbitMQ
```

#### Orders
```
PORT                :   Port the Orders microservice should listen on
DB_HOST             :   MongoDB host's IP or FQDN
DB_DATABASE         :   MongoDB database name
DB_AUTHDB           :   MongoDB auth database
DB_USERNAME         :   Username to connect to the database
DB_PASSWORD         :   Password to connect to the database
REDIS_HOST          :   Redis host's IP or FQDN
REDIS_PORT          :   Redis port
RABBITMQ_HOST       :   RabbitMQ host's IP or FQDN
RABBITMQ_PORT       :   RabbitMQ port
RABBITMQ_USERNAME   :   Username to connect to RabbitMQ
RABBITMQ_PASSWORD   :   Password to connect to RabbitMQ
```


## PostgreSQL Configuration

See the included `.sql` files for the schema.


## RabbitMQ Configuration

#### Exchanges:
- `pos.exchange`

#### Queues & Bindings
```
Queue           Binding           Exchange
----------------------------------------------

branch.org      branch.org        pos.exchange

branches.add    branches.add      pos.exchange

employee.info   employee.info     pos.exchange

user.branches   user.branches     pos.exchange

user.orgs       user.orgs         pos.exchange
```
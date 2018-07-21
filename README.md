# WooCommerce Order Explorer

A stand alone JavaFx application to download and manage orders from WooCommerce offline.

## Use Case

We use this app in our sports club to handle orders for team apparel.
When the athletes pick up their orders we needed a quick offline way
to see who ordered what and to mark orders (and single products in an order) as delivered.

## How to

### Download data from your WooCommerce store

#### Configure WooCommerce

* [Enable the REST API](https://docs.woocommerce.com/document/woocommerce-rest-api/#section-2)
* [Generate API keys](https://docs.woocommerce.com/document/woocommerce-rest-api/#section-3)

#### Configure WooCommerce Order Explorer

* Start the program
* Go to `File > Settings`
* Fill the settings form and press Apply

* A error message will warn you if your settings were not correct

#### Download Order Data

* Go to `File > Reload`
* Wait for the app do download all orders and all thumbnails
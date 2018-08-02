# README #

Jordan Wood / Wolfii resume for minecraft serverside programming

### What is this?
* This is a collation of many pieces of my work relating to spigot that isnt protected by some sort of disclosure agreement.
* I included all of the work in one repository to both make it easy to browse and easier to link work relating to what I am applying for

### Plugin Information
* DataAccessLayer is a plugin created to communicate with RethinkDB, a document database similar to mongo. In addition to this it creates and maintains a connection to RabbitMQ, a messaging layer.
* InventoryAPI is a relatively robust inventory management plugin to allow many other plugins to create and manage menus within a single player inventory.
* MinigameCore, whilst lacking many features, is intended to be a complete API to create an approachable abstraction of the bukkit api to allow for rapid development of minigames without having to repeat code. In addition to this it comminicates with a management program that will reschedule the game running depending on demand on the network. It was designed to allow a network to be easily scaled
* Moderation is in essence a permission and punishment plugin. However, it presents some very interesting interactionwith bungee within punishment bridge class. It effectively keeps track of and controls player connections allowing players to be disconnected from Spigot servers without actually being disconnected from the network by continually sending keep alive packets to the player after they have been disconnected.
* ModerationSlave is a companion plugin of moderation's that deals with some much more basic functionality of the punishment pipeline
* Vanity is a plugin created to monetise a server. Creating a basis for robust and feature rich items and collating them in an Inventory API menu.

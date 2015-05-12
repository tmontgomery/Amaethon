# Low Latency Communications Agenda (9am - 4pm with 12-1pm Lunch)

Intro to SBE: 9-10am
* Break: 10 min

Intro to Aeron: 10-11am
* Break: 10 min

Exercise 1: 11-12pm
* intro to auctions and exercises: 20 min
* given domain model of an auction system, turn it into a service that listens on an Aeron stream
(receiving and decoding) for the following commands:
    1. new auction
    1. bid on auction
* SBE schemas are provided for the commands

Exercise 2: 1-2pm
* add activity feed to auction service (sending and encoding). Events are:
    1. new auction
    1. new high bid on auction
    1. auction won
    1. list of active auctions (periodic)
* SBE schemas are provided for the events on the activity feed
* break: 10 min

Exercise 3: 2-3pm
* discussion of how schema's could work (new version?, new templateIds?, deprecate?): 15 min
* add a new type of auction to the service. A fixed amount of items.
* new domain model including new auction type provided
* SBE schemas updated for commands and activity feed events
* break: 10 min

Exercise 4: 3-4pm (If we have time and the WiFi cooperates!)

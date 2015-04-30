# Low Latency Communications Agenda (9am - 4pm with 12-1pm Lunch)

Intro to SBE: 9-10am
* Break: 10 min

Intro to Aeron: 10-11am
* Break: 10 min

Exercise 1: 11-12pm
* intro to auctions and exercises: 20 min
* given domain model of an auction system, turn it into a service that listens on an Aeron stream for the following commands:
    1. new auction
    1. bid on auction
* a CLI tool is provided that will send the appropriate commands
* SBE schemas are provided for the commands

Exercise 2: 1-2pm
* add activity feed to auction service _and_ CLI tool. Events are:
    1. new auction
    1. new high bid on auction
    1. auction won
* SBE schemas are provided for the events on the activity feed
* break: 10 min

Exercise 3: 2-3pm
* add a new type of auction to the service. A fixed amount of items. Optionally, no time limit. Fixed price.
* new domain model including new auction type provided
* new CLI tool provided that understands the new commands and activity feed events
* SBE schemas updated for commands and activity feed events
* break: 10 min

Exercise 4: 3-4pm
* auction free-for-all!
* set up server with completed auction service in it for entire workshop to use (LAN preferred, but could be AWS)
* each participant has a fixed budget for all items. Service will track budgets.
* 3 auctions. 1 at a time. 10 minutes each. (Give 30 min to work before starting)

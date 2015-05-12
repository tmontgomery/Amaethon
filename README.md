Amaethon
========

Auctioning with Aeron and SBE!

Why Amaethon?
-------------

[Amaethon](http://en.wikipedia.org/wiki/Amaethon)

Directory Structure
-------------------

Basic Service (Exercise 1)

    basic-service

Service with Activity Feed (Exercise 2)

    activity-feed

Advanced Service (Exercise 3)

    advanced-service

Solutions

    solutions

Individual Solutions

    solutions/basic-service
    solutions/activity-feed
    solutions/advanced-service

Building
--------

You require the following to build Amaethon:

* Latest stable [Oracle JDK 8](http://www.oracle.com/technetwork/java/)

You must first build and install [Agrona](https://github.com/real-logic/Agrona) into the local maven repository

    $ ./gradlew

After Agrona is compiled and installed, then you can build Amaethon.

Full clean and build of all modules

    $ ./gradlew
    
Running Exercises
-----------------

Once successfully built, the Amaethon exercises can be run

    $ ./gradlew :basic-service:run
    $ ./gradlew :activity-feed:run
    $ ./gradlew :advanced-service:run

Running Exercise Solutions
--------------------------

Once successfully built, the Amaethon solutions to the exercises can be run

    $ ./gradlew :solutions:basic-service:run
    $ ./gradlew :solutions:activity-feed:run
    $ ./gradlew :solutions:advanced-service:run

Generating SBE Codecs
---------------------

The SBE codecs for all the exercises are included. But if you want to generate the codecs yourself, then you will
need to have the latest SBE in a directory along side the Amaethon directory.

    $ cd ..
    $ git clone https://github.com/real-logic/simple-binary-encoding.git
    $ cd simple-binary-encoding
    $ ./gradlew

Then you may build Amaethon. This assumes you are currently in the `simple-binary-encoding` directory from
the last set of steps.

    $ cd ../Amaethon
    $ ./gradlew 

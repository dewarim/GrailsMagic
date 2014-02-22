GrailsMagic
===========

Magic the Gathering Utilities

The goal of this project is to offer a collection of useful utilities for magic card collectors, traders and players.

Currently, I am working on implementing a client for the official API of http://magickartenmarkt.de, the largest online market for Magic The Gathering in Europe (as far as I know).

Other sub projects may include scanning cards and automatic detection of cards from images via the openCV library, historical pricing data or other stuff - whatever seems interesing. If you got any ideas (and / or maybe want to help?), please let me know (mail to ingo_wiarda@dewarim.de).

# Current state

The software is very alpha - the project is very young and will likely see much refactoring and improvements.

## Features

* Fetch the first page of your cards from magickartenmarkt.de
* Store data in a temporary database in RAM

## TODO

(everything)

* Implement data structures for all parts of the MKM API.
* Use a real database for persistence (PostgreSQL)
* Add tests
* Add documentation
* Implement hashCode/equals for domain objects
* ...

# How to install

(to be defined)

You will need Java 7 (JDK) , Grails (from grails.org), a rudimentary understanding of Java [and Groovy].

After installing Java and Grails, you can start a shell (on Windows: command line, start: cmd.exe), change to the project's directory and type 

	grails run-app

Oh, and you probably need to import the magickartenmarkt.de-SSL cert into your java certificate store (see: doc/ssl-howto.txt for a hint). If this seems daunting to you, you may wish to wait until the installation process runs a little bit smoother.

## Configuration file

Copy the sample configuration file from the doc folder to your .grails folder.

# Security

At the moment the application is intended for personal use on your own hardware . Of course, being a Grails application, there is no reason not to put it online on your server ... but since access to the Magickartenmarkt requires a (free) personal API key, you will have to configure the software to use your own key. And if someone gets your key, they can buy magic cards with it, so be careful.

The code is open sourced because (among other things) personally I would not like to enter my API key in a proprietary application.

# License

Currently licensend under the GPL 3, see LICENSE file.

# Author

Ingo Wiarda

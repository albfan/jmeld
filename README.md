# JMeld

A visual diff and merge tool. It is build to be fast with large files and runs on any operating system.

This is how it looks like
![jmeld](res/jmeld.png)

And here is a isolate diff component
![jmeld component](res/jmeld-component.png)

## Background

This repo comes from a svn migration to git from [jmeld](http://keeskuip.home.xs4all.nl/jmeld/) which in fact, is modelled after the fantastic program [meld](http://meldmerge.org)

## Compilation

JMeld can be compiled with the command:

    $ ant

or with maven

    $ mvn clean install

## Usage

Start jmeld with the command:

    $ java -jar JMeld.jar <file1> <file2>

## License

This license of this program is the LGPL.


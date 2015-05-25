# XEvePro

An XML event processor. Implementing a command line interface for [Esper](http://www.espertech.com/) to:
* Read EPL modules from files.
* Read events from files containing subsequent XML documents.
* Synchronize Esper to event time expressed as xsd:DateTime in the events
* Produce configurable CSV output from the EPL queries. Can be directed to a file.
* Time the execution from a selectable command line argument onwards, save results to file.

## Development status

Currently functions as expected by the author. No further roadmap has been established, but new features may be added if needed.

## Installation

Has been developed as an [SBT](http://www.scala-sbt.org/) project under [IDEA](https://www.jetbrains.com/idea/). Execution should be as simple as:

1) Install SBT if not already installed.

2) Clone this repository.

3) Run SBT from repository root: `$ sbt`

4) Compile: `> compile`

5) Run: `> run -r input/time_extract.epl -r input/queries_parameters.epl -r input/queries_comm_pack.epl --time=- -i input/eventRun_sample.xml`

Output should look like:

    May 25, 2015 2:29:07 PM com.espertech.esper.core.service.EPServiceProviderImpl doInitialize
    INFO: Initializing engine URI 'default' version 5.2.0
    eve:eve4,LostAfterCommissioning,epc:030001.0012345.100000010093
    ...
	eve:eve5,CounterfeitInPacking,epc:030001.0054321.100000010081
	...
	At 9.0E-6: Command: -i, Parameter: input/eventRun_sample.xml
	At 0.258807: Done

## Instructions for use

Some instructions are available in the github wiki.

## Credits

The included [example from pharmaceutical industry](http://windermere.aston.ac.uk/~solankm2/papers/MonikaSolankiISWC2014.pdf) appears courtesy of [Monika Solanki](http://www.monikasolanki.com).

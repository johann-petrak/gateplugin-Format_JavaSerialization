# gateplugin-Format_Misc

This plugin adds support to load and write documents in several
new formats with and without some compression algorithms. 

The formats supported and their extensions are:
* GATE XML: same as the traditional default XML format used for GATE documents, but 
using its own extensions, ```gatexml```. This makes it easier to distinguish between
XML documents that are known to be GATE format and generic other XML documents. 
* Java object serialization: this stores GATE documents using the default Java object
serialization mechanism used with ObjectStreams. 

For each of the formats above there is also support for compression, using the following
compression algorithms:
* GZIP compression: this uses the JAVA built-in compression algorithm, and an additional
extension ```.gz``` so for example a GATE XML document compressed with GZIP will have
the extension ```.gatexml.gz```.
* Snappy compression: this uses the Snappy snappy-java compression format and the additional
extension ```.snappy```. This file format can also read files compressed with as Snappy raw,
provided they use the same file extension ```.snappy```. 


List of all combinations of formats and compression algorithms supported, with their extensions
and mime types:
* GATE XML: ```.gatexml```, ```application/xml+gate```, ```text/xml+gate```
* GATE XML, Snappy compressed: ```.gatexml.snappy```, ```application/xml+gate+snappy```, ```text/xml+gate+snappy```
* GATE XML, GZIP compressed: ```.gatexml.gz```, ```application/xml+gate+gzip```, ```text/xml+gate+gzip```
* Java object serialization: ```.ser```, ```application/javaserialization```
* Java object serialization, Snappy compressed: ```.ser.snappy```, ```application/javaserialization+snappy```
* Java object serialization, GZIP compressed: ```.ser.gz```, ```application/javaserialization+gz```



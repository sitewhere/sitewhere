====================================
Handling Direct Socket Event Sources
====================================
There are many cases where SiteWhere must interact with devices that have proprietary
protocols and which need to connect directly to the system via a TCP/IP socket. For
example, a GPS tracking device may periodically connect to a server socket and
transmit waypoint data that has been collected and buffered. These devices generally
connect using `GPRS <http://en.wikipedia.org/wiki/General_Packet_Radio_Service>`_ 
over a cellular connection and use messages based on 
`NMEA <http://en.wikipedia.org/wiki/NMEA_0183>`_ sentences. In order to provide the
maximum flexibility in interacting with such devices, SiteWhere provides some core
components that can be used for easy integration.


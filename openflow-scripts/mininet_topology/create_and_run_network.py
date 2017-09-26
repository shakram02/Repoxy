#!/usr/bin/python
from mininet import clean
from mininet.log import setLogLevel
from mininet.net import Mininet
from mininet.util import dumpNodeConnections

from complex_topology import ComplexTopo


def create_network():
    try:
        import sys
        controller_ip = sys.argv[1]
    except IndexError:
        controller_ip = "192.168.1.248"

    controller_port = 6833

    net = Mininet()
    topo = ComplexTopo(net, 4, 2, controller_ip, controller_port)
    topo.build_network()

    return net


network = None
try:
    clean.cleanup()
    setLogLevel('info')
    network = create_network()
    network.start()

    print "[Showing connections]"
    dumpNodeConnections(network.hosts)
    print "[Testing network]"
    network.pingAll()
    network.stop()
except KeyboardInterrupt:
    if network is not None:
        network.stop()

#!/usr/bin/python
from mininet import clean
from mininet.log import setLogLevel
from mininet.net import Mininet
from mininet.node import RemoteController
from mininet.util import dumpNodeConnections

import complex_topology


def create_network():
    try:
        import sys
        controller_ip = sys.argv[1]
    except IndexError:
        controller_ip = "192.168.1.248"

    controller_port = 6833
    # controller = RemoteController('c0', ip=controller_ip, port=6833)

    topo = complex_topology.ComplexTopo()

    net = Mininet(topo, build=False)
    controller = net.addController('c0', RemoteController, ip=controller_ip, port=controller_port)

    topo.build_network(net, 4, 2, controller)

    return net


network = None
try:
    clean.cleanup()
    setLogLevel('info')
    network = create_network()
    network.start()

    print "Showing connections"
    dumpNodeConnections(network.hosts)
    print "Testing network"
    network.pingAll()
    network.stop()
except KeyboardInterrupt:
    if network is not None:
        network.stop()

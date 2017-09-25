#!/usr/bin/python
from mininet import clean
from mininet.node import RemoteController
from mininet.net import Mininet
from mininet.util import dumpNodeConnections
from mininet.log import setLogLevel
import complex_topology


def create_network():
    try:
        import sys
        controller_ip = sys.argv[1]
    except IndexError:
        controller_ip = "192.168.1.248"

    controller_port = 6833
    # controller = RemoteController('c0', ip=controller_ip, port=6833)

    topo = complex_topology.ComplexTopo(4, 2)
    net = Mininet(topo, build=False)

    c0 = net.addController('c0', controller=RemoteController, ip=controller_ip, port=controller_port)

    # Connect switches to controller
    for s in net.switches:
        s.start([c0])

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

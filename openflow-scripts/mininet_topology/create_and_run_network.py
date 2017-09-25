#!/usr/bin/python
from mininet import clean
from mininet.node import RemoteController
from mininet.net import Mininet
from mininet.util import dumpNodeConnections
from mininet.log import setLogLevel
from mininet.topo import Topo


class ComplexTopo(Topo):
    """Single switch connected to n hosts"""

    def build(self, n, m):
        last_switch = None

        for s in range(m):
            # Create a new switch
            switch = self.addSwitch('s%s' % s)

            # Connect all switches
            if last_switch is not None:
                self.addLink(switch, last_switch)

            last_switch = switch
            self.create_hosts(n, switch, n * s)

    def create_controller(self, ip, port):
        pass

    def create_hosts(self, n, switch, host_id):
        """ Add n hosts to the given switch """

        # Add n hosts for each switch
        for h in range(n):
            host = self.addHost('h%s' % host_id)
            self.addLink(switch, host)
            host_id += 1


def create_network():
    try:
        import sys
        controller_ip = sys.argv[1]
    except IndexError:
        controller_ip = "192.168.1.248"

    controller_port = 6833
    # controller = RemoteController('c0', ip=controller_ip, port=6833)

    topo = ComplexTopo(4, 2)
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

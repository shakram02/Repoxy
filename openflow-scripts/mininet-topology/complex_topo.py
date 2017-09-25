#!/usr/bin/python
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

    def create_hosts(self, n, switch, host_id):
        """ Add n hosts to the given switch """

        # Add n hosts for each switch
        for h in range(n):
            host = self.addHost('h%s' % host_id)
            self.addLink(switch, host)
            host_id += 1

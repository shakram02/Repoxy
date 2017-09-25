#!/usr/bin/python
from mininet.topo import Topo


class ComplexTopo(Topo):
    """Single switch connected to n hosts"""

    def build_network(self, net, switch_count, hosts_per_switch, controller):
        hosts = self._create_hosts(net, switch_count, hosts_per_switch)
        switches = self._create_switches(net, switch_count)

        self._connect_hosts_to_switches(net, hosts, switches, hosts_per_switch)
        self._connect_switches(net, switches)
        self._connect_controller_to_switches(switches, controller)
        self.build()

    @staticmethod
    def _connect_hosts_to_switches(net, hosts, switches, hosts_per_switch):

        for switch in reversed(switches):
            # Take the number of hosts of this switch out of the list
            for i in range(hosts_per_switch):
                net.addLink(switch, hosts.pop())

    @staticmethod
    def _connect_switches(net, switches):
        for i in range(len(switches) - 1):
            net.addLink(switches[i], switches[i + 1])

    @staticmethod
    def _connect_controller_to_switches(switches, controller):
        for s in switches:
            s.start([controller])

    @staticmethod
    def _create_switches(net, switch_count):
        """ Creates network switches """
        return [net.addSwitch('s%s' % x) for x in range(switch_count)]

    @staticmethod
    def _create_hosts(net, switch_count, hosts_per_switch):
        """ Creates all hosts in network """
        return [net.addHost('h%s' % x) for x in range(switch_count * hosts_per_switch)]

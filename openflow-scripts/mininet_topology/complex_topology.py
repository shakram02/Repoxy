#!/usr/bin/python
from mininet.node import RemoteController


class ComplexTopo:
    def __init__(self, net, switch_count, hosts_per_switch, controller_ip, controller_port):
        self.net = net
        self.switch_count = switch_count
        self.hosts_per_switch = hosts_per_switch
        self.controller = self._create_controller(controller_ip, controller_port)

    def build_network(self):
        hosts = self._create_hosts()
        switches = self._create_switches()

        self.net.addController(self.controller)
        self._connect_hosts_to_switches(hosts, switches)
        self._connect_switches(switches)
        self._connect_controller_to_switches(switches)

    def _connect_hosts_to_switches(self, hosts, switches):
        for switch in reversed(switches):
            # Take the number of hosts of this switch out of the list
            for i in range(self.hosts_per_switch):
                self.net.addLink(switch, hosts.pop())

    def _connect_switches(self, switches):
        for i in range(len(switches) - 1):
            self.net.addLink(switches[i], switches[i + 1])

    def _connect_controller_to_switches(self, switches):
        for s in switches:
            s.start([self.controller])

    @staticmethod
    def _create_controller(controller_ip, controller_port):
        return RemoteController('c0', ip=controller_ip, port=controller_port)

    def _create_switches(self):
        """ Creates network switches """
        return [self.net.addSwitch('s%s' % x) for x in range(self.switch_count)]

    def _create_hosts(self):
        """ Creates all hosts in network """
        return [self.net.addHost('h%s' % x) for x in range(self.switch_count * self.hosts_per_switch)]

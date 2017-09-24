#!/usr/bin/python

from mininet.topo import Topo
from mininet.net import Mininet
from mininet.util import dumpNodeConnections
from mininet.log import setLogLevel

class ComplexTopo(Topo):
 "Single switch connected to n hosts"

 def build(self, n, m):
  last_switch = None
  
  for s in range(m):
   # Create a new switch
   switch = self.addSwitch('s%s'% s)

   # Connect all switches
   if last_switch is not None:
     self.addLink(switch,last_switch)

   last_switch = switch
   self.create_hosts(n, switch, n*s)

 def create_hosts(self, n, switch, host_id):
  """ Add n hosts to the given switch """
 
  # Add n hosts for each switch
  for h in range(n):
   host = self.addHost('h%s' % host_id)
   self.addLink(switch, host)
   host_id += 1


def main():
 # 
 topo = ComplexTopo(4, 2)
 net = Mininet(topo)
 net.start()

 print "Showing connections"
 dumpNodeConnections(net.hosts)
 print "Testing network"
 net.pingAll()
 net.stop()

if __name__ == '__main__':
 setLogLevel('info')
 main()

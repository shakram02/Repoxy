from mininet.node import RemoteController
from mininet.net import Mininet
from mininet.util import dumpNodeConnections
from mininet.log import setLogLevel
from . import complex_topo


def main():
    import sys
    controller_ip = sys.argv[1]
    controller = RemoteController('c0', ip=controller_ip, port=6833)

    topo = complex_topo.ComplexTopo(4, 2)
    net = Mininet(topo)
    net.addController(controller)
    net.start()

    print "Showing connections"
    dumpNodeConnections(net.hosts)
    print "Testing network"
    net.pingAll()
    net.stop()


if __name__ == '__main__':
    setLogLevel('info')
    main()

package multipaxos
import scala.io.Source
import scala.actors._
import scala.actors.Actor._
import scala.concurrent._
import scala.util.control.Breaks._

// argh, need to change the act method...
class Server(sname: String, l_id:Int) extends Actor{
    val name = sname
    val leader_id = l_id
    val acceptor = new Acceptor(name, l_id)
    val replica = new Replica(name, l_id)

    var leader = new Leader(name, l_id)
    var servers = List[Server]()

    def init_servers(inits: List[Server]) = {
      servers = inits
      acceptor.init(servers)
      replica.init(servers)
      if(isLeader) {
        leader.init(getReplicas(servers), getAcceptors(servers))
      }

    }
    def leaderServer():Server = {
        return servers(leader_id)
    }

    def getAcceptor():Acceptor={ return acceptor}

    def getReplicas():Replica={return replica}

    def isLeader():Boolean = leaderServer().name == name

    def getReplicas(ss: List[Server]):List[Replica] = ss.map( s => s.replica)

    def getAcceptors(ss: List[Server]):List[Acceptor] = ss.map( s => s.acceptor)

    def act(){
        acceptor.start
        replica.start  
        if(isLeader()){leader.start}
            
        while(true){
            receive{
                case ("request", c: Command) => {
                    replica !("request", c)
                }
            }
        }

    }

     def printArray()={
        replica.printArray()

    }

    }

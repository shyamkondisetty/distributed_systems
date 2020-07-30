package com.dist.simplekafka.kip500

import java.util.concurrent.ConcurrentHashMap
import scala.jdk.CollectionConverters._

class LeaderLeaseTracker(var leases: ConcurrentHashMap[String, Lease], var clock: SystemClock, var server: Controller) extends Logging with LeaseTracker {
  val now: Long = clock.nanoTime
  this.leases.values.forEach((l: Lease) => {
    def foo(l: Lease) = {
      l.refresh(now)
    }

    foo(l)
  })
  info("Creating leader tracker with " + this.leases)
  private val scheduler = new HeartBeatScheduler(this.expireLeases)

  //<codeFragment name="removeAndProposeForReplication">
  override def expireLeases() = {
    val now = System.nanoTime
    val leaseIds = leases.keySet
    for (leaseId <- leaseIds.asScala) {
      val lease = leases.get(leaseId)
      if (lease.getExpirationTime < now) {
        leases.remove(leaseId)
        server.propose(FenceBroker(lease.getName))
      }
    }
  }

  //<codeFragment name="addAndRefresh">
  //</codeFragment>
  override def addLease(lease: Lease): Unit = {
    leases.put(lease.getName, lease)
    lease.refresh(clock.nanoTime)
  }

  override def start(): Unit = {
    scheduler.start()
  }

  override def stop(): Unit = {
    scheduler.cancel()
  }

  override def refreshLease(name: String): Unit = {
    leases.get(name).refresh(clock.nanoTime)
  }

  override def revokeLease(name: String): Unit = {
    leases.remove(name)
  }
}
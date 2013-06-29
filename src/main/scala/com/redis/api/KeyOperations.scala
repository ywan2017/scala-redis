package com.redis
package api

import scala.concurrent.Future
import serialization._
import akka.pattern.ask
import akka.actor._
import akka.util.Timeout

trait KeyOperations {
  import KeyCommands._

  implicit val timeout: Timeout

  // KEYS
  // returns all the keys matching the glob-style pattern.
  def keys[A](pattern: Any = "*")(implicit format: Format, parse: Parse[A]): ActorRef => Future[Option[List[Option[A]]]] = {client: ActorRef=>
    client.ask(Keys(pattern)).mapTo[Option[List[Option[A]]]]
  }

  // RANDOMKEY
  // return a randomly selected key from the currently selected DB.
  def randomkey[A](implicit parse: Parse[A]): ActorRef => Future[Option[A]] = {client: ActorRef=>
    client.ask(RandomKey).mapTo[Option[A]]
  }

  // RENAME (oldkey, newkey)
  // atomically renames the key oldkey to newkey.
  def rename(oldkey: Any, newkey: Any)(implicit format: Format): ActorRef => Future[Option[Boolean]] = {client: ActorRef=>
    client.ask(Rename(oldkey, newkey)).mapTo[Option[Boolean]]
  }
  
  // RENAMENX (oldkey, newkey)
  // rename oldkey into newkey but fails if the destination key newkey already exists.
  def renamenx(oldkey: Any, newkey: Any)(implicit format: Format): ActorRef => Future[Option[Boolean]] = {client: ActorRef=>
    client.ask(Rename(oldkey, newkey, nx = true)).mapTo[Option[Boolean]]
  }
 
  // DBSIZE
  // return the size of the db.
  def dbsize: ActorRef => Future[Option[Long]] = {client: ActorRef=>
    client.ask(DBSize).mapTo[Option[Long]]
  }

  // EXISTS (key)
  // test if the specified key exists.
  def exists(key: Any)(implicit format: Format): ActorRef => Future[Option[Boolean]] = {client: ActorRef=>
    client.ask(Exists(key)).mapTo[Option[Boolean]]
  }

  // DELETE (key1 key2 ..)
  // deletes the specified keys.
  def del(key: Any, keys: Any*)(implicit format: Format): ActorRef => Future[Option[Long]] = {client: ActorRef=>
    client.ask(Delete(key, keys:_*)).mapTo[Option[Long]]
  }

  // TYPE (key)
  // return the type of the value stored at key in form of a string.
  def getType(key: Any)(implicit format: Format): ActorRef => Future[Option[String]] = {client: ActorRef=>
    client.ask(GetType(key)).mapTo[Option[String]]
  }

  // EXPIRE (key, expiry)
  // sets the expire time (in sec.) for the specified key.
  def expire(key: Any, ttl: Int)(implicit format: Format): ActorRef => Future[Option[Boolean]] = {client: ActorRef=>
    client.ask(Expire(key, ttl)).mapTo[Option[Boolean]]
  }

  // PEXPIRE (key, expiry)
  // sets the expire time (in milli sec.) for the specified key.
  def pexpire(key: Any, ttlInMillis: Int)(implicit format: Format): ActorRef => Future[Option[Boolean]] = {client: ActorRef=>
    client.ask(Expire(key, ttlInMillis, millis = true)).mapTo[Option[Boolean]]
  }

  // EXPIREAT (key, unix timestamp)
  // sets the expire time for the specified key.
  def expireat(key: Any, timestamp: Long)(implicit format: Format): ActorRef => Future[Option[Boolean]] = {client: ActorRef=>
    client.ask(ExpireAt(key, timestamp)).mapTo[Option[Boolean]]
  }

  // PEXPIREAT (key, unix timestamp)
  // sets the expire timestamp in millis for the specified key.
  def pexpireat(key: Any, timestampInMillis: Long)(implicit format: Format): ActorRef => Future[Option[Boolean]] = {client: ActorRef=>
    client.ask(ExpireAt(key, timestampInMillis, millis = true)).mapTo[Option[Boolean]]
  }

  // TTL (key)
  // returns the remaining time to live of a key that has a timeout
  def ttl(key: Any)(implicit format: Format): ActorRef => Future[Option[Long]] = {client: ActorRef=>
    client.ask(TTL(key)).mapTo[Option[Long]]
  }

  // PTTL (key)
  // returns the remaining time to live of a key that has a timeout in millis
  def pttl(key: Any)(implicit format: Format): ActorRef => Future[Option[Long]] = {client: ActorRef=>
    client.ask(TTL(key, millis = true)).mapTo[Option[Long]]
  }

  // FLUSHDB the DB
  // removes all the DB data.
  def flushdb: ActorRef => Future[Option[Boolean]] = {client: ActorRef=>
    client.ask(FlushDB(all = false)).mapTo[Option[Boolean]]
  }

  // FLUSHALL the DB's
  // removes data from all the DB's.
  def flushall: ActorRef => Future[Option[Boolean]] = {client: ActorRef=>
    client.ask(FlushDB(all = true)).mapTo[Option[Boolean]]
  }

  // MOVE
  // Move the specified key from the currently selected DB to the specified destination DB.
  def move(key: Any, db: Int)(implicit format: Format): ActorRef => Future[Option[Boolean]] = {client: ActorRef=>
    client.ask(Move(key, db)).mapTo[Option[Boolean]]
  }
  
  // QUIT
  // exits the server.
  def quit: ActorRef => Future[Option[Boolean]] = {client: ActorRef=>
    // @Todo : send("QUIT")(disconnect)
    client.ask(Quit).mapTo[Option[Boolean]]
  }
  
  // AUTH
  // auths with the server.
  def auth(secret: Any)(implicit format: Format): ActorRef => Future[Option[Boolean]] = {client: ActorRef=>
    client.ask(Auth(secret)).mapTo[Option[Boolean]]
  }
}

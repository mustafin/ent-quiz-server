object Test {

  trait Observer[T] {
    def onError(error: Throwable): Unit

    def onCompleted(): Unit

    def onNext(value: T): Unit
  }

  trait Observable[T]{
    def subscribe(observer: Observer[T]): Subscription
  }

  trait Subscription{
    def unsubscribe(): Unit
    def isUnsubscribed: Boolean
  }



}
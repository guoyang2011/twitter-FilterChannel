package cn.changhong.twitter

/**
 * Created by yangguo on 15-3-12.
 */
package filter {

  import com.twitter.util.Future

  abstract class Service[Req, Rep] extends ((Req) => Future[Rep]) {
    def apply(request: Req): Future[Rep]
  }

  abstract class Filter[ReqIn, RepOut, TempReqIn, TempRepOut] extends ((ReqIn, Service[TempReqIn, TempRepOut]) => Future[RepOut]) {
    def apply(request: ReqIn, service: Service[TempReqIn, TempRepOut]): Future[RepOut]

    def andThen[Req2, Rep2](next: Filter[TempReqIn, TempRepOut, Req2, Rep2]) = {
      new Filter[ReqIn, RepOut, Req2, Rep2] {
        override def apply(request: ReqIn, service: Service[Req2, Rep2]): Future[RepOut] = Filter.this(request, new Service[TempReqIn, TempRepOut] {
          override def apply(request: TempReqIn): Future[TempRepOut] = next(request, service)
        })
      }
    }
    def andThen(service: Service[TempReqIn, TempRepOut]) = {
      new Service[ReqIn, RepOut] {
        override def apply(request: ReqIn): Future[RepOut] = Filter.this(request, service)
      }
    }
  }

}


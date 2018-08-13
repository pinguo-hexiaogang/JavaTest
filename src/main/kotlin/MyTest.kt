import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

fun main(args: Array<String>) {
    val flowable1 = Flowable.fromCallable {
        System.out.println("first callable")
        return@fromCallable "first string"
    }.subscribeOn(Schedulers.io())

    val flowable2 = Flowable.fromCallable {
        System.out.println("second callable")
        return@fromCallable "second string"
    }.subscribeOn(Schedulers.computation())

    Flowable.concat(flowable1, flowable2).observeOn(Schedulers.newThread()).subscribe {
        System.out.println(it)
    }


    Thread.sleep(2000)
}


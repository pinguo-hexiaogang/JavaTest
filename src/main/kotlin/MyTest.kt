import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    /* val flowable1 = Flowable.fromCallable {
         System.out.println("first callable")
         return@fromCallable "first string"
     }.subscribeOn(Schedulers.io())

     val flowable2 = Flowable.fromCallable {
         System.out.println("second callable")
         return@fromCallable "second string"
     }.subscribeOn(Schedulers.computation())

     Flowable.concat(flowable1, flowable2).observeOn(Schedulers.newThread()).subscribe {
         System.out.println(it)
     }*/

    //testMultiThread()

    //testKotlinReturn()
    testZip()

    Thread.sleep(5000)
}

private fun testKotlinReturn() {
    (0 until 100).forEach {
        println(it)
        if (it == 20) return
    }

}

private fun testMultiThread() {

    Flowable.just("a", "b", "c")
            .doOnNext {
                println("on next,data:$it,thread:${Thread.currentThread()}")
            }
            .flatMap {
                createFlowable(it)
            }.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.single())
            .subscribe {
                println("on subscribe data:$it,thread:${Thread.currentThread()}")
            }

}

private fun createFlowable(data: String): Flowable<String> {
    val flowable = Flowable.create<String>({
        println("data:$data,thread:${Thread.currentThread()}")
        Thread.sleep(3000)
        it.onNext(data)
        it.onComplete()
    }, BackpressureStrategy.BUFFER)
    //.subscribeOn(Schedulers.io())
    return flowable
}

private fun testZip() {
    var emmiter: FlowableEmitter<Int>? = null
    val flowable1 = Flowable.just(1, 2)
    val flowable2 = Flowable.create<Int>({
        emmiter = it
    }, BackpressureStrategy.BUFFER)

    val flowable3 = Flowable.just(1)
            .delay(4000, TimeUnit.MILLISECONDS)
            .doOnNext { emmiter?.onNext(3) }
            .subscribe({})

    Flowable.zip(flowable1, flowable2, BiFunction<Int, Int, Int> { t1, t2 -> t1 + t2 })
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .subscribe({
                println("value is:$it")
            }, {}, {
                print("complete")
            })

}


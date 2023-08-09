package top.iseason.bukkittemplate.utils.bukkit

import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import top.iseason.bukkittemplate.BukkitTemplate
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import java.util.function.Supplier

object SchedulerUtils {

    /**
     * 在异步线程非阻塞调用
     */
    fun castAsync(task: Runnable) = CompletableFuture.runAsync(task)

    /**
     * 在主线程非阻塞调用
     */
    fun castSync(task: Runnable) = Bukkit.getScheduler().runTask(BukkitTemplate.getPlugin(), task)

    /**
     * 在异步线程阻塞调用
     */
    fun <U> callAsync(task: Supplier<U>) = CompletableFuture.supplyAsync(task).get()

    /**
     * 在主线程阻塞调用
     */
    fun <T> callSync(task: Callable<T>) = Bukkit.getScheduler().callSyncMethod(BukkitTemplate.getPlugin(), task)

    /**
     * 提交一个 bukkit Consume任务，可在任务中取消自己
     * @param delay 延迟 单位tick
     * @param period 循环周期 单位tick
     * @param async 是否异步
     * @param consumer 任务消费者
     */
    fun consume(
        delay: Long = 0,
        period: Long = 0,
        async: Boolean = false,
        consumer: Consumer<BukkitTask>
    ): BukkitTask {
        val runnable = SubmitRunnable(consumer)
        runnable.task = submit(delay, period, async, runnable)
        return runnable.task
    }

    /**
     * 提交一个 bukkit runnable任务
     * @param delay 延迟 单位tick
     * @param period 循环周期 单位tick
     * @param async 是否异步
     * @param task 你的任务
     */
    fun submit(
        delay: Long = 0,
        period: Long = 0,
        async: Boolean = false,
        runnable: Runnable
    ): BukkitTask {
        check(delay >= 0) { "delay must grater than 0" }
        check(period >= 0) { "period must grater than 0" }
        return if (async) {
            if (period > 0) {
                Bukkit.getScheduler().runTaskTimerAsynchronously(BukkitTemplate.getPlugin(), runnable, delay, period)
            } else {
                Bukkit.getScheduler().runTaskLaterAsynchronously(BukkitTemplate.getPlugin(), runnable, delay)
            }
        } else {
            if (period > 0) {
                Bukkit.getScheduler().runTaskTimer(BukkitTemplate.getPlugin(), runnable, delay, period)
            } else {
                Bukkit.getScheduler().runTaskLater(BukkitTemplate.getPlugin(), runnable, delay)
            }
        }
    }

    private class SubmitRunnable(private val consumer: Consumer<BukkitTask>) : Runnable {
        lateinit var task: BukkitTask
        override fun run() {
            consumer.accept(task)
        }
    }

}